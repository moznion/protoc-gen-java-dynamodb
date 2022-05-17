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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.Collections;
import net.moznion.protoc.plugin.dynamodb.generated.RangeKeyEntityProto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RangeKeyEntityTest {
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
            .generateCreateTableRequest(RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity.class)
            .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT);

    assertEquals("range-key-entity-table", createTableRequest.getTableName());
    dynamodbClient.createTable(createTableRequest);

    final String hashKey = "foo-hash-key";

    final long rangeKey1 = 1;
    final RangeKeyEntityProto.RangeKeyEntity entityWithRangeKey1 =
        RangeKeyEntityProto.RangeKeyEntity.newBuilder()
            .setHashKeyStr(hashKey)
            .setRangeKeyInt64(rangeKey1)
            .build();
    dynamodbMapper.save(entityWithRangeKey1.toDynamoDBEntity());

    final long rangeKey2 = 2;
    final RangeKeyEntityProto.RangeKeyEntity entityWithRangeKey2 =
        RangeKeyEntityProto.RangeKeyEntity.newBuilder()
            .setHashKeyStr(hashKey)
            .setRangeKeyInt64(rangeKey2)
            .build();
    dynamodbMapper.save(entityWithRangeKey2.toDynamoDBEntity());

    {
      final PaginatedQueryList<RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity>()
                  .withHashKeyValues(entityWithRangeKey1.toDynamoDBEntity())
                  .withRangeKeyCondition(
                      "rangeKeyInt64",
                      new Condition()
                          .withComparisonOperator(ComparisonOperator.EQ)
                          .withAttributeValueList(
                              Collections.singletonList(
                                  new AttributeValue().withN(String.valueOf(rangeKey1))))));
      assertEquals(1, retrieved.size());
      assertEquals(entityWithRangeKey1, retrieved.get(0).toRangeKeyEntity());
    }

    {
      final PaginatedQueryList<RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<RangeKeyEntityProto.RangeKeyEntity.DynamoDBEntity>()
                  .withHashKeyValues(entityWithRangeKey2.toDynamoDBEntity())
                  .withRangeKeyCondition(
                      "rangeKeyInt64",
                      new Condition()
                          .withComparisonOperator(ComparisonOperator.EQ)
                          .withAttributeValueList(
                              Collections.singletonList(
                                  new AttributeValue().withN(String.valueOf(rangeKey2))))));
      assertEquals(1, retrieved.size());
      assertEquals(entityWithRangeKey2, retrieved.get(0).toRangeKeyEntity());
    }
  }
}
