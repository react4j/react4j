package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

final class ViewDescriptor {

    private final String _name;

    private final TypeElement _element;

    private final ViewType _type;

    private final boolean _exportBuilder;
    private final boolean _sting;
    private final boolean _hasConstructor;
    private final boolean _hasPostConstruct;
    private final boolean _shouldSetDefaultPriority;

    private final ExecutableElement _constructor;

    @Nullable
    private ExecutableElement _render;

    @Nullable
    private ExecutableElement _preUpdate;

    @Nullable
    private ExecutableElement _postRender;

    @Nullable
    private ExecutableElement _postUpdate;

    @Nullable
    private ExecutableElement _postMount;

    @Nullable
    private ExecutableElement _onError;
    /**
     * Methods that are inputs accessors.
     * These should be implemented as accesses to the underlying inputs value.
     */
    @Nullable
    private List<InputDescriptor> _inputs;
    /**
     * Methods that are inputs accessors.
     * These should be implemented as accesses to the underlying inputs value.
     */
    @Nullable
    private List<OnInputChangeDescriptor> _onInputChangeDescriptors;
    /**
     * Descriptors for methods annotated by @ScheduleRender.
     */
    @Nullable
    private List<ScheduleRenderDescriptor> _scheduleRenderDescriptors;
    /**
     * Descriptors for methods annotated by @Publish.
     */
    @Nullable
    private List<PublishDescriptor> _publishDescriptors;
    /**
     * Descriptors for methods annotated by @PreRender.
     */
    @Nullable
    private List<RenderHookDescriptor> _preRenderHooks;
    /**
     * Descriptors for methods annotated by @PostRender.
     */
    @Nullable
    private List<RenderHookDescriptor> _postRenderHooks;

    @Nullable
    private Boolean _validateInputs;

    @Nullable
    private Boolean _viewAccessesDeprecatedElements;

    @Nullable
    private Boolean _builderAccessesDeprecatedElements;

    @Nullable
    private List<PreludeChecksDescriptor> _preludeCheckCandidates;

    @Nullable
    private Boolean _hasDisposableInput;

    @Nullable
    private Boolean _hasNoValidateMethod;

    ViewDescriptor(
            final String name,
            final TypeElement element,
            final ExecutableElement constructor,
            final ViewType type,
            final boolean exportBuilder,
            final boolean sting,
            final boolean hasConstructor,
            final boolean hasPostConstruct,
            final boolean shouldSetDefaultPriority) {
        _name = name;
        _element = element;
        _constructor = constructor;
        _type = type;
        _exportBuilder = exportBuilder;
        _sting = sting;
        _hasConstructor = hasConstructor;
        _hasPostConstruct = hasPostConstruct;
        _shouldSetDefaultPriority = shouldSetDefaultPriority;
    }

    String keySuffix() {
        return "_" + _element.getSimpleName() + "_"
                + shortSha(_element.getQualifiedName().toString());
    }

    ExecutableElement getConstructor() {
        return _constructor;
    }

    boolean enableSting() {
        return _sting;
    }

    boolean exportBuilder() {
        return _exportBuilder;
    }

    boolean hasConstructor() {
        return _hasConstructor;
    }

    boolean hasPostConstruct() {
        return _hasPostConstruct;
    }

    boolean shouldSetDefaultPriority() {
        return _shouldSetDefaultPriority;
    }

    boolean requireRender() {
        return ViewType.NO_RENDER != _type;
    }

    String getPackageName() {
        return GeneratorUtil.getQualifiedPackageName(_element);
    }

    String getName() {
        return _name;
    }

    String getDisplayName() {
        return trimViewSuffix(getName());
    }

    ClassName getClassName() {
        return ClassName.get(getElement());
    }

    TypeElement getElement() {
        return _element;
    }

    DeclaredType getDeclaredType() {
        return (DeclaredType) _element.asType();
    }

    ClassName getEnhancedClassName() {
        return GeneratorUtil.getGeneratedClassName(_element, "React4j_", "");
    }

    ClassName getBuilderClassName() {
        return ClassName.get(getPackageName(), getBuilderSimpleName());
    }

    String getBuilderSimpleName() {
        return trimViewSuffix(GeneratorUtil.getGeneratedSimpleClassName(_element, "", "")) + "Builder";
    }

    ClassName getFactoryClassName() {
        return GeneratorUtil.getGeneratedClassName(_element, "React4j_", "Factory");
    }

    private static String trimViewSuffix(final String name) {
        return name.endsWith("View") ? name.substring(0, name.length() - "View".length()) : name;
    }

    ClassName getArezClassName() {
        final String simpleName = "Arez_" + GeneratorUtil.getGeneratedSimpleClassName(_element, "React4j_", "");
        return ClassName.get(getPackageName(), simpleName);
    }

    TypeName getViewType() {
        final var typeNames =
                getDeclaredType().getTypeArguments().stream().map(TypeName::get).toList();
        if (!typeNames.isEmpty()) {
            return ParameterizedTypeName.get(ClassName.get(getElement()), typeNames.toArray(new TypeName[0]));
        } else {
            return ClassName.get(getElement());
        }
    }

    boolean needsInjection() {
        return !_constructor.getParameters().stream()
                .filter(parameter -> !AnnotationsUtil.hasAnnotationOfType(parameter, Constants.INPUT_CLASSNAME))
                .toList()
                .isEmpty();
    }

    boolean trackRender() {
        return (ViewType.MAYBE_TRACKING == _type || ViewType.TRACKING == _type) && hasRender();
    }

    ViewType getType() {
        return _type;
    }

    int syntheticKeyParts() {
        return (int) getInputs().stream().filter(InputDescriptor::isImmutable).count();
    }

    boolean hasDisposableInput() {
        if (null == _hasDisposableInput) {
            _hasDisposableInput = getInputs().stream().anyMatch(InputDescriptor::isDisposable);
        }
        return _hasDisposableInput;
    }

    boolean hasNoValidateMethod() {
        if (null == _hasNoValidateMethod) {
            _hasNoValidateMethod = getInputs().stream().noneMatch(InputDescriptor::hasValidateMethod);
        }
        return _hasNoValidateMethod;
    }

    List<InputDescriptor> getImmutableInputs() {
        assert null != _inputs;
        return _inputs.stream().filter(InputDescriptor::isImmutable).collect(Collectors.toList());
    }

    List<InputDescriptor> getConstructorInputs() {
        return getConstructor().getParameters().stream()
                .map(parameter -> (VariableElement) parameter)
                .filter(parameter -> AnnotationsUtil.hasAnnotationOfType(parameter, Constants.INPUT_CLASSNAME))
                .map(parameter -> {
                    final InputDescriptor input =
                            findInputNamed(parameter.getSimpleName().toString());
                    assert null != input;
                    return input;
                })
                .toList();
    }

    List<VariableElement> getInjectableConstructorParameters() {
        return getConstructor().getParameters().stream()
                .map(parameter -> (VariableElement) parameter)
                .filter(parameter -> !AnnotationsUtil.hasAnnotationOfType(parameter, Constants.INPUT_CLASSNAME))
                .toList();
    }

    List<InputDescriptor> getUpdateOnChangeInputs() {
        return getInputs().stream()
                .filter(InputDescriptor::shouldUpdateOnChange)
                // Observable properties already checked above
                .filter(p -> !p.isObservable())
                .toList();
    }

    List<InputDescriptor> getObservableInputs() {
        return getInputs().stream().filter(InputDescriptor::isObservable).toList();
    }

    List<InputDescriptor> getInputs() {
        assert null != _inputs;
        return _inputs;
    }

    void setInputs(final List<InputDescriptor> events) {
        _inputs = events;
    }

    List<PreludeChecksDescriptor> getPreludeCheckCandidates() {
        assert null != _preludeCheckCandidates;
        return _preludeCheckCandidates;
    }

    void setPreludeCheckCandidates(final List<PreludeChecksDescriptor> preludeCheckCandidates) {
        _preludeCheckCandidates = preludeCheckCandidates;
    }

    @Nullable
    InputDescriptor findInputNamed(final String name) {
        return getInputs().stream()
                .filter(p -> p.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    /**
     * Needs to be invoked after all the inputs have been completely constructed.
     */
    void sortInputs() {
        assert null != _inputs;
        _inputs.sort(InputComparator.COMPARATOR);
    }

    List<OnInputChangeDescriptor> getPreUpdateOnInputChangeDescriptors() {
        return getOnInputChangeDescriptors().stream()
                .filter(OnInputChangeDescriptor::isPreUpdate)
                .collect(Collectors.toList());
    }

    List<OnInputChangeDescriptor> getPostUpdateOnInputChangeDescriptors() {
        return getOnInputChangeDescriptors().stream()
                .filter(o -> !o.isPreUpdate())
                .collect(Collectors.toList());
    }

    private List<OnInputChangeDescriptor> getOnInputChangeDescriptors() {
        assert null != _onInputChangeDescriptors;
        return _onInputChangeDescriptors;
    }

    void setOnInputChangeDescriptors(List<OnInputChangeDescriptor> onInputChangeDescriptors) {
        _onInputChangeDescriptors = onInputChangeDescriptors;
    }

    List<ScheduleRenderDescriptor> getScheduleRenderDescriptors() {
        assert null != _scheduleRenderDescriptors;
        return _scheduleRenderDescriptors;
    }

    void setScheduleRenderDescriptors(final List<ScheduleRenderDescriptor> scheduleRenderDescriptors) {
        _scheduleRenderDescriptors = scheduleRenderDescriptors;
    }

    List<PublishDescriptor> getPublishDescriptors() {
        assert null != _publishDescriptors;
        return _publishDescriptors;
    }

    void setPublishDescriptors(final List<PublishDescriptor> publishDescriptors) {
        _publishDescriptors = publishDescriptors;
    }

    List<RenderHookDescriptor> getPreRenderDescriptors() {
        assert null != _preRenderHooks;
        return _preRenderHooks;
    }

    void setPreRenderDescriptors(final List<RenderHookDescriptor> preRenderDescriptors) {
        _preRenderHooks = preRenderDescriptors;
    }

    List<RenderHookDescriptor> getPostRenderDescriptors() {
        assert null != _postRenderHooks;
        return _postRenderHooks;
    }

    void setPostRenderDescriptors(final List<RenderHookDescriptor> postRenderDescriptors) {
        _postRenderHooks = postRenderDescriptors;
    }

    boolean hasObservableInputs() {
        return getInputs().stream().anyMatch(InputDescriptor::isObservable);
    }

    @Nullable
    ExecutableElement getPreUpdate() {
        return _preUpdate;
    }

    void setPreUpdate(final ExecutableElement preUpdate) throws ProcessorException {
        if (null != _preUpdate) {
            throw new ProcessorException(
                    "@PreUpdate target duplicates existing method named " + _preUpdate.getSimpleName(), preUpdate);
        } else {
            _preUpdate = preUpdate;
        }
    }

    boolean shouldGenerateRender() {
        assert null != _preRenderHooks;
        assert null != _postRenderHooks;
        return hasRender() || !_preRenderHooks.isEmpty() || !_postRenderHooks.isEmpty();
    }

    boolean hasRender() {
        return null != _render;
    }

    ExecutableElement getRender() {
        assert null != _render;
        return _render;
    }

    void setRender(final ExecutableElement render) {
        if (null != _render) {
            throw new ProcessorException(
                    MemberChecks.mustNot(
                            Constants.RENDER_CLASSNAME,
                            "be present when another method named " + _render.getSimpleName()
                                    + " exists with the same annotation"),
                    render);
        }
        _render = render;
    }

    @Nullable
    ExecutableElement getPostRender() {
        return _postRender;
    }

    void setPostRender(final ExecutableElement postRender) throws ProcessorException {
        if (null != _postRender) {
            throw new ProcessorException(
                    "@PostMountOrUpdate target duplicates existing method named " + _postRender.getSimpleName(),
                    postRender);
        } else {
            _postRender = postRender;
        }
    }

    @Nullable
    ExecutableElement getPostUpdate() {
        return _postUpdate;
    }

    void setPostUpdate(final ExecutableElement postUpdate) throws ProcessorException {
        if (null != _postUpdate) {
            throw new ProcessorException(
                    "@PostUpdate target duplicates existing method named " + _postUpdate.getSimpleName(), postUpdate);
        } else {
            _postUpdate = postUpdate;
        }
    }

    @Nullable
    ExecutableElement getPostMount() {
        return _postMount;
    }

    void setPostMount(final ExecutableElement postMount) throws ProcessorException {
        if (null != _postMount) {
            throw new ProcessorException(
                    "@PostMount target duplicates existing method named " + _postMount.getSimpleName(), postMount);
        } else {
            _postMount = postMount;
        }
    }

    @Nullable
    ExecutableElement getOnError() {
        return _onError;
    }

    void setOnError(final ExecutableElement onError) throws ProcessorException {

        if (null != _onError) {
            throw new ProcessorException(
                    "@OnError target duplicates existing method named " + _onError.getSimpleName(), onError);
        } else {
            _onError = onError;
        }
    }

    private boolean shouldGenerateLifecycle() {
        return generateComponentDidMount()
                || generateShouldComponentUpdate()
                || generateComponentPreUpdate()
                || generateComponentDidUpdate()
                || generateComponentWillUnmount()
                || generateComponentDidCatch();
    }

    boolean shouldGenerateLiteLifecycle() {
        // A "Lite" lifecycle is the one used in production code and is a subset of the non-lite lifecycle. The
        // non-lite exists if there are extra lifecycle methods generated for collecting debug information.
        // If both modes have the same lifecycle hooks then we avoid generating a lite variant.
        return (generateComponentDidUpdateInLiteLifecycle() != generateComponentDidUpdate()
                        || generateComponentWillUnmountInLiteLifecycle() != generateComponentWillUnmount()
                        || generateComponentDidMountInLiteLifecycle() != generateComponentDidMount())
                && shouldGenerateLifecycle();
    }

    boolean generateShouldComponentUpdate() {
        return trackRender()
                || hasObservableInputs()
                || !getUpdateOnChangeInputs().isEmpty()
                || shouldValidateInputs();
    }

    boolean generateShouldComponentUpdateInLiteLifecycle() {
        // We currently always generate shouldComponentUpdate. If we happen to be a non-tracking view
        // with no mutable inputs and no inputs that are validated, then we just return false to block re-render
        return true;
    }

    boolean generateComponentDidCatch() {
        return null != _onError;
    }

    boolean generateComponentWillUnmountInLiteLifecycle() {
        return true; // type == STATELESS || type == TRACKING || MAYBE_TRACKING
    }

    boolean generateComponentWillUnmount() {
        return true; // type == STATELESS || type == TRACKING || MAYBE_TRACKING
    }

    boolean generateComponentPreUpdate() {
        return hasPreUpdateOnInputChange() || null != _preUpdate;
    }

    boolean generateComponentDidMount() {
        return generateComponentDidMountInLiteLifecycle()
                ||
                // We do it when tracking render so we can store debug information in state
                trackRender();
    }

    boolean generateComponentDidMountInLiteLifecycle() {
        return null != _postMount || null != _postRender;
    }

    boolean hasPreUpdateOnInputChange() {
        return !getPreUpdateOnInputChangeDescriptors().isEmpty();
    }

    boolean hasPostUpdateOnInputChange() {
        return !getPostUpdateOnInputChangeDescriptors().isEmpty();
    }

    boolean generateComponentDidUpdate() {
        return generateComponentDidUpdateInLiteLifecycle()
                ||
                // We do it when tracking render so we can store debug information in state
                trackRender();
    }

    boolean generateComponentDidUpdateInLiteLifecycle() {
        return hasPostUpdateOnInputChange() || null != _postUpdate || null != _postRender;
    }

    boolean shouldValidateInputs() {
        if (null == _validateInputs) {
            _validateInputs = getInputs().stream()
                    .anyMatch(input -> input.hasValidateMethod()
                            || (input.isNonNull() && (input.isRequired() || input.isFromTreeContext())));
        }
        return _validateInputs;
    }

    boolean viewAccessesDeprecatedElements() {
        if (null == _viewAccessesDeprecatedElements) {
            _viewAccessesDeprecatedElements = isDeprecated(_element)
                    || isDeprecated(_constructor)
                    || isDeprecated(_preUpdate)
                    || isDeprecated(_postRender)
                    || isDeprecated(_postMount)
                    || isDeprecated(_postUpdate)
                    || isDeprecated(_onError)
                    || getInputs().stream()
                            .anyMatch(p -> isDeprecated(p.getElement())
                                    || p.hasValidateMethod() && isDeprecated(p.getValidateMethod()))
                    || getPostUpdateOnInputChangeDescriptors().stream().anyMatch(d -> isDeprecated(d.getMethod()));
        }
        return _viewAccessesDeprecatedElements;
    }

    boolean builderAccessesDeprecatedElements() {
        if (null == _builderAccessesDeprecatedElements) {
            _builderAccessesDeprecatedElements = isDeprecated(_element)
                    || getInputs().stream()
                            .anyMatch(p -> p.hasDefaultMethod() && isDeprecated(p.getDefaultMethod())
                                    || p.hasDefaultField() && isDeprecated(p.getDefaultField()));
        }
        return _builderAccessesDeprecatedElements;
    }

    private boolean isDeprecated(@Nullable final Element element) {
        return null != element && null != element.getAnnotation(Deprecated.class);
    }

    private String shortSha(final String text) {
        try {
            final MessageDigest algo = MessageDigest.getInstance("SHA-1");
            final byte[] digest = algo.digest(text.getBytes());
            final var sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                final byte value = digest[i];
                sb.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
