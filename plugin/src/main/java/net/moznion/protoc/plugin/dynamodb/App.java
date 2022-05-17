package net.moznion.protoc.plugin.dynamodb;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.Feature;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import com.salesforce.jprotoc.Generator;
import com.salesforce.jprotoc.GeneratorException;
import com.salesforce.jprotoc.ProtoTypeMap;
import com.salesforce.jprotoc.ProtocPlugin;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App extends Generator {
	public static void main(String[] args) {
		ProtocPlugin.generate(new App());
	}

	private ProtoTypeMap protoTypeMap;

	@Override
	public List<File> generateFiles(CodeGeneratorRequest originalRequest)
		throws GeneratorException {

		// register the custom options extension
		final CodeGeneratorRequest request;
		try {
			final ExtensionRegistry registry = ExtensionRegistry.newInstance();
			OptionsProto.registerAllExtensions(registry);
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
								  Options.from(file.getOptions().getExtension(OptionsProto.fileopt));
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
		return Collections.singletonList(Feature.FEATURE_NONE);
	}

	private Stream<File> handleProtoFile(final FileDescriptorProto fileDescriptor,
										 final Options opts
	) {
		final String protoPackage = fileDescriptor.getPackage();
		final String javaPackage = fileDescriptor.getOptions().hasJavaPackage()
			? fileDescriptor.getOptions().getJavaPackage()
			: protoPackage;

		final CodeGenerator codeGenerator = new CodeGenerator(this::applyTemplate);

		return fileDescriptor.getMessageTypeList().stream()
							 .flatMap(
								 descriptor -> codeGenerator.generateDynamodbEntityCode(
									 descriptor,
									 getFileName(fileDescriptor, descriptor),
									 protoPackage,
									 javaPackage,
									 opts
								 ));
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
		final String protoPackage = fileDescriptor.hasPackage() ? "." + fileDescriptor.getPackage() : "";
		final String protoTypeName = protoPackage + "." + messageDescriptor.getName();
		return Optional.ofNullable(protoTypeMap.toJavaTypeName(protoTypeName))
					   .map(javaType -> javaType.substring(0, javaType.lastIndexOf('.')))
					   .map(javaType -> javaType.substring(javaType.lastIndexOf('.') + 1))
					   .orElseThrow(() -> new IllegalArgumentException(
						   "failed to find filename for proto '" + fileDescriptor.getName() + "'"));
	}
}
