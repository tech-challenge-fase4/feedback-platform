package br.com.techchallenge.feedback.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Intervalo de dias (inclusive) coberto por um relatório. */
public record PeriodoRelatorio(LocalDate inicio, LocalDate fim) {

    public static final int DIAS_DA_SEMANA = 7;

    public PeriodoRelatorio {
        Objects.requireNonNull(inicio, "inicio é obrigatório");
        Objects.requireNonNull(fim, "fim é obrigatório");
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("inicio não pode ser depois de fim");
        }
    }

    /**
     * Semana do relatório a partir de "hoje".
     * Execução agendada (segunda): os 7 dias anteriores (segunda a domingo).
     * Demonstração (incluirHoje = true): os 6 dias anteriores + hoje.
     */
    public static PeriodoRelatorio semana(LocalDate hoje, boolean incluirHoje) {
        LocalDate fim = incluirHoje ? hoje : hoje.minusDays(1);
        return new PeriodoRelatorio(fim.minusDays(DIAS_DA_SEMANA - 1), fim);
    }

    /** Todos os dias do período, em ordem. */
    public List<LocalDate> dias() {
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
            dias.add(d);
        }
        return dias;
    }

    public boolean contem(LocalDate dia) {
        return !dia.isBefore(inicio) && !dia.isAfter(fim);
    }
}
