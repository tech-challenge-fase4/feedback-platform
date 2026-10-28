package br.com.techchallenge.feedback.entrypoint.lambda.notificar;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.techchallenge.feedback.domain.Avaliacao;
import br.com.techchallenge.feedback.infrastructure.dynamodb.AvaliacaoItemMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Converte a "NewImage" que chega pelo DynamoDB Streams (JSON no formato
 * {"campo": {"S": "texto"}} / {"campo": {"N": "1"}}) em uma Avaliacao,
 * reaproveitando o MESMO mapeamento usado pelo repositório.
 */
public final class ImagemStreamDynamoDb {

    private ImagemStreamDynamoDb() {
    }

    /**
     * @param novaImagem campo {@code dynamodb.NewImage} de um registro do stream
     * @return a avaliação gravada
     * @throws IllegalStateException se faltar algum atributo no item
     */
    public static Avaliacao paraAvaliacao(JsonNode novaImagem) {
        Map<String, AttributeValue> item = new HashMap<>();
        for (Map.Entry<String, JsonNode> campo : novaImagem.properties()) {
            JsonNode valor = campo.getValue();
            if (valor.has("S")) {
                item.put(campo.getKey(), AttributeValue.fromS(valor.get("S").asText()));
            } else if (valor.has("N")) {
                item.put(campo.getKey(), AttributeValue.fromN(valor.get("N").asText()));
            }
        }
        return AvaliacaoItemMapper.deItem(item);
    }
}
