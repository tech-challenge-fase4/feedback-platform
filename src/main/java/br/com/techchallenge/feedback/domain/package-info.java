/**
 * Camada de DOMÍNIO: as regras de negócio, em Java puro.
 *
 * <p>Aqui ficam a entidade {@link br.com.techchallenge.feedback.domain.Avaliacao}, a regra de
 * {@link br.com.techchallenge.feedback.domain.Urgencia} (0–3 ALTA, 4–6 MEDIA, 7–10 BAIXA) e o
 * cálculo do {@link br.com.techchallenge.feedback.domain.RelatorioSemanal}.
 *
 * <p>Não importa AWS SDK, Quarkus nem Jackson: as regras podem ser testadas sem nuvem e não
 * mudam se a infraestrutura mudar.
 */
package br.com.techchallenge.feedback.domain;
