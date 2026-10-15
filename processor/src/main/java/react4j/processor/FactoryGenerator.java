package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.SuppressWarningsUtil;

final class FactoryGenerator {

    private static final ClassName NATIVE_VIEW_CLASSNAME = ClassName.get("react4j.internal", "NativeView");

    private static final ClassName GUARDS_CLASSNAME = ClassName.get("org.realityforge.braincheck", "Guards");

    private static final ClassName REACT_CLASSNAME = ClassName.get("react4j", "React");

    private static final ClassName JS_CLASSNAME = ClassName.get("jsinterop.base", "Js");

    private FactoryGenerator() {}

    static TypeSpec buildType(final ProcessingEnvironment processingEnv, final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder(descriptor.getFactoryClassName());
        builder.addAnnotation(ClassName.get("org.jspecify.annotations", "NullMarked"));
        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, React4jProcessor.class.getName());
        GeneratorUtil.addOriginatingTypes(descriptor.getElement(), builder);

        builder.addModifiers(Modifier.FINAL);

        if (descriptor.enableSting()) {
            builder.addAnnotation(ClassName.bestGuess(Constants.STING_INJECTABLE_CLASSNAME));
            builder.addAnnotation(ClassName.bestGuess(Constants.STING_EAGER_CLASSNAME));
            GeneratorUtil.copyWhitelistedAnnotations(
                    descriptor.getElement(),
                    builder,
                    Arrays.asList(Constants.STING_NAMED_CLASSNAME, Constants.STING_CONTRIBUTE_TO_CLASSNAME));
        }

        final var constructor =
                ElementsUtil.getConstructors(descriptor.getElement()).get(0);

        buildFields(processingEnv, builder, descriptor, constructor);
        buildConstructor(processingEnv, descriptor, builder, constructor);
        buildCreateMethod(descriptor, builder);
        builder.addType(buildInjectSupport(descriptor));

        return builder.build();
    }

    private static void buildConstructor(
            final ProcessingEnvironment processingEnv,
            final ViewDescriptor descriptor,
            final TypeSpec.Builder builder,
            final ExecutableElement constructor) {
        final var ctor = MethodSpec.constructorBuilder();

        final var additionalSuppressions = new ArrayList<String>();

        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                processingEnv, ctor, additionalSuppressions, Collections.singletonList(constructor.asType()));
        GeneratorUtil.copyWhitelistedAnnotations(constructor, ctor);

        final var whitelistedAnnotations = new ArrayList<>(GeneratorUtil.ANNOTATION_WHITELIST);
        whitelistedAnnotations.add("sting.Named");
        whitelistedAnnotations.add("javax.inject.Named");
        for (final var parameter : descriptor.getInjectableConstructorParameters()) {
            final var name = parameter.getSimpleName().toString();
            final var param =
                    ParameterSpec.builder(GeneratorUtil.getTypeName(parameter.asType()), name, Modifier.FINAL);

            GeneratorUtil.copyWhitelistedAnnotations(parameter, param, whitelistedAnnotations);
            ctor.addParameter(param.build());
            ctor.addStatement("this.$N = $N", name, name);
        }

        Nullability.preserveScope(constructor, ctor);
        ctor.addStatement("InjectSupport.setFactory( this )");

        builder.addMethod(ctor.build());
    }

    private static void buildFields(
            final ProcessingEnvironment processingEnv,
            final TypeSpec.Builder builder,
            final ViewDescriptor descriptor,
            final ExecutableElement constructor) {
        for (final var parameter : descriptor.getInjectableConstructorParameters()) {
            final var field = FieldSpec.builder(
                    GeneratorUtil.getTypeName(parameter.asType()),
                    parameter.getSimpleName().toString(),
                    Modifier.PRIVATE,
                    Modifier.FINAL);
            final var types = Arrays.asList(constructor.asType(), parameter.asType());
            SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, field, types);
            GeneratorUtil.copyWhitelistedAnnotations(parameter, field);
            builder.addField(field.build());
        }
    }

    private static void buildCreateMethod(final ViewDescriptor descriptor, final TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.STATIC)
                .addParameter(ParameterSpec.builder(NATIVE_VIEW_CLASSNAME, "view", Modifier.FINAL)
                        .build())
                .returns(descriptor.getEnhancedClassName())
                .addStatement("return InjectSupport.create( view )")
                .build());
    }

    private static TypeSpec buildInjectSupport(final ViewDescriptor descriptor) {
        // We create a separate InjectSupport class to avoid generating a clinit for the factory class
        // which can block optimizations in GWT
        final var builder =
                TypeSpec.classBuilder("InjectSupport").addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

        builder.addField(buildFactoryField(descriptor).build());
        builder.addMethod(buildSetFactoryMethod(descriptor).build());
        builder.addMethod(buildInjectCreateMethod(descriptor).build());

        return builder.build();
    }

    private static FieldSpec.Builder buildFactoryField(final ViewDescriptor descriptor) {
        return FieldSpec.builder(descriptor.getFactoryClassName(), "c_factory", Modifier.STATIC, Modifier.PRIVATE);
    }

    private static MethodSpec.Builder buildSetFactoryMethod(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("setFactory")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(descriptor.getFactoryClassName(), "factory", Modifier.FINAL)
                        .build());
        final var block = CodeBlock.builder();
        block.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
        block.addStatement(
                "$T.invariant( () -> null == c_factory, () -> \"Attempted to instantiate the React4j "
                        + "view factory for the view named '$N' a second time\" )",
                GUARDS_CLASSNAME,
                descriptor.getName());
        block.endControlFlow();
        method.addCode(block.build());
        method.addStatement("c_factory = factory");
        return method;
    }

    private static MethodSpec.Builder buildInjectCreateMethod(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(NATIVE_VIEW_CLASSNAME, "view", Modifier.FINAL)
                        .build())
                .returns(descriptor.getEnhancedClassName());
        final var block = CodeBlock.builder();
        block.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
        block.addStatement(
                "$T.invariant( () -> null != c_factory, () -> \"Attempted to create an instance of the React4j "
                        + "view named '$N' before the view factory has been initialized. Please see "
                        + "the documentation at https://react4j.github.io/dependency_injection for directions how to "
                        + "configure dependency injection.\" )",
                GUARDS_CLASSNAME,
                descriptor.getName());
        block.endControlFlow();
        method.addCode(block.build());
        for (final var input : descriptor.getConstructorInputs()) {
            final var parameter = input.getParameter();
            assert null != parameter;
            final var localName = parameter.getSimpleName().toString();
            final var convertMethodName = getConverter(input);
            if (!input.getType().getKind().isPrimitive() && !input.isNonNull()) {
                final var extraction = CodeBlock.builder();
                extraction.addStatement("$T $N", GeneratorUtil.getTypeName(input.getType()), localName);
                extraction.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
                extraction.addStatement(
                        "$N = null != view.requireInputs().getAsAny( $T.Inputs.$N ) ? "
                                + "view.requireInputs().getAsAny( $T.Inputs.$N ).$N() : null",
                        localName,
                        descriptor.getEnhancedClassName(),
                        input.getConstantName(),
                        descriptor.getEnhancedClassName(),
                        input.getConstantName(),
                        convertMethodName);
                extraction.nextControlFlow("else");
                extraction.addStatement(
                        "$N = $T.uncheckedCast( view.requireInputs().getAsAny( $T.Inputs.$N ) )",
                        localName,
                        JS_CLASSNAME,
                        descriptor.getEnhancedClassName(),
                        input.getConstantName());
                extraction.endControlFlow();
                method.addCode(extraction.build());
            } else {
                method.addStatement(
                        "final $T $N = view.requireInputs().getAsAny( $T.Inputs.$N ).$N()",
                        GeneratorUtil.getTypeName(input.getType()),
                        localName,
                        descriptor.getEnhancedClassName(),
                        input.getConstantName(),
                        convertMethodName);
            }
        }

        final var constructor =
                ElementsUtil.getConstructors(descriptor.getElement()).get(0);
        final var args = new StringBuilder("return new $T( view");
        final var params = new ArrayList<>();
        params.add(descriptor.getArezClassName());
        for (final VariableElement parameter : constructor.getParameters()) {
            args.append(", ");
            final var input = descriptor.getConstructorInputs().stream()
                    .filter(d -> parameter.equals(d.getParameter()))
                    .findAny()
                    .orElse(null);
            if (null != input) {
                args.append("$N");
                params.add(parameter.getSimpleName().toString());
            } else {
                args.append("c_factory.$N");
                params.add(parameter.getSimpleName().toString());
            }
        }
        args.append(" )");
        return method.addStatement(args.toString(), params.toArray());
    }

    private static String getConverter(final InputDescriptor input) {
        return switch (input.getType().getKind()) {
            case BOOLEAN -> "asBoolean";
            case BYTE -> "asByte";
            case CHAR -> "asChar";
            case DOUBLE -> "asDouble";
            case FLOAT -> "asFloat";
            case INT -> "asInt";
            case LONG -> "asLong";
            case SHORT -> "asShort";
            case TYPEVAR, ARRAY -> "cast";
            case DECLARED ->
                ((DeclaredType) input.getType()).asElement().toString().equals("java.lang.String")
                        ? "asString"
                        : "cast";
            default ->
                throw new ProcessorException(
                        "Return type of @Input method is not yet handled. Type: "
                                + input.getType().getKind(),
                        input.getElement());
        };
    }
}
