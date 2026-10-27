package br.com.techchallenge.feedback.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado consolidado da semana: total, média, quantidade por dia e por urgência.
 * Só faz CÁLCULO; a formatação do e-mail fica em outra classe (application/formatacao).
 */
public record RelatorioSemanal(PeriodoRelatorio periodo, int total, double media,
                               Map<LocalDate, Integer> porDia, Map<Urgencia, Integer> porUrgencia,
                               List<Avaliacao> avaliacoes) {

    public RelatorioSemanal {
        porDia = Collections.unmodifiableMap(new LinkedHashMap<>(porDia));
        porUrgencia = Collections.unmodifiableMap(new EnumMap<>(porUrgencia));
        avaliacoes = List.copyOf(avaliacoes);
    }

    /** Consolida as avaliações do período. Avaliações fora do período são ignoradas. */
    public static RelatorioSemanal consolidar(List<Avaliacao> avaliacoes, PeriodoRelatorio periodo) {
        Map<LocalDate, Integer> porDia = new LinkedHashMap<>();
        for (LocalDate dia : periodo.dias()) {
            porDia.put(dia, 0); // dias sem avaliação aparecem com 0
        }
        Map<Urgencia, Integer> porUrgencia = new EnumMap<>(Urgencia.class);
        for (Urgencia u : Urgencia.values()) {
            porUrgencia.put(u, 0);
        }

        List<Avaliacao> doPeriodo = new ArrayList<>();
        for (Avaliacao a : avaliacoes) {
            if (periodo.contem(a.dia())) {
                doPeriodo.add(a);
            }
        }
        doPeriodo.sort(Comparator.comparing(Avaliacao::dataEnvio));

        double soma = 0;
        for (Avaliacao a : doPeriodo) {
            porDia.merge(a.dia(), 1, Integer::sum);
            porUrgencia.merge(a.urgencia(), 1, Integer::sum);
            soma += a.nota();
        }
        double media = doPeriodo.isEmpty() ? 0 : soma / doPeriodo.size();

        return new RelatorioSemanal(periodo, doPeriodo.size(), media, porDia, porUrgencia, doPeriodo);
    }
}
