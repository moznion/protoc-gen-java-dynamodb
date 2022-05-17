package net.moznion.protoc.plugin.dynamodb;

import java.util.List;
import lombok.Value;

@Value
class DynamodbAttributeFields {
	List<DynamodbAttributeField> items;

	DynamodbAttributeFields validate() throws IllegalArgumentException {
		boolean hashKeyExists = false;
		boolean rangeKeyExists = false;
		for (final DynamodbAttributeField field : items) {
			if (field.isHashKey()) {
				if (hashKeyExists) {
					throw new IllegalArgumentException(
						"it must have only one hash-key specifier, but that appears multiple times");
				}
				hashKeyExists = true;
			}

			if (field.isRangeKey()) {
				if (rangeKeyExists) {
					throw new IllegalArgumentException(
						"it must have only one range-key specifier, but that appears multiple times");
				}
				rangeKeyExists = true;
			}
		}

		if (!hashKeyExists) {
			throw new IllegalArgumentException(
				"it must have one hash-key specifier, but it doesn't have that");
		}

		return this;
	}
}
