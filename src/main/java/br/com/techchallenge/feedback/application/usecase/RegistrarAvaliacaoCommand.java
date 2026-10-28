package br.com.techchallenge.feedback.application.usecase;

/**
 * Dados de ENTRADA do caso de uso "registrar avaliação".
 * Os campos podem vir nulos (ausentes ou com tipo errado na requisição);
 * quem decide se isso é válido é o caso de uso/domínio, não o handler.
 *
 * @param descricao texto enviado, ou {@code null} se ausente ou não for texto
 * @param nota      nota enviada, ou {@code null} se ausente ou não for inteiro
 */
public record RegistrarAvaliacaoCommand(String descricao, Integer nota) {
}
