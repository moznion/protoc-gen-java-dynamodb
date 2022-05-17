package net.moznion.protoc.plugin.dynamodb;

import lombok.Value;

@Value
class DynamodbEntityClassNameProvider {
	private static final String DEFAULT_DYNAMODB_ENTITY_CLASS_NAME = "DynamoDBEntity";

	FileOptions opts;

	String getDynamodbEntityClassName() {
		final String name = opts.getDynamodbEntityClassName();
		if (name == null || name.isBlank()) {
			return DEFAULT_DYNAMODB_ENTITY_CLASS_NAME;
		}
		return name;
	}
}
