package net.moznion.protoc.plugin.dynamodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import net.moznion.protoc.plugin.dynamodb.generated.IgnoredFieldsEntityProto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IgnoredFieldsEntityTest {
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
            .generateCreateTableRequest(
                IgnoredFieldsEntityProto.IgnoredFieldsEntity.DynamoDBEntity.class)
            .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT);

    assertEquals("ignored-fields-entity-table", createTableRequest.getTableName());
    dynamodbClient.createTable(createTableRequest);

    final String hashKey = "hash-key";
    final String ignoredStr = "ignored";
    final long ignoredInt64 = 123;

    final IgnoredFieldsEntityProto.IgnoredFieldsEntity entity =
        IgnoredFieldsEntityProto.IgnoredFieldsEntity.newBuilder()
            .setHashKeyStr(hashKey)
            .setIgnoredStr(ignoredStr)
            .setIgnoredInt64(ignoredInt64)
            .build();
    dynamodbMapper.save(entity.toDynamoDBEntity());

    final PaginatedQueryList<IgnoredFieldsEntityProto.IgnoredFieldsEntity.DynamoDBEntity>
        retrieved =
            dynamodbMapper.query(
                IgnoredFieldsEntityProto.IgnoredFieldsEntity.DynamoDBEntity.class,
                new DynamoDBQueryExpression<
                        IgnoredFieldsEntityProto.IgnoredFieldsEntity.DynamoDBEntity>()
                    .withHashKeyValues(entity.toDynamoDBEntity()));
    assertEquals(1, retrieved.size());
    assertNotEquals(entity, retrieved.get(0).toIgnoredFieldsEntity());
    assertEquals(hashKey, retrieved.get(0).toIgnoredFieldsEntity().getHashKeyStr());
    assertEquals("", retrieved.get(0).toIgnoredFieldsEntity().getIgnoredStr());
    assertEquals(0, retrieved.get(0).toIgnoredFieldsEntity().getIgnoredInt64());
  }
}
