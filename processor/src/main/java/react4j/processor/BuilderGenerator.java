package react4j.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
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

    private static final ClassName INPUT_KEYS_CLASSNAME = ClassName.get("react4j.internal", "InputKeys");

    private static final ClassName ARRAY_KEY_STRATEGY_CLASSNAME = ClassName.get("react4j.internal", "ArrayKeyStrategy");

    private static final ClassName REACT_NODE_CLASSNAME = ClassName.get("react4j", "ReactNode");

    private static final ClassName RENDERABLE_CLASSNAME = ClassName.get("react4j", "Renderable");

    private static final ClassName BUILDABLE_CLASSNAME = ClassName.get("react4j", "Buildable");

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
                                && ("java.util.ArrayList"
                                                .equals(type.asElement().toString())
                                        || "java.util.HashSet"
                                                .equals(type.asElement().toString()))
                        || null != input.getEnhancer() && input.getEnhancer().usesConcreteCollections());
        final var suppression = SuppressWarningsUtil.maybeSuppressWarningsAnnotation(
                concreteCollections ? "NonApiType" : null,
                descriptor.builderAccessesDeprecatedElements()
                                || descriptor.getInputs().stream().anyMatch(input -> {
                                    final var enhancer = input.getEnhancer();
                                    return null != enhancer && enhancer.usesDeprecatedTypes();
                                })
                        ? "deprecation"
                        : null);
        if (suppression != null) {
            builder.addAnnotation(suppression);
        }

        // Private constructor so can not instantiate
        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        for (final var input : descriptor.getImmutableInputs()) {
            final var generator = input.getKeyGenerator();
            if (null != generator) {
                builder.addField(FieldSpec.builder(
                                generator.type(),
                                keyGeneratorField(input),
                                Modifier.PRIVATE,
                                Modifier.STATIC,
                                Modifier.FINAL)
                        .initializer("new $T()", ClassName.get(generator.element()))
                        .build());
            }
        }

        final var builderDescriptor = buildBuilderDescriptor(descriptor);
        InputEnhancer.validateBuilder(processingEnv, descriptor, builderDescriptor);

        final var steps = builderDescriptor.getSteps();

        builder.addMethod(buildStaticNewBuilderMethod(descriptor));

        for (final var step : steps) {
            builder.addType(buildBuilderStepInterface(processingEnv, descriptor, step));
        }

        // first step which may be required input, optional inputs, or build terminal step.
        buildStaticStepMethodMethods(processingEnv, descriptor, builder, builderDescriptor);

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
            final BuilderDescriptor builderDescriptor) {
        final var step = builderDescriptor.getSteps().get(0);
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
        if (helpers.isEmpty()) {
            return;
        }
        // A shortcut's type parameters must not shadow types used anywhere in the generated builder.
        final var reservedNames = new HashSet<>(
                Set.of("Builder", CONTEXT_HOLDER, "Generated", "NullMarked", "Nullable", "NonNull", "NullUnmarked"));
        for (final var generated : builderDescriptor.getSteps()) {
            reservedNames.add("Step" + generated.getIndex());
        }
        for (final var type : List.of(
                DISPOSABLE_CLASSNAME,
                IDENTIFIABLE_CLASSNAME,
                REACT_ELEMENT_CLASSNAME,
                CONTEXT_CLASSNAME,
                CONTEXT_RENDER_FUNCTION_CLASSNAME,
                CONTEXTS_CLASSNAME,
                REACT_CLASSNAME,
                KEYED_CLASSNAME,
                INPUT_KEYS_CLASSNAME,
                ARRAY_KEY_STRATEGY_CLASSNAME,
                REACT_NODE_CLASSNAME,
                RENDERABLE_CLASSNAME,
                BUILDABLE_CLASSNAME,
                JS_PROPERTY_MAP_CLASSNAME,
                CONTRACT_CLASSNAME,
                descriptor.getBuilderClassName(),
                descriptor.getClassName(),
                descriptor.getEnhancedClassName())) {
            reservedNames.addAll(type.simpleNames());
        }
        EnhancerMethod.reserveTypeNames(descriptor.getDeclaredType(), reservedNames);
        for (final var input : descriptor.getInputs()) {
            EnhancerMethod.reserveTypeNames(input.getType(), reservedNames);
            final var enhancer = input.getEnhancer();
            if (null != enhancer) {
                EnhancerMethod.reserveTypeNames(enhancer.getElement().asType(), reservedNames);
                for (final var helper : enhancer.getHelpers()) {
                    final var type = enhancer.methodType(processingEnv.getTypeUtils(), helper);
                    for (final var parameter : type.getParameterTypes()) {
                        EnhancerMethod.reserveTypeNames(parameter, reservedNames);
                    }
                    for (final var thrown : type.getThrownTypes()) {
                        EnhancerMethod.reserveTypeNames(thrown, reservedNames);
                    }
                    for (final var variable : type.getTypeVariables()) {
                        EnhancerMethod.reserveTypeNames(variable, reservedNames);
                    }
                }
            }
        }
        for (final var entry : helpers.values()) {
            builder.addMethod(EnhancerMethod.shortcut(
                    processingEnv, descriptor, entry.enhancer(), entry.method(), entry.result(), reservedNames));
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
        if (!stepMethod.isRenderableInput()
                && descriptor.getInputs().stream()
                        .noneMatch(input ->
                                input.isRenderableInput() && (input.hasDefaultField() || input.hasDefaultMethod()))) {
            addPureContract(method);
        }

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
            configureInputVarargs(processingEnv, stepMethod, method);
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
            final StepMethod stepMethod,
            final Step step,
            final Consumer<MethodSpec.Builder> action) {
        final var method = MethodSpec.methodBuilder(stepMethod.getName());
        method.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        if (!stepMethod.isRenderableInput()) {
            addPureContract(method);
        }
        action.accept(method);
        configureStepMethodReturns(descriptor, method, step, stepMethod.getStepMethodType());
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

        if (step.getMethods().stream().anyMatch(StepMethod::isBuildIntrinsic)) {
            builder.addSuperinterface(BUILDABLE_CLASSNAME);
        }

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
            // Magically handle the step method named build
            if (stepMethod.isBuildIntrinsic()) {
                builder.addMethod(buildStepInterfaceMethod(descriptor, stepMethod, step, m -> {})
                        .build());
            } else {
                builder.addMethod(buildStepInterfaceMethod(descriptor, stepMethod, step, m -> {
                            final var inputMethod = stepMethod.getMethod();
                            if (null != inputMethod) {
                                final InputDescriptor input = stepMethod.getInput();
                                assert null != input;
                                final var inputMethodType = input.getMethodType();
                                assert null != inputMethodType;
                                GeneratorUtil.copyTypeParameters(inputMethodType, m);
                            }
                            final var type = stepMethod.getType();
                            configureInputVarargs(processingEnv, stepMethod, m);
                            final var parameter = ParameterSpec.builder(type, stepMethod.getName());
                            final var inputElement = stepMethod.getElement();
                            if (null != inputElement) {
                                Nullability.preserveScope(inputElement, m);
                                GeneratorUtil.copyWhitelistedAnnotations(inputElement, m);
                                final var inputType = stepMethod.getTypeMirror();
                                assert null != inputType;
                                SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, parameter, inputType);
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

        if (!stepMethod.isRenderableInput()) {
            addPureContract(method);
        }

        final InputDescriptor input = stepMethod.getInput();
        final ExecutableType inputMethodType = null != input && input.isMethodInput() ? input.getMethodType() : null;
        if (null != inputMethodType) {
            GeneratorUtil.copyTypeParameters(inputMethodType, method);
        }
        final var type = stepMethod.getType();
        final var parameter = ParameterSpec.builder(type, stepMethod.getName(), Modifier.FINAL);
        configureInputVarargs(processingEnv, stepMethod, method);
        final var inputElement = stepMethod.getElement();
        if (null != inputElement) {
            GeneratorUtil.copyWhitelistedAnnotations(inputElement, method);
            Nullability.preserveScope(inputElement, method);
            final var inputType = stepMethod.getTypeMirror();
            assert null != inputType;
            SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, parameter, inputType);
        }
        method.addParameter(parameter.build());

        final var valueName =
                stepMethod.isRenderableInput() ? "$react4j$" + stepMethod.getName() : stepMethod.getName();
        if (stepMethod.isRenderableInput()) {
            assert null != input;
            method.addStatement(
                    "final var $N = $L", valueName, materializeInput(input, CodeBlock.of("$N", stepMethod.getName())));
        }

        if (null != input
                && input.isImmutable()
                && 1 == descriptor.syntheticKeyParts()
                && !usesFramedKeys(descriptor)) {
            final var strategy = input.getImmutableInputKeyStrategy();
            if (ImmutableInputKeyStrategy.KEYED == strategy) {
                method.addStatement(
                        "_element.setKey( $T.getKey( $N ) + " + "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        KEYED_CLASSNAME,
                        valueName,
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            } else if (ImmutableInputKeyStrategy.IS_STRING == strategy
                    || ImmutableInputKeyStrategy.TO_STRING == strategy
                    || ImmutableInputKeyStrategy.ENUM == strategy) {
                method.addStatement(
                        "_element.setKey( $N + ( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                        valueName,
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            } else if (ImmutableInputKeyStrategy.DYNAMIC == strategy) {
                final var keyValue = "$" + stepMethod.getName() + "$";
                method.addStatement("final $T $N = $N", Object.class, keyValue, valueName);
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
                        valueName,
                        REACT_CLASSNAME,
                        descriptor.keySuffix(),
                        descriptor.getClassName());
            }
        }

        if (null != input && input.isDisposable()) {
            method.addStatement("assert $T.isNotDisposed( $N )", DISPOSABLE_CLASSNAME, valueName);
        }
        if (stepMethod.isChildIntrinsic()) {
            assert null != inputElement;
            assert null != input;
            method.addStatement(
                    "_element.input( $T.Inputs.$N, $T.of( $N ) )",
                    descriptor.getEnhancedClassName(),
                    input.getConstantName(),
                    REACT_NODE_CLASSNAME,
                    valueName);
        } else {

            assert null != input;
            method.addStatement(
                    "_element.input( $T.Inputs.$N, $N )",
                    descriptor.getEnhancedClassName(),
                    input.getConstantName(),
                    valueName);
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

    private static void configureInputVarargs(
            final ProcessingEnvironment env, final StepMethod step, final MethodSpec.Builder method) {
        if (step.getType() instanceof ArrayTypeName) {
            method.varargs();
            final var type = Objects.requireNonNull(step.getTypeMirror());
            if (!env.getTypeUtils().isSameType(type, env.getTypeUtils().erasure(type))) {
                // Input setters store or forward the array without writing into it.
                method.addAnnotation(SuppressWarningsUtil.suppressWarningsAnnotation("unchecked"));
            }
        }
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
                        "_element.input( $T.Inputs.$N, $L )",
                        descriptor.getEnhancedClassName(),
                        current.getConstantName(),
                        materializeInput(current, CodeBlock.of("$N", current.getName())));
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
        if (usesFramedKeys(descriptor)) {
            method.addStatement("final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, elementName);
            final var key = CodeBlock.builder();
            for (final var input : syntheticInputs) {
                key.add("$T.frame($L) + ", INPUT_KEYS_CLASSNAME, inputKey(input));
            }
            key.add(
                    "($T.enableViewNames() ? $S : $T.class.getName())",
                    REACT_CLASSNAME,
                    descriptor.keySuffix(),
                    descriptor.getClassName());
            method.addStatement("$N.setKey($L)", elementName, key.build());
        } else if (syntheticInputs.size() > 1) {
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

            final var key = CodeBlock.join(
                    syntheticInputs.stream()
                            .map(BuilderGenerator::unframedInputKey)
                            .toList(),
                    " + \"-\" + ");
            method.addStatement(
                    "$N.setKey( $L + ( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                    elementName,
                    key,
                    REACT_CLASSNAME,
                    descriptor.keySuffix(),
                    descriptor.getClassName());
        }

        method.addStatement("return $N", elementName).returns(REACT_NODE_CLASSNAME);
        return method.build();
    }

    private static boolean usesFramedKeys(final ViewDescriptor descriptor) {
        return descriptor.getImmutableInputs().stream()
                .anyMatch(input -> null != input.getKeyGenerator() || input.getType() instanceof ArrayType);
    }

    private static CodeBlock inputKey(final InputDescriptor input) {
        if (null != input.getKeyGenerator()) {
            return CodeBlock.of("$N.getKey($L)", keyGeneratorField(input), InputValues.read(input, "inputs"));
        }
        if (input.getType() instanceof ArrayType) {
            var component = input.getType();
            var dimensions = 0;
            while (component instanceof ArrayType array) {
                component = array.getComponentType();
                dimensions++;
            }
            final String strategy;
            if (component.getKind().isPrimitive()) {
                strategy = component.getKind().name();
            } else {
                final var type = TypeName.get(component).withoutAnnotations();
                strategy = switch (input.getImmutableInputKeyStrategy()) {
                    case IS_STRING, ENUM -> "STRING";
                    case TO_STRING ->
                        ClassName.get(Float.class).equals(type)
                                        || ClassName.get(Double.class).equals(type)
                                ? "FLOATING_POINT"
                                : "STRING";
                    case KEYED, AREZ_IDENTIFIABLE, DYNAMIC ->
                        input.getImmutableInputKeyStrategy().name();
                };
            }
            return CodeBlock.of(
                    "$T.arrayKey($L, $L, $T.$L)",
                    INPUT_KEYS_CLASSNAME,
                    InputValues.read(input, "inputs"),
                    dimensions,
                    ARRAY_KEY_STRATEGY_CLASSNAME,
                    strategy);
        }
        final var value = ImmutableInputKeyStrategy.TO_STRING == input.getImmutableInputKeyStrategy()
                ? InputValues.read(input, "inputs")
                : CodeBlock.of(
                        "inputs.get($T.Inputs.$N)",
                        input.getDescriptor().getEnhancedClassName(),
                        input.getConstantName());
        return scalarKey(input.getImmutableInputKeyStrategy(), value);
    }

    private static CodeBlock scalarKey(final ImmutableInputKeyStrategy strategy, final CodeBlock value) {
        return switch (strategy) {
            case KEYED -> CodeBlock.of("$T.getKey($L)", KEYED_CLASSNAME, value);
            case AREZ_IDENTIFIABLE ->
                CodeBlock.of("$T.valueOf($T.<Object>getArezId($L))", String.class, IDENTIFIABLE_CLASSNAME, value);
            case DYNAMIC ->
                CodeBlock.of(
                        "$T.valueOf($L instanceof $T ? $T.getKey($L) : "
                                + "$L instanceof $T ? $T.<Object>getArezId($L) : $L)",
                        String.class,
                        value,
                        KEYED_CLASSNAME,
                        KEYED_CLASSNAME,
                        value,
                        value,
                        IDENTIFIABLE_CLASSNAME,
                        IDENTIFIABLE_CLASSNAME,
                        value,
                        value);
            case IS_STRING, ENUM, TO_STRING -> CodeBlock.of("$T.valueOf($L)", String.class, value);
        };
    }

    private static CodeBlock unframedInputKey(final InputDescriptor input) {
        final var value = CodeBlock.of(
                "inputs.get( $T.Inputs.$N )", input.getDescriptor().getEnhancedClassName(), input.getConstantName());
        return switch (input.getImmutableInputKeyStrategy()) {
            case KEYED -> CodeBlock.of("$T.getKey( ($T) $L )", KEYED_CLASSNAME, input.getType(), value);
            case IS_STRING, ENUM -> CodeBlock.of("( ($T) $L )", input.getType(), value);
            case TO_STRING -> {
                final var kind = input.getType().getKind();
                final var scalar =
                        kind.isPrimitive() ? CodeBlock.of("$T.requireNonNull( $L )", Objects.class, value) : value;
                final var converted =
                        switch (kind) {
                            case BYTE, SHORT, INT, LONG, FLOAT -> CodeBlock.of("(double) $L", scalar);
                            default -> scalar;
                        };
                yield CodeBlock.of("$T.valueOf( ($T) $L )", String.class, input.getType(), converted);
            }
            case DYNAMIC -> {
                final var name = "$" + input.getName() + "$";
                yield CodeBlock.of(
                        "( $N instanceof $T ? $T.getKey( $N ) : "
                                + "$N instanceof $T ? $T.<$T>getArezId( $N ) : $T.valueOf( $N ) )",
                        name,
                        KEYED_CLASSNAME,
                        KEYED_CLASSNAME,
                        name,
                        name,
                        IDENTIFIABLE_CLASSNAME,
                        IDENTIFIABLE_CLASSNAME,
                        Object.class,
                        name,
                        String.class,
                        name);
            }
            case AREZ_IDENTIFIABLE ->
                CodeBlock.of(
                        "$T.valueOf( $T.<$T>getArezId( ($T) $L ) )",
                        String.class,
                        IDENTIFIABLE_CLASSNAME,
                        Object.class,
                        input.getType(),
                        value);
        };
    }

    private static String keyGeneratorField(final InputDescriptor input) {
        return "$keyGenerator_" + input.getName();
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
                        "inputs.set( $T.Inputs.$N, $L )",
                        descriptor.getEnhancedClassName(),
                        input.getConstantName(),
                        materializeInput(
                                input,
                                CodeBlock.of(
                                        "$T.$N" + (input.hasDefaultField() ? "" : "()"),
                                        descriptor.getClassName(),
                                        input.hasDefaultField()
                                                ? input.getDefaultField().getSimpleName()
                                                : input.getDefaultMethod().getSimpleName())));
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
                if (hasNonOptionalChild) {
                    optionalInputStep.addMethod(
                            input, hasSingleOptional ? StepMethodType.ADVANCE : StepMethodType.STAY);
                } else {
                    optionalInputStep.addMethod(
                            input,
                            input.isSpecialChildrenInput() || hasSingleOptional
                                    ? StepMethodType.TERMINATE
                                    : StepMethodType.STAY);
                }
            } else {
                if (null != optionalInputStep) {
                    // Need this when we have children magic input that is required that follows the optional inputs.
                    optionalInputStep.addMethod(input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE);
                    hasRequiredAfterOptional = true;
                }
                // Single method step
                final var step = builder.addStep();
                step.addMethod(input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE);
                if (input.getName().equals("children")) {
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
        step.addTerminalMethod("build", REACT_NODE_CLASSNAME);
    }

    private static CodeBlock materializeInput(final InputDescriptor input, final CodeBlock value) {
        return input.isRenderableInput()
                ? CodeBlock.of(
                        "$T.$N( $L )",
                        RENDERABLE_CLASSNAME,
                        input.isRenderableArrayInput() ? "toReactNodes" : "toReactNode",
                        value)
                : value;
    }
}
