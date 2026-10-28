package br.com.techchallenge.feedback.entrypoint.lambda.receber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.techchallenge.feedback.application.usecase.AvaliacaoRegistrada;
import br.com.techchallenge.feedback.application.usecase.RegistrarAvaliacaoCommand;
import br.com.techchallenge.feedback.application.usecase.RegistrarAvaliacaoUseCase;
import br.com.techchallenge.feedback.domain.AvaliacaoInvalidaException;
import br.com.techchallenge.feedback.entrypoint.lambda.http.RespostaApiGateway;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * FUNÇÃO 1 (feedback-receber-avaliacao) — ADAPTADOR DE ENTRADA HTTP.
 *
 * Só traduz: evento do API Gateway → comando → caso de uso → resposta HTTP.
 * Nenhuma regra de negócio aqui (validação, urgência e gravação estão no caso de uso/domínio).
 */
@Named("receberAvaliacao")
public class ReceberAvaliacaoHandler implements RequestStreamHandler {

    private static final Logger LOG = Logger.getLogger(ReceberAvaliacaoHandler.class);

    private final RegistrarAvaliacaoUseCase registrarAvaliacao;
    private final ObjectMapper mapper;

    /**
     * @param registrarAvaliacao caso de uso que valida e grava a avaliação
     * @param mapper             conversor JSON do Quarkus
     */
    @Inject
    public ReceberAvaliacaoHandler(RegistrarAvaliacaoUseCase registrarAvaliacao, ObjectMapper mapper) {
        this.registrarAvaliacao = registrarAvaliacao;
        this.mapper = mapper;
    }

    /**
     * Trata {@code POST /avaliacao}. Responde 201 (gravada), 400 (entrada inválida)
     * ou 500 (erro inesperado), sempre no formato da integração proxy do API Gateway.
     *
     * @param entrada evento do API Gateway; o JSON da requisição vem em texto no campo {@code body}
     * @param saida   onde a resposta HTTP é escrita
     * @param contexto contexto da execução na AWS
     * @throws IOException se não for possível escrever a resposta
     */
    @Override
    public void handleRequest(InputStream entrada, OutputStream saida, Context contexto) throws IOException {
        try {
            JsonNode evento = mapper.readTree(entrada);
            JsonNode body = evento.path("body");
            if (!body.isTextual() || body.asText().isBlank()) {
                RespostaApiGateway.erro(mapper, saida, 400, "Corpo da requisição ausente");
                return;
            }

            JsonNode corpo = mapper.readTree(body.asText());
            RegistrarAvaliacaoCommand comando = new RegistrarAvaliacaoCommand(
                    textoOuNulo(corpo.get("descricao")),
                    inteiroOuNulo(corpo.get("nota")));

            AvaliacaoRegistrada registrada = registrarAvaliacao.executar(comando);

            // Por privacidade, o texto da descrição NÃO vai para o log.
            LOG.info("Avaliação salva id=" + registrada.id()
                    + " nota=" + registrada.nota() + " urgencia=" + registrada.urgencia());

            ObjectNode resposta = mapper.createObjectNode();
            resposta.put("id", registrada.id());
            resposta.put("urgencia", registrada.urgencia().name());
            resposta.put("dataEnvio", registrada.dataEnvio().toString());
            resposta.put("mensagem", "Avaliação recebida com sucesso");
            RespostaApiGateway.escrever(mapper, saida, 201, resposta);

        } catch (AvaliacaoInvalidaException e) {
            LOG.warn("Requisição inválida: " + e.getMessage());
            RespostaApiGateway.erro(mapper, saida, 400, e.getMessage());
        } catch (JsonProcessingException e) {
            LOG.warn("JSON inválido: " + e.getOriginalMessage());
            RespostaApiGateway.erro(mapper, saida, 400,
                    "Corpo inválido. Envie um JSON com 'descricao' (texto) e 'nota' (inteiro de 0 a 10)");
        } catch (Exception e) {
            LOG.error("Erro inesperado ao salvar avaliação", e);
            RespostaApiGateway.erro(mapper, saida, 500, "Erro interno ao processar a avaliação");
        }
    }

    /** Aceita só texto de verdade ("abc"); número, lista etc. viram null (= inválido). */
    private static String textoOuNulo(JsonNode campo) {
        return (campo != null && campo.isTextual()) ? campo.asText() : null;
    }

    /** Aceita só inteiro de verdade (9); "9" entre aspas ou 9.5 viram null (= inválido). */
    private static Integer inteiroOuNulo(JsonNode campo) {
        return (campo != null && campo.isInt()) ? campo.intValue() : null;
    }
}