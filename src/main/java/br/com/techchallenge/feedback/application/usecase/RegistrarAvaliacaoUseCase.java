package br.com.techchallenge.feedback.application.usecase;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Objects;

import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.application.port.GeradorDeId;
import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.domain.AvaliacaoInvalidaException;


/**
 * CASO DE USO da Função 1: registrar uma avaliação recebida.
 *
 * Depende apenas de interfaces (portas) e de tipos do Java. Não sabe que existe
 * HTTP, API Gateway, Lambda nem DynamoDB.
 */
public class RegistrarAvaliacaoUseCase {

    private final AvaliacaoRepository repositorio;
    private final GeradorDeId geradorDeId;
    private final Clock relogio;
    private final ZoneId zona;

    public RegistrarAvaliacaoUseCase(AvaliacaoRepository repositorio, GeradorDeId geradorDeId,
                                     Clock relogio, ZoneId zona) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.geradorDeId = Objects.requireNonNull(geradorDeId);
        this.relogio = Objects.requireNonNull(relogio);
        this.zona = Objects.requireNonNull(zona);
    }

    /** @throws AvaliacaoInvalidaException se os dados não respeitarem as regras */
    public AvaliacaoRegistrada executar(RegistrarAvaliacaoCommand comando) {
        if (comando.nota() == null) {
            throw new AvaliacaoInvalidaException(
                    "O campo 'nota' é obrigatório e deve ser um número inteiro de 0 a 10");
        }
        Avaliacao avaliacao = Avaliacao.nova(geradorDeId.novoId(), comando.descricao(),
                comando.nota(), relogio.instant(), zona);

        repositorio.salvar(avaliacao);

        return new AvaliacaoRegistrada(avaliacao.id(), avaliacao.nota(),
                avaliacao.urgencia(), avaliacao.dataEnvio());
    }
}
