package br.com.techchallenge.feedback.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Intervalo de dias (inclusive) coberto por um relatório.
 *
 * @param inicio primeiro dia do período (inclusive)
 * @param fim    último dia do período (inclusive)
 */
public record PeriodoRelatorio(LocalDate inicio, LocalDate fim) {

    /** Quantidade de dias de um relatório semanal. */
    public static final int DIAS_DA_SEMANA = 7;

    /**
     * Maior período aceito, em dias. Cada dia é uma consulta ao banco,
     * então o limite também limita o custo de um relatório sob demanda.
     */
    public static final int MAX_DIAS = 31;

    /**
     * @throws NullPointerException     se {@code inicio} ou {@code fim} forem nulos
     * @throws PeriodoInvalidoException se {@code inicio} for depois de {@code fim}
     *                                  ou o período passar de {@value #MAX_DIAS} dias
     */
    public PeriodoRelatorio {
        Objects.requireNonNull(inicio, "inicio é obrigatório");
        Objects.requireNonNull(fim, "fim é obrigatório");
        if (inicio.isAfter(fim)) {
            throw new PeriodoInvalidoException("A data de início não pode ser depois da data de fim");
        }
        if (ChronoUnit.DAYS.between(inicio, fim) + 1 > MAX_DIAS) {
            throw new PeriodoInvalidoException("O período deve ter no máximo " + MAX_DIAS + " dias");
        }
    }

    /**
     * Semana do relatório a partir de "hoje".
     * Execução agendada (segunda): os 7 dias anteriores (segunda a domingo).
     * Demonstração (incluirHoje = true): os 6 dias anteriores + hoje.
     *
     * @param hoje        data local da execução
     * @param incluirHoje se {@code true}, o período termina hoje; senão, ontem
     * @return período de {@value #DIAS_DA_SEMANA} dias
     */
    public static PeriodoRelatorio semana(LocalDate hoje, boolean incluirHoje) {
        LocalDate fim = incluirHoje ? hoje : hoje.minusDays(1);
        return new PeriodoRelatorio(fim.minusDays(DIAS_DA_SEMANA - 1), fim);
    }

    /** @return todos os dias do período, em ordem crescente */
    public List<LocalDate> dias() {
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
            dias.add(d);
        }
        return dias;
    }

    /**
     * @param dia data a verificar
     * @return {@code true} se a data está dentro do período (limites inclusive)
     */
    public boolean contem(LocalDate dia) {
        return !dia.isBefore(inicio) && !dia.isAfter(fim);
    }
}