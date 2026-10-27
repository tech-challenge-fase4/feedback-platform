package br.com.techchallenge.feedback.infrastructure.dynamodb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.techchallenge.feedback.application.port.AvaliacaoRepository;
import br.com.techchallenge.feedback.domain.Avaliacao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

/**
 * ADAPTADOR: implementa a porta AvaliacaoRepository usando o DynamoDB.
 * Trocar de banco = escrever outra classe que implementa a mesma interface.
 */
@Singleton
public class DynamoDbAvaliacaoRepository implements AvaliacaoRepository {

    public static final String INDICE_DIA = "indice-dia";

    private final DynamoDbClient dynamo;
    private final String tabela;

    @Inject
    public DynamoDbAvaliacaoRepository(DynamoDbClient dynamo,
                                       @ConfigProperty(name = "app.tabela") String tabela) {
        this.dynamo = dynamo;
        this.tabela = tabela;
    }

    @Override
    public void salvar(Avaliacao avaliacao) {
        dynamo.putItem(PutItemRequest.builder()
                .tableName(tabela)
                .item(AvaliacaoItemMapper.paraItem(avaliacao))
                .build());
    }

    /** Consulta o índice por dia (sem varrer a tabela inteira). */
    @Override
    public List<Avaliacao> buscarPorDia(LocalDate dia) {
        QueryRequest consulta = QueryRequest.builder()
                .tableName(tabela)
                .indexName(INDICE_DIA)
                .keyConditionExpression("#d = :dia")
                .expressionAttributeNames(Map.of("#d", AvaliacaoItemMapper.DIA))
                .expressionAttributeValues(Map.of(":dia", AttributeValue.fromS(dia.toString())))
                .build();

        List<Avaliacao> resultado = new ArrayList<>();
        for (QueryResponse pagina : dynamo.queryPaginator(consulta)) {
            for (Map<String, AttributeValue> item : pagina.items()) {
                resultado.add(AvaliacaoItemMapper.deItem(item));
            }
        }
        return resultado;
    }
}
