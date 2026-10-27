package br.com.techchallenge.feedback.application.usecase;

import java.time.Instant;

import br.com.techchallenge.feedback.domain.Urgencia;

/** Dados de SAÍDA do caso de uso "registrar avaliação" (sem a descrição, por privacidade). */
public record AvaliacaoRegistrada(String id, int nota, Urgencia urgencia, Instant dataEnvio) {
}
