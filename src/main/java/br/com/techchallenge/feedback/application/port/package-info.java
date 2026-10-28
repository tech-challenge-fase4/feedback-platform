/**
 * PORTAS de saída: interfaces que os casos de uso usam para falar com o mundo externo
 * (gravar avaliações, enviar notificações, gerar ids).
 *
 * <p>As implementações ficam em {@code infrastructure}. Trocar o DynamoDB ou o SNS por outra
 * tecnologia exige apenas uma nova implementação, sem mexer nos casos de uso
 * (Inversão de Dependência).
 */
package br.com.techchallenge.feedback.application.port;
