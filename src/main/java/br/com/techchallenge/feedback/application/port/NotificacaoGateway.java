package br.com.techchallenge.feedback.application.port;

/**
 * PORTA de saída: "avisar os administradores".
 * Não diz COMO (SNS, SES, Slack...). Quem implementa é a infraestrutura.
 */
public interface NotificacaoGateway {

    void enviar(Notificacao notificacao);
}