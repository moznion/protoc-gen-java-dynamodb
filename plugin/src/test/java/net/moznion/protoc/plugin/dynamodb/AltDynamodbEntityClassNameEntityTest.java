package net.moznion.protoc.plugin.dynamodb;

import net.moznion.protoc.plugin.dynamodb.generated.AltDynamodbEntityClassNameEntityOuterClass;
import org.junit.jupiter.api.Test;

public class AltDynamodbEntityClassNameEntityTest {
	@Test
	void shouldHaveAlternativeDynamodbEntityClassName() {
		// it sohuld have `DYNAMODB`
		new AltDynamodbEntityClassNameEntityOuterClass.AltDynamodbEntityClassNameEntity.DYNAMODB();
	}
}
