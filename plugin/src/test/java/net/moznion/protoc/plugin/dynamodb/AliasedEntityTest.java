package net.moznion.protoc.plugin.dynamodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AliasedEntityTest {
  private static final String DYNAMODB_LOCAL_ENDPOINT = "http://127.0.0.1:38000";
  private static final ProvisionedThroughput DEFAULT_TABLE_THROUGHPUT =
      new ProvisionedThroughput(1L, 1L);

  private static AmazonDynamoDB dynamodbClient;
  private static DynamoDBMapper dynamodbMapper;

  @BeforeAll
  static void setupAll() {
    dynamodbClient =
        AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(DYNAMODB_LOCAL_ENDPOINT, "us-west-1"))
            .withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials("fakeAx", "fakeSecret")))
            .build();
    dynamodbMapper = new DynamoDBMapper(dynamodbClient, DynamoDBMapperConfig.DEFAULT);
  }

  @BeforeEach
  void setupEach() {
    dynamodbClient.listTables().getTableNames().forEach(dynamodbClient::deleteTable);
  }

  @Test
  void shouldInsertAndLookupEntitySuccessfully() throws Exception {
    final CreateTableRequest createTableRequest =
        dynamodbMapper
            .generateCreateTableRequest(AliasedEntityProto.AliasedEntity.DynamoDBEntity.class)
            .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT);

    final String tableName = "aliased-entity-table";
    assertEquals(tableName, createTableRequest.getTableName());
    dynamodbClient.createTable(createTableRequest);

    final String hashKey = "hashKey";
    final long int64Var = 12345;

    final AliasedEntityProto.AliasedEntity entity =
        AliasedEntityProto.AliasedEntity.newBuilder()
            .setHashKeyStr(hashKey)
            .setInt64Var(int64Var)
            .build();
    dynamodbMapper.save(entity.toDynamoDBEntity());

    final QueryResult result =
        dynamodbClient.query(
            new QueryRequest(tableName)
                .addKeyConditionsEntry(
                    "hash-key",
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.EQ)
                        .withAttributeValueList(new AttributeValue().withS(hashKey))));
    assertEquals(1, result.getCount());
    assertNull(result.getItems().get(0).get("hashKeyStr"));
    assertEquals(hashKey, result.getItems().get(0).get("hash-key").getS());
    assertNull(result.getItems().get(0).get("int64Var"));
    assertEquals(String.valueOf(int64Var), result.getItems().get(0).get("int64-var").getN());
  }
}
