package br.com.techchallenge.feedback.entrypoint.lambda.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Conhece o formato de resposta que o API Gateway (integração proxy) espera:
 * {@code {"statusCode": ..., "headers": {...}, "body": "<json em texto>"}}.
 *
 * Quando {@code app.cors-origem} está configurada, toda resposta leva o header
 * {@code Access-Control-Allow-Origin}, para que a página web de testes possa ler a resposta.
 */
public final class RespostaApiGateway {

    /**
     * Origem da página web autorizada a ler as respostas (CORS), ex.: {@code https://dxxxx.cloudfront.net}.
     * Vazia = header não enviado (Postman e curl não precisam dele).
     */
    private static final Optional<String> ORIGEM_CORS =
            ConfigProvider.getConfig().getOptionalValue("app.cors-origem", String.class);

    private RespostaApiGateway() {
    }

    /**
     * Escreve uma resposta com corpo JSON.
     *
     * @param mapper conversor JSON
     * @param saida  stream de saída da Lambda
     * @param status código HTTP (ex.: 201, 400, 500)
     * @param corpo  objeto JSON que vai no {@code body}
     * @throws IOException se não for possível escrever na saída
     */
    public static void escrever(ObjectMapper mapper, OutputStream saida, int status, ObjectNode corpo)
            throws IOException {
        ObjectNode resposta = mapper.createObjectNode();
        resposta.put("statusCode", status);
        ObjectNode headers = resposta.putObject("headers");
        headers.put("Content-Type", "application/json; charset=utf-8");
        ORIGEM_CORS.ifPresent(origem -> headers.put("Access-Control-Allow-Origin", origem).put("Vary", "Origin"));
        resposta.put("body", mapper.writeValueAsString(corpo));
        mapper.writeValue(saida, resposta);
    }

    /**
     * Escreve uma resposta de erro no formato {@code {"erro": "<mensagem>"}}.
     *
     * @param mapper   conversor JSON
     * @param saida    stream de saída da Lambda
     * @param status   código HTTP de erro
     * @param mensagem motivo, em português, para quem chamou a API
     * @throws IOException se não for possível escrever na saída
     */
    public static void erro(ObjectMapper mapper, OutputStream saida, int status, String mensagem)
            throws IOException {
        ObjectNode corpo = mapper.createObjectNode();
        corpo.put("erro", mensagem);
        escrever(mapper, saida, status, corpo);
    }
}
