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
 * @param id        identificador único (UUID gerado na criação)
 * @param descricao texto da avaliação, de 1 a {@value #MAX_DESCRICAO} caracteres
 * @param nota      nota de {@value Urgencia#NOTA_MINIMA} a {@value Urgencia#NOTA_MAXIMA}
 * @param urgencia  urgência, que precisa corresponder à nota (ver {@link Urgencia#deNota(int)})
 * @param dataEnvio instante do envio (UTC)
 * @param dia       data local do envio (ex.: no fuso de São Paulo); usada para agrupar por dia
 */
public record Avaliacao(String id, String descricao, int nota, Urgencia urgencia,
                        Instant dataEnvio, LocalDate dia) {

    /** Tamanho máximo da descrição, em caracteres. */
    public static final int MAX_DESCRICAO = 1000;

    /**
     * Valida todos os campos. Usado na criação ({@link #nova}) e ao ler um item já gravado.
     *
     * @throws AvaliacaoInvalidaException se id ou descrição estiverem vazios, a descrição passar
     *                                    do limite, a nota estiver fora da faixa ou a urgência não
     *                                    corresponder à nota
     * @throws NullPointerException       se {@code dataEnvio} ou {@code dia} forem nulos
     */
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
     *
     * @param id        identificador já gerado
     * @param descricao texto enviado pelo estudante (espaços nas pontas são removidos)
     * @param nota      nota enviada pelo estudante
     * @param agora     instante atual (vem de um {@link java.time.Clock}, para ser testável)
     * @param zona      fuso usado para calcular o dia local
     * @return a avaliação válida, pronta para ser gravada
     * @throws AvaliacaoInvalidaException se algum dado não respeitar as regras
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