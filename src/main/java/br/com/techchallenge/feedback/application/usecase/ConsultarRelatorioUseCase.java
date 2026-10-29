package br.com.techchallenge.feedback.application.usecase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.domain.PeriodoRelatorio;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;

/**
 * CASO DE USO da Função 4: consolidar as avaliações de um período, SEM enviar nada.
 * Também é reutilizado pela Função 3, que consolida a semana e depois envia o e-mail.
 */
public class ConsultarRelatorioUseCase {

    private final AvaliacaoRepository repositorio;
    private final Clock relogio;
    private final ZoneId zona;

    /**
     * @param repositorio de onde as avaliações são lidas, dia a dia
     * @param relogio     fonte da data/hora atual (fixa nos testes)
     * @param zona        fuso usado para saber qual é o dia de "hoje"
     */
    public ConsultarRelatorioUseCase(AvaliacaoRepository repositorio, Clock relogio, ZoneId zona) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.relogio = Objects.requireNonNull(relogio);
        this.zona = Objects.requireNonNull(zona);
    }

    /**
     * Lê as avaliações de cada dia do período e consolida.
     *
     * @param periodo dias que o relatório cobre (já validado pelo domínio)
     * @return o relatório consolidado
     */
    public RelatorioSemanal executar(PeriodoRelatorio periodo) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (LocalDate dia : periodo.dias()) {
            avaliacoes.addAll(repositorio.buscarPorDia(dia));
        }
        return RelatorioSemanal.consolidar(avaliacoes, periodo);
    }

    /**
     * Período padrão da consulta sob demanda: os 6 dias anteriores + hoje,
     * para que uma avaliação recém-enviada já apareça no relatório.
     *
     * @return o relatório dos últimos 7 dias, incluindo hoje
     */
    public RelatorioSemanal ultimosSeteDias() {
        LocalDate hoje = LocalDate.ofInstant(relogio.instant(), zona);
        return executar(PeriodoRelatorio.semana(hoje, true));
    }
}