package net.moznion.protoc.plugin.dynamodb;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class DynamodbAttributeField {
	private static final String BYTES_ARRAY_TYPE = "byte[]";
	private static final Map<Descriptors.FieldDescriptor.JavaType, String> PRIMITIVE_CLASSES_MAP =
		ImmutableMap.<Descriptors.FieldDescriptor.JavaType, String>builder()
					.put(Descriptors.FieldDescriptor.JavaType.INT, Integer.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.LONG, Long.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.FLOAT, Float.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.DOUBLE, Double.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.BOOLEAN, Boolean.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.STRING, String.class.getSimpleName())
					.put(Descriptors.FieldDescriptor.JavaType.BYTE_STRING, BYTES_ARRAY_TYPE)
					.build();

	String name;
	String fieldName;
	String aliasName;
	boolean isHashKey;
	boolean isRangeKey;
	List<String> gsiHashKeyIndices;
	List<String> gsiRangeKeyIndices;
	DescriptorProtos.FieldDescriptorProto descriptor;

	static DynamodbAttributeField from(final DescriptorProtos.FieldDescriptorProto fieldDescriptor) {
		final String fieldName = fieldDescriptor.getName();
		final OptionsProto.FieldOptions fieldOpt =
			fieldDescriptor.getOptions().getExtension(OptionsProto.fieldopt);
		return new DynamodbAttributeField(
			fieldName,
			fieldDescriptor.getJsonName(),
			fieldOpt.getJavaDynamodbAlias(),
			fieldOpt.getJavaDynamodbHashKey(),
			fieldOpt.getJavaDynamodbRangeKey(),
			fieldOpt.getJavaDynamodbHashKeyGsiNamesList(),
			fieldOpt.getJavaDynamodbRangeKeyGsiNamesList(),
			fieldDescriptor
		);
	}

	DynamodbAttributeField validate() throws IllegalArgumentException {
		if (isHashKey && isRangeKey) {
			throw new IllegalArgumentException(
				"hash-key specifier and range-key specifier is exclusive");
		}

		// TODO gsi config overwrap

		return this;
	}

	boolean isRepeated() {
		return descriptor.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
	}

	String getJavaTypeName() {
		final DescriptorProtos.FieldDescriptorProto fieldDescriptorProto = getDescriptor();
		return Optional.of(fieldDescriptorProto.getType())
					   .map(Descriptors.FieldDescriptor.Type::valueOf)
					   .map(Descriptors.FieldDescriptor.Type::getJavaType)
					   .map(PRIMITIVE_CLASSES_MAP::get)
					   .map(t -> {
						   if (isRepeated()) {
							   if (t.equals(BYTES_ARRAY_TYPE)) {
								   throw new IllegalArgumentException("`repeated bytes` is prohibited type");
							   }
							   return "java.util.List<" + t + ">";
						   }
						   return t;
					   })
					   .orElseThrow(() -> new IllegalArgumentException(
						   "failed to find java type for field:\n" + fieldDescriptorProto));
	}

	boolean isBytesArrayType() {
		return getJavaTypeName().equals(BYTES_ARRAY_TYPE);
	}
}
