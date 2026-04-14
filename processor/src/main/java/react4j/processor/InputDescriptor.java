package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

@SuppressWarnings( "Duplicates" )
final class InputDescriptor
{
  enum Origin
  {
    METHOD,
    CONSTRUCTOR_PARAMETER
  }

  @Nonnull
  private final ViewDescriptor _descriptor;
  @Nonnull
  private final Origin _origin;
  @Nonnull
  private final String _name;
  @Nonnull
  private final String _qualifier;
  @Nonnull
  private final Element _element;
  @Nonnull
  private final TypeMirror _type;
  @Nullable
  private final ExecutableElement _method;
  @Nullable
  private final ExecutableType _methodType;
  @Nullable
  private final VariableElement _parameter;
  @Nullable
  private final Element _inputType;
  private final boolean _contextSource;
  private final boolean _shouldUpdateOnChange;
  private final boolean _observable;
  private final boolean _disposable;
  private final boolean _dependency;
  private final boolean _observeOnRender;
  private final boolean _observeOnRenderRequiresRuntimeCheck;
  @Nullable
  private final ImmutableInputKeyStrategy _immutableInputKeyStrategy;
  @Nonnull
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

  InputDescriptor( @Nonnull final ViewDescriptor descriptor,
                   @Nonnull final Origin origin,
                   @Nonnull final String name,
                   @Nonnull final String qualifier,
                   @Nonnull final Element element,
                   @Nonnull final TypeMirror type,
                   @Nullable final ExecutableElement method,
                   @Nullable final ExecutableType methodType,
                   @Nullable final VariableElement parameter,
                   @Nullable final Element inputType,
                   final boolean contextSource,
                   final boolean shouldUpdateOnChange,
                   final boolean observable,
                   final boolean disposable,
                   final boolean dependency,
                   final boolean observeOnRender,
                   final boolean observeOnRenderRequiresRuntimeCheck,
                   @Nullable final ImmutableInputKeyStrategy immutableInputKeyStrategy,
                   @Nonnull final String requiredValue )
  {
    _descriptor = Objects.requireNonNull( descriptor );
    _origin = Objects.requireNonNull( origin );
    _name = Objects.requireNonNull( name );
    _qualifier = Objects.requireNonNull( qualifier );
    _element = Objects.requireNonNull( element );
    _type = Objects.requireNonNull( type );
    _method = method;
    _methodType = methodType;
    _parameter = parameter;
    _inputType = inputType;
    _contextSource = contextSource;
    _shouldUpdateOnChange = shouldUpdateOnChange;
    _observable = observable;
    _disposable = disposable;
    _dependency = dependency;
    _observeOnRender = observeOnRender;
    _observeOnRenderRequiresRuntimeCheck = observeOnRenderRequiresRuntimeCheck;
    _immutableInputKeyStrategy = immutableInputKeyStrategy;
    _requiredValue = Objects.requireNonNull( requiredValue );
  }

  @Nonnull
  Origin getOrigin()
  {
    return _origin;
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  @Nonnull
  String getQualifier()
  {
    return _qualifier;
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }

  @Nonnull
  TypeMirror getType()
  {
    return _type;
  }

  boolean isMethodInput()
  {
    return Origin.METHOD == _origin;
  }

  boolean isConstructorParameterInput()
  {
    return Origin.CONSTRUCTOR_PARAMETER == _origin;
  }

  @Nullable
  ExecutableElement getMethod()
  {
    return _method;
  }

  @Nullable
  ExecutableType getMethodType()
  {
    return _methodType;
  }

  @Nullable
  VariableElement getParameter()
  {
    return _parameter;
  }

  @Nullable
  Element getInputType()
  {
    return _inputType;
  }

  boolean shouldUpdateOnChange()
  {
    return _shouldUpdateOnChange;
  }

  boolean isObservable()
  {
    return _observable;
  }

  boolean isDisposable()
  {
    return _disposable;
  }

  boolean isDependency()
  {
    return _dependency;
  }

  boolean shouldObserveOnRender()
  {
    return _observeOnRender;
  }

  boolean observeOnRenderRequiresRuntimeCheck()
  {
    return _observeOnRenderRequiresRuntimeCheck;
  }

  boolean isImmutable()
  {
    return null != _immutableInputKeyStrategy;
  }

  @Nonnull
  String getRequiredValue()
  {
    return _requiredValue;
  }

  void markAsOnChangePresent()
  {
    _onChangePresent = true;
  }

  void suppressMutableInputAccessedInPostConstruct()
  {
    _suppressMutableInputAccessedInPostConstruct = true;
  }

  boolean needsMutableInputAccessedInPostConstructInvariant()
  {
    return !_suppressMutableInputAccessedInPostConstruct && mayNeedMutableInputAccessedInPostConstructInvariant();
  }

  boolean mayNeedMutableInputAccessedInPostConstructInvariant()
  {
    return !isImmutable() && !_onChangePresent && _descriptor.hasPostConstruct();
  }

  @Nonnull
  ImmutableInputKeyStrategy getImmutableInputKeyStrategy()
  {
    assert null != _immutableInputKeyStrategy;
    return _immutableInputKeyStrategy;
  }

  boolean hasValidateMethod()
  {
    return null != _validateMethod;
  }

  @Nonnull
  ExecutableElement getValidateMethod()
  {
    assert null != _validateMethod;
    return _validateMethod;
  }

  void setValidateMethod( @Nonnull final ExecutableElement method )
  {
    if ( null != _validateMethod )
    {
      throw new ProcessorException( MemberChecks.toSimpleName( Constants.INPUT_VALIDATE_CLASSNAME ) +
                                    " target duplicates existing method named " + _validateMethod.getSimpleName(),
                                    method );
    }
    else
    {
      _validateMethod = Objects.requireNonNull( method );
    }
  }

  boolean hasDefaultField()
  {
    return null != _defaultField;
  }

  @Nonnull
  VariableElement getDefaultField()
  {
    assert null != _defaultField;
    return _defaultField;
  }

  void setDefaultField( @Nonnull final VariableElement field )
  {
    if ( isContextSource() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INPUT_DEFAULT_CLASSNAME,
                                                          "be specified for a @Input method that specifies source=CONTEXT" ),
                                    field );
    }
    if ( null != _defaultMethod )
    {
      throw new ProcessorException( "@InputDefault target duplicates existing method named " +
                                    _defaultMethod.getSimpleName(), field );
    }
    else if ( null != _defaultField )
    {
      throw new ProcessorException( "@InputDefault target duplicates existing field named " +
                                    _defaultField.getSimpleName(), field );
    }
    else
    {
      _defaultField = Objects.requireNonNull( field );
    }
  }

  boolean hasDefaultMethod()
  {
    return null != _defaultMethod;
  }

  @Nonnull
  ExecutableElement getDefaultMethod()
  {
    assert null != _defaultMethod;
    return _defaultMethod;
  }

  void setDefaultMethod( @Nonnull final ExecutableElement method )
  {
    if ( isContextSource() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.INPUT_DEFAULT_CLASSNAME,
                                                          "be specified for a @Input method that specifies source=CONTEXT" ),
                                    method );
    }
    if ( null != _defaultMethod )
    {
      throw new ProcessorException( "@InputDefault target duplicates existing method named " +
                                    _defaultMethod.getSimpleName(), method );
    }
    else
    {
      /*
       * As all methods are processed first, there is no chance that a duplicate field will be detected
       * prior to the field being set. If there is a duplicate field it will be detected in setDefaultField()
       */
      assert null == _defaultField;
      _defaultMethod = Objects.requireNonNull( method );
    }
  }

  boolean isOptional()
  {
    return _optional;
  }

  boolean isRequired()
  {
    return !isOptional();
  }

  boolean isNonNull()
  {
    if ( null == _isNonNull )
    {
      _isNonNull = AnnotationsUtil.hasNonnullAnnotation( getElement() );
    }
    return _isNonNull;
  }

  void markAsOptional()
  {
    _optional = true;
  }

  @Nonnull
  String getConstantName()
  {
    return getName();
  }

  boolean isContextSource()
  {
    return _contextSource;
  }

  boolean isSpecialChildrenInput()
  {
    return getName().equals( "children" ) || getName().equals( "child" );
  }
}
