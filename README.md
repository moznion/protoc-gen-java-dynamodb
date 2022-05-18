# protoc-gen-java-dynamodb [![Test](https://github.com/moznion/protoc-gen-java-dynamodb/actions/workflows/test.yaml/badge.svg)](https://github.com/moznion/protoc-gen-java-dynamodb/actions/workflows/test.yaml) ![Maven Central](https://search.maven.org/artifact/net.moznion/protoc-gen-java-dynamodb)

A [Protocol Buffers](https://developers.google.com/protocol-buffers) code generator plugin for Java that generates [Amazon DynamoDB](https://aws.amazon.com/dynamodb) entity code according to the protoc schema.

## Getting Started

```protobuf
syntax = "proto3";

package com.example.dynamodb;

import "protos/options.proto";

option java_package = "com.example.dynamodb";
option java_outer_classname = "ExampleEntityProto";

option (net.moznion.protoc.plugin.dynamodb.fileopt).java_dynamodb_table_name = "example-entity-table";

message ExampleEntity {
  string hash_key = 1 [(net.moznion.protoc.plugin.dynamodb.fieldopt).java_dynamodb_hash_key = true];
  int64 range_key = 2 [(net.moznion.protoc.plugin.dynamodb.fieldopt).java_dynamodb_range_key = true];
  bool bool_var = 3;
}
```

And when it gives this plugin the avobe protobuf schema:

```
$ protoc -I . --java_out=./out/src/main/java/ --plugin=protoc-gen-java-dynamodb=./protoc-gen-java-dynamodb/build/scripts/protoc-gen-java-dynamodb --java-dynamodb_out=./out/src/main/java ./example.proto
```

Then the plugin generates the following code in `ExampleEntityProto` class:

```java
    // protoc-gen-java-dynamodb plugin generated (((
    @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable(tableName = "example-entity-table")
    public static class DynamoDBEntity {
      private String hashKey;

      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute()
      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey()
      public String getHashKey() {
        return this.hashKey;
      }

      public void setHashKey(final String v) {
        this.hashKey = v;
      }

      private Long rangeKey;

      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute()
      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey()
      public Long getRangeKey() {
        return this.rangeKey;
      }

      public void setRangeKey(final Long v) {
        this.rangeKey = v;
      }

      private Boolean boolVar;

      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute()
      public Boolean getBoolVar() {
        return this.boolVar;
      }

      public void setBoolVar(final Boolean v) {
        this.boolVar = v;
      }

      public DynamoDBEntity() {}

      public DynamoDBEntity(final String hashKey, final Long rangeKey, final Boolean boolVar) {
        this.hashKey = hashKey;
        this.rangeKey = rangeKey;
        this.boolVar = boolVar;
      }

      public ExampleEntity toExampleEntity() {
        return new ExampleEntity.Builder()
          .setHashKey(this.hashKey)
          .setRangeKey(this.rangeKey)
          .setBoolVar(this.boolVar)
          .build();
      }

    }

    public DynamoDBEntity toDynamoDBEntity() {
      return new DynamoDBEntity(this.getHashKey(), this.getRangeKey(), this.getBoolVar());
    }

    // ))) protoc-gen-java-dynamodb plugin generated
```

So then you can use the AWS SDK provided DynamoDB utilities like the following.

```java
final DynamoDBMapper dynamodbMapper = new DynamoDBMapper(...);

final ExampleEntityProto.ExampleEntity entity = ExampleEntityProto.ExampleEntity.newBuilder()
                                                                  .setHashKey("hashKey")
                                                                  .setRangeKey(1234567890)
                                                                  .setBoolVar(true)
                                                                  .build();
dynamodbMapper.save(entity.toDynamoDBEntity()); // this plugin generates `#toDynamoDBEntity()` method

// retrieve the entity vise versa
final PaginatedQueryList<ExampleEntityProto.ExampleEntity.DynamoDBEntity> result =
    dynamodbMapper.query(
        ExampleEntityProto.ExampleEntity.DynamoDBEntity.class,
        new DynamoDBQueryExpression<ExampleEntityProto.ExampleEntity.DynamoDBEntity>().withHashKeyValues(entity.toDynamoDBEntity())
    );
final ExampleEntityProto.ExampleEntity retrievedEntry = result.get(0).toExampleEntity(); // <= equal to `entity`
```

## Options

See also [protos/options.proto](./protos/options.proto)

### File Options

#### String: `java_dynamodb_table_name`

A DynamoDB table name for the protobuf message schema. If this value is missing, this plugin ignores the message (i.e. it doesn't generate the DynamoDB related code).

#### (optional) String: `java_dynamodb_entity_class_name`

An alternative name of the generated DynamoDB entity class name. The default class name is `DynamoDBEntity`.


### Field Options

#### Bool: `java_dynamodb_hash_key`

An annotation that marks the specified field as the hash key. This is a mandatory parameter and must appear only one time.

#### (optional) Bool: `java_dynamodb_range_key`

An annotation that marks the specified field as the range key. This must appear one time at most.

#### (optional) Bool: `java_dynamodb_ignore`

If this option is given, the field is ignored by the DynamoDB code generator.

#### (optional) String: `java_dynamodb_alias`

An alternative name of the generated DynamoDB attribute name. If this option is set, it puts `@DynamoDBAttribute(attributeName = "${SPECIFIED_ALIAS_ATTRIBUTE_NAME}")` annotation onto the field instead of the default one.

#### (optional) List<String>: `java_dynamodb_hash_key_gsi_names`

A specifier of the GSI hash keys. If this option is given, it puts `@DynamoDBIndexHashKey(globalSecondaryIndexNames = { ... ${GSI_INDEX_NAMES} ... })` annotation onto the field.

#### (optional) List<String>: `java_dynamodb_range_key_gsi_names`

A specifier of the GSI range keys. If this option is given, it puts `@DynamoDBIndexRangeKey(globalSecondaryIndexNames = { ... ${GSI_INDEX_NAMES} ... })` annotation onto the field.

## Note

Some of the code were diverted from [Fadelis/protoc-gen-java-optional](https://github.com/Fadelis/protoc-gen-java-optional)

## Author

moznion (<moznion@mail.moznion.net>)

