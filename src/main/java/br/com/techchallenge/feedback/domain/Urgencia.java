package br.com.techchallenge.feedback.domain;

/**
 * Nível de urgência de uma avaliação, calculado a partir da nota.
 * É o ÚNICO lugar do sistema que conhece a faixa válida de notas (0 a 10).
 */
public enum Urgencia {
    /** Nota de 0 a 3: crítica, gera e-mail imediato aos administradores. */
    ALTA,
    /** Nota de 4 a 6: entra só no relatório semanal. */
    MEDIA,
    /** Nota de 7 a 10: entra só no relatório semanal. */
    BAIXA;

    /** Menor nota aceita. */
    public static final int NOTA_MINIMA = 0;
    /** Maior nota aceita. */
    public static final int NOTA_MAXIMA = 10;

    /**
     * Calcula a urgência: 0 a 3 = ALTA, 4 a 6 = MEDIA, 7 a 10 = BAIXA.
     *
     * @param nota nota da avaliação
     * @return a urgência correspondente
     * @throws AvaliacaoInvalidaException se a nota estiver fora da faixa 0 a 10
     */
    public static Urgencia deNota(int nota) {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new AvaliacaoInvalidaException(
                    "O campo 'nota' deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA);
        }
        if (nota <= 3) {
            return ALTA;
        }
        if (nota <= 6) {
            return MEDIA;
        }
        return BAIXA;
    }

    /** @return {@code true} se esta urgência exige aviso imediato aos administradores */
    public boolean ehCritica() {
        return this == ALTA;
    }
}
