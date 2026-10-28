package br.com.techchallenge.feedback.application.port;

/**
 * Mensagem a ser enviada aos administradores (assunto + texto).
 *
 * @param assunto  assunto do e-mail (pode ter acentos; cada canal adapta se precisar)
 * @param mensagem corpo do e-mail, em texto simples
 */
public record Notificacao(String assunto, String mensagem) {

    /** @throws IllegalArgumentException se o assunto ou a mensagem estiverem vazios */
    public Notificacao {
        if (assunto == null || assunto.isBlank()) {
            throw new IllegalArgumentException("assunto é obrigatório");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("mensagem é obrigatória");
        }
    }
}
