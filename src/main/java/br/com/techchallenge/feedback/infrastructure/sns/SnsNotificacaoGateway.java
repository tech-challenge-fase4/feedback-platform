package br.com.techchallenge.feedback.infrastructure.sns;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.techchallenge.feedback.application.port.Notificacao;
import br.com.techchallenge.feedback.application.port.NotificacaoGateway;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/** ADAPTADOR: implementa a porta NotificacaoGateway publicando no tópico SNS (e-mail). */
@Singleton
public class SnsNotificacaoGateway implements NotificacaoGateway {

    private final SnsClient sns;
    private final String topicoArn;

    @Inject
    public SnsNotificacaoGateway(SnsClient sns, @ConfigProperty(name = "app.topico-arn") String topicoArn) {
        this.sns = sns;
        this.topicoArn = topicoArn;
    }

    @Override
    public void enviar(Notificacao notificacao) {
        sns.publish(PublishRequest.builder()
                .topicArn(topicoArn)
                .subject(AssuntoSns.normalizar(notificacao.assunto()))
                .message(notificacao.mensagem())
                .build());
    }
}
