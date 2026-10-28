package br.com.techchallenge.feedback.application.formatacao;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import br.com.techchallenge.feedback.application.port.Notificacao;
import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.domain.RelatorioSemanal;

/** Transforma o RelatorioSemanal (cálculo) no texto do e-mail (apresentação). */
public class RelatorioFormatter {

    /** Máximo de avaliações listadas no e-mail (o e-mail do SNS tem limite de 256 KB). */
    public static final int LIMITE_LISTA = 100;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ZoneId zona;

    /** @param zona fuso em que as datas de envio são mostradas no e-mail */
    public RelatorioFormatter(ZoneId zona) {
        this.zona = Objects.requireNonNull(zona);
    }

    /**
     * Monta o e-mail com período, total, média, quantidade por dia, quantidade por urgência
     * e a lista das avaliações (descrição, urgência e data de envio).
     *
     * @param r relatório já calculado
     * @return assunto e texto do e-mail do relatório
     */
    public Notificacao formatar(RelatorioSemanal r) {
        String inicio = DATA.format(r.periodo().inicio());
        String fim = DATA.format(r.periodo().fim());
        String assunto = "Relatório semanal de feedbacks - " + inicio + " a " + fim;

        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO SEMANAL DE FEEDBACKS\n");
        sb.append("Período: ").append(inicio).append(" a ").append(fim).append("\n\n");
        sb.append("Total de avaliações: ").append(r.total()).append("\n");
        sb.append("Média das notas: ").append(String.format(PT_BR, "%.2f", r.media())).append("\n\n");

        sb.append("QUANTIDADE DE AVALIAÇÕES POR DIA\n");
        r.porDia().forEach((dia, qtd) -> sb.append("- ").append(DATA.format(dia)).append(": ").append(qtd).append("\n"));

        sb.append("\nQUANTIDADE DE AVALIAÇÕES POR URGÊNCIA\n");
        r.porUrgencia().forEach((urg, qtd) -> sb.append("- ").append(urg.name()).append(": ").append(qtd).append("\n"));

        sb.append("\nAVALIAÇÕES DO PERÍODO\n");
        if (r.avaliacoes().isEmpty()) {
            sb.append("Nenhuma avaliação recebida no período.\n");
        }
        int mostradas = 0;
        for (Avaliacao a : r.avaliacoes()) {
            if (mostradas == LIMITE_LISTA) {
                sb.append("\n... e mais ").append(r.avaliacoes().size() - LIMITE_LISTA)
                        .append(" avaliações (lista limitada a ").append(LIMITE_LISTA).append(").\n");
                break;
            }
            sb.append("\nDescrição: ").append(a.descricao()).append("\n")
                    .append("Urgência: ").append(a.urgencia().name()).append("\n")
                    .append("Data de envio: ").append(DATA_HORA.format(a.dataEnvio().atZone(zona))).append("\n");
            mostradas++;
        }
        return new Notificacao(assunto, sb.toString());
    }
}
