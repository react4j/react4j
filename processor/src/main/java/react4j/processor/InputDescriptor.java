package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

@SuppressWarnings( "Duplicates" )
final class InputDescriptor
{
  @Nonnull
  private final ViewDescriptor _descriptor;
  @Nonnull
  private final String _name;
  @Nonnull
  private final String _qualifier;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  private final boolean _contextSource;
  private final boolean _shouldUpdateOnChange;
  private final boolean _observable;
  private final boolean _disposable;
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

  InputDescriptor( @Nonnull final ViewDescriptor descriptor,
                   @Nonnull final String name,
                   @Nonnull final String qualifier,
                   @Nonnull final ExecutableElement method,
                   @Nonnull final ExecutableType methodType,
                   final boolean contextSource,
                   final boolean shouldUpdateOnChange,
                   final boolean observable,
                   final boolean disposable,
                   @Nullable final ImmutableInputKeyStrategy immutableInputKeyStrategy,
                   @Nonnull final String requiredValue )
  {
    _descriptor = Objects.requireNonNull( descriptor );
    _name = Objects.requireNonNull( name );
    _qualifier = Objects.requireNonNull( qualifier );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _contextSource = contextSource;
    _shouldUpdateOnChange = shouldUpdateOnChange;
    _observable = observable;
    _disposable = disposable;
    _immutableInputKeyStrategy = immutableInputKeyStrategy;
    _requiredValue = Objects.requireNonNull( requiredValue );
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
  ExecutableElement getMethod()
  {
    return _method;
  }

  @Nonnull
  ExecutableType getMethodType()
  {
    return _methodType;
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
