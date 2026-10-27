package br.com.techchallenge.feedback.infrastructure.sns;

import java.text.Normalizer;

/**
 * Regra ESPECÍFICA do SNS: o assunto do e-mail só aceita ASCII, sem quebra de linha,
 * com até 100 caracteres. Por isso ela fica na infraestrutura, e não no domínio:
 * o resto do sistema pode escrever "Relatório" com acento normalmente.
 */
public final class AssuntoSns {

    public static final int MAX = 100;

    private AssuntoSns() {
    }

    public static String normalizar(String assunto) {
        String semAcento = Normalizer.normalize(assunto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String ascii = semAcento.replaceAll("[\\r\\n\\t]+", " ").replaceAll("[^\\x20-\\x7E]", "").trim();
        if (ascii.isEmpty()) {
            return "Notificacao";
        }
        return ascii.length() > MAX ? ascii.substring(0, MAX).trim() : ascii;
    }
}
