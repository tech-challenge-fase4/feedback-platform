package br.com.techchallenge.feedback.application.usecase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.techchallenge.feedback.application.formatacao.RelatorioFormatter;
import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.application.port.NotificacaoGateway;
import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.domain.PeriodoRelatorio;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;

/**
 * CASO DE USO da Função 3: consolidar a semana e enviar o relatório aos administradores.
 */
public class GerarRelatorioSemanalUseCase {

    private final AvaliacaoRepository repositorio;
    private final NotificacaoGateway notificacao;
    private final RelatorioFormatter formatador;
    private final Clock relogio;
    private final ZoneId zona;

    public GerarRelatorioSemanalUseCase(AvaliacaoRepository repositorio, NotificacaoGateway notificacao,
                                        RelatorioFormatter formatador, Clock relogio, ZoneId zona) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.notificacao = Objects.requireNonNull(notificacao);
        this.formatador = Objects.requireNonNull(formatador);
        this.relogio = Objects.requireNonNull(relogio);
        this.zona = Objects.requireNonNull(zona);
    }

    /**
     * @param incluirHoje false = 7 dias anteriores (execução agendada);
     *                    true = 6 dias anteriores + hoje (demonstração)
     */
    public RelatorioSemanal executar(boolean incluirHoje) {
        LocalDate hoje = LocalDate.ofInstant(relogio.instant(), zona);
        PeriodoRelatorio periodo = PeriodoRelatorio.semana(hoje, incluirHoje);

        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (LocalDate dia : periodo.dias()) {
            avaliacoes.addAll(repositorio.buscarPorDia(dia));
        }

        RelatorioSemanal relatorio = RelatorioSemanal.consolidar(avaliacoes, periodo);
        notificacao.enviar(formatador.formatar(relatorio));
        return relatorio;
    }
}
