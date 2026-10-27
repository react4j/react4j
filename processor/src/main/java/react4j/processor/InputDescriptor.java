package react4j.processor;

import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.TypeName;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;
import react4j.processor.Nullability.Nullness;

@SuppressWarnings("Duplicates")
final class InputDescriptor {

    private final ViewDescriptor _descriptor;

    private final String _name;

    private final String _qualifier;

    private final Element _element;

    private final TypeMirror _type;

    @Nullable
    private final ExecutableElement _method;

    @Nullable
    private final ExecutableType _methodType;

    @Nullable
    private final VariableElement _parameter;

    private final boolean _fromTreeContext;
    private final boolean _shouldUpdateOnChange;
    private final boolean _observable;
    private final boolean _disposable;

    @Nullable
    private final ImmutableInputKeyStrategy _immutableInputKeyStrategy;

    private final String _requiredValue;

    private boolean _onChangePresent;
    private boolean _suppressMutableInputAccessedInPostConstruct;

    @Nullable
    private VariableElement _defaultField;

    @Nullable
    private ExecutableElement _defaultMethod;

    @Nullable
    private ExecutableElement _validateMethod;
    /**
     * Flag set to true if input is optional.
     */
    private boolean _optional;

    @Nullable
    private Nullness _nullness;

    @Nullable
    private InputEnhancer _enhancer;

    @Nullable
    InputEnhancer getEnhancer() {
        return _enhancer;
    }

    void setEnhancer(final InputEnhancer enhancer) {
        _enhancer = enhancer;
    }

    InputDescriptor(
            final ViewDescriptor descriptor,
            final String name,
            final String qualifier,
            final Element element,
            final TypeMirror type,
            @Nullable final ExecutableElement method,
            @Nullable final ExecutableType methodType,
            @Nullable final VariableElement parameter,
            final boolean fromTreeContext,
            final boolean shouldUpdateOnChange,
            final boolean observable,
            final boolean disposable,
            @Nullable final ImmutableInputKeyStrategy immutableInputKeyStrategy,
            final String requiredValue) {
        _descriptor = descriptor;
        _name = name;
        _qualifier = qualifier;
        _element = element;
        _type = type;
        _method = method;
        _methodType = methodType;
        _parameter = parameter;
        _fromTreeContext = fromTreeContext;
        _shouldUpdateOnChange = shouldUpdateOnChange;
        _observable = observable;
        _disposable = disposable;
        _immutableInputKeyStrategy = immutableInputKeyStrategy;
        _requiredValue = requiredValue;
    }

    String getName() {
        return _name;
    }

    String getQualifier() {
        return _qualifier;
    }

    Element getElement() {
        return _element;
    }

    TypeMirror getType() {
        return _type;
    }

    ViewDescriptor getDescriptor() {
        return _descriptor;
    }

    boolean isMethodInput() {
        return !isImmutable();
    }

    @Nullable
    ExecutableElement getMethod() {
        return _method;
    }

    @Nullable
    ExecutableType getMethodType() {
        return _methodType;
    }

    @Nullable
    VariableElement getParameter() {
        return _parameter;
    }

    boolean shouldUpdateOnChange() {
        return _shouldUpdateOnChange;
    }

    boolean isObservable() {
        return _observable;
    }

    boolean isDisposable() {
        return _disposable;
    }

    boolean isImmutable() {
        return null != _immutableInputKeyStrategy;
    }

    String getRequiredValue() {
        return _requiredValue;
    }

    void markAsOnChangePresent() {
        _onChangePresent = true;
    }

    void suppressMutableInputAccessedInPostConstruct() {
        _suppressMutableInputAccessedInPostConstruct = true;
    }

    boolean needsMutableInputAccessedInPostConstructInvariant() {
        return !_suppressMutableInputAccessedInPostConstruct && mayNeedMutableInputAccessedInPostConstructInvariant();
    }

    boolean mayNeedMutableInputAccessedInPostConstructInvariant() {
        return !isImmutable() && !_onChangePresent && _descriptor.hasPostConstruct();
    }

    ImmutableInputKeyStrategy getImmutableInputKeyStrategy() {
        assert null != _immutableInputKeyStrategy;
        return _immutableInputKeyStrategy;
    }

    boolean hasValidateMethod() {
        return null != _validateMethod;
    }

    ExecutableElement getValidateMethod() {
        assert null != _validateMethod;
        return _validateMethod;
    }

    void setValidateMethod(final ExecutableElement method) {
        if (null != _validateMethod) {
            throw new ProcessorException(
                    MemberChecks.toSimpleName(Constants.INPUT_VALIDATE_CLASSNAME)
                            + " target duplicates existing method named " + _validateMethod.getSimpleName(),
                    method);
        } else {
            _validateMethod = method;
        }
    }

    boolean hasDefaultField() {
        return null != _defaultField;
    }

    VariableElement getDefaultField() {
        assert null != _defaultField;
        return _defaultField;
    }

    void setDefaultField(final VariableElement field) {
        if (isFromTreeContext()) {
            throw new ProcessorException(
                    MemberChecks.mustNot(
                            Constants.INPUT_DEFAULT_CLASSNAME,
                            "be specified for an @Input that has fromTreeContext=true"),
                    field);
        }
        if (null != _defaultMethod) {
            throw new ProcessorException(
                    "@InputDefault target duplicates existing method named " + _defaultMethod.getSimpleName(), field);
        } else if (null != _defaultField) {
            throw new ProcessorException(
                    "@InputDefault target duplicates existing field named " + _defaultField.getSimpleName(), field);
        } else {
            _defaultField = field;
        }
    }

    boolean hasDefaultMethod() {
        return null != _defaultMethod;
    }

    ExecutableElement getDefaultMethod() {
        assert null != _defaultMethod;
        return _defaultMethod;
    }

    void setDefaultMethod(final ExecutableElement method) {
        if (isFromTreeContext()) {
            throw new ProcessorException(
                    MemberChecks.mustNot(
                            Constants.INPUT_DEFAULT_CLASSNAME,
                            "be specified for an @Input that has fromTreeContext=true"),
                    method);
        }
        if (null != _defaultMethod) {
            throw new ProcessorException(
                    "@InputDefault target duplicates existing method named " + _defaultMethod.getSimpleName(), method);
        } else {
            /*
             * As all methods are processed first, there is no chance that a duplicate field will be detected
             * prior to the field being set. If there is a duplicate field it will be detected in setDefaultField()
             */
            assert null == _defaultField;
            _defaultMethod = method;
        }
    }

    boolean isOptional() {
        return _optional;
    }

    boolean isRequired() {
        return !isOptional();
    }

    Nullability.Nullness getNullness() {
        if (_nullness == null) {
            _nullness = Nullability.requireSpecified(getElement(), getType(), _descriptor.getElement());
        }
        return _nullness;
    }

    boolean isNonNull() {
        return getNullness() == Nullability.Nullness.NONNULL;
    }

    TypeName getTypeName() {
        return Nullability.typeName(getElement(), getType(), _descriptor.getElement());
    }

    static boolean isNodeType(final TypeMirror type) {
        return type instanceof DeclaredType declared
                && (Constants.VNODE_CLASSNAME.equals(declared.asElement().toString())
                        || Constants.RENDERABLE_CLASSNAME.equals(
                                declared.asElement().toString()));
    }

    boolean isRenderableInput() {
        return isNodeType(_type) || isRenderableArrayInput();
    }

    boolean isRenderableArrayInput() {
        return _type instanceof ArrayType array && isNodeType(array.getComponentType());
    }

    TypeMirror getBuilderType(final ProcessingEnvironment env) {
        if (!isRenderableInput()) {
            return _type;
        }
        final var renderable = env.getElementUtils()
                .getTypeElement(Constants.RENDERABLE_CLASSNAME)
                .asType();
        return isRenderableArrayInput() ? env.getTypeUtils().getArrayType(renderable) : renderable;
    }

    TypeName getBuilderTypeName() {
        final var type = getTypeName();
        if (!isRenderableInput()) {
            return type;
        }
        final var renderable = ClassName.bestGuess(Constants.RENDERABLE_CLASSNAME);
        return type instanceof ArrayTypeName array
                ? ArrayTypeName.of(renderable.annotated(array.componentType().annotations()))
                        .annotated(type.annotations())
                : renderable.annotated(type.annotations());
    }

    void markAsOptional() {
        _optional = true;
    }

    String getConstantName() {
        return getName();
    }

    boolean isFromTreeContext() {
        return _fromTreeContext;
    }

    boolean isSpecialChildrenInput() {
        return getName().equals("children") || getName().equals("child");
    }
}
