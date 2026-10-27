package br.com.techchallenge.feedback.entrypoint.lambda.http;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Conhece o formato de resposta que o API Gateway (integração proxy) espera:
 * {"statusCode": ..., "headers": {...}, "body": "<json em texto>"}.
 */
public final class RespostaApiGateway {

    private RespostaApiGateway() {
    }

    public static void escrever(ObjectMapper mapper, OutputStream saida, int status, ObjectNode corpo)
            throws IOException {
        ObjectNode resposta = mapper.createObjectNode();
        resposta.put("statusCode", status);
        resposta.putObject("headers").put("Content-Type", "application/json; charset=utf-8");
        resposta.put("body", mapper.writeValueAsString(corpo));
        mapper.writeValue(saida, resposta);
    }

    public static void erro(ObjectMapper mapper, OutputStream saida, int status, String mensagem)
            throws IOException {
        ObjectNode corpo = mapper.createObjectNode();
        corpo.put("erro", mensagem);
        escrever(mapper, saida, status, corpo);
    }
}
