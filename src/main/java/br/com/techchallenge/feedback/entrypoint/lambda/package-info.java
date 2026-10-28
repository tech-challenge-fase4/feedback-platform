/**
 * Os handlers AWS Lambda. Cada um só traduz o evento recebido para um caso de uso e
 * devolve a resposta; nenhuma regra de negócio fica aqui.
 *
 * <p>O Quarkus escolhe o handler pelo nome do {@code @Named}, informado na variável
 * {@code QUARKUS_LAMBDA_HANDLER} de cada função.
 */
package br.com.techchallenge.feedback.entrypoint.lambda;
