package br.com.techchallenge.feedback.application.port;

/** PORTA de saída: gera identificadores únicos (nos testes, usamos um gerador fixo). */
@FunctionalInterface
public interface GeradorDeId {

    /** @return um identificador que ainda não foi usado */
    String novoId();
}
