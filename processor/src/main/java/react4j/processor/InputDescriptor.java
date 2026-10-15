package react4j.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

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
    private Boolean _isNonNull;

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

    boolean isNonNull() {
        if (null == _isNonNull) {
            _isNonNull = Nullability.isNonNull(getElement(), getType(), _descriptor.getElement());
        }
        return _isNonNull;
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
