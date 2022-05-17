package net.moznion.protoc.plugin.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Value;

@Value
class DynamodbAttributeFields {
	List<DynamodbAttributeField> items;

	DynamodbAttributeFields validate() throws IllegalArgumentException {
		boolean hashKeyExists = false;
		boolean rangeKeyExists = false;

		final Map<String, GSICounter> gsiCounters = new HashMap<>();

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

			field.getGsiHashKeyIndices().forEach(idx -> {
				final GSICounter gsiCounter = gsiCounters.get(idx);
				if (gsiCounter == null) {
					final GSICounter counter = new GSICounter();
					counter.incrementHashKeyCount();
					gsiCounters.put(idx, counter);
					return;
				}
				gsiCounter.incrementHashKeyCount();
			});
			field.getGsiRangeKeyIndices().forEach(idx -> {
				final GSICounter gsiCounter = gsiCounters.get(idx);
				if (gsiCounter == null) {
					final GSICounter counter = new GSICounter();
					counter.incrementRangeKeyCount();
					gsiCounters.put(idx, counter);
					return;
				}
				gsiCounter.incrementRangeKeyCount();
			});
		}

		if (!hashKeyExists) {
			throw new IllegalArgumentException(
				"it must have one hash-key specifier, but it doesn't have that");
		}

		for (final Map.Entry<String, GSICounter> entry : gsiCounters.entrySet()) {
			entry.getValue().validate(entry.getKey());
		}

		return this;
	}

	@Data
	private static class GSICounter {
		int hashKeyCount = 0;
		int rangeKeyCount = 0;

		void incrementHashKeyCount() {
			hashKeyCount++;
		}

		void incrementRangeKeyCount() {
			rangeKeyCount++;
		}

		void validate(final String gsiName) throws IllegalArgumentException {
			if (hashKeyCount == 0) {
				throw new IllegalArgumentException(
					"GSI \"" + gsiName + "\" doesn't have the hash key");
			}

			if (hashKeyCount >= 2) {
				throw new IllegalArgumentException("GSI \""
													   + gsiName
													   + "\" has the multiple hash keys, it needs that key has only one GSI hash key");
			}

			if (rangeKeyCount >= 2) {
				throw new IllegalArgumentException("GSI \""
													   + gsiName
													   + "\" has the multiple range keys, it needs that key has only one GSI range key");
			}
		}
	}
}
