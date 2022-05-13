package net.moznion.protoc.plugin.dynamodb;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.Feature;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import com.salesforce.jprotoc.Generator;
import com.salesforce.jprotoc.GeneratorException;
import com.salesforce.jprotoc.ProtoTypeMap;
import com.salesforce.jprotoc.ProtocPlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class App extends Generator {
	private static final String NEWLINE = "\n";
	private static final String JAVA_EXTENSION = ".java";
	private static final String DIR_SEPARATOR = "/";
	private static final String CLASS_SCOPE = "class_scope:";
	private static final Map<JavaType, String> PRIMITIVE_CLASSES =
		ImmutableMap.<JavaType, String>builder()
					.put(JavaType.INT, Integer.class.getSimpleName())
					.put(JavaType.LONG, Long.class.getSimpleName())
					.put(JavaType.FLOAT, Float.class.getSimpleName())
					.put(JavaType.DOUBLE, Double.class.getSimpleName())
					.put(JavaType.BOOLEAN, Boolean.class.getSimpleName())
					.put(JavaType.STRING, String.class.getSimpleName())
					.put(JavaType.BYTE_STRING, ByteString.class.getName())
					.build();
	//private static final Map<String, String> PRIMITIVE_OPTIONALS =
	//	ImmutableMap.<String, String>builder()
	//				.put(Integer.class.getSimpleName(), OptionalInt.class.getName())
	//				.put(Long.class.getSimpleName(), OptionalLong.class.getName())
	//				.put(Double.class.getSimpleName(), OptionalDouble.class.getName())
	//				.build();

	public static void main(String[] args) {
		ProtocPlugin.generate(new App());
	}

	private ProtoTypeMap protoTypeMap;

	@Value
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class Options {
		String tableName;
		String hashKey;
		boolean noStrict;
		String rangeKey;
		Set<String> ignoreFieldNames;
		Map<String, List<String>> gsiHashKeys;
		Map<String, List<String>> gsiRangeKeys;

		static Options from(OptionsProto.Options opts) {
			return new Options(
				opts.getJavaDynamodbTableName(),
				opts.getJavaDynamodbHashKey(),
				opts.getJavaDynamodbNoStrictMode(),
				opts.getJavaDynamodbRangeKey(),
				new HashSet<>(opts.getJavaDynamodbIgnoreFieldNamesList()),
				opts.getJavaDynamodbGsiHashKeysList()
					.stream()
					.collect(Collectors.toMap(
						OptionsProto.DynamoDBIndexKey::getFieldName,
						OptionsProto.DynamoDBIndexKey::getIndexNamesList
					)),
				opts.getJavaDynamodbGsiRangeKeysList()
					.stream()
					.collect(Collectors.toMap(
						OptionsProto.DynamoDBIndexKey::getFieldName,
						OptionsProto.DynamoDBIndexKey::getIndexNamesList
					))
			);
		}

		boolean shouldGenerate() {
			return tableName != null
				&& !tableName.isBlank()
				&& hashKey != null
				&& !hashKey.isBlank();
		}

		boolean isStrictMode() {
			return !noStrict;
		}
	}

	@Override
	public List<File> generateFiles(CodeGeneratorRequest originalRequest)
		throws GeneratorException {
		// register the custom options extension
		final CodeGeneratorRequest request;
		try {
			final ExtensionRegistry registry = ExtensionRegistry.newInstance();
			registry.add(OptionsProto.opt);
			request = CodeGeneratorRequest.parseFrom(originalRequest.toByteArray(), registry);
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}

		// create a map from proto types to java types
		this.protoTypeMap = ProtoTypeMap.of(request.getProtoFileList());

		return request.getProtoFileList().stream()
					  .map(file -> {
						  if (request.getFileToGenerateList().contains(file.getName())) {
							  final Options opts =
								  Options.from(file.getOptions().getExtension(OptionsProto.opt));
							  if (opts.shouldGenerate()) {
								  return Optional.of(
									  (Supplier<Stream<File>>) () -> handleProtoFile(file, opts));
							  }
						  }
						  return Optional.<Supplier<Stream<File>>>empty();
					  })
					  .filter(Optional::isPresent)
					  .flatMap(maybeHandler -> maybeHandler.get().get())
					  .collect(Collectors.toList());
	}

	@Override
	protected List<Feature> supportedFeatures() {
		return Collections.singletonList(Feature.FEATURE_PROTO3_OPTIONAL);
	}

	private Stream<File> handleProtoFile(final FileDescriptorProto fileDescriptor,
										 final Options opts
	) {
		final String protoPackage = fileDescriptor.getPackage();
		final String javaPackage = fileDescriptor.getOptions().hasJavaPackage()
			? fileDescriptor.getOptions().getJavaPackage()
			: protoPackage;

		return fileDescriptor.getMessageTypeList().stream()
							 .flatMap(descriptor -> handleMessage(descriptor,
																  getFileName(
																	  fileDescriptor,
																	  descriptor
																  ), protoPackage, javaPackage, opts
							 ));
	}

	@Value
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class DynamodbAttributeField {
		String name;
		String fieldName;
		boolean isHashKey;
		boolean isRangeKey;
		List<String> gsiHashKeyIndices;
		List<String> gsiRangeKeyIndices;
		FieldDescriptorProto descriptor;

		static DynamodbAttributeField from(final FieldDescriptorProto fieldDescriptor,
										   final Options opt
		) {
			final String fieldName = fieldDescriptor.getName();
			return new DynamodbAttributeField(
				fieldName,
				fieldDescriptor.getJsonName(),
				fieldName.equals(opt.hashKey),
				fieldName.equals(opt.rangeKey),
				opt.getGsiHashKeys().getOrDefault(fieldName, Collections.emptyList()),
				opt.getGsiRangeKeys().getOrDefault(fieldName, Collections.emptyList()),
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
	}

	@Value
	private static class DynamodbAttributeFields {
		List<DynamodbAttributeField> fields;

		DynamodbAttributeFields validate() throws IllegalArgumentException {
			boolean hashKeyExists = false;
			boolean rangeKeyExists = false;
			for (final DynamodbAttributeField field : fields) {
				if (field.isHashKey) {
					if (hashKeyExists) {
						throw new IllegalArgumentException(
							"it must have only one hash-key specifier, but that appears multiple times");
					}
					hashKeyExists = true;
				}

				if (field.isRangeKey) {
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

	private Stream<File> handleMessage(
		final DescriptorProto messageDescriptor,
		final String fileName,
		final String protoPackage,
		final String javaPackage,
		final Options opts
	) {
		String javaPackagePath =
			javaPackage.isEmpty() ? "" : javaPackage.replace(".", DIR_SEPARATOR) + DIR_SEPARATOR;
		String protoPackagePath = protoPackage.isEmpty() ? "" : protoPackage + ".";

		final String outerClassName = messageDescriptor.getName();

		String filePath = javaPackagePath + fileName + JAVA_EXTENSION;
		String fullMethodName = protoPackagePath + outerClassName;

		final DynamodbAttributeFields fields = new DynamodbAttributeFields(
			messageDescriptor.getFieldList()
							 .stream()
							 .filter(field -> !opts.getIgnoreFieldNames().contains(field.getName()))
							 .map(field -> DynamodbAttributeField.from(field, opts).validate())
							 .collect(Collectors.toList())
		).validate();

		final String attributesCode = fields.getFields().stream()
											.map(this::buildAttributesCode)
											.filter(Optional::isPresent)
											.map(Optional::get)
											.collect(Collectors.collectingAndThen(
												Collectors.joining(NEWLINE),
												Optional::of
											))
											.filter(value -> !value.isEmpty()).orElse("");
		final String dynamodbInnerClassName = outerClassName + "DynamoDBEntity"; // TODO replacable

		final String dynamoDBEntityInnerClassCode = buildDynamoDBEntityInnerClassCode(
			opts.tableName,
			dynamodbInnerClassName,
			attributesCode,
			buildNoArgConstructorCode(dynamodbInnerClassName),
			buildAllArgsConstructorCode(dynamodbInnerClassName, fields),
			buildToOuterClassMethodCode(outerClassName, fields)
		);

		return Stream.of(File.newBuilder()
							 .setName(filePath)
							 .setContent(dynamoDBEntityInnerClassCode + "\n" + buildToDynamodbEntityCode(dynamodbInnerClassName, fields) + "\n")
							 .setInsertionPoint(CLASS_SCOPE + fullMethodName)
							 .build());
	}

	private Optional<String> buildAttributesCode(final DynamodbAttributeField field) {
		// hasFieldPresence(fieldDescriptor) // TODO

		String javaTypeName = getJavaTypeName(field.getDescriptor());
		Map<?, ?> context = ImmutableMap.builder()
										.put("javaFieldType", javaTypeName)
										.put("javaFieldName", field.getFieldName())
										.put(
											"javaMethodName",
											getJavaMethodName(field.getFieldName())
										)
										.put("isHashKey", field.isHashKey())
										.put("isRangeKey", field.isRangeKey())
										.put("gsiHashKeyIndices", field.getGsiHashKeyIndices())
										.put("gsiRangeKeyIndices", field.getGsiRangeKeyIndices())
										.build();
		return Optional.of(applyTemplate(templatePath("attribute.mustache"), context));
	}

	private String buildNoArgConstructorCode(final String dynamodbInnerClassName) {
		return "  public " + dynamodbInnerClassName + "() {}\n";
	}

	private String buildAllArgsConstructorCode(final String dynamodbInnerClassName, final DynamodbAttributeFields fields) {
		final List<String> arguments = new ArrayList<>();
		final StringBuilder bindCode = new StringBuilder();

		for (final DynamodbAttributeField field : fields.getFields()) {
			final String fieldName = field.getFieldName();

			final String javaTypeName = getJavaTypeName(field.getDescriptor());
			arguments.add("final " + javaTypeName + " " + fieldName);

			bindCode.append("    this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
		}

		return "  public " + dynamodbInnerClassName + "(" + String.join(", ", arguments) + ") {\n" + bindCode + "  }\n";
	}

	private String buildToOuterClassMethodCode(final String outerClassName, final DynamodbAttributeFields fields) {
		return "  public " + outerClassName + " to" + outerClassName + "() {\n" +
			"    new " + outerClassName + ".Builder()." + fields.getFields().stream().map(f -> "set"+getJavaMethodName(f.getFieldName())+"(this."+f.getFieldName()+")").collect(Collectors.joining(".")) + ".build();\n" +
			"  }\n";
	}

	private String buildDynamoDBEntityInnerClassCode(final String tableName, final String dynamodbInnerClassName, final String attributesCode, final String noArgConstructorCode, final String allArgsConstructorCode, final String toOuterClassCode) {
		return "@com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable(tableName = \"" + tableName + "\")\n" +
			"public static class " + dynamodbInnerClassName + " {\n" +
			attributesCode + "\n" +
			noArgConstructorCode + "\n" +
			allArgsConstructorCode + "\n" +
			toOuterClassCode +
			"\n}\n";
	}

	private String buildToDynamodbEntityCode(final String dynamodbInnerClassName, final DynamodbAttributeFields fields) {
		return "public " + dynamodbInnerClassName + " to" + dynamodbInnerClassName + "() {\n" +
			"  return new " + dynamodbInnerClassName + "(" + fields.getFields().stream().map(f -> "this." + f.getFieldName()).collect(Collectors.joining(", ")) + ");\n" +
			"}\n";
	}

	private static final String TEMPLATES_DIRECTORY = "templates" + DIR_SEPARATOR;

	private static String templatePath(String path) {
		return TEMPLATES_DIRECTORY + path;
	}

	private String getFileName(FileDescriptorProto fileDescriptor,
							   DescriptorProto messageDescriptor
	) {
		if (fileDescriptor.getOptions().getJavaMultipleFiles()) {
			return messageDescriptor.getName();
		}
		if (fileDescriptor.getOptions().hasJavaOuterClassname()) {
			return fileDescriptor.getOptions().getJavaOuterClassname();
		}
		String protoPackage = fileDescriptor.hasPackage() ? "." + fileDescriptor.getPackage() : "";
		String protoTypeName = protoPackage + "." + messageDescriptor.getName();
		return Optional.ofNullable(protoTypeMap.toJavaTypeName(protoTypeName))
					   .map(javaType -> javaType.substring(0, javaType.lastIndexOf('.')))
					   .map(javaType -> javaType.substring(javaType.lastIndexOf('.') + 1))
					   .orElseThrow(() -> new IllegalArgumentException(
						   "Failed to find filename for proto '" + fileDescriptor.getName() + "'"));
	}

	private String getJavaMethodName(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
	}

	private String getJavaTypeName(FieldDescriptorProto fieldDescriptor) {
		String protoTypeName = fieldDescriptor.getTypeName();
		if (protoTypeName.isEmpty()) {
			return Optional.of(fieldDescriptor.getType())
						   .map(FieldDescriptor.Type::valueOf)
						   .map(FieldDescriptor.Type::getJavaType)
						   .map(PRIMITIVE_CLASSES::get)
						   .orElseThrow(() -> new IllegalArgumentException(
							   "Failed to find java type for field:\n" + fieldDescriptor));
		}
		return Optional.ofNullable(protoTypeMap.toJavaTypeName(protoTypeName))
					   .orElseThrow(() -> new IllegalArgumentException(
						   "Failed to find java type for prototype '" + protoTypeName + "'"));
	}
}
