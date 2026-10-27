package br.com.techchallenge.feedback.application.port;

import java.time.LocalDate;
import java.util.List;

import br.com.techchallenge.feedback.domain.Avaliacao;

/**
 * PORTA de saída: o que os casos de uso precisam de um "lugar para guardar avaliações".
 * Não diz COMO (DynamoDB, banco relacional, memória...). Quem implementa é a infraestrutura.
 */
public interface AvaliacaoRepository {

    void salvar(Avaliacao avaliacao);

    /** Todas as avaliações cujo dia local é {@code dia}. */
    List<Avaliacao> buscarPorDia(LocalDate dia);
}
