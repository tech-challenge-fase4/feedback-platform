package br.com.techchallenge.feedback.infrastructure.dynamodb;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.domain.Urgencia;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * ÚNICO lugar que conhece o formato do item no DynamoDB (nomes dos campos, tipos S e N).
 * Usado pelo repositório e também pela Função 2 (que recebe o item pelo DynamoDB Streams).
 */
public final class AvaliacaoItemMapper {

    /** Nomes dos atributos do item na tabela (e no índice {@code indice-dia}). */
    public static final String ID = "id";
    public static final String DESCRICAO = "descricao";
    public static final String NOTA = "nota";
    public static final String URGENCIA = "urgencia";
    public static final String DATA_ENVIO = "dataEnvio";
    public static final String DIA = "dia";

    private AvaliacaoItemMapper() {
    }

    /**
     * @param a avaliação do domínio
     * @return item no formato do DynamoDB, pronto para o {@code PutItem}
     */
    public static Map<String, AttributeValue> paraItem(Avaliacao a) {
        return Map.of(
                ID, AttributeValue.fromS(a.id()),
                DESCRICAO, AttributeValue.fromS(a.descricao()),
                NOTA, AttributeValue.fromN(String.valueOf(a.nota())),
                URGENCIA, AttributeValue.fromS(a.urgencia().name()),
                DATA_ENVIO, AttributeValue.fromS(a.dataEnvio().toString()),
                DIA, AttributeValue.fromS(a.dia().toString()));
    }

    /**
     * Converte o item de volta para o domínio, que valida os dados novamente.
     *
     * @param item item lido da tabela ou recebido pelo DynamoDB Streams
     * @return a avaliação
     * @throws IllegalStateException se faltar algum atributo no item
     * @throws br.com.techchallenge.feedback.domain.AvaliacaoInvalidaException se os dados forem
     *         incoerentes (ex.: urgência que não corresponde à nota)
     */
    public static Avaliacao deItem(Map<String, AttributeValue> item) {
        return new Avaliacao(
                texto(item, ID),
                texto(item, DESCRICAO),
                Integer.parseInt(numero(item, NOTA)),
                Urgencia.valueOf(texto(item, URGENCIA)),
                Instant.parse(texto(item, DATA_ENVIO)),
                LocalDate.parse(texto(item, DIA)));
    }

    private static String texto(Map<String, AttributeValue> item, String campo) {
        AttributeValue valor = item.get(campo);
        if (valor == null || valor.s() == null) {
            throw new IllegalStateException("Campo de texto ausente no item do DynamoDB: " + campo);
        }
        return valor.s();
    }

    private static String numero(Map<String, AttributeValue> item, String campo) {
        AttributeValue valor = item.get(campo);
        if (valor == null || valor.n() == null) {
            throw new IllegalStateException("Campo numérico ausente no item do DynamoDB: " + campo);
        }
        return valor.n();
    }
}
