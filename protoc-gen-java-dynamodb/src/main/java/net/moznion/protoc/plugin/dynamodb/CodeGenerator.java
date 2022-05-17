package net.moznion.protoc.plugin.dynamodb;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.compiler.PluginProtos;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;

@Value
class CodeGenerator {
  private static final String NEWLINE = "\n";
  private static final String DIR_SEPARATOR = "/";
  private static final String JAVA_EXTENSION = ".java";
  private static final String CLASS_SCOPE = "class_scope:";
  private static final String TEMPLATES_DIRECTORY = "templates" + DIR_SEPARATOR;
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

  BiFunction<String, Map<String, ?>, String> applyTemplate;

  Stream<PluginProtos.CodeGeneratorResponse.File> generateDynamodbEntityCode(
      final DescriptorProtos.DescriptorProto messageDescriptor,
      final String fileName,
      final String protoPackage,
      final String javaPackage,
      final FileOptions opts) {
    final DynamodbAttributeFields fields =
        new DynamodbAttributeFields(
                messageDescriptor.getFieldList().stream()
                    .filter(
                        field ->
                            !field
                                .getOptions()
                                .getExtension(OptionsProto.fieldopt)
                                .getJavaDynamodbIgnore())
                    .map(field -> DynamodbAttributeField.from(field).validate())
                    .collect(Collectors.toList()))
            .validate();

    final String dynamodbInnerClassName =
        new DynamodbEntityClassNameProvider(opts).getDynamodbEntityClassName();

    final String javaPackagePath =
        javaPackage.isEmpty() ? "" : javaPackage.replace(".", DIR_SEPARATOR) + DIR_SEPARATOR;
    final String protoPackagePath = protoPackage.isEmpty() ? "" : protoPackage + ".";
    final String outerClassName = messageDescriptor.getName();
    final String filePath = javaPackagePath + fileName + JAVA_EXTENSION;
    final String fullMethodName = protoPackagePath + outerClassName;

    final String dynamoDBEntityCode =
        buildDynamoDBEntityInnerClassCode(
            opts.getTableName(), outerClassName, dynamodbInnerClassName, fields);

    return Stream.of(
        PluginProtos.CodeGeneratorResponse.File.newBuilder()
            .setName(filePath)
            .setContent(dynamoDBEntityCode)
            .setInsertionPoint(CLASS_SCOPE + fullMethodName)
            .build());
  }

  private Optional<String> buildAttributesCode(final DynamodbAttributeField field) {
    return Optional.of(
        applyTemplate.apply(
            templatePath("attribute.mustache"),
            ImmutableMap.<String, Object>builder()
                .put("javaFieldType", field.getJavaTypeName())
                .put("javaFieldName", field.getFieldName())
                .put("javaMethodName", getJavaMethodName(field.getFieldName()))
                .put("fieldAliasName", field.getAliasName())
                .put("isHashKey", field.isHashKey())
                .put("isRangeKey", field.isRangeKey())
                .put(
                    "gsiHashKeyIndices",
                    field.getGsiHashKeyIndices().stream()
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.joining(", ")))
                .put(
                    "gsiRangeKeyIndices",
                    field.getGsiRangeKeyIndices().stream()
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.joining(", ")))
                .build()));
  }

  private String buildNoArgConstructorCode(final String dynamodbInnerClassName) {
    return "  public " + dynamodbInnerClassName + "() {}\n";
  }

  private String buildAllArgsConstructorCode(
      final String dynamodbInnerClassName, final DynamodbAttributeFields fields) {
    final List<String> arguments = new ArrayList<>();
    final List<String> fieldNames = new ArrayList<>();

    for (final DynamodbAttributeField field : fields.getItems()) {
      final String fieldName = field.getFieldName();
      arguments.add("final " + field.getJavaTypeName() + " " + fieldName);
      fieldNames.add(fieldName);
    }

    return applyTemplate.apply(
        templatePath("dynamodb_entity_all_args_constructor.mustache"),
        ImmutableMap.of(
            "dynamodbInnerClassName", dynamodbInnerClassName,
            "fieldNames", fieldNames,
            "arguments", String.join(", ", arguments)));
  }

  private String buildToOuterClassMethodCode(
      final String outerClassName, final DynamodbAttributeFields fields) {
    return applyTemplate.apply(
        templatePath("to_outer_class_method.mustache"),
        ImmutableMap.of(
            "outerClassName",
            outerClassName,
            "decorators",
            fields.getItems().stream()
                .map(
                    f -> {
                      final String accessorPrefix = f.isRepeated() ? "addAll" : "set";
                      final String fieldName = f.getFieldName();

                      return (Map<String, ?>)
                          ImmutableMap.of(
                              "accessor",
                              accessorPrefix + getJavaMethodName(fieldName),
                              "fieldName",
                              fieldName,
                              "isBytes",
                              f.isBytesArrayType());
                    })
                .collect(Collectors.toList())));
  }

  private String buildToDynamodbEntityCode(
      final String dynamodbInnerClassName, final DynamodbAttributeFields fields) {
    return applyTemplate.apply(
        templatePath("to_dynamodb_entity_method.mustache"),
        ImmutableMap.of(
            "dynamodbInnerClassName",
            dynamodbInnerClassName,
            "constructorArgs",
            fields.getItems().stream()
                .map(
                    f -> {
                      final String javaMethodName =
                          getJavaMethodName(f.getFieldName()) + (f.isRepeated() ? "List" : "");
                      final String extensionForBytes =
                          f.getJavaTypeName().equals(BYTES_ARRAY_TYPE) ? ".toByteArray()" : "";
                      return "this.get" + javaMethodName + "()" + extensionForBytes;
                    })
                .collect(Collectors.joining(", "))));
  }

  private String buildDynamoDBEntityInnerClassCode(
      final String tableName,
      final String outerClassName,
      final String dynamodbInnerClassName,
      final DynamodbAttributeFields fields) {
    final String attributesCode =
        fields.getItems().stream()
            .map(this::buildAttributesCode)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.collectingAndThen(Collectors.joining(NEWLINE), Optional::of))
            .filter(value -> !value.isEmpty())
            .orElse("");

    return applyTemplate.apply(
        templatePath("dynamodb_entity.mustache"),
        ImmutableMap.<String, Object>builder()
            .put("tableName", tableName)
            .put("dynamodbInnerClassName", dynamodbInnerClassName)
            .put("attributesCode", attributesCode)
            .put("noArgConstructorCode", buildNoArgConstructorCode(dynamodbInnerClassName))
            .put(
                "allArgsConstructorCode",
                buildAllArgsConstructorCode(dynamodbInnerClassName, fields))
            .put("toOuterClassCode", buildToOuterClassMethodCode(outerClassName, fields))
            .put("toDynamoDBEntityCode", buildToDynamodbEntityCode(dynamodbInnerClassName, fields))
            .build());
  }

  private static String templatePath(String path) {
    return TEMPLATES_DIRECTORY + path;
  }

  private String getJavaMethodName(String fieldName) {
    return fieldName.substring(0, 1).toUpperCase(Locale.ROOT) + fieldName.substring(1);
  }
}
