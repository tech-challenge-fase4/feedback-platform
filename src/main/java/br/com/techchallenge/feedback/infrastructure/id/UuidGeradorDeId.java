package br.com.techchallenge.feedback.infrastructure.id;

import java.util.UUID;

import br.com.techchallenge.feedback.application.port.GeradorDeId;
import jakarta.inject.Singleton;

/** ADAPTADOR: gera ids aleatórios (UUID). */
@Singleton
public class UuidGeradorDeId implements GeradorDeId {

    /** @return um UUID versão 4 em texto */
    @Override
    public String novoId() {
        return UUID.randomUUID().toString();
    }
}