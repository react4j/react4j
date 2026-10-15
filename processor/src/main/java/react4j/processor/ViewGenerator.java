package react4j.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.SuppressWarningsUtil;

@SuppressWarnings("Duplicates")
final class ViewGenerator {

    private static final ClassName GUARDS_CLASSNAME = ClassName.get("org.realityforge.braincheck", "Guards");

    private static final ClassName AREZ_CLASSNAME = ClassName.get("arez", "Arez");

    private static final ClassName OBSERVER_CLASSNAME = ClassName.get("arez", "Observer");

    private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get("arez", "ObservableValue");

    private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get("arez", "Disposable");

    private static final ClassName AREZ_FEATURE_CLASSNAME = ClassName.get("arez.annotations", "Feature");

    private static final ClassName SUPPRESS_AREZ_WARNINGS_CLASSNAME =
            ClassName.get("arez.annotations", "SuppressArezWarnings");

    private static final ClassName ACTION_CLASSNAME = ClassName.get("arez.annotations", "Action");

    private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get("arez.annotations", "DepType");

    private static final ClassName PRIORITY_CLASSNAME = ClassName.get("arez.annotations", "Priority");

    private static final ClassName EXECUTOR_CLASSNAME = ClassName.get("arez.annotations", "Executor");

    private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get("arez.annotations", "Observable");

    private static final ClassName OBSERVE_ANNOTATION_CLASSNAME = ClassName.get("arez.annotations", "Observe");

    private static final ClassName OBSERVER_REF_ANNOTATION_CLASSNAME = ClassName.get("arez.annotations", "ObserverRef");

    private static final ClassName COMPONENT_OBSERVABLE_CLASSNAME =
            ClassName.get("arez.component", "ComponentObservable");

    private static final ClassName COMPONENT_NAME_REF_ANNOTATION_CLASSNAME =
            ClassName.get("arez.annotations", "ComponentNameRef");

    private static final ClassName COMPONENT_ID_REF_ANNOTATION_CLASSNAME =
            ClassName.get("arez.annotations", "ComponentIdRef");

    private static final ClassName COMPONENT_STATE_REF_ANNOTATION_CLASSNAME =
            ClassName.get("arez.annotations", "ComponentStateRef");

    private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
            ClassName.get("arez.annotations", "ObservableValueRef");

    private static final ClassName AREZ_COMPONENT_CLASSNAME = ClassName.get("arez.annotations", "ArezComponent");

    private static final ClassName JS_ERROR_CLASSNAME = ClassName.get("react4j", "JsError");

    private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get("jsinterop.annotations", "JsConstructor");

    private static final ClassName JS_CLASSNAME = ClassName.get("jsinterop.base", "Js");

    private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get("jsinterop.base", "JsPropertyMap");

    public static final ClassName OBJECT = ClassName.get("java.lang", "Object");

    private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
            ParameterizedTypeName.get(JS_PROPERTY_MAP_CLASSNAME, Nullability.nullable(OBJECT));

    private static final ClassName REACT_NODE_CLASSNAME = ClassName.get("react4j", "ReactNode");

    private static final ClassName REACT_ERROR_INFO_CLASSNAME = ClassName.get("react4j", "ReactErrorInfo");

    private static final ClassName REACT_CLASSNAME = ClassName.get("react4j", "React");

    private static final ClassName CONTEXT_CLASSNAME = ClassName.get("react4j", "Context");

    private static final ClassName CONTEXTS_CLASSNAME = ClassName.get("react4j", "Contexts");

    private static final ClassName VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME =
            ClassName.get("react4j.internal", "ViewConstructorFunction");

    private static final ClassName ON_COMPONENT_DID_MOUNT_CLASSNAME =
            ClassName.get("react4j.internal", "OnComponentDidMount");

    private static final ClassName ON_COMPONENT_DID_UPDATE_CLASSNAME =
            ClassName.get("react4j.internal", "OnComponentDidUpdate");

    private static final ClassName ON_COMPONENT_WILL_UNMOUNT_CLASSNAME =
            ClassName.get("react4j.internal", "OnComponentWillUnmount");

    private static final ClassName ON_GET_SNAPSHOT_BEFORE_UPDATE_CLASSNAME =
            ClassName.get("react4j.internal", "OnGetSnapshotBeforeUpdate");

    private static final ClassName ON_COMPONENT_SHOULD_UPDATE_CLASSNAME =
            ClassName.get("react4j.internal", "OnShouldComponentUpdate");

    private static final ClassName ON_COMPONENT_DID_CATCH_CLASSNAME =
            ClassName.get("react4j.internal", "OnComponentDidCatch");

    private static final ClassName NATIVE_VIEW_CLASSNAME = ClassName.get("react4j.internal", "NativeView");

    private static final ClassName VIEW_STATE_CLASSNAME = ClassName.get("react4j.internal", "ViewState");

    private static final ClassName SCHEDULER_UTIL_CLASSNAME = ClassName.get("react4j.internal", "SchedulerUtil");

    private static final ClassName INTROSPECT_UTIL_CLASSNAME = ClassName.get("react4j.internal", "IntrospectUtil");

    private static final String FRAMEWORK_INTERNAL_PREFIX = "$$react4j$$_";

    private static final String RENDER_METHOD = FRAMEWORK_INTERNAL_PREFIX + "render";

    private static final String SHOULD_COMPONENT_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "shouldComponentUpdate";

    private static final String COMPONENT_PRE_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentPreUpdate";

    private static final String COMPONENT_DID_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidUpdate";

    private static final String COMPONENT_DID_MOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidMount";

    private static final String COMPONENT_WILL_UNMOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentWillUnmount";

    private static final String VALIDATE_INPUTS_METHOD = FRAMEWORK_INTERNAL_PREFIX + "validateInputValues";

    private static final String STATE_FIELD = FRAMEWORK_INTERNAL_PREFIX + "state";

    private static final String VIEW_FIELD = "view";

    private static final String IS_READY_METHOD = FRAMEWORK_INTERNAL_PREFIX + "isReady";

    private static final String NATIVE_VIEW_FIELD = FRAMEWORK_INTERNAL_PREFIX + "nativeView";

    private ViewGenerator() {}

    static TypeSpec buildType(final ProcessingEnvironment processingEnv, final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder(descriptor.getEnhancedClassName());
        builder.addAnnotation(ClassName.get("org.jspecify.annotations", "NullMarked"));
        builder.addTypeVariables(GeneratorUtil.getTypeArgumentsAsNames(descriptor.getDeclaredType()));
        final TypeElement typeElement = descriptor.getElement();
        GeneratorUtil.copyWhitelistedAnnotations(
                typeElement, builder, Collections.singletonList(Deprecated.class.getName()));

        builder.superclass(descriptor.getViewType());

        final AnnotationSpec suppression = SuppressWarningsUtil.maybeSuppressWarningsAnnotation(
                descriptor.viewAccessesDeprecatedElements() ? "deprecation" : null);
        if (null != suppression) {
            builder.addAnnotation(suppression);
        }
        builder.addAnnotation(buildSuppressArezWarnings(descriptor));
        final AnnotationSpec.Builder arezAnnotation = AnnotationSpec.builder(AREZ_COMPONENT_CLASSNAME)
                .addMember(
                        "name", "$S", typeElement.getQualifiedName().toString().replace(".", "_"))
                .addMember("disposeNotifier", "$T.DISABLE", AREZ_FEATURE_CLASSNAME)
                .addMember("defaultSkipIfDisposed", "$T.ENABLE", AREZ_FEATURE_CLASSNAME)
                .addMember("sting", "$T.DISABLE", AREZ_FEATURE_CLASSNAME);
        if (!descriptor.trackRender()) {
            arezAnnotation.addMember("allowEmpty", "true");
        } else if (descriptor.shouldSetDefaultPriority()) {
            arezAnnotation.addMember("defaultPriority", "$T.LOWEST", PRIORITY_CLASSNAME);
        }
        builder.addAnnotation(arezAnnotation.build());
        builder.addModifiers(Modifier.ABSTRACT);

        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, React4jProcessor.class.getName());
        GeneratorUtil.addOriginatingTypes(typeElement, builder);

        if (!descriptor.getPublishDescriptors().isEmpty()) {
            builder.addType(buildContextHolder(descriptor));
        }

        if (needsNativeView(descriptor)) {
            builder.addField(
                    FieldSpec.builder(NATIVE_VIEW_CLASSNAME, NATIVE_VIEW_FIELD, Modifier.PRIVATE, Modifier.FINAL)
                            .build());
        }

        builder.addMethod(buildConstructor(processingEnv, descriptor).build());

        for (final ScheduleRenderDescriptor element : descriptor.getScheduleRenderDescriptors()) {
            final var method = GeneratorUtil.overrideMethod(processingEnv, typeElement, element.getMethod());
            if (element.skipShouldViewUpdate()) {
                method.addStatement("$N.forceUpdate()", NATIVE_VIEW_FIELD);
            } else {
                method.addStatement(
                        "$N.setState( $T.uncheckedCast( $T.of() ) )",
                        NATIVE_VIEW_FIELD,
                        JS_CLASSNAME,
                        JS_PROPERTY_MAP_CLASSNAME);
            }
            builder.addMethod(method.build());
        }
        if (descriptor.trackRender()) {
            builder.addField(FieldSpec.builder(TypeName.INT, STATE_FIELD, Modifier.PRIVATE)
                    .build());
        }

        builder.addType(buildFactory());
        if (!descriptor.getInputs().isEmpty()) {
            builder.addType(buildInputsType(descriptor));
        }

        builder.addMethod(buildConstructorFnMethod(descriptor).build());

        if (descriptor.getInputs().stream()
                .anyMatch(InputDescriptor::needsMutableInputAccessedInPostConstructInvariant)) {
            builder.addMethod(buildIsReadyMethod().build());
        }

        for (final var input : descriptor.getInputs()) {
            if (input.isMethodInput()) {
                builder.addMethod(buildInputMethod(input).build());
            }
            if (input.isObservable()) {
                builder.addMethod(buildInputObservableValueRefMethod(input).build());
            }
        }

        if (descriptor.shouldValidateInputs()) {
            builder.addMethod(buildInputValidatorMethod(descriptor).build());
        }

        if (descriptor.generateShouldComponentUpdate()) {
            builder.addMethod(buildShouldComponentUpdate(descriptor).build());
        }
        if (descriptor.generateComponentDidMount()) {
            builder.addMethod(buildComponentDidMount(descriptor).build());
        }
        if (descriptor.generateComponentPreUpdate()) {
            builder.addMethod(buildComponentPreUpdate(descriptor).build());
        }
        if (descriptor.generateComponentDidUpdate()) {
            builder.addMethod(buildComponentDidUpdate(descriptor).build());
        }
        if (descriptor.generateComponentWillUnmount()) {
            builder.addMethod(buildComponentWillUnmount(descriptor).build());
        }

        if (descriptor.shouldGenerateRender()) {
            builder.addMethod(buildRender(descriptor).build());
        }

        if (descriptor.trackRender()) {
            builder.addField(FieldSpec.builder(
                            TypeName.BOOLEAN, FRAMEWORK_INTERNAL_PREFIX + "scheduledDebugStateUpdate", Modifier.PRIVATE)
                    .build());
            builder.addMethod(buildOnRenderDepsChange(descriptor).build());
            builder.addMethod(buildGetRenderObserver(descriptor).build());
            builder.addMethod(buildGetComponentId().build());
            builder.addMethod(buildGetComponentName().build());
            builder.addMethod(buildStoreDebugDataAsState().build());
        }

        if (descriptor.shouldGenerateLiteLifecycle()) {
            builder.addType(buildNativeView(descriptor, true));
        }
        builder.addType(buildNativeView(descriptor, false));

        return builder.build();
    }

    private static AnnotationSpec buildSuppressArezWarnings(final ViewDescriptor descriptor) {
        final AnnotationSpec.Builder annotation = AnnotationSpec.builder(SUPPRESS_AREZ_WARNINGS_CLASSNAME);
        // Theoretically, we could scan the underlying component to detect the presence of `@Action` annotated
        // that do not explicitly set skipIfDisposed parameter and only set defaultSkipIfDisposed if one such
        // action is found. Then we would not need to add this next suppression. But there is no harm in adding
        // suppression so doing that as it requires less work.
        annotation.addMember("value", "$S", "Arez:UnnecessaryDefault");
        if (!descriptor.trackRender()) {
            annotation.addMember("value", "$S", "Arez:UnnecessaryAllowEmpty");
        }
        return annotation.build();
    }

    private static boolean needsNativeView(final ViewDescriptor descriptor) {
        return descriptor.trackRender()
                || !descriptor.getScheduleRenderDescriptors().isEmpty()
                || descriptor.getInputs().stream().anyMatch(InputDescriptor::isMethodInput);
    }

    private static MethodSpec.Builder buildConstructor(
            final ProcessingEnvironment processingEnv, final ViewDescriptor descriptor) {
        final var nativeViewType = Nullability.isMarked(descriptor.getConstructor())
                ? NATIVE_VIEW_CLASSNAME
                : NATIVE_VIEW_CLASSNAME.annotated(
                        AnnotationSpec.builder(ClassName.get("org.jspecify.annotations", "NonNull"))
                                .build());
        final var componentParameter = ParameterSpec.builder(nativeViewType, NATIVE_VIEW_FIELD, Modifier.FINAL);
        final var ctor = MethodSpec.constructorBuilder();
        ctor.addParameter(componentParameter.build());

        final var parameters = descriptor.getConstructor().getParameters();
        if (!parameters.isEmpty()) {
            final var sb = new StringBuilder();
            final var params = new ArrayList<>();
            sb.append("super( ");
            var first = true;
            for (final VariableElement element : parameters) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append("$N");
                final var name = element.getSimpleName().toString();
                params.add(name);
                final var ctorParameter =
                        ParameterSpec.builder(GeneratorUtil.getTypeName(element.asType()), name, Modifier.FINAL);
                GeneratorUtil.copyWhitelistedAnnotations(element, ctorParameter);
                SuppressWarningsUtil.addSuppressWarningsIfRequired(processingEnv, ctor, element.asType());

                ctor.addParameter(ctorParameter.build());
            }
            sb.append(" )");
            ctor.addStatement(sb.toString(), params.toArray());
        }

        if (needsNativeView(descriptor)) {
            ctor.addStatement("this.$N = $N", NATIVE_VIEW_FIELD, NATIVE_VIEW_FIELD);
        }
        Nullability.preserveScope(descriptor.getConstructor(), ctor);

        return ctor;
    }

    private static FieldSpec.Builder buildInputKeyConstantField(final InputDescriptor descriptor, final int index) {
        final var name = descriptor.getName();

        final var field = FieldSpec.builder(
                TypeName.get(String.class), descriptor.getConstantName(), Modifier.STATIC, Modifier.FINAL);
        if (descriptor.isSpecialChildrenInput()) {
            return field.initializer("$S", "children");
        } else {
            return field.initializer(
                    "$T.shouldMinimizeInputKeys() ? $S : $S",
                    REACT_CLASSNAME,
                    Character.toString((char) ('a' + index)),
                    name);
        }
    }

    private static MethodSpec.Builder buildInputMethod(final InputDescriptor input) {
        assert input.isMethodInput();
        assert !input.isImmutable();
        final var methodElement = input.getMethod();
        final ExecutableType methodType = input.getMethodType();
        assert null != methodElement;
        assert null != methodType;
        final TypeMirror returnType = methodType.getReturnType();
        final var method = MethodSpec.methodBuilder(
                        methodElement.getSimpleName().toString())
                .returns(input.getTypeName());
        Nullability.preserveScope(methodElement, method);
        if (returnType instanceof DeclaredType type
                && (type.asElement().toString().equals("java.util.ArrayList")
                        || type.asElement().toString().equals("java.util.HashSet"))) {
            // An override must retain the concrete collection type from the view contract.
            method.addAnnotation(SuppressWarningsUtil.suppressWarningsAnnotation("NonApiType"));
        }
        GeneratorUtil.copyTypeParameters(methodType, method);
        GeneratorUtil.copyAccessModifiers(methodElement, method);
        GeneratorUtil.copyWhitelistedAnnotations(methodElement, method);

        method.addAnnotation(Override.class);

        if (input.isObservable()) {
            final AnnotationSpec.Builder annotation = AnnotationSpec.builder(OBSERVABLE_ANNOTATION_CLASSNAME)
                    .addMember("name", "$S", input.getName())
                    .addMember("expectSetter", "false")
                    .addMember("readOutsideTransaction", "$T.ENABLE", AREZ_FEATURE_CLASSNAME);
            method.addAnnotation(annotation.build());
        }

        if (input.needsMutableInputAccessedInPostConstructInvariant()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
            block.addStatement(
                    "$T.apiInvariant( () -> $N(), " + "() -> \"The view '\" + this + \"' accessed the input named '"
                            + input.getName()
                            + "' before the view is ready (possibly in a @PostConstruct annotated method?) and "
                            + "does not have a @OnInputChange annotated method to cover the input and reflect changes "
                            + "of the input onto the view. This is considered a likely bug and the @Input should be "
                            + "made immutable or an @OnInputChange method added to cover the input. "
                            + MemberChecks.suppressedBy(
                                            Constants.WARNING_MUTABLE_INPUT_ACCESSED_IN_POST_CONSTRUCT,
                                            Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME)
                                    .replace("\"", "\\\"")
                            + " to the @Input annotated method.\" )",
                    GUARDS_CLASSNAME,
                    IS_READY_METHOD);
            block.endControlFlow();
            method.addCode(block.build());
        }

        final var convertMethodName = getConverter(returnType, methodElement);
        final var resultKind = methodElement.getReturnType().getKind();
        if (!resultKind.isPrimitive() && input.getNullness() == Nullability.Nullness.NULLABLE) {
            // Need to add a Nullable if it is not present as invariant path can return nulls.

            final var block = CodeBlock.builder();
            block.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
            block.addStatement(
                    "return null != $N.requireInputs().getAsAny( Inputs.$N ) ? "
                            + "$N.requireInputs().getAsAny( Inputs.$N ).$N() : null",
                    NATIVE_VIEW_FIELD,
                    input.getConstantName(),
                    NATIVE_VIEW_FIELD,
                    input.getConstantName(),
                    convertMethodName);
            block.nextControlFlow("else");
            block.addStatement(
                    "return $T.uncheckedCast( $N.requireInputs().getAsAny( Inputs.$N ) )",
                    JS_CLASSNAME,
                    NATIVE_VIEW_FIELD,
                    input.getConstantName());
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement(
                    "return $N.requireInputs().getAsAny( Inputs.$N ).$N()",
                    NATIVE_VIEW_FIELD,
                    input.getConstantName(),
                    convertMethodName);
        }
        return method;
    }

    private static String getConverter(final TypeMirror type, final Element element) {
        switch (type.getKind()) {
            case BOOLEAN:
                return "asBoolean";
            case BYTE:
                return "asByte";
            case CHAR:
                return "asChar";
            case DOUBLE:
                return "asDouble";
            case FLOAT:
                return "asFloat";
            case INT:
                return "asInt";
            case LONG:
                return "asLong";
            case SHORT:
                return "asShort";
            case TYPEVAR:
            case ARRAY:
                return "cast";
            case DECLARED:
                if (((DeclaredType) type).asElement().toString().equals("java.lang.String")) {
                    return "asString";
                } else {
                    return "cast";
                }
            default:
                throw new ProcessorException(
                        "Return type of @Input method is not yet " + "handled. Type: " + type.getKind(), element);
        }
    }

    private static MethodSpec.Builder buildInputObservableValueRefMethod(final InputDescriptor input) {
        return MethodSpec.methodBuilder(toObservableValueRefMethodName(input))
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME)
                .returns(ParameterizedTypeName.get(OBSERVABLE_CLASSNAME, WildcardTypeName.subtypeOf(OBJECT)));
    }

    private static String toObservableValueRefMethodName(final InputDescriptor input) {
        final var name = input.getName();
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1) + "ObservableValue";
    }

    private static void buildOnInputChangeInvocations(
            final CodeBlock.Builder code, final List<OnInputChangeDescriptor> onInputChanges) {
        // The list of inputs we need to check for changes
        final var inputs = onInputChanges.stream()
                .flatMap(d -> d.getInputs().stream())
                .distinct()
                .toList();

        for (final var input : inputs) {
            code.addStatement(
                    "final boolean $N = !$T.isTripleEqual( inputs.get( Inputs.$N ), prevInputs.get( Inputs.$N ) )",
                    input.getName(),
                    JS_CLASSNAME,
                    input.getConstantName(),
                    input.getConstantName());
        }
        for (final var onInputChange : onInputChanges) {
            final var onChangeBlock = CodeBlock.builder();
            onChangeBlock.beginControlFlow("if ( "
                    + onInputChange.getInputs().stream()
                            .map(InputDescriptor::getName)
                            .collect(Collectors.joining(" && "))
                    + " )");
            final var sb = new StringBuilder();
            final var params = new ArrayList<>();
            sb.append("$N( ");
            params.add(onInputChange.getMethod().getSimpleName().toString());
            var requireComma = false;
            for (final var input : onInputChange.getInputs()) {
                if (requireComma) {
                    sb.append(", ");
                }
                requireComma = true;
                final var convertMethodName = getConverter(input.getType(), input.getElement());
                final var resultKind = input.getType().getKind();
                if (!resultKind.isPrimitive() && !input.isNonNull()) {
                    sb.append("$T.uncheckedCast( inputs.getAsAny( Inputs.$N ) )");
                    params.add(JS_CLASSNAME);
                    params.add(input.getConstantName());
                } else {
                    sb.append("inputs.getAsAny( Inputs.$N ).$N()");
                    params.add(input.getConstantName());
                    params.add(convertMethodName);
                }
            }

            sb.append(" )");
            onChangeBlock.addStatement(sb.toString(), params.toArray());
            onChangeBlock.endControlFlow();
            code.add(onChangeBlock.build());
        }
    }

    private static MethodSpec.Builder buildComponentDidMount(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder(COMPONENT_DID_MOUNT_METHOD).addModifiers(Modifier.PRIVATE);
        final var postRender = descriptor.getPostRender();
        if (null != postRender) {
            method.addStatement("$N()", postRender.getSimpleName().toString());
        }
        final var postMount = descriptor.getPostMount();
        if (null != postMount) {
            method.addStatement("$N()", postMount.getSimpleName().toString());
        }
        if (descriptor.trackRender()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( $T.shouldStoreDebugDataAsState() && $T.areSpiesEnabled() )", REACT_CLASSNAME, AREZ_CLASSNAME);
            block.addStatement("$N()", FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState");
            block.endControlFlow();
            method.addCode(block.build());
        }

        return method;
    }

    private static MethodSpec.Builder buildShouldComponentUpdate(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder(SHOULD_COMPONENT_UPDATE_METHOD)
                .returns(TypeName.BOOLEAN)
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextInputs", Modifier.FINAL)
                        .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                        .build());

        final var observableInputs = descriptor.getObservableInputs();
        if (!observableInputs.isEmpty()) {
            method.addAnnotation(AnnotationSpec.builder(ACTION_CLASSNAME)
                    .addMember("verifyRequired", "false")
                    .addMember("skipIfDisposed", "$T.DISABLE", AREZ_FEATURE_CLASSNAME)
                    .addMember("reportParameters", "false")
                    .build());
        } else {
            method.addModifiers(Modifier.PRIVATE);
        }

        method.addStatement("assert null != nextInputs");

        if (descriptor.shouldValidateInputs()) {
            final var validateBlock = CodeBlock.builder();
            validateBlock.beginControlFlow("if ( $T.shouldValidateInputValues() )", REACT_CLASSNAME);
            validateBlock.addStatement("$N( nextInputs )", VALIDATE_INPUTS_METHOD);
            validateBlock.endControlFlow();
            method.addCode(validateBlock.build());
        }

        final var updateOnChangeInputs = descriptor.getUpdateOnChangeInputs();

        if (observableInputs.isEmpty() && updateOnChangeInputs.isEmpty()) {
            if (descriptor.trackRender()) {
                method.addStatement("return $T.SCHEDULED == $N", VIEW_STATE_CLASSNAME, STATE_FIELD);
            } else {
                method.addStatement("return false");
            }
        } else {
            method.addStatement(
                    "final $T inputs = $N.requireInputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD);

            final var hasObservableInputsToUpdateOnChange =
                    observableInputs.stream().anyMatch(InputDescriptor::shouldUpdateOnChange);

            if (hasObservableInputsToUpdateOnChange) {
                method.addStatement("boolean modified = false");
            }

            for (final var input : observableInputs) {
                final var block = CodeBlock.builder();
                block.beginControlFlow(
                        "if ( !$T.isTripleEqual( inputs.get( Inputs.$N ), nextInputs.get( Inputs.$N ) ) )",
                        JS_CLASSNAME,
                        input.getConstantName(),
                        input.getConstantName());
                block.addStatement("$N().reportChanged()", toObservableValueRefMethodName(input));
                if (input.shouldUpdateOnChange()) {
                    block.addStatement("modified = true");
                }
                block.endControlFlow();
                method.addCode(block.build());
            }

            for (final var input : updateOnChangeInputs) {
                final var block = CodeBlock.builder();
                block.beginControlFlow(
                        "if ( !$T.isTripleEqual( inputs.get( Inputs.$N ), nextInputs.get( Inputs.$N ) ) )",
                        JS_CLASSNAME,
                        input.getConstantName(),
                        input.getConstantName());
                block.addStatement("return true");
                block.endControlFlow();
                method.addCode(block.build());
            }
            if (hasObservableInputsToUpdateOnChange) {
                if (descriptor.trackRender()) {
                    method.addStatement("return modified || $T.SCHEDULED == $N", VIEW_STATE_CLASSNAME, STATE_FIELD);
                } else {
                    method.addStatement("return modified");
                }
            } else {
                if (descriptor.trackRender()) {
                    method.addStatement("return $T.SCHEDULED == $N", VIEW_STATE_CLASSNAME, STATE_FIELD);
                } else {
                    method.addStatement("return false");
                }
            }
        }

        return method;
    }

    private static MethodSpec.Builder buildComponentPreUpdate(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder(COMPONENT_PRE_UPDATE_METHOD).addModifiers(Modifier.PRIVATE);
        final var hasPreUpdateOnInputChange = descriptor.hasPreUpdateOnInputChange();
        if (hasPreUpdateOnInputChange) {
            method.addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL)
                    .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                    .build());
            final var block = CodeBlock.builder();
            block.beginControlFlow("if ( null != prevInputs )");
            block.addStatement(
                    "final $T inputs = $N.requireInputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD);
            buildOnInputChangeInvocations(block, descriptor.getPreUpdateOnInputChangeDescriptors());
            block.endControlFlow();
            method.addCode(block.build());
        }
        final var preUpdate = descriptor.getPreUpdate();
        if (null != preUpdate) {
            method.addStatement("$N()", preUpdate.getSimpleName().toString());
        }
        return method;
    }

    private static MethodSpec.Builder buildComponentDidUpdate(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder(COMPONENT_DID_UPDATE_METHOD).addModifiers(Modifier.PRIVATE);

        if (descriptor.hasPostUpdateOnInputChange()) {
            method.addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL)
                    .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                    .build());
            final var block = CodeBlock.builder();
            block.beginControlFlow("if ( null != prevInputs )");
            block.addStatement(
                    "final $T inputs = $N.requireInputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD);
            buildOnInputChangeInvocations(block, descriptor.getPostUpdateOnInputChangeDescriptors());
            block.endControlFlow();
            method.addCode(block.build());
        }
        final var postRender = descriptor.getPostRender();
        if (null != postRender) {
            method.addStatement("$N()", postRender.getSimpleName().toString());
        }
        final var postUpdate = descriptor.getPostUpdate();
        if (null != postUpdate) {
            method.addStatement("$N()", postUpdate.getSimpleName().toString());
        }
        if (descriptor.trackRender()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( $T.shouldStoreDebugDataAsState() && $T.areSpiesEnabled() )", REACT_CLASSNAME, AREZ_CLASSNAME);
            block.addStatement("$N()", FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState");
            block.endControlFlow();
            method.addCode(block.build());
        }
        return method;
    }

    private static MethodSpec.Builder buildComponentWillUnmount(final ViewDescriptor descriptor) {
        final var method =
                MethodSpec.methodBuilder(COMPONENT_WILL_UNMOUNT_METHOD).addModifiers(Modifier.PRIVATE);

        // We always dispose here rather than checking hasArezElements()
        // as this code path is only invoked when there are Arez elements, when we are in non-production
        // mode (and thus this makes debugging easier). Thus, no need to have a guard
        method.addStatement("(($T) this).dispose()", descriptor.getArezClassName());

        if (descriptor.trackRender()) {
            method.addStatement("$N = $T.UNMOUNTED", STATE_FIELD, VIEW_STATE_CLASSNAME);
        }
        return method;
    }

    private static MethodSpec.Builder buildRender(final ViewDescriptor descriptor) {
        final var render = descriptor.hasRender() ? descriptor.getRender() : null;

        final var method = MethodSpec.methodBuilder(RENDER_METHOD)
                .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                .returns(REACT_NODE_CLASSNAME);

        if (descriptor.trackRender()) {
            final AnnotationSpec.Builder observe = AnnotationSpec.builder(OBSERVE_ANNOTATION_CLASSNAME)
                    .addMember("name", "$S", "render")
                    .addMember("priority", "$T.LOW", PRIORITY_CLASSNAME)
                    .addMember("executor", "$T.EXTERNAL", EXECUTOR_CLASSNAME)
                    // Needs AREZ_OR_NONE in scenario where all inputs are disposed and view
                    // thus accesses no dependencies before exiting render
                    .addMember("depType", "$T.AREZ_OR_NONE", DEP_TYPE_CLASSNAME)
                    .addMember("observeLowerPriorityDependencies", "true")
                    .addMember("reportResult", "false");
            method.addAnnotation(observe.build());

            method.addStatement("$N = $T.IDLE", STATE_FIELD, VIEW_STATE_CLASSNAME);
        }
        method.addStatement("assert $T.isNotDisposed( this )", DISPOSABLE_CLASSNAME);

        if (descriptor.trackRender()) {
            var candidateIndex = 0;
            for (final var candidate : descriptor.getPreludeCheckCandidates()) {
                if (candidate.observeMode() == ObserveMode.NO_OBSERVE) {
                    if (candidate.element() instanceof ExecutableElement) {
                        method.addStatement(
                                "$N()", candidate.element().getSimpleName().toString());
                    }
                    continue;
                }
                final var varName = "$$react4jv$$_candidate" + candidateIndex++;
                final var memberName = candidate.element().getSimpleName().toString();
                method.addStatement(
                        "final $T $N = $N" + (candidate.element() instanceof ExecutableElement ? "()" : ""),
                        candidate.type(),
                        varName,
                        memberName);
                addRenderPreludeObservationCheck(method, candidate, varName);
            }
        }

        if (descriptor.trackRender()) {
            method.addStatement("$T.pauseUntilRenderLoopComplete()", SCHEDULER_UTIL_CLASSNAME);
        }

        final var sb = new StringBuilder();
        final var args = new ArrayList<>();
        final var publishDescriptors = descriptor.getPublishDescriptors();
        for (final PublishDescriptor publish : publishDescriptors) {
            sb.append("$T.$N.provide( $N(), ");
            args.add(ClassName.bestGuess("ContextHolder"));
            args.add("CONTEXT_" + publish.getMethod().getSimpleName());
            args.add(publish.getMethod().getSimpleName());
        }
        for (final var hook : descriptor.getPreRenderDescriptors()) {
            method.addStatement("$N()", hook.getMethod().getSimpleName().toString());
        }

        final var isTrackingType = ViewType.TRACKING == descriptor.getType();
        final var postRenderDescriptors = descriptor.getPostRenderDescriptors();
        final var explicitReturnRequired = isTrackingType || !postRenderDescriptors.isEmpty();

        if (null != render) {
            sb.append("$N()");
            args.add(render.getSimpleName().toString());
        } else {
            sb.append("null");
        }

        final var publishCount = publishDescriptors.size();
        sb.append(" )".repeat(publishCount));

        if (!explicitReturnRequired) {
            method.addStatement("return " + sb, args.toArray());
        } else {
            args.add(0, REACT_NODE_CLASSNAME);
            method.addStatement("final $T result = " + sb, args.toArray());

            if (isTrackingType) {
                final var depCheckBlock = CodeBlock.builder();
                depCheckBlock.beginControlFlow(
                        "if ( $T.shouldCheckInvariants() && $T.areSpiesEnabled() )", AREZ_CLASSNAME, AREZ_CLASSNAME);
                final var getObserverMethodName = FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver";
                depCheckBlock.addStatement(
                        "$T.invariant( () -> !$N().getContext().getSpy()."
                                + "asObserverInfo( $N() ).getDependencies().isEmpty(), "
                                + "() -> \"View render completed on '\" + this + \"' without accessing "
                                + "any Arez dependencies but has a type set to TRACKING. The render method "
                                + "needs to access an Arez dependency or the type should be changed to "
                                + "STATEFUL or MAYBE_TRACKING.\" )",
                        GUARDS_CLASSNAME,
                        getObserverMethodName,
                        getObserverMethodName);
                depCheckBlock.endControlFlow();
                method.addCode(depCheckBlock.build());
            }

            for (final var hook : postRenderDescriptors) {
                method.addStatement("$N()", hook.getMethod().getSimpleName().toString());
            }

            method.addStatement("return result");
        }
        return method;
    }

    private static MethodSpec.Builder buildGetComponentId() {
        return MethodSpec.methodBuilder(FRAMEWORK_INTERNAL_PREFIX + "getComponentId")
                .addAnnotation(COMPONENT_ID_REF_ANNOTATION_CLASSNAME)
                .addModifiers(Modifier.ABSTRACT)
                .returns(TypeName.INT);
    }

    private static MethodSpec.Builder buildGetComponentName() {
        return MethodSpec.methodBuilder(FRAMEWORK_INTERNAL_PREFIX + "getComponentName")
                .addAnnotation(COMPONENT_NAME_REF_ANNOTATION_CLASSNAME)
                .addModifiers(Modifier.ABSTRACT)
                .returns(ClassName.get(String.class));
    }

    private static MethodSpec.Builder buildGetRenderObserver(final ViewDescriptor descriptor) {
        assert descriptor.trackRender();
        return MethodSpec.methodBuilder(FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver")
                .addAnnotation(AnnotationSpec.builder(OBSERVER_REF_ANNOTATION_CLASSNAME)
                        .addMember("name", "$S", "render")
                        .build())
                .addModifiers(Modifier.ABSTRACT)
                .returns(OBSERVER_CLASSNAME);
    }

    private static MethodSpec.Builder buildStoreDebugDataAsState() {
        final var method = MethodSpec.methodBuilder(FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState")
                .addModifiers(Modifier.PRIVATE);

        final var block = CodeBlock.builder();
        final var flag = FRAMEWORK_INTERNAL_PREFIX + "scheduledDebugStateUpdate";
        block.beginControlFlow("if ( $N )", flag);
        block.addStatement("$N = false", flag);
        block.nextControlFlow("else");
        block.addStatement(
                "final var newState = $T.<$T>uncheckedCast( $T.of() )",
                JS_CLASSNAME,
                JS_PROPERTY_MAP_T_OBJECT_CLASSNAME,
                JS_PROPERTY_MAP_CLASSNAME);
        // Present id as state. Useful to track when instance ids change.
        block.addStatement("newState.set( $S, $N() )", "Arez.id", FRAMEWORK_INTERNAL_PREFIX + "getComponentId");
        block.addStatement("newState.set( $S, $N() )", "Arez.name", FRAMEWORK_INTERNAL_PREFIX + "getComponentName");

        block.addStatement(
                "$T.collectDependencyDebugData( $N(), newState )",
                INTROSPECT_UTIL_CLASSNAME,
                FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver");

        final var onUpdateBlock = CodeBlock.builder();
        onUpdateBlock.beginControlFlow(
                "if ( $T.prepareStateUpdate( newState, $N.state() ) )", INTROSPECT_UTIL_CLASSNAME, NATIVE_VIEW_FIELD);
        onUpdateBlock.addStatement("$N.setState( newState )", NATIVE_VIEW_FIELD);
        // Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
        onUpdateBlock.addStatement("$N.forceUpdate()", NATIVE_VIEW_FIELD);
        onUpdateBlock.addStatement("$N = true", flag);
        onUpdateBlock.endControlFlow();
        block.add(onUpdateBlock.build());
        block.endControlFlow();

        method.addCode(block.build());

        return method;
    }

    private static MethodSpec.Builder buildOnRenderDepsChange(final ViewDescriptor descriptor) {
        assert descriptor.trackRender();
        final var method = MethodSpec.methodBuilder("onRenderDepsChange");

        final var outer = CodeBlock.builder();
        outer.beginControlFlow("if ( $T.IDLE == $N )", VIEW_STATE_CLASSNAME, STATE_FIELD);
        outer.addStatement("$N = $T.SCHEDULED", STATE_FIELD, VIEW_STATE_CLASSNAME);
        if (descriptor.hasObservableInputs()) {
            outer.addStatement(
                    "$N.setState( $T.uncheckedCast( $T.of() ) )",
                    NATIVE_VIEW_FIELD,
                    JS_CLASSNAME,
                    JS_PROPERTY_MAP_CLASSNAME);
        } else {
            outer.addStatement("$N.forceUpdate()", NATIVE_VIEW_FIELD);
        }
        outer.endControlFlow();
        method.addCode(outer.build());
        return method;
    }

    private static MethodSpec.Builder buildIsReadyMethod() {
        return MethodSpec.methodBuilder(IS_READY_METHOD)
                .addModifiers(Modifier.ABSTRACT)
                .returns(TypeName.BOOLEAN)
                .addAnnotation(COMPONENT_STATE_REF_ANNOTATION_CLASSNAME);
    }

    private static MethodSpec.Builder buildInputValidatorMethod(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder(VALIDATE_INPUTS_METHOD)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "inputs", Modifier.FINAL)
                        .build());
        if (descriptor.hasNoValidateMethod()) {
            method.addModifiers(Modifier.STATIC);
        }
        for (final var input : descriptor.getInputs()) {
            final var requiresNonnullInvariant = input.isNonNull() && (input.isRequired() || input.isFromTreeContext());
            if (requiresNonnullInvariant || input.hasValidateMethod()) {
                final var name = input.getName();
                final var rawName = "raw$" + name;
                final var typedName = "typed$" + name;
                method.addStatement(
                        "final $T $N = inputs.get( Inputs.$N )", Object.class, rawName, input.getConstantName());
                if (requiresNonnullInvariant) {
                    final var block = CodeBlock.builder();
                    block.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);

                    if (input.isFromTreeContext()) {
                        final var qualifier = input.getQualifier();
                        if (qualifier.isEmpty()) {
                            block.addStatement(
                                    "$T.apiInvariant( () -> null != $N, () -> \"Context value of type $N is missing"
                                            + " when constructing view named '$N'. Ensure a parent view publishes the"
                                            + " value to the context.\" ) ",
                                    GUARDS_CLASSNAME,
                                    rawName,
                                    input.getType().toString(),
                                    descriptor.getName());
                        } else {
                            block.addStatement(
                                    "$T.apiInvariant( () -> null != $N, () -> \"Context value of type $N with"
                                            + " qualifier '$N' is missing when constructing view named '$N'. Ensure a"
                                            + " parent view publishes the value to the context.\" ) ",
                                    GUARDS_CLASSNAME,
                                    rawName,
                                    input.getType().toString(),
                                    qualifier,
                                    descriptor.getName());
                        }
                    } else {
                        block.addStatement(
                                "$T.apiInvariant( () -> null != $N, () -> \"Required input named '$N' is missing from"
                                    + " view named '$N' so it was either incorrectly omitted or a null value has been"
                                    + " incorrectly specified.\" ) ",
                                GUARDS_CLASSNAME,
                                rawName,
                                input.getName(),
                                descriptor.getName());
                    }
                    block.endControlFlow();
                    method.addCode(block.build());
                }
                if (input.hasValidateMethod()) {
                    final var block = CodeBlock.builder();
                    block.beginControlFlow("if ( null != $N )", rawName);
                    final TypeMirror returnType = input.getType();
                    block.addStatement(
                            "final $T $N = $T.$N( $N )",
                            returnType,
                            typedName,
                            JS_CLASSNAME,
                            getConverter(returnType, input.getElement()),
                            rawName);
                    block.addStatement(
                            "$N( $N )",
                            input.getValidateMethod().getSimpleName().toString(),
                            typedName);
                    block.endControlFlow();
                    method.addCode(block.build());
                }
            }
        }
        return method;
    }

    private static MethodSpec.Builder buildConstructorFnMethod(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("getConstructorFunction")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .returns(VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME);

        final var shouldGenerateLiteLifecycle = descriptor.shouldGenerateLiteLifecycle();
        if (shouldGenerateLiteLifecycle) {
            method.addStatement(
                    "final $T viewConstructor = ( $T.shouldStoreDebugDataAsState() || "
                            + "$T.shouldValidateInputValues() ) ? $T::new : $T::new",
                    VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME,
                    REACT_CLASSNAME,
                    REACT_CLASSNAME,
                    ClassName.bestGuess("NativeView"),
                    ClassName.bestGuess("LiteNativeView"));
        } else {
            method.addStatement(
                    "final $T viewConstructor = $T::new",
                    VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME,
                    ClassName.bestGuess("NativeView"));
        }
        final var codeBlock = CodeBlock.builder();
        codeBlock.beginControlFlow("if ( $T.enableViewNames() )", REACT_CLASSNAME);
        codeBlock.addStatement(
                "$T.asPropertyMap( viewConstructor ).set( \"displayName\", $S )",
                JS_CLASSNAME,
                descriptor.getDisplayName());
        codeBlock.endControlFlow();

        method.addCode(codeBlock.build());

        method.addStatement("return viewConstructor");
        return method;
    }

    private static TypeSpec buildInputsType(final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder("Inputs");

        // Ensure it cannot be subclassed
        builder.addModifiers(Modifier.FINAL);
        builder.addModifiers(Modifier.STATIC);

        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        // These fields have been moved to a separate class to avoid a <clinit> on containing class
        final var inputs = descriptor.getInputs();
        final var inputCount = inputs.size();
        for (var i = 0; i < inputCount; i++) {
            builder.addField(buildInputKeyConstantField(inputs.get(i), i).build());
        }

        return builder.build();
    }

    private static TypeSpec buildFactory() {
        final var builder = TypeSpec.classBuilder("Factory");

        // Ensure it cannot be subclassed
        builder.addModifiers(Modifier.FINAL);
        builder.addModifiers(Modifier.STATIC);

        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        // This field has been moved to a separate class to avoid a <clinit> on containing class as that forces
        // every call to React_MyView to first check <clinit> has been invoked.
        final var field = FieldSpec.builder(
                        VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME, "TYPE", Modifier.STATIC, Modifier.FINAL)
                .initializer("getConstructorFunction()");
        builder.addField(field.build());

        return builder.build();
    }

    private static TypeSpec buildNativeView(final ViewDescriptor descriptor, final boolean lite) {
        final var builder = TypeSpec.classBuilder((lite ? "Lite" : "") + "NativeView");

        // Ensure it cannot be subclassed
        builder.addModifiers(Modifier.FINAL);
        builder.addModifiers(Modifier.STATIC);
        builder.addModifiers(Modifier.PRIVATE);

        builder.superclass(NATIVE_VIEW_CLASSNAME);
        builder.addTypeVariables(GeneratorUtil.getTypeArgumentsAsNames(descriptor.getDeclaredType()));

        final TypeName viewFieldType;
        if (descriptor.getElement().getTypeParameters().isEmpty()) {
            viewFieldType = descriptor.getEnhancedClassName();
        } else {
            final var typeNames = GeneratorUtil.getTypeArgumentsAsNames(descriptor.getDeclaredType())
                    .toArray(new TypeName[0]);
            viewFieldType = ParameterizedTypeName.get(descriptor.getEnhancedClassName(), typeNames);
        }
        final var disposableInputs = descriptor.getImmutableInputs().stream()
                .filter(InputDescriptor::isDisposable)
                .toList();

        builder.addField(FieldSpec.builder(viewFieldType, VIEW_FIELD, Modifier.PRIVATE, Modifier.FINAL)
                .addAnnotation(
                        disposableInputs.isEmpty()
                                ? ClassName.get("org.jspecify.annotations", "NonNull")
                                : GeneratorUtil.NULLABLE_CLASSNAME)
                .build());

        if (lite) {
            if (descriptor.generateComponentDidMountInLiteLifecycle()) {
                builder.addSuperinterface(ON_COMPONENT_DID_MOUNT_CLASSNAME);
            }
            if (descriptor.generateComponentDidUpdateInLiteLifecycle()) {
                builder.addSuperinterface(ON_COMPONENT_DID_UPDATE_CLASSNAME);
            }
            if (descriptor.generateShouldComponentUpdateInLiteLifecycle()) {
                builder.addSuperinterface(ON_COMPONENT_SHOULD_UPDATE_CLASSNAME);
            }
            if (descriptor.generateComponentWillUnmountInLiteLifecycle()) {
                builder.addSuperinterface(ON_COMPONENT_WILL_UNMOUNT_CLASSNAME);
            }
        } else {
            if (descriptor.generateComponentDidMount()) {
                builder.addSuperinterface(ON_COMPONENT_DID_MOUNT_CLASSNAME);
            }
            if (descriptor.generateComponentDidUpdate()) {
                builder.addSuperinterface(ON_COMPONENT_DID_UPDATE_CLASSNAME);
            }
            // We currently always generate shouldComponentUpdate. If we happen to be a non-tracking view
            // with no mutable inputs and no inputs that are validated, then we just return false to block re-render
            builder.addSuperinterface(ON_COMPONENT_SHOULD_UPDATE_CLASSNAME);
            if (descriptor.generateComponentWillUnmount()) {
                builder.addSuperinterface(ON_COMPONENT_WILL_UNMOUNT_CLASSNAME);
            }
        }
        if (descriptor.generateComponentPreUpdate()) {
            builder.addSuperinterface(ON_GET_SNAPSHOT_BEFORE_UPDATE_CLASSNAME);
        }
        if (descriptor.generateComponentDidCatch()) {
            builder.addSuperinterface(ON_COMPONENT_DID_CATCH_CLASSNAME);
        }

        // build the constructor
        {
            final var inputs = ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "inputs", Modifier.FINAL)
                    .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME);
            final var method = MethodSpec.constructorBuilder()
                    .addParameter(inputs.build())
                    .addAnnotation(JS_CONSTRUCTOR_CLASSNAME);
            method.addStatement("super( inputs )");
            if (!descriptor.getConstructorInputs().isEmpty()) {
                method.addStatement("assert null != inputs");
            }

            final var prefix = new StringBuilder();
            final var params = new ArrayList<>();
            params.add(VIEW_FIELD);
            for (final var input : disposableInputs) {
                if (!prefix.isEmpty()) {
                    prefix.append(" || ");
                }
                if (!input.getType().getKind().isPrimitive() && input.getNullness() == Nullability.Nullness.NULLABLE) {
                    prefix.append("$T.isDisposed( $T.uncheckedCast( inputs.getAsAny( Inputs.$N ) ) )");
                    params.add(DISPOSABLE_CLASSNAME);
                    params.add(JS_CLASSNAME);
                    params.add(input.getConstantName());
                } else {
                    prefix.append("$T.isDisposed( inputs.getAsAny( Inputs.$N ).$N() )");
                    params.add(DISPOSABLE_CLASSNAME);
                    params.add(input.getConstantName());
                    params.add(getConverter(input.getType(), input.getElement()));
                }
            }

            if (descriptor.needsInjection()) {
                if (prefix.isEmpty()) {
                    method.addStatement("$N = $T.create( this )", VIEW_FIELD, descriptor.getFactoryClassName());
                } else {
                    params.add(descriptor.getFactoryClassName());
                    method.addStatement("$N = " + prefix + " ? null : $T.create( this )", params.toArray());
                }
            } else {
                final var infix = BuilderGenerator.asTypeArgumentsInfix(descriptor.getDeclaredType());
                for (final var input : descriptor.getConstructorInputs()) {
                    final var localName = input.getName();
                    final var convertMethodName = getConverter(input.getType(), input.getElement());
                    if (!input.getType().getKind().isPrimitive()
                            && input.getNullness() == Nullability.Nullness.NULLABLE) {
                        final var extraction = CodeBlock.builder();
                        extraction.addStatement("$T $N", input.getTypeName().withoutAnnotations(), localName);
                        extraction.beginControlFlow("if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME);
                        extraction.addStatement(
                                "$N = null != inputs.getAsAny( Inputs.$N ) ? "
                                        + "inputs.getAsAny( Inputs.$N ).$N() : null",
                                localName,
                                input.getConstantName(),
                                input.getConstantName(),
                                convertMethodName);
                        extraction.nextControlFlow("else");
                        extraction.addStatement(
                                "$N = $T.uncheckedCast( inputs.getAsAny( Inputs.$N ) )",
                                localName,
                                JS_CLASSNAME,
                                input.getConstantName());
                        extraction.endControlFlow();
                        method.addCode(extraction.build());
                    } else {
                        method.addStatement(
                                "final $T $N = inputs.getAsAny( Inputs.$N ).$N()",
                                input.getTypeName().withoutAnnotations(),
                                localName,
                                input.getConstantName(),
                                convertMethodName);
                    }
                }
                if (prefix.isEmpty()) {
                    final var statement = new StringBuilder("$N = new $T" + infix + "( this");
                    final var args = new ArrayList<>();
                    args.add(VIEW_FIELD);
                    args.add(descriptor.getArezClassName());
                    for (final var input : descriptor.getConstructorInputs()) {
                        statement.append(", $N");
                        args.add(input.getName());
                    }
                    statement.append(" )");
                    method.addStatement(statement.toString(), args.toArray());
                } else {
                    final var statement = new StringBuilder("$N = " + prefix + " ? null : new $T" + infix + "( this");
                    params.add(descriptor.getArezClassName());
                    for (final var input : descriptor.getConstructorInputs()) {
                        statement.append(", $N");
                        params.add(input.getName());
                    }
                    statement.append(" )");
                    method.addStatement(statement.toString(), params.toArray());
                }
            }

            if (descriptor.shouldValidateInputs()) {
                final var block = CodeBlock.builder();
                block.beginControlFlow("if ( $T.shouldValidateInputValues() )", REACT_CLASSNAME);
                block.addStatement("assert null != inputs");
                if (descriptor.hasNoValidateMethod()) {
                    block.addStatement("$T.$N( inputs )", descriptor.getEnhancedClassName(), VALIDATE_INPUTS_METHOD);
                } else {
                    if (descriptor.hasDisposableInput()) {
                        block.beginControlFlow("if ( null != $N )", VIEW_FIELD);
                    }
                    block.addStatement("$N.$N( inputs )", VIEW_FIELD, VALIDATE_INPUTS_METHOD);
                    if (descriptor.hasDisposableInput()) {
                        block.endControlFlow();
                    }
                }
                block.endControlFlow();
                method.addCode(block.build());
            }

            builder.addMethod(method.build());
        }

        if (lite ? descriptor.generateComponentDidMountInLiteLifecycle() : descriptor.generateComponentDidMount()) {
            // We add this so the DevTool sees any debug data saved
            builder.addMethod(buildNativeViewDidMount(descriptor));
        }
        if (descriptor.generateShouldComponentUpdateInLiteLifecycle() && !descriptor.generateShouldComponentUpdate()) {
            builder.addMethod(buildNativeShouldComponentUpdate(descriptor, true));
        } else if (lite
                ? descriptor.generateShouldComponentUpdateInLiteLifecycle()
                : descriptor.generateShouldComponentUpdate()) {
            builder.addMethod(buildNativeShouldComponentUpdate(descriptor, false));
        }

        if (descriptor.generateComponentPreUpdate()) {
            builder.addMethod(buildNativeViewPreUpdate(descriptor));
        }
        if (lite ? descriptor.generateComponentDidUpdateInLiteLifecycle() : descriptor.generateComponentDidUpdate()) {
            // We add this for Arez views so the DevTool sees any debug data saved
            builder.addMethod(buildNativeViewDidUpdate(descriptor));
        }
        if (lite
                ? descriptor.generateComponentWillUnmountInLiteLifecycle()
                : descriptor.generateComponentWillUnmount()) {
            builder.addMethod(buildNativeViewWillUnmount(descriptor));
        }
        if (descriptor.generateComponentDidCatch()) {
            builder.addMethod(buildNativeViewDidCatch(descriptor).build());
        }

        builder.addMethod(buildNativeRender(descriptor));

        return builder.build();
    }

    private static MethodSpec buildNativeRender(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("render")
                .addAnnotation(Override.class)
                .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .returns(REACT_NODE_CLASSNAME);
        if (descriptor.shouldGenerateRender()) {
            if (descriptor.hasDisposableInput()) {
                final var block = CodeBlock.builder();
                block.beginControlFlow(
                        "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
                block.addStatement("return $N.$N()", VIEW_FIELD, RENDER_METHOD);
                block.nextControlFlow("else");
                block.addStatement("return null");
                block.endControlFlow();
                method.addCode(block.build());
            } else {
                method.addStatement("return $N.$N()", VIEW_FIELD, RENDER_METHOD);
            }
        } else {
            method.addStatement("return null");
        }
        return method.build();
    }

    private static MethodSpec buildNativeViewDidMount(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("componentDidMount")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            block.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_DID_MOUNT_METHOD);
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_DID_MOUNT_METHOD);
        }
        return method.build();
    }

    private static MethodSpec buildNativeShouldComponentUpdate(
            final ViewDescriptor descriptor, final boolean fastReturnFalse) {
        final var method = MethodSpec.methodBuilder("shouldComponentUpdate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextInputs", Modifier.FINAL)
                        .build());
        if (fastReturnFalse) {
            method.addStatement("return false");
        } else if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            block.addStatement("return $N.$N( nextInputs )", VIEW_FIELD, SHOULD_COMPONENT_UPDATE_METHOD);
            block.nextControlFlow("else");
            block.addStatement("return false");
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement("return $N.$N( nextInputs )", VIEW_FIELD, SHOULD_COMPONENT_UPDATE_METHOD);
        }
        return method.build();
    }

    private static MethodSpec buildNativeViewPreUpdate(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("getSnapshotBeforeUpdate")
                .addAnnotation(Override.class)
                .addAnnotation(GeneratorUtil.NULLABLE_CLASSNAME)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .returns(TypeName.get(Object.class))
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL)
                        .build())
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevState", Modifier.FINAL)
                        .build());
        if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            block.addStatement(
                    "$N.$N(" + (descriptor.hasPreUpdateOnInputChange() ? " prevInputs " : "") + ")",
                    VIEW_FIELD,
                    COMPONENT_PRE_UPDATE_METHOD);
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement(
                    "$N.$N(" + (descriptor.hasPreUpdateOnInputChange() ? " prevInputs " : "") + ")",
                    VIEW_FIELD,
                    COMPONENT_PRE_UPDATE_METHOD);
        }
        method.addStatement("return null");
        return method.build();
    }

    private static MethodSpec buildNativeViewDidUpdate(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("componentDidUpdate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL)
                        .build());
        if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            if (descriptor.hasPostUpdateOnInputChange()) {
                block.addStatement("$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD);
            } else {
                block.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD);
            }
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            if (descriptor.hasPostUpdateOnInputChange()) {
                method.addStatement("$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD);
            } else {
                method.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD);
            }
        }
        return method.build();
    }

    private static MethodSpec buildNativeViewWillUnmount(final ViewDescriptor descriptor) {
        final var method = MethodSpec.methodBuilder("componentWillUnmount")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            block.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_WILL_UNMOUNT_METHOD);
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement("$N.$N()", VIEW_FIELD, COMPONENT_WILL_UNMOUNT_METHOD);
        }
        return method.build();
    }

    private static MethodSpec.Builder buildNativeViewDidCatch(final ViewDescriptor descriptor) {
        final var onError = descriptor.getOnError();
        assert null != onError;
        final var method = MethodSpec.methodBuilder("componentDidCatch")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(JS_ERROR_CLASSNAME, "error", Modifier.FINAL)
                        .build())
                .addParameter(ParameterSpec.builder(REACT_ERROR_INFO_CLASSNAME, "info", Modifier.FINAL)
                        .build());

        final var parameters = onError.getParameters();
        final var args = parameters.isEmpty()
                ? "()"
                : "( "
                        + parameters.stream()
                                .map(p -> GeneratorUtil.getTypeName(p.asType())
                                                .toString()
                                                .equals(Constants.JS_ERROR_CLASSNAME)
                                        ? "error"
                                        : "info")
                                .collect(Collectors.joining(", "))
                        + " )";

        if (descriptor.hasDisposableInput()) {
            final var block = CodeBlock.builder();
            block.beginControlFlow(
                    "if ( null != $N && $T.isNotDisposed( $N ) )", VIEW_FIELD, DISPOSABLE_CLASSNAME, VIEW_FIELD);
            block.addStatement("$N.$N" + args, VIEW_FIELD, onError.getSimpleName());
            block.endControlFlow();
            method.addCode(block.build());
        } else {
            method.addStatement("$N.$N" + args, VIEW_FIELD, onError.getSimpleName());
        }
        return method;
    }

    private static void addRenderPreludeObservationCheck(
            final MethodSpec.Builder method, final PreludeChecksDescriptor candidate, final String varName) {
        final ObserveMode mode = candidate.observeMode();
        if (ObserveMode.NO_OBSERVE != mode) {
            final var block = CodeBlock.builder();
            if (ObserveMode.RUNTIME_CHECK == mode) {
                block.beginControlFlow(
                        "if ( $N instanceof $T && !$T.observe( $N ) )",
                        varName,
                        COMPONENT_OBSERVABLE_CLASSNAME,
                        COMPONENT_OBSERVABLE_CLASSNAME,
                        varName);
            } else if (ObserveMode.OBSERVE_NONNULL == mode) {
                block.beginControlFlow("if ( !$T.observe( $N ) )", COMPONENT_OBSERVABLE_CLASSNAME, varName);
            } else if (ObserveMode.OBSERVE_NULLABLE == mode) {
                block.beginControlFlow("if ( !$T.maybeObserve( $N ) )", COMPONENT_OBSERVABLE_CLASSNAME, varName);
            }

            block.addStatement("return null");
            block.endControlFlow();
            method.addCode(block.build());
        }
    }

    private static TypeSpec buildContextHolder(final ViewDescriptor descriptor) {
        final var builder = TypeSpec.classBuilder("ContextHolder");
        GeneratorUtil.copyTypeParameters(descriptor.getElement(), builder);

        builder.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);

        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        for (final var publish : descriptor.getPublishDescriptors()) {
            final var type = GeneratorUtil.getTypeName(publish.getMethodType().getReturnType())
                    .withoutAnnotations()
                    .box();
            final var field = FieldSpec.builder(
                    ParameterizedTypeName.get(CONTEXT_CLASSNAME, type),
                    "CONTEXT_" + publish.getMethod().getSimpleName(),
                    Modifier.PRIVATE,
                    Modifier.STATIC,
                    Modifier.FINAL);
            final var qualifier = publish.getQualifier();
            if (qualifier.isEmpty()) {
                field.initializer("$T.get( $T.class )", CONTEXTS_CLASSNAME, type);
            } else {
                field.initializer("$T.get( $T.class, $S )", CONTEXTS_CLASSNAME, type, qualifier);
            }
            builder.addField(field.build());
        }

        return builder.build();
    }
}
