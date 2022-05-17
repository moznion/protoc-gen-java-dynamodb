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
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.Arrays;
import java.util.Collections;
import net.moznion.protoc.plugin.dynamodb.generated.GsiEntityProto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GsiEntityTest {
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
            .generateCreateTableRequest(GsiEntityProto.GsiEntity.DynamoDBEntity.class)
            .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT);
    createTableRequest.setGlobalSecondaryIndexes(
        Arrays.asList(
            new GlobalSecondaryIndex()
                .withIndexName("gsi0")
                .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT)
                .withKeySchema(
                    new KeySchemaElement()
                        .withAttributeName("hashKeyStr")
                        .withKeyType(KeyType.HASH),
                    new KeySchemaElement().withAttributeName("barStr").withKeyType(KeyType.RANGE))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL)),
            new GlobalSecondaryIndex()
                .withIndexName("gsi1")
                .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT)
                .withKeySchema(
                    new KeySchemaElement().withAttributeName("fooStr").withKeyType(KeyType.HASH),
                    new KeySchemaElement().withAttributeName("barStr").withKeyType(KeyType.RANGE))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL)),
            new GlobalSecondaryIndex()
                .withIndexName("gsi2")
                .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT)
                .withKeySchema(
                    new KeySchemaElement().withAttributeName("fooStr").withKeyType(KeyType.HASH),
                    new KeySchemaElement().withAttributeName("buzStr").withKeyType(KeyType.RANGE))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL)),
            new GlobalSecondaryIndex()
                .withIndexName("gsi3")
                .withProvisionedThroughput(DEFAULT_TABLE_THROUGHPUT)
                .withKeySchema(
                    new KeySchemaElement().withAttributeName("quxStr").withKeyType(KeyType.HASH))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))));
    dynamodbClient.createTable(createTableRequest);

    final String hashKey = "hashKey";
    final String foo = "foo";
    final String bar = "bar";
    final String buz = "buz";
    final String qux = "qux";

    final GsiEntityProto.GsiEntity entity =
        GsiEntityProto.GsiEntity.newBuilder()
            .setHashKeyStr(hashKey)
            .setFooStr(foo)
            .setBarStr(bar)
            .setBuzStr(buz)
            .setQuxStr(qux)
            .build();
    dynamodbMapper.save(entity.toDynamoDBEntity());

    {
      final PaginatedQueryList<GsiEntityProto.GsiEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              GsiEntityProto.GsiEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<GsiEntityProto.GsiEntity.DynamoDBEntity>()
                  .withIndexName("gsi0")
                  .withConsistentRead(false)
                  .withHashKeyValues(entity.toDynamoDBEntity())
                  .withRangeKeyCondition(
                      "barStr",
                      new Condition()
                          .withComparisonOperator(ComparisonOperator.EQ)
                          .withAttributeValueList(
                              Collections.singletonList(new AttributeValue().withS(bar)))));
      assertEquals(1, retrieved.size());
      assertEquals(entity, retrieved.get(0).toGsiEntity());
    }
    {
      final PaginatedQueryList<GsiEntityProto.GsiEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              GsiEntityProto.GsiEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<GsiEntityProto.GsiEntity.DynamoDBEntity>()
                  .withIndexName("gsi1")
                  .withConsistentRead(false)
                  .withHashKeyValues(entity.toDynamoDBEntity())
                  .withRangeKeyCondition(
                      "barStr",
                      new Condition()
                          .withComparisonOperator(ComparisonOperator.EQ)
                          .withAttributeValueList(
                              Collections.singletonList(new AttributeValue().withS(bar)))));
      assertEquals(1, retrieved.size());
      assertEquals(entity, retrieved.get(0).toGsiEntity());
    }
    {
      final PaginatedQueryList<GsiEntityProto.GsiEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              GsiEntityProto.GsiEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<GsiEntityProto.GsiEntity.DynamoDBEntity>()
                  .withIndexName("gsi2")
                  .withConsistentRead(false)
                  .withHashKeyValues(entity.toDynamoDBEntity())
                  .withRangeKeyCondition(
                      "buzStr",
                      new Condition()
                          .withComparisonOperator(ComparisonOperator.EQ)
                          .withAttributeValueList(
                              Collections.singletonList(new AttributeValue().withS(buz)))));
      assertEquals(1, retrieved.size());
      assertEquals(entity, retrieved.get(0).toGsiEntity());
    }
    {
      final PaginatedQueryList<GsiEntityProto.GsiEntity.DynamoDBEntity> retrieved =
          dynamodbMapper.query(
              GsiEntityProto.GsiEntity.DynamoDBEntity.class,
              new DynamoDBQueryExpression<GsiEntityProto.GsiEntity.DynamoDBEntity>()
                  .withIndexName("gsi3")
                  .withConsistentRead(false)
                  .withHashKeyValues(entity.toDynamoDBEntity()));
      assertEquals(1, retrieved.size());
      assertEquals(entity, retrieved.get(0).toGsiEntity());
    }
  }
}
