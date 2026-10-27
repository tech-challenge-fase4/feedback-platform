package br.com.techchallenge.feedback.entrypoint.lambda.notificar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.techchallenge.feedback.application.usecase.NotificarAvaliacaoCriticaUseCase;
import br.com.techchallenge.feedback.domain.Avaliacao;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * FUNÇÃO 2 (feedback-notificar-urgencia) — ADAPTADOR DE ENTRADA do DynamoDB Streams.
 *
 * Só traduz: registro do stream → Avaliacao → caso de uso.
 * A decisão "é crítica? então avisa" é do caso de uso.
 */
@Named("notificarUrgencia")
public class NotificarUrgenciaHandler implements RequestStreamHandler {

    private static final Logger LOG = Logger.getLogger(NotificarUrgenciaHandler.class);

    private final NotificarAvaliacaoCriticaUseCase notificarAvaliacaoCritica;
    private final ObjectMapper mapper;

    @Inject
    public NotificarUrgenciaHandler(NotificarAvaliacaoCriticaUseCase notificarAvaliacaoCritica,
                                    ObjectMapper mapper) {
        this.notificarAvaliacaoCritica = notificarAvaliacaoCritica;
        this.mapper = mapper;
    }

    @Override
    public void handleRequest(InputStream entrada, OutputStream saida, Context contexto) throws IOException {
        JsonNode evento = mapper.readTree(entrada);

        for (JsonNode registro : evento.path("Records")) {
            // Só itens NOVOS interessam (o filtro do gatilho já faz isso; aqui é defesa extra).
            if (!"INSERT".equals(registro.path("eventName").asText())) {
                continue;
            }
            Avaliacao avaliacao = ImagemStreamDynamoDb.paraAvaliacao(registro.path("dynamodb").path("NewImage"));

            boolean enviado = notificarAvaliacaoCritica.executar(avaliacao);

            LOG.info((enviado ? "Alerta de urgência enviado" : "Avaliação não crítica, sem alerta")
                    + " para avaliação id=" + avaliacao.id());
        }
    }
}
