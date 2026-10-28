package br.com.techchallenge.feedback.application.port;

/**
 * PORTA de saída: "avisar os administradores".
 * Não diz COMO (SNS, SES, Slack...). Quem implementa é a infraestrutura.
 */
public interface NotificacaoGateway {

    /**
     * Envia a mensagem aos administradores.
     *
     * @param notificacao assunto e texto já formatados
     */
    void enviar(Notificacao notificacao);
}