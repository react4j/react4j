package react4j.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

@SuppressWarnings("DuplicatedCode")
final class BuilderGenerator {

    private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get("arez", "Disposable");

    private static final ClassName IDENTIFIABLE_CLASSNAME = ClassName.get("arez.component", "Identifiable");

    private static final ClassName REACT_ELEMENT_CLASSNAME = ClassName.get("react4j", "ReactElement");

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("react4j", "Context");

    private static final ClassName CONTEXT_RENDER_FUNCTION_CLASSNAME =
            ClassName.get("react4j", "Context", "ConsumerRenderFunction");

    private static final ClassName CONTEXTS_CLASSNAME = ClassName.get("react4j", "Contexts");

    private static final ClassName REACT_CLASSNAME = ClassName.get("react4j", "React");

    private static final ClassName KEYED_CLASSNAME = ClassName.get("react4j", "Keyed");

    private static final ClassName REACT_NODE_CLASSNAME = ClassName.get("react4j", "ReactNode");

    private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get("jsinterop.base", "JsPropertyMap");

    private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME = ParameterizedTypeName.get(
            JS_PROPERTY_MAP_CLASSNAME, Nullability.nullable(ClassName.get("java.lang", "Object")));

    private static final ClassName CONTRACT_CLASSNAME = ClassName.get("org.jetbrains.annotations", "Contract");

    private static final String CONTEXT_METHOD_PREFIX = "$context_";

    private static final String CONTEXT_FIELD_PREFIX = "_" + CONTEXT_METHOD_PREFIX;

    private static final String CONTEXT_INPUT_PREFIX = "CONTEXT_";

    private static final String CONTEXT_HOLDER = "ContextHolder";

    private BuilderGenerator() {}

    static TypeSpec buildType(final ProcessingEnvironment processingEnv, final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder(descriptor.getBuilderClassName());
        builder.addAnnotation(ClassName.get("org.jspecify.annotations", "NullMarked"));
        GeneratorUtil.addOriginatingTypes(descriptor.getElement(), builder);
        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, React4jProcessor.class.getName());
        builder.addModifiers(Modifier.FINAL);
        if (descriptor.exportBuilder()) {
            builder.addModifiers(Modifier.PUBLIC);
        }
        GeneratorUtil.copyWhitelistedAnnotations(
                descriptor.getElement(), builder, Collections.singletonList(Deprecated.class.getName()));

        // Builder signatures mirror view contracts, including concrete collection types.
        final var concreteCollections = descriptor.getInputs().stream()
                .anyMatch(input -> input.getType() instanceof DeclaredType type
                        && (type.asElement().toString().equals("java.util.ArrayList")
                                || type.asElement().toString().equals("java.util.HashSet")));
        final var suppression = SuppressWarningsUtil.maybeSuppressWarningsAnnotation(
                concreteCollections ? "NonApiType" : null,
                descriptor.builderAccessesDeprecatedElements() ? "deprecation" : null);
        if (suppression != null) {
            builder.addAnnotation(suppression);
        }

        // Private constructor so can not instantiate
        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        final var builderDescriptor = buildBuilderDescriptor(descriptor);
        InputEnhancer.validateBuilder(processingEnv, descriptor, builderDescriptor);

        final var steps = builderDescriptor.getSteps();

        builder.addMethod(buildStaticNewBuilderMethod(descriptor));

        for (final var step : steps) {
            builder.addType(buildBuilderStepInterface(processingEnv, descriptor, step));
        }

        // first step which may be required input, optional inputs, or build terminal step.
        buildStaticStepMethodMethods(processingEnv, descriptor, builder, steps.get(0));

        if (descriptor.getInputs().stream().anyMatch(InputDescriptor::isFromTreeContext)) {
            builder.addType(buildContextHolder(descriptor));
        }

        builder.addType(buildBuilder(processingEnv, descriptor, builderDescriptor));

        return builder.build();
    }

    private record StaticHelper(InputEnhancer enhancer, ExecutableElement method, TypeName result) {}

    private static void buildStaticStepMethodMethods(
            final ProcessingEnvironment processingEnv,
            final ViewDescriptor descriptor,
            final TypeSpec.Builder builder,
            final Step step) {
        for (final var method : step.getMethods()) {
            builder.addMethod(buildStaticStepMethodMethod(processingEnv, descriptor, step, method));
        }
        final var helpers = new LinkedHashMap<String, StaticHelper>();
        for (final var stepMethod : step.getMethods()) {
            final var input = stepMethod.getInput();
            final var enhancer = null == input ? null : input.getEnhancer();
            if (null != enhancer) {
                for (final var helper : enhancer.getHelpers()) {
                    final var result = stepReturnType(descriptor, step, stepMethod.getStepMethodType());
                    final var key = enhancer.signature(processingEnv.getTypeUtils(), helper, result.toString());
                    final var previous = helpers.get(key);
                    if (null == previous
                            || processingEnv
                                    .getElementUtils()
                                    .overrides(helper, previous.method(), enhancer.getElement())) {
                        helpers.put(key, new StaticHelper(enhancer, helper, result));
                    }
                }
            }
        }
        for (final var entry : helpers.values()) {
            builder.addMethod(EnhancerMethod.shortcut(
                    processingEnv, descriptor, entry.enhancer(), entry.method(), entry.result()));
        }
    }

    private static MethodSpec buildStaticNewBuilderMethod(final ViewDescriptor descriptor) {
        final var infix = descriptor.getDeclaredType().getTypeArguments().isEmpty() ? "" : "<>";
        final var method = MethodSpec.methodBuilder("newBuilder")
                .addModifiers(Modifier.STATIC)
                .returns(parameterizeIfRequired(descriptor, ClassName.bestGuess("Step1")))
                .addStatement("return new $T" + infix + "()", ClassName.bestGuess("Builder"));
        if (descriptor.exportBuilder()) {
            method.addModifiers(Modifier.PUBLIC);
        }
        GeneratorUtil.copyTypeParameters(descriptor.getElement(), method);
        return method.build();
    }

    private static MethodSpec buildStaticStepMethodMethod(
            final ProcessingEnvironment processingEnv,
            final ViewDescriptor descriptor,
            final Step step,
            final StepMethod stepMethod) {
        final var method = MethodSpec.methodBuilder(stepMethod.getName());
        addPureContract(method);

        method.addModifiers(Modifier.STATIC);
        if (descriptor.exportBuilder()) {
            method.addModifiers(Modifier.PUBLIC);
        }
        if (!stepMethod.isBuildIntrinsic()) {
            GeneratorUtil.copyTypeParameters(descriptor.getElement(), method);
        }

        if (stepMethod.isBuildIntrinsic()) {
            method.addStatement("return newBuilder().build()");
        } else {
            final var type = stepMethod.getType();
            final var parameter = ParameterSpec.builder(type, stepMethod.getName(), Modifier.FINAL);
            final var inputElement = stepMethod.getElement();
            if (null != inputElement) {
                GeneratorUtil.copyWhitelistedAnnotations(inputElement, method);
                Nullability.preserveScope(inputElement, method);
                final var inputType = stepMethod.getTypeMirror();
                assert null != inputType;
                SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, parameter, inputType);
            }
            if (type instanceof ArrayTypeName) {
                method.varargs();
            }
            method.addParameter(parameter.build());

            final var infix = asTypeArgumentsInfix(descriptor.getDeclaredType());
            if (infix.isEmpty()) {
                // No type parameters
                method.addStatement("return newBuilder().$N( $N )", stepMethod.getName(), stepMethod.getName());
            } else {
                method.addStatement(
                        "return $T." + infix + "newBuilder().$N( $N )",
                        descriptor.getBuilderClassName(),
                        stepMethod.getName(),
                        stepMethod.getName());
            }
        }
        configureStepMethodReturns(descriptor, method, step, stepMethod.getStepMethodType());
        return method.build();
    }

    private static MethodSpec.Builder buildStepInterfaceMethod(
            final ViewDescriptor descriptor,
            final String name,
            final Step step,
            final StepMethodType stepMethodType,
            final Consumer<MethodSpec.Builder> action) {
        final var method = MethodSpec.methodBuilder(name);
        method.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        addPureContract(method);
        action.accept(method);
        configureStepMethodReturns(descriptor, method, step, stepMethodType);
        return method;
    }

    private static void configureStepMethodReturns(
            final ViewDescriptor descriptor,
            final MethodSpec.Builder method,
            final Step step,
            final StepMethodType stepMethodType) {
        method.returns(stepReturnType(descriptor, step, stepMethodType));
    }

    static TypeName stepReturnType(final ViewDescriptor descriptor, final Step step, final StepMethodType type) {
        final var returnIndex = step.getIndex() + (StepMethodType.STAY == type ? 0 : 1);
        return StepMethodType.TERMINATE == type
                ? REACT_NODE_CLASSNAME
                : parameterizeIfRequired(descriptor, ClassName.bestGuess("Step" + returnIndex));
    }

    private static TypeName parameterizeIfRequired(final ViewDescriptor descriptor, final ClassName className) {
        final var variableNames = GeneratorUtil.getTypeArgumentsAsNames(descriptor.getDeclaredType());
        if (variableNames.isEmpty()) {
            return className;
        } else {
            return ParameterizedTypeName.get(className, variableNames.toArray(new TypeName[0]));
        }
    }

    private static TypeSpec buildBuilderStepInterface(
            final ProcessingEnvironment processingEnv, final ViewDescriptor descriptor, final Step step) {
        final var stepIndex = step.getIndex();
        final var builder = TypeSpec.interfaceBuilder("Step" + stepIndex);
        builder.addModifiers(Modifier.STATIC);
        if (descriptor.exportBuilder()) {
            builder.addModifiers(Modifier.PUBLIC);
        }
        builder.addTypeVariables(GeneratorUtil.getTypeArgumentsAsNames(descriptor.getDeclaredType()));

        if (!descriptor.getDeclaredType().getTypeArguments().isEmpty()) {
            builder.addAnnotation(SuppressWarningsUtil.suppressWarningsAnnotation("unused"));
        }

        for (final StepMethod stepMethod : step.getMethods()) {
            final var enhancedInput = stepMethod.getInput();
            final var enhancer = null == enhancedInput ? null : enhancedInput.getEnhancer();
            if (null != enhancer) {
                builder.addSuperinterface(
                        enhancer.superinterface(stepReturnType(descriptor, step, stepMethod.getStepMethodType())));
            }
            final var stepMethodType = stepMethod.getStepMethodType();
            // Magically handle the step method named build
            if (stepMethod.isBuildIntrinsic()) {
                builder.addMethod(buildStepInterfaceMethod(descriptor, "build", step, stepMethodType, m -> {})
                        .build());
            } else {
                builder.addMethod(buildStepInterfaceMethod(
                                descriptor, stepMethod.getName(), step, stepMethodType, m -> {
                                    final var inputMethod = stepMethod.getMethod();
                                    if (null != inputMethod) {
                                        final InputDescriptor input = stepMethod.getInput();
                                        assert null != input;
                                        final var inputMethodType = input.getMethodType();
                                        assert null != inputMethodType;
                                        GeneratorUtil.copyTypeParameters(inputMethodType, m);
                                    }
                                    if (stepMethod.isChildrenIntrinsic()) {
                                        m.varargs();
                                    }
                                    final var type = stepMethod.getType();
                                    if (type instanceof ArrayTypeName) {
                                        m.varargs();
                                    }
                                    final var parameter = ParameterSpec.builder(type, stepMethod.getName());
                                    final var inputElement = stepMethod.getElement();
                                    if (null != inputElement) {
                                        Nullability.preserveScope(inputElement, m);
                                        GeneratorUtil.copyWhitelistedAnnotations(inputElement, m);
                                        final var inputType = stepMethod.getTypeMirror();
                                        assert null != inputType;
                                        SuppressWarningsUtil.addSuppressWarningsIfRequired(
                                                processingEnv, parameter, inputType);
                                    }
                                    m.addParameter(parameter.build());
                                })
                        .build());
            }
        }

        return builder.build();
    }

    private static MethodSpec buildBuilderStepImpl(
            final ProcessingEnvironment processingEnv,
            final ViewDescriptor descriptor,
            final Step step,
            final StepMethod stepMethod) {
        final var method = MethodSpec.methodBuilder(stepMethod.getName());
        method.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        method.addAnnotation(Override.class);

        addPureContract(method);

        final InputDescriptor input = stepMethod.getInput();
        final ExecutableType inputMethodType = null != input && input.isMethodInput() ? input.getMethodType() : null;
        if (null != inputMethodType) {
            GeneratorUtil.copyTypeParameters(inputMethodType, method);
        }
        final var type = stepMethod.getType();
        final var parameter = ParameterSpec.builder(type, stepMethod.getName(), Modifier.FINAL);
        if (type instanceof ArrayTypeName) {
            method.varargs();
        }
        final var inputElement = stepMethod.getElement();
        if (null != inputElement) {
            GeneratorUtil.copyWhitelistedAnnotations(inputElement, method);
            Nullability.preserveScope(inputElement, method);
            final var inputType = stepMethod.getTypeMirror();
            assert null != inputType;
            SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, parameter, inputType);
        }
        method.addParameter(parameter.build());

        if (null != input && input.isImmutable() && 1 == descriptor.syntheticKeyParts()) {
            final var strategy = input.getImmutableInputKeyStrategy();
            if (ImmutableInputKeyStrategy.KEYED == strategy) {
                method.addStatement(
                        "_element.setKey( $T.getKey( $N ) + " + "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        KEYED_CLASSNAME,
                        stepMethod.getName(),
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            } else if (ImmutableInputKeyStrategy.IS_STRING == strategy
                    || ImmutableInputKeyStrategy.TO_STRING == strategy
                    || ImmutableInputKeyStrategy.ENUM == strategy) {
                method.addStatement(
                        "_element.setKey( $N + ( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        stepMethod.getName(),
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            } else if (ImmutableInputKeyStrategy.DYNAMIC == strategy) {
                final var keyValue = "$" + stepMethod.getName() + "$";
                method.addStatement("final $T $N = $N", Object.class, keyValue, stepMethod.getName());
                method.addStatement(
                        "_element.setKey( ( $N instanceof $T ? $T.getKey( $N ) : "
                                + "$N instanceof $T ? $T.<$T>getArezId( $N ) : $T.valueOf( $N ) ) + "
                                + "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        keyValue,
                        KEYED_CLASSNAME,
                        KEYED_CLASSNAME,
                        keyValue,
                        keyValue,
                        IDENTIFIABLE_CLASSNAME,
                        IDENTIFIABLE_CLASSNAME,
                        Object.class,
                        keyValue,
                        String.class,
                        keyValue,
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            } else {
                assert ImmutableInputKeyStrategy.AREZ_IDENTIFIABLE == strategy;
                method.addStatement(
                        "_element.setKey( $T.<Object>getArezId( $N ) + "
                                + "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        IDENTIFIABLE_CLASSNAME,
                        stepMethod.getName(),
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            }
        }

        if (null != input && input.isDisposable()) {
            method.addStatement("assert $T.isNotDisposed( $N )", DISPOSABLE_CLASSNAME, stepMethod.getName());
        }
        if (stepMethod.isChildrenIntrinsic()) {
            method.varargs();
            assert null != input;
            method.addStatement(
                    "_element.input( $T.Inputs.$N, $N )",
                    descriptor.getEnhancedClassName(),
                    input.getConstantName(),
                    stepMethod.getName());
        } else if (stepMethod.isChildrenStreamIntrinsic()) {
            method.addStatement("children( $N.toArray( $T[]::new ) )", stepMethod.getName(), REACT_NODE_CLASSNAME);
        } else if (stepMethod.isChildIntrinsic()) {
            assert null != inputElement;
            assert null != input;
            method.addStatement(
                    "_element.input( $T.Inputs.$N, $T.of( $N ) )",
                    descriptor.getEnhancedClassName(),
                    input.getConstantName(),
                    REACT_NODE_CLASSNAME,
                    stepMethod.getName());
        } else {

            assert null != input;
            method.addStatement(
                    "_element.input( $T.Inputs.$N, $N )",
                    descriptor.getEnhancedClassName(),
                    input.getConstantName(),
                    stepMethod.getName());
        }

        if (StepMethodType.TERMINATE == stepMethod.getStepMethodType()) {
            method.addStatement("return build()");
        } else {
            method.addStatement("return this");
        }
        configureStepMethodReturns(descriptor, method, step, stepMethod.getStepMethodType());

        return method.build();
    }

    private static MethodSpec buildBuildStepImpl(final ViewDescriptor descriptor, final boolean exposed) {
        final var method = MethodSpec.methodBuilder("build")
                .addModifiers(exposed ? Modifier.PUBLIC : Modifier.PRIVATE, Modifier.FINAL);
        if (exposed) {
            method.addAnnotation(Override.class);
        }
        addPureContract(method);
        return buildBuildMethodContent(descriptor, method, "_element");
    }

    private static FieldSpec buildContextBuildField(final InputDescriptor current) {
        final var typeName = ParameterizedTypeName.get(
                CONTEXT_RENDER_FUNCTION_CLASSNAME,
                current.getTypeName().withoutAnnotations().box());
        return FieldSpec.builder(typeName, CONTEXT_FIELD_PREFIX + current.getName(), Modifier.PRIVATE, Modifier.FINAL)
                .initializer("this::$N", CONTEXT_METHOD_PREFIX + current.getName())
                .build();
    }

    private static MethodSpec buildContextBuildStepImpl(
            final ViewDescriptor descriptor, final InputDescriptor current, @Nullable final InputDescriptor next) {
        final var method = MethodSpec.methodBuilder(CONTEXT_METHOD_PREFIX + current.getName())
                .addModifiers(Modifier.PRIVATE)
                .returns(REACT_NODE_CLASSNAME)
                .addParameter(ParameterSpec.builder(
                                Nullability.nullable(current.getTypeName()), current.getName(), Modifier.FINAL)
                        .build())
                .addStatement(
                        "_element.input( $T.Inputs.$N, $N )",
                        descriptor.getEnhancedClassName(),
                        current.getConstantName(),
                        current.getName());
        if (null == next) {
            method.addStatement("return build( _element.dup() )");
        } else {
            method.addStatement(
                    "return $T.$N.consumer().render( $N )",
                    ClassName.bestGuess(CONTEXT_HOLDER),
                    CONTEXT_INPUT_PREFIX + next.getConstantName(),
                    CONTEXT_FIELD_PREFIX + next.getName());
        }

        return method.build();
    }

    private static MethodSpec buildContextBuildStepImpl(
            final InputDescriptor firstContextInput, final boolean exposed) {
        final var method = MethodSpec.methodBuilder("build")
                .addModifiers(exposed ? Modifier.PUBLIC : Modifier.PRIVATE, Modifier.FINAL)
                .returns(REACT_NODE_CLASSNAME)
                .addStatement(
                        "return $T.$N.consumer().render( $N )",
                        ClassName.bestGuess(CONTEXT_HOLDER),
                        CONTEXT_INPUT_PREFIX + firstContextInput.getConstantName(),
                        CONTEXT_FIELD_PREFIX + firstContextInput.getName());
        addPureContract(method);
        return method.build();
    }

    private static MethodSpec buildInternalBuildStepImpl(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(REACT_ELEMENT_CLASSNAME, "element", Modifier.FINAL)
                        .build());
        return buildBuildMethodContent(descriptor, method, "element");
    }

    private static MethodSpec buildBuildMethodContent(
            final ViewDescriptor descriptor, final MethodSpec.Builder method, final String elementName) {
        final var syntheticInputs = descriptor.getInputs().stream()
                .filter(InputDescriptor::isImmutable)
                .toList();
        if (syntheticInputs.size() > 1) {
            method.addStatement("final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, elementName);

            for (final var input : syntheticInputs) {
                if (ImmutableInputKeyStrategy.DYNAMIC == input.getImmutableInputKeyStrategy()) {
                    method.addStatement(
                            "final $T $N = inputs.get( $T.Inputs.$N )",
                            Object.class,
                            "$" + input.getName() + "$",
                            descriptor.getEnhancedClassName(),
                            input.getConstantName());
                }
            }

            final var sb = new StringBuilder();
            sb.append("$N.setKey( ");
            final var params = new ArrayList<>();
            params.add(elementName);
            boolean firstInput = true;
            for (final var input : syntheticInputs) {
                if (!firstInput) {
                    sb.append(" + \"-\" + ");
                } else {
                    firstInput = false;
                }
                final var strategy = input.getImmutableInputKeyStrategy();
                if (ImmutableInputKeyStrategy.KEYED == strategy) {
                    sb.append("$T.getKey( ($T) inputs.get( $T.Inputs.$N ) )");
                    params.add(KEYED_CLASSNAME);
                    params.add(input.getType());
                    params.add(descriptor.getEnhancedClassName());
                    params.add(input.getConstantName());
                } else if (ImmutableInputKeyStrategy.IS_STRING == strategy
                        || ImmutableInputKeyStrategy.ENUM == strategy) {
                    sb.append("( ($T) inputs.get( $T.Inputs.$N ) )");
                    params.add(input.getType());
                    params.add(descriptor.getEnhancedClassName());
                    params.add(input.getConstantName());
                } else if (ImmutableInputKeyStrategy.TO_STRING == strategy) {
                    final var inputType = input.getType();
                    final var kind = inputType.getKind();
                    final var value = kind.isPrimitive()
                            ? "$T.requireNonNull( inputs.get( $T.Inputs.$N ) )"
                            : "inputs.get( $T.Inputs.$N )";
                    if (TypeKind.LONG == kind
                            || TypeKind.INT == kind
                            || TypeKind.SHORT == kind
                            || TypeKind.BYTE == kind
                            || TypeKind.FLOAT == kind) {
                        sb.append("$T.valueOf( ($T) (double) " + value + " )");
                    } else {
                        sb.append("$T.valueOf( ($T) " + value + " )");
                    }
                    params.add(String.class);
                    params.add(inputType);
                    if (kind.isPrimitive()) {
                        params.add(Objects.class);
                    }
                    params.add(descriptor.getEnhancedClassName());
                    params.add(input.getConstantName());
                } else if (ImmutableInputKeyStrategy.DYNAMIC == strategy) {
                    final var name = "$" + input.getName() + "$";
                    sb.append("( $N instanceof $T ? $T.getKey( $N ) : " + "$N instanceof $T ? $T.<$T>getArezId( $N ) : "
                            + "$T.valueOf( $N ) )");
                    params.add(name);
                    params.add(KEYED_CLASSNAME);
                    params.add(KEYED_CLASSNAME);
                    params.add(name);
                    params.add(name);
                    params.add(IDENTIFIABLE_CLASSNAME);
                    params.add(IDENTIFIABLE_CLASSNAME);
                    params.add(Object.class);
                    params.add(name);
                    params.add(String.class);
                    params.add(name);
                } else {
                    assert ImmutableInputKeyStrategy.AREZ_IDENTIFIABLE == strategy;
                    sb.append("$T.valueOf( $T.<$T>getArezId( ($T) inputs.get( $T.Inputs.$N ) ) )");
                    params.add(String.class);
                    params.add(IDENTIFIABLE_CLASSNAME);
                    params.add(Object.class);
                    params.add(input.getType());
                    params.add(descriptor.getEnhancedClassName());
                    params.add(input.getConstantName());
                }
            }
            sb.append(" + ( $T.enableViewNames() ? $S : $T.class.getName() ) )");
            params.add(REACT_CLASSNAME);
            params.add(descriptor.keySuffix());
            params.add(descriptor.getClassName());
            method.addStatement(sb.toString(), params.toArray());
        }

        method.addStatement("return $N", elementName).returns(REACT_NODE_CLASSNAME);
        return method.build();
    }

    private static TypeSpec buildContextHolder(final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder(CONTEXT_HOLDER);
        GeneratorUtil.copyTypeParameters(descriptor.getElement(), builder);

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC);

        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        final var fromTreeContextInputs = descriptor.getInputs().stream()
                .filter(InputDescriptor::isFromTreeContext)
                .toList();

        for (final var input : fromTreeContextInputs) {
            final var type = input.getTypeName().withoutAnnotations().box();
            final var field = FieldSpec.builder(
                    ParameterizedTypeName.get(CONTEXT_CLASSNAME, type),
                    CONTEXT_INPUT_PREFIX + input.getConstantName(),
                    Modifier.PRIVATE,
                    Modifier.STATIC,
                    Modifier.FINAL);
            final var qualifier = input.getQualifier();
            if (qualifier.isEmpty()) {
                field.initializer("$T.get( $T.class )", CONTEXTS_CLASSNAME, type.withoutAnnotations());
            } else {
                field.initializer("$T.get( $T.class, $S )", CONTEXTS_CLASSNAME, type.withoutAnnotations(), qualifier);
            }
            builder.addField(field.build());
        }

        return builder.build();
    }

    private static TypeSpec buildBuilder(
            final ProcessingEnvironment processingEnv,
            final ViewDescriptor descriptor,
            final BuilderDescriptor builderDescriptor) {
        final var builder = TypeSpec.classBuilder("Builder");
        GeneratorUtil.copyTypeParameters(descriptor.getElement(), builder);
        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC);
        final var steps = builderDescriptor.getSteps();
        for (var i = 0; i < steps.size(); i++) {
            builder.addSuperinterface(getParameterizedTypeName(descriptor, ClassName.bestGuess("Step" + (i + 1))));
        }

        final var inputsWithDefaults = descriptor.getInputs().stream()
                .filter(p -> p.hasDefaultField() || p.hasDefaultMethod())
                .toList();
        if (!inputsWithDefaults.isEmpty()) {
            final var method = MethodSpec.constructorBuilder();
            method.addStatement(
                    "_element = $T.createViewElement( $T.Factory.TYPE )",
                    REACT_ELEMENT_CLASSNAME,
                    descriptor.getEnhancedClassName());
            method.addStatement("final $T inputs = _element.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME);
            for (final var input : inputsWithDefaults) {
                method.addStatement(
                        "inputs.set( $T.Inputs.$N, $T.$N" + (input.hasDefaultField() ? "" : "()") + " )",
                        descriptor.getEnhancedClassName(),
                        input.getConstantName(),
                        descriptor.getClassName(),
                        input.hasDefaultField()
                                ? input.getDefaultField().getSimpleName()
                                : input.getDefaultMethod().getSimpleName());
            }

            builder.addMethod(method.build());
        }

        final var stepMethodsAdded = new HashSet<String>();
        for (final var step : steps) {
            for (final var stepMethod : step.getMethods()) {
                if (stepMethodsAdded.add(stepMethod.getName() + stepMethod.getType())) {
                    if (!stepMethod.isBuildIntrinsic()) {
                        builder.addMethod(buildBuilderStepImpl(processingEnv, descriptor, step, stepMethod));
                    }
                }
            }
        }

        final var field = FieldSpec.builder(REACT_ELEMENT_CLASSNAME, "_element", Modifier.PRIVATE, Modifier.FINAL);
        if (inputsWithDefaults.isEmpty()) {
            field.initializer(
                    "$T.createViewElement( $T.Factory.TYPE )",
                    REACT_ELEMENT_CLASSNAME,
                    descriptor.getEnhancedClassName());
        }
        builder.addField(field.build());

        final var exposesBuild =
                steps.stream().flatMap(step -> step.getMethods().stream()).anyMatch(StepMethod::isBuildIntrinsic);
        final var fromTreeContextInputs = descriptor.getInputs().stream()
                .filter(InputDescriptor::isFromTreeContext)
                .toList();
        if (!fromTreeContextInputs.isEmpty()) {
            builder.addMethod(buildInternalBuildStepImpl(descriptor));
            builder.addMethod(buildContextBuildStepImpl(fromTreeContextInputs.get(0), exposesBuild));
            final var size = fromTreeContextInputs.size();
            for (var i = 0; i < size; i++) {
                final var current = fromTreeContextInputs.get(i);
                final var next = i == size - 1 ? null : fromTreeContextInputs.get(i + 1);
                builder.addField(buildContextBuildField(current));
                builder.addMethod(buildContextBuildStepImpl(descriptor, current, next));
            }
        } else {
            builder.addMethod(buildBuildStepImpl(descriptor, exposesBuild));
        }

        return builder.build();
    }

    private static TypeName getParameterizedTypeName(final ViewDescriptor descriptor, final ClassName baseName) {
        final var arguments = descriptor.getDeclaredType().getTypeArguments();
        if (arguments.isEmpty()) {
            return baseName;
        } else {
            return ParameterizedTypeName.get(
                    baseName, arguments.stream().map(GeneratorUtil::getTypeName).toArray(TypeName[]::new));
        }
    }

    private static void addPureContract(final MethodSpec.Builder method) {
        method.addAnnotation(AnnotationSpec.builder(CONTRACT_CLASSNAME)
                .addMember("pure", "true")
                .build());
    }

    static String asTypeArgumentsInfix(final DeclaredType declaredType) {
        final var typeArguments = declaredType.getTypeArguments();
        return typeArguments.isEmpty()
                ? ""
                : "<" + typeArguments.stream().map(TypeMirror::toString).collect(Collectors.joining(", ")) + ">";
    }

    private static BuilderDescriptor buildBuilderDescriptor(final ViewDescriptor descriptor) {
        final var builder = new BuilderDescriptor();

        Step optionalInputStep = null;
        final var inputs = descriptor.getInputs().stream()
                .filter(p -> !p.isFromTreeContext())
                .toList();

        final var inputsSize = inputs.size();

        final var hasSingleOptional =
                inputs.stream().filter(InputDescriptor::isOptional).count() == 1;
        final var hasNonOptionalChild = inputs.stream()
                        .filter(i -> i.isSpecialChildrenInput() && !i.isOptional())
                        .count()
                == 1;
        var hasRequiredAfterOptional = false;
        for (var i = 0; i < inputsSize; i++) {
            final var input = inputs.get(i);
            final var isLast = i == inputsSize - 1;
            if (input.isOptional()) {
                if (null == optionalInputStep) {
                    optionalInputStep = builder.addStep();
                }
                if (input.getName().equals("children")) {
                    addChildrenStreamInputStepMethod(optionalInputStep);
                }
                if (hasNonOptionalChild) {
                    optionalInputStep.addMethod(
                            input, hasSingleOptional ? StepMethodType.ADVANCE : StepMethodType.STAY);
                } else {
                    optionalInputStep.addMethod(
                            input, hasSingleOptional ? StepMethodType.TERMINATE : StepMethodType.STAY);
                }
            } else {
                if (null != optionalInputStep) {
                    // Need this when we have children magic input that is required that follows the optional inputs.
                    optionalInputStep.addMethod(input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE);
                    // This is when children are built up using child steps
                    if (input.getName().equals("children")) {
                        addChildrenStreamInputStepMethod(optionalInputStep);
                    }
                    hasRequiredAfterOptional = true;
                }
                // Single method step
                final var step = builder.addStep();
                step.addMethod(input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE);
                if (input.getName().equals("children")) {
                    addChildrenStreamInputStepMethod(step);
                    addBuildStep(step);
                }
            }
        }
        if (null != optionalInputStep && !hasRequiredAfterOptional) {
            addBuildStep(optionalInputStep);
        }
        if (inputs.isEmpty()) {
            addBuildStep(builder.addStep());
        }

        return builder;
    }

    /**
     * Setup the "build" intrinsic step.
     */
    private static void addBuildStep(final Step step) {
        step.addTerminalMethod("build", "build", REACT_NODE_CLASSNAME);
    }

    /**
     * A helper intrinsic that converts children streams.
     */
    private static void addChildrenStreamInputStepMethod(final Step step) {
        final ParameterizedTypeName typeName = ParameterizedTypeName.get(
                ClassName.get(Stream.class), WildcardTypeName.subtypeOf(REACT_NODE_CLASSNAME));

        // TODO: Replace this with input enhancer
        step.addTerminalMethod("children", "*children_stream*", typeName);
    }
}
