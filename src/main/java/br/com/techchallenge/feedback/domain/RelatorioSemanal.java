package br.com.techchallenge.feedback.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado consolidado da semana: total, média, quantidade por dia e por urgência.
 * Só faz CÁLCULO; a formatação do e-mail fica em outra classe (application/formatacao).
 *
 * @param periodo     dias cobertos pelo relatório
 * @param total       quantidade de avaliações do período
 * @param media       média das notas (0 quando não há avaliações)
 * @param porDia      quantidade por dia, em ordem, incluindo dias com 0
 * @param porUrgencia quantidade por urgência, incluindo urgências com 0
 * @param avaliacoes  avaliações do período, ordenadas pela data de envio
 */
public record RelatorioSemanal(PeriodoRelatorio periodo, int total, double media,
                               Map<LocalDate, Integer> porDia, Map<Urgencia, Integer> porUrgencia,
                               List<Avaliacao> avaliacoes) {

    /** Guarda cópias imutáveis das coleções, para o relatório não mudar depois de criado. */
    public RelatorioSemanal {
        porDia = Collections.unmodifiableMap(new LinkedHashMap<>(porDia));
        porUrgencia = Collections.unmodifiableMap(new EnumMap<>(porUrgencia));
        avaliacoes = List.copyOf(avaliacoes);
    }

    /**
     * Consolida as avaliações do período. Avaliações fora do período são ignoradas.
     *
     * @param avaliacoes avaliações lidas do repositório
     * @param periodo    dias que o relatório cobre
     * @return o relatório com total, média e contagens
     */
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

        /**
     * Média das notas de cada dia do período, na mesma ordem de {@link #porDia()}.
     * Dias sem avaliação ficam com {@code null}, para não confundir com média 0
     * (nota 0 é uma nota válida).
     *
     * @return média por dia; {@code null} nos dias sem avaliação
     */
    public Map<LocalDate, Double> mediaPorDia() {
        Map<LocalDate, Integer> somaPorDia = new HashMap<>();
        for (Avaliacao a : avaliacoes) {
            somaPorDia.merge(a.dia(), a.nota(), Integer::sum);
        }
        Map<LocalDate, Double> medias = new LinkedHashMap<>();
        porDia.forEach((dia, quantidade) ->
                medias.put(dia, quantidade == 0 ? null : (double) somaPorDia.get(dia) / quantidade));
        return Collections.unmodifiableMap(medias);
    }

}
