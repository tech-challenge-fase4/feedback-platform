package br.com.techchallenge.feedback.application.port;

import java.time.LocalDate;
import java.util.List;

import br.com.techchallenge.feedback.domain.Avaliacao;

/**
 * PORTA de saída: o que os casos de uso precisam de um "lugar para guardar avaliações".
 * Não diz COMO (DynamoDB, banco relacional, memória...). Quem implementa é a infraestrutura.
 */
public interface AvaliacaoRepository {

    /**
     * Grava uma avaliação nova.
     *
     * @param avaliacao avaliação já validada pelo domínio
     */
    void salvar(Avaliacao avaliacao);

    /**
     * @param dia data local (ex.: no fuso de São Paulo)
     * @return todas as avaliações cujo dia local é {@code dia}; lista vazia se não houver
     */
    List<Avaliacao> buscarPorDia(LocalDate dia);
}
