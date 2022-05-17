// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: aliased_entity.proto

package net.moznion.protoc.plugin.dynamodb.generated;

public final class AliasedEntityProto {
  private AliasedEntityProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface AliasedEntityOrBuilder extends
      // @@protoc_insertion_point(interface_extends:net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The hashKeyStr.
     */
    java.lang.String getHashKeyStr();
    /**
     * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The bytes for hashKeyStr.
     */
    com.google.protobuf.ByteString
        getHashKeyStrBytes();

    /**
     * <code>int64 int64_var = 2 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The int64Var.
     */
    long getInt64Var();
  }
  /**
   * Protobuf type {@code net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity}
   */
  public static final class AliasedEntity extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity)
      AliasedEntityOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use AliasedEntity.newBuilder() to construct.
    private AliasedEntity(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private AliasedEntity() {
      hashKeyStr_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new AliasedEntity();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private AliasedEntity(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              hashKeyStr_ = s;
              break;
            }
            case 16: {

              int64Var_ = input.readInt64();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.class, net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.Builder.class);
    }

    public static final int HASH_KEY_STR_FIELD_NUMBER = 1;
    private volatile java.lang.Object hashKeyStr_;
    /**
     * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The hashKeyStr.
     */
    @java.lang.Override
    public java.lang.String getHashKeyStr() {
      java.lang.Object ref = hashKeyStr_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        hashKeyStr_ = s;
        return s;
      }
    }
    /**
     * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The bytes for hashKeyStr.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getHashKeyStrBytes() {
      java.lang.Object ref = hashKeyStr_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        hashKeyStr_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int INT64_VAR_FIELD_NUMBER = 2;
    private long int64Var_;
    /**
     * <code>int64 int64_var = 2 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
     * @return The int64Var.
     */
    @java.lang.Override
    public long getInt64Var() {
      return int64Var_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(hashKeyStr_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, hashKeyStr_);
      }
      if (int64Var_ != 0L) {
        output.writeInt64(2, int64Var_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(hashKeyStr_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, hashKeyStr_);
      }
      if (int64Var_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, int64Var_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity)) {
        return super.equals(obj);
      }
      net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity other = (net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity) obj;

      if (!getHashKeyStr()
          .equals(other.getHashKeyStr())) return false;
      if (getInt64Var()
          != other.getInt64Var()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + HASH_KEY_STR_FIELD_NUMBER;
      hash = (53 * hash) + getHashKeyStr().hashCode();
      hash = (37 * hash) + INT64_VAR_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getInt64Var());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity)
        net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntityOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.class, net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.Builder.class);
      }

      // Construct using net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        hashKeyStr_ = "";

        int64Var_ = 0L;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor;
      }

      @java.lang.Override
      public net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity getDefaultInstanceForType() {
        return net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.getDefaultInstance();
      }

      @java.lang.Override
      public net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity build() {
        net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity buildPartial() {
        net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity result = new net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity(this);
        result.hashKeyStr_ = hashKeyStr_;
        result.int64Var_ = int64Var_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity) {
          return mergeFrom((net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity other) {
        if (other == net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity.getDefaultInstance()) return this;
        if (!other.getHashKeyStr().isEmpty()) {
          hashKeyStr_ = other.hashKeyStr_;
          onChanged();
        }
        if (other.getInt64Var() != 0L) {
          setInt64Var(other.getInt64Var());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object hashKeyStr_ = "";
      /**
       * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @return The hashKeyStr.
       */
      public java.lang.String getHashKeyStr() {
        java.lang.Object ref = hashKeyStr_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          hashKeyStr_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @return The bytes for hashKeyStr.
       */
      public com.google.protobuf.ByteString
          getHashKeyStrBytes() {
        java.lang.Object ref = hashKeyStr_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          hashKeyStr_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @param value The hashKeyStr to set.
       * @return This builder for chaining.
       */
      public Builder setHashKeyStr(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        hashKeyStr_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @return This builder for chaining.
       */
      public Builder clearHashKeyStr() {
        
        hashKeyStr_ = getDefaultInstance().getHashKeyStr();
        onChanged();
        return this;
      }
      /**
       * <code>string hash_key_str = 1 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @param value The bytes for hashKeyStr to set.
       * @return This builder for chaining.
       */
      public Builder setHashKeyStrBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        hashKeyStr_ = value;
        onChanged();
        return this;
      }

      private long int64Var_ ;
      /**
       * <code>int64 int64_var = 2 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @return The int64Var.
       */
      @java.lang.Override
      public long getInt64Var() {
        return int64Var_;
      }
      /**
       * <code>int64 int64_var = 2 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @param value The int64Var to set.
       * @return This builder for chaining.
       */
      public Builder setInt64Var(long value) {
        
        int64Var_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 int64_var = 2 [(.net.moznion.protoc.plugin.dynamodb.fieldopt) = { ... }</code>
       * @return This builder for chaining.
       */
      public Builder clearInt64Var() {
        
        int64Var_ = 0L;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity)
    }

    @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable(tableName = "aliased-entity-table")
    public static class DynamoDBEntity {
      private String hashKeyStr;
    
      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute(attributeName = "hash-key")
      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey(attributeName = "hash-key")
      public String getHashKeyStr() {
        return this.hashKeyStr;
      }
    
      public void setHashKeyStr(final String v) {
        this.hashKeyStr = v;
      }
    
      private Long int64Var;
    
      @com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute(attributeName = "int64-var")
      public Long getInt64Var() {
        return this.int64Var;
      }
    
      public void setInt64Var(final Long v) {
        this.int64Var = v;
      }
    
      public DynamoDBEntity() {}
    
      public DynamoDBEntity(final String hashKeyStr, final Long int64Var) {
        this.hashKeyStr = hashKeyStr;
        this.int64Var = int64Var;
      }
    
      public AliasedEntity toAliasedEntity() {
        return new AliasedEntity.Builder().setHashKeyStr(this.hashKeyStr).setInt64Var(this.int64Var).build();
      }
    
    }
    
    public DynamoDBEntity toDynamoDBEntity() {
      return new DynamoDBEntity(this.getHashKeyStr(), this.getInt64Var());
    }
    
    // @@protoc_insertion_point(class_scope:net.moznion.protoc.plugin.dynamodb.generated.AliasedEntity)
    private static final net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity();
    }

    public static net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<AliasedEntity>
        PARSER = new com.google.protobuf.AbstractParser<AliasedEntity>() {
      @java.lang.Override
      public AliasedEntity parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new AliasedEntity(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<AliasedEntity> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<AliasedEntity> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public net.moznion.protoc.plugin.dynamodb.generated.AliasedEntityProto.AliasedEntity getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024aliased_entity.proto\022,net.moznion.prot" +
      "oc.plugin.dynamodb.generated\032\032dependenci" +
      "es/options.proto\"_\n\rAliasedEntity\022*\n\014has" +
      "h_key_str\030\001 \001(\tB\024\212\265\030\002\010\001\212\265\030\n\"\010hash-key\022\"\n" +
      "\tint64_var\030\002 \001(\003B\017\212\265\030\013\"\tint64-varB\\\n,net" +
      ".moznion.protoc.plugin.dynamodb.generate" +
      "dB\022AliasedEntityProto\202\265\030\026\n\024aliased-entit" +
      "y-tableb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          net.moznion.protoc.plugin.dynamodb.OptionsProto.getDescriptor(),
        });
    internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_net_moznion_protoc_plugin_dynamodb_generated_AliasedEntity_descriptor,
        new java.lang.String[] { "HashKeyStr", "Int64Var", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(net.moznion.protoc.plugin.dynamodb.OptionsProto.fieldopt);
    registry.add(net.moznion.protoc.plugin.dynamodb.OptionsProto.fileopt);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    net.moznion.protoc.plugin.dynamodb.OptionsProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
