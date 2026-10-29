package br.com.techchallenge.feedback.application.usecase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import br.com.techchallenge.feedback.application.formatacao.RelatorioFormatter;
import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.application.port.NotificacaoGateway;
import br.com.techchallenge.feedback.domain.PeriodoRelatorio;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;

/**
 * CASO DE USO da Função 3: consolidar a semana e enviar o relatório aos administradores.
 * A consolidação é a mesma da consulta sob demanda ({@link ConsultarRelatorioUseCase});
 * este caso de uso só acrescenta o envio do e-mail.
 */
public class GerarRelatorioSemanalUseCase {

    private final ConsultarRelatorioUseCase consulta;
    private final NotificacaoGateway notificacao;
    private final RelatorioFormatter formatador;
    private final Clock relogio;
    private final ZoneId zona;

    /**
     * @param repositorio de onde as avaliações são lidas, dia a dia
     * @param notificacao canal de envio do relatório
     * @param formatador  monta o texto do e-mail do relatório
     * @param relogio     fonte da data/hora atual (fixa nos testes)
     * @param zona        fuso usado para saber qual é o dia de "hoje"
     */
    public GerarRelatorioSemanalUseCase(AvaliacaoRepository repositorio, NotificacaoGateway notificacao,
                                        RelatorioFormatter formatador, Clock relogio, ZoneId zona) {
        this.consulta = new ConsultarRelatorioUseCase(repositorio, relogio, zona);
        this.notificacao = Objects.requireNonNull(notificacao);
        this.formatador = Objects.requireNonNull(formatador);
        this.relogio = Objects.requireNonNull(relogio);
        this.zona = Objects.requireNonNull(zona);
    }

    /**
     * Lê as avaliações do período, consolida e envia o relatório aos administradores.
     *
     * @param incluirHoje false = 7 dias anteriores (execução agendada);
     *                    true = 6 dias anteriores + hoje (demonstração)
     * @return o relatório enviado
     */
    public RelatorioSemanal executar(boolean incluirHoje) {
        LocalDate hoje = LocalDate.ofInstant(relogio.instant(), zona);
        PeriodoRelatorio periodo = PeriodoRelatorio.semana(hoje, incluirHoje);

        RelatorioSemanal relatorio = consulta.executar(periodo);
        notificacao.enviar(formatador.formatar(relatorio));
        return relatorio;
    }
}
