package br.com.techchallenge.feedback.domain;

/**
 * Erro de regra de negócio: os dados da avaliação não respeitam as regras do domínio.
 * A mensagem é escrita para ser mostrada ao usuário da API.
 */
public class AvaliacaoInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** @param mensagem motivo do erro, em português, pronto para ir na resposta HTTP 400 */
    public AvaliacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
