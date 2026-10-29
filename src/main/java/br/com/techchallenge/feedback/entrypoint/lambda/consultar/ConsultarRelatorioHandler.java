package br.com.techchallenge.feedback.entrypoint.lambda.consultar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.techchallenge.feedback.application.usecase.ConsultarRelatorioUseCase;
import br.com.techchallenge.feedback.domain.PeriodoInvalidoException;
import br.com.techchallenge.feedback.domain.PeriodoRelatorio;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;
import br.com.techchallenge.feedback.entrypoint.lambda.http.RespostaApiGateway;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Adaptador HTTP da Função 4: traduz {@code GET /relatorio?inicio=aaaa-mm-dd&fim=aaaa-mm-dd}
 * em uma chamada ao caso de uso e devolve o relatório em JSON.
 *
 * Por privacidade, a resposta tem SÓ números agregados (total, médias e contagens);
 * o texto das avaliações não sai por este endpoint.
 */
@Named("consultarRelatorio")
public class ConsultarRelatorioHandler implements RequestStreamHandler {

    private static final Logger LOG = Logger.getLogger(ConsultarRelatorioHandler.class);

    private final ConsultarRelatorioUseCase consultarRelatorio;
    private final ObjectMapper mapper;

    /**
     * @param consultarRelatorio caso de uso que consolida o período
     * @param mapper             conversor JSON
     */
    @Inject
    public ConsultarRelatorioHandler(ConsultarRelatorioUseCase consultarRelatorio, ObjectMapper mapper) {
        this.consultarRelatorio = consultarRelatorio;
        this.mapper = mapper;
    }

    /**
     * Sem parâmetros: últimos 7 dias, incluindo hoje. Com {@code inicio} e {@code fim}: o período
     * pedido (até {@value PeriodoRelatorio#MAX_DIAS} dias).
     */
    @Override
    public void handleRequest(InputStream entrada, OutputStream saida, Context contexto) throws IOException {
        try {
            // Sem query string, o API Gateway manda "queryStringParameters": null.
            JsonNode parametros = mapper.readTree(entrada).path("queryStringParameters");
            String inicio = textoOuNulo(parametros.get("inicio"));
            String fim = textoOuNulo(parametros.get("fim"));

            RelatorioSemanal relatorio;
            if (inicio == null && fim == null) {
                relatorio = consultarRelatorio.ultimosSeteDias();
            } else if (inicio == null || fim == null) {
                RespostaApiGateway.erro(mapper, saida, 400,
                        "Informe 'inicio' e 'fim' juntos (formato aaaa-mm-dd) ou nenhum dos dois");
                return;
            } else {
                relatorio = consultarRelatorio.executar(
                        new PeriodoRelatorio(LocalDate.parse(inicio), LocalDate.parse(fim)));
            }

            LOG.info("Relatório consultado: " + relatorio.periodo().inicio() + " a " + relatorio.periodo().fim()
                    + ", " + relatorio.total() + " avaliações");
            RespostaApiGateway.escrever(mapper, saida, 200, paraJson(relatorio));

        } catch (DateTimeParseException e) {
            RespostaApiGateway.erro(mapper, saida, 400, "As datas devem estar no formato aaaa-mm-dd");
        } catch (PeriodoInvalidoException e) {
            RespostaApiGateway.erro(mapper, saida, 400, e.getMessage());
        } catch (Exception e) {
            LOG.error("Erro inesperado ao consultar relatório", e);
            RespostaApiGateway.erro(mapper, saida, 500, "Erro interno ao gerar o relatório");
        }
    }

    private ObjectNode paraJson(RelatorioSemanal r) {
        ObjectNode corpo = mapper.createObjectNode();
        corpo.put("inicio", r.periodo().inicio().toString());
        corpo.put("fim", r.periodo().fim().toString());
        corpo.put("total", r.total());
        corpo.put("media", arredondar(r.media()));

        ObjectNode porUrgencia = corpo.putObject("porUrgencia");
        r.porUrgencia().forEach((urgencia, quantidade) -> porUrgencia.put(urgencia.name(), quantidade));

        ArrayNode porDia = corpo.putArray("porDia");
        Map<LocalDate, Double> mediaPorDia = r.mediaPorDia();
        r.porDia().forEach((dia, quantidade) -> {
            ObjectNode item = porDia.addObject();
            item.put("dia", dia.toString());
            item.put("quantidade", quantidade);
            Double media = mediaPorDia.get(dia);
            if (media == null) {
                item.putNull("media");
            } else {
                item.put("media", arredondar(media));
            }
        });
        return corpo;
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    private static String textoOuNulo(JsonNode campo) {
        return (campo != null && campo.isTextual() && !campo.asText().isBlank()) ? campo.asText().trim() : null;
    }
}
