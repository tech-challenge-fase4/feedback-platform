package br.com.techchallenge.feedback.application.usecase;

import java.util.Objects;

import br.com.techchallenge.feedback.application.formatacao.MensagemUrgenciaFormatter;
import br.com.techchallenge.feedback.application.port.NotificacaoGateway;
import br.com.techchallenge.feedback.domain.Avaliacao;

/**
 * CASO DE USO da Função 2: avisar os administradores sobre uma avaliação crítica.
 *
 * A regra "só avisa se a urgência for ALTA" está AQUI, no código, e não só
 * no filtro do gatilho da AWS. Assim ela é testável e não depende da configuração.
 */
public class NotificarAvaliacaoCriticaUseCase {

    private final NotificacaoGateway notificacao;
    private final MensagemUrgenciaFormatter formatador;

    public NotificarAvaliacaoCriticaUseCase(NotificacaoGateway notificacao,
                                            MensagemUrgenciaFormatter formatador) {
        this.notificacao = Objects.requireNonNull(notificacao);
        this.formatador = Objects.requireNonNull(formatador);
    }

    /** @return true se o aviso foi enviado; false se a avaliação não era crítica */
    public boolean executar(Avaliacao avaliacao) {
        if (!avaliacao.urgencia().ehCritica()) {
            return false;
        }
        notificacao.enviar(formatador.formatar(avaliacao));
        return true;
    }
}
