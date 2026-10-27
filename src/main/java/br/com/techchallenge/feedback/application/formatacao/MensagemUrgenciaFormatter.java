package br.com.techchallenge.feedback.application.formatacao;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import br.com.techchallenge.feedback.application.port.Notificacao;
import br.com.techchallenge.feedback.domain.Avaliacao;

/** Monta o e-mail de aviso de urgência (Descrição, Urgência e Data de envio). */
public class MensagemUrgenciaFormatter {

    public static final String ASSUNTO = "[URGENTE] Nova avaliação crítica recebida";
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ZoneId zona;

    public MensagemUrgenciaFormatter(ZoneId zona) {
        this.zona = Objects.requireNonNull(zona);
    }

    public Notificacao formatar(Avaliacao a) {
        String texto = """
                ALERTA: AVALIAÇÃO CRÍTICA RECEBIDA

                Descrição: %s
                Urgência: %s
                Data de envio: %s

                Ação recomendada: entrar em contato com a turma ou o professor responsável.
                """.formatted(a.descricao(), a.urgencia().name(), DATA_HORA.format(a.dataEnvio().atZone(zona)));
        return new Notificacao(ASSUNTO, texto);
    }
}
