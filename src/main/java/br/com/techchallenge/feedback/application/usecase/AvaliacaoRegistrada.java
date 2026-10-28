package br.com.techchallenge.feedback.application.usecase;

import java.time.Instant;

import br.com.techchallenge.feedback.domain.Urgencia;

/**
 * Dados de SAÍDA do caso de uso "registrar avaliação" (sem a descrição, por privacidade).
 *
 * @param id        identificador gerado
 * @param nota      nota gravada
 * @param urgencia  urgência calculada a partir da nota
 * @param dataEnvio instante do envio (UTC, precisão de segundos)
 */
public record AvaliacaoRegistrada(String id, int nota, Urgencia urgencia, Instant dataEnvio) {
}
