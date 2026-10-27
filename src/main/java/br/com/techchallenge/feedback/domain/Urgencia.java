package br.com.techchallenge.feedback.domain;

/**
 * Nível de urgência de uma avaliação, calculado a partir da nota.
 * É o ÚNICO lugar do sistema que conhece a faixa válida de notas (0 a 10).
 */
public enum Urgencia {
    ALTA, MEDIA, BAIXA;

    public static final int NOTA_MINIMA = 0;
    public static final int NOTA_MAXIMA = 10;

    /** 0 a 3 = ALTA, 4 a 6 = MEDIA, 7 a 10 = BAIXA. */
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

    public boolean ehCritica() {
        return this == ALTA;
    }
}
