package br.com.techchallenge.feedback.domain;

/**
 * Erro de regra de negócio: o período pedido para um relatório não é válido
 * (início depois do fim ou mais dias do que o permitido).
 * A mensagem é escrita para ser mostrada ao usuário da API.
 *
 * Estende {@link IllegalArgumentException} para continuar compatível com quem
 * já tratava o erro "início depois do fim" por esse tipo.
 */
public class PeriodoInvalidoException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /** @param mensagem motivo do erro, em português, pronto para ir na resposta HTTP 400 */
    public PeriodoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
