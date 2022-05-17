package net.moznion.protoc.plugin.dynamodb;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class FileOptions {
	String tableName;
	String dynamodbEntityClassName;

	static FileOptions from(OptionsProto.FileOptions opts) {
		return new FileOptions(opts.getJavaDynamodbTableName(), opts.getJavaDynamodbEntityClassName());
	}

	boolean shouldGenerate() {
		return tableName != null && !tableName.isBlank();
	}
}
