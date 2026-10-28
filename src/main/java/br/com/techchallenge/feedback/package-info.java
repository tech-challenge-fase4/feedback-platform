/**
 * Plataforma de feedback de aulas: estudantes enviam avaliações, administradores recebem
 * alertas de avaliações críticas e um relatório semanal.
 *
 * <p>O código segue a <b>Clean Architecture</b>. As dependências apontam sempre para dentro:
 * <pre>
 *   entrypoint, infrastructure  →  application  →  domain
 * </pre>
 * <ul>
 *   <li>{@code domain}: regras de negócio puras, sem nenhuma biblioteca externa;</li>
 *   <li>{@code application}: casos de uso e as interfaces (portas) de que eles precisam;</li>
 *   <li>{@code infrastructure}: implementações das portas com AWS (DynamoDB, SNS) e a montagem;</li>
 *   <li>{@code entrypoint}: os 3 handlers AWS Lambda, que só traduzem eventos para casos de uso.</li>
 * </ul>
 *
 * <p>As 3 funções Lambda usam o mesmo pacote ({@code function.zip}); a variável de ambiente
 * {@code QUARKUS_LAMBDA_HANDLER} escolhe qual handler cada uma executa.
 */
package br.com.techchallenge.feedback;
