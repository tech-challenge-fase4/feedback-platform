package br.com.techchallenge.feedback.infrastructure.config;

import java.time.Clock;
import java.time.ZoneId;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.techchallenge.feedback.application.formatacao.MensagemUrgenciaFormatter;
import br.com.techchallenge.feedback.application.formatacao.RelatorioFormatter;
import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.application.port.GeradorDeId;
import br.com.techchallenge.feedback.application.port.NotificacaoGateway;
import br.com.techchallenge.feedback.application.usecase.GerarRelatorioSemanalUseCase;
import br.com.techchallenge.feedback.application.usecase.NotificarAvaliacaoCriticaUseCase;
import br.com.techchallenge.feedback.application.usecase.RegistrarAvaliacaoUseCase;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * "MONTAGEM" da aplicação (composition root).
 *
 * Os casos de uso são classes Java puras, sem anotações do Quarkus. É aqui que o Quarkus
 * aprende a criá-los, entregando as implementações concretas (DynamoDB, SNS, UUID) para as
 * interfaces que eles pedem. É a Inversão de Dependência na prática.
 */
@Singleton
public class ConfiguracaoCasosDeUso {

    @Produces
    @Singleton
    Clock relogio() {
        return Clock.systemUTC();
    }

    @Produces
    @Singleton
    ZoneId zona(@ConfigProperty(name = "app.zona") String zona) {
        return ZoneId.of(zona);
    }

    @Produces
    @Singleton
    RegistrarAvaliacaoUseCase registrarAvaliacao(AvaliacaoRepository repositorio, GeradorDeId geradorDeId,
                                                 Clock relogio, ZoneId zona) {
        return new RegistrarAvaliacaoUseCase(repositorio, geradorDeId, relogio, zona);
    }

    @Produces
    @Singleton
    NotificarAvaliacaoCriticaUseCase notificarAvaliacaoCritica(NotificacaoGateway notificacao, ZoneId zona) {
        return new NotificarAvaliacaoCriticaUseCase(notificacao, new MensagemUrgenciaFormatter(zona));
    }

    @Produces
    @Singleton
    GerarRelatorioSemanalUseCase gerarRelatorioSemanal(AvaliacaoRepository repositorio,
                                                       NotificacaoGateway notificacao,
                                                       Clock relogio, ZoneId zona) {
        return new GerarRelatorioSemanalUseCase(repositorio, notificacao,
                new RelatorioFormatter(zona), relogio, zona);
    }
}
