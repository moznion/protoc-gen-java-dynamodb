package net.moznion.protoc.plugin.dynamodb;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.google.protobuf.ByteString;
import java.util.Arrays;
import net.moznion.protoc.plugin.dynamodb.generated.AllTypesEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AllTypesEntityTest {
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
            .generateCreateTableRequest(AllTypesEntity.DynamoDBEntity.class)
            .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT);

    assertEquals("all-types-entity-table", createTableRequest.getTableName());
    dynamodbClient.createTable(createTableRequest);

    final String fooHashKey = "foo-hash-key";
    final AllTypesEntity fooEntity = AllTypesEntity.newBuilder().setHashKeyStr(fooHashKey).build();
    dynamodbMapper.save(fooEntity.toDynamoDBEntity());

    final String barHashKey = "bar-hash-key";
    final AllTypesEntity barEntity =
        AllTypesEntity.newBuilder()
            .setHashKeyStr(barHashKey)
            .setBoolVar(true)
            .setBytesVar(ByteString.copyFromUtf8("hello"))
            .setInt32Var(32)
            .setInt64Var(64)
            .setUint32Var(320)
            .setUint64Var(640)
            .setFloatVar(12.34f)
            .setDoubleVar(56.78)
            .addAllStrList(Arrays.asList("str", "var", "iable"))
            .addAllBoolList(Arrays.asList(true, false, true))
            .addAllInt32List(Arrays.asList(1, 2, 3))
            .addAllInt64List(Arrays.asList(4L, 5L, 6L))
            .addAllUint32List(Arrays.asList(10, 20, 30))
            .addAllUint64List(Arrays.asList(40L, 50L, 60L))
            .addAllFloatList(Arrays.asList(12.3f, 45.6f, 78.9f))
            .addAllDoubleList(Arrays.asList(123.4, 567.8, 901.2))
            .build();
    dynamodbMapper.save(barEntity.toDynamoDBEntity());

    {
      final PaginatedQueryList<AllTypesEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              AllTypesEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<AllTypesEntity.DynamoDBEntity>()
                  .withHashKeyValues(fooEntity.toDynamoDBEntity()));
      assertEquals(1, retrieved.size());
      assertEquals(fooEntity, retrieved.get(0).toAllTypesEntity());
    }

    {
      final PaginatedQueryList<AllTypesEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              AllTypesEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<AllTypesEntity.DynamoDBEntity>()
                  .withHashKeyValues(barEntity.toDynamoDBEntity()));
      assertEquals(1, retrieved.size());
      assertEquals(barEntity, retrieved.get(0).toAllTypesEntity());
    }
  }
}
