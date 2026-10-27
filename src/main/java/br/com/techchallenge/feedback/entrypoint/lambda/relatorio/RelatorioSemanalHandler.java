package br.com.techchallenge.feedback.entrypoint.lambda.relatorio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.techchallenge.feedback.application.usecase.GerarRelatorioSemanalUseCase;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * FUNÇÃO 3 (feedback-relatorio-semanal) — ADAPTADOR DE ENTRADA do EventBridge Scheduler.
 *
 * Só traduz: evento ({} ou {"incluirHoje": true}) → caso de uso → resumo de saída.
 */
@Named("relatorioSemanal")
public class RelatorioSemanalHandler implements RequestStreamHandler {

    private static final Logger LOG = Logger.getLogger(RelatorioSemanalHandler.class);

    private final GerarRelatorioSemanalUseCase gerarRelatorioSemanal;
    private final ObjectMapper mapper;

    @Inject
    public RelatorioSemanalHandler(GerarRelatorioSemanalUseCase gerarRelatorioSemanal, ObjectMapper mapper) {
        this.gerarRelatorioSemanal = gerarRelatorioSemanal;
        this.mapper = mapper;
    }

    @Override
    public void handleRequest(InputStream entrada, OutputStream saida, Context contexto) throws IOException {
        JsonNode evento = mapper.readTree(entrada);
        boolean incluirHoje = evento.path("incluirHoje").asBoolean(false);

        RelatorioSemanal relatorio = gerarRelatorioSemanal.executar(incluirHoje);

        LOG.info("Relatório enviado: " + relatorio.total() + " avaliações, média " + relatorio.media());
        mapper.writeValue(saida, Map.of("total", relatorio.total(), "media", relatorio.media()));
    }
}
