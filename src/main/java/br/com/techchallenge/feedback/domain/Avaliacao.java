package br.com.techchallenge.feedback.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidade central: uma avaliação de aula.
 *
 * O construtor valida tudo, então NÃO EXISTE Avaliacao inválida em memória
 * (nota fora da faixa, descrição vazia ou urgência que não bate com a nota).
 *
 * @param dataEnvio instante do envio (UTC)
 * @param dia       data local do envio (ex.: no fuso de São Paulo); usada para agrupar por dia
 */
public record Avaliacao(String id, String descricao, int nota, Urgencia urgencia,
                        Instant dataEnvio, LocalDate dia) {

    public static final int MAX_DESCRICAO = 1000;

    public Avaliacao {
        if (id == null || id.isBlank()) {
            throw new AvaliacaoInvalidaException("A avaliação precisa de um id");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new AvaliacaoInvalidaException("O campo 'descricao' é obrigatório e deve ser um texto");
        }
        if (descricao.length() > MAX_DESCRICAO) {
            throw new AvaliacaoInvalidaException(
                    "O campo 'descricao' deve ter no máximo " + MAX_DESCRICAO + " caracteres");
        }
        Urgencia esperada = Urgencia.deNota(nota); // também valida a faixa 0 a 10
        if (urgencia != esperada) {
            throw new AvaliacaoInvalidaException(
                    "Urgência " + urgencia + " não corresponde à nota " + nota + " (esperado " + esperada + ")");
        }
        Objects.requireNonNull(dataEnvio, "dataEnvio é obrigatória");
        Objects.requireNonNull(dia, "dia é obrigatório");
    }

    /**
     * Cria uma avaliação nova: remove espaços da descrição, calcula a urgência
     * e registra data de envio (precisão de segundos) e dia local.
     */
    public static Avaliacao nova(String id, String descricao, int nota, Instant agora, ZoneId zona) {
        if (descricao == null) {
            throw new AvaliacaoInvalidaException("O campo 'descricao' é obrigatório e deve ser um texto");
        }
        Instant envio = agora.truncatedTo(ChronoUnit.SECONDS);
        return new Avaliacao(id, descricao.trim(), nota, Urgencia.deNota(nota),
                envio, LocalDate.ofInstant(envio, zona));
    }
}