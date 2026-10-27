package br.com.techchallenge.feedback.application.usecase;

/**
 * Dados de ENTRADA do caso de uso "registrar avaliação".
 * Os campos podem vir nulos (ausentes ou com tipo errado na requisição);
 * quem decide se isso é válido é o caso de uso/domínio, não o handler.
 */
public record RegistrarAvaliacaoCommand(String descricao, Integer nota) {
}
