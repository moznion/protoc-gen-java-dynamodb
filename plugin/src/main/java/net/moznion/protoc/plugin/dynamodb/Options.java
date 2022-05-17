package net.moznion.protoc.plugin.dynamodb;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class Options {
	String tableName;

	static Options from(OptionsProto.FileOptions opts) {
		return new Options(opts.getJavaDynamodbTableName());
	}

	boolean shouldGenerate() {
		return tableName != null && !tableName.isBlank();
	}
}
