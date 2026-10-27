package br.com.techchallenge.feedback.application.port;

/** Mensagem a ser enviada aos administradores (assunto + texto). */
public record Notificacao(String assunto, String mensagem) {

    public Notificacao {
        if (assunto == null || assunto.isBlank()) {
            throw new IllegalArgumentException("assunto é obrigatório");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("mensagem é obrigatória");
        }
    }
}
