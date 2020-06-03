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
final class PropDescriptor
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
  private final boolean _contextProp;
  private final boolean _shouldUpdateOnChange;
  private final boolean _observable;
  private final boolean _disposable;
  @Nullable
  private final ImmutablePropKeyStrategy _immutablePropKeyStrategy;
  @Nonnull
  private final String _requiredValue;
  private boolean _onChangePresent;
  private boolean _suppressMutablePropAccessedInPostConstruct;
  @Nullable
  private VariableElement _defaultField;
  @Nullable
  private ExecutableElement _defaultMethod;
  @Nullable
  private ExecutableElement _validateMethod;
  /**
   * Flag set to true if prop is optional.
   */
  private boolean _optional;

  PropDescriptor( @Nonnull final ViewDescriptor descriptor,
                  @Nonnull final String name,
                  @Nonnull final String qualifier,
                  @Nonnull final ExecutableElement method,
                  @Nonnull final ExecutableType methodType,
                  final boolean contextProp,
                  final boolean shouldUpdateOnChange,
                  final boolean observable,
                  final boolean disposable,
                  @Nullable final ImmutablePropKeyStrategy immutablePropKeyStrategy,
                  @Nonnull final String requiredValue )
  {
    _descriptor = Objects.requireNonNull( descriptor );
    _name = Objects.requireNonNull( name );
    _qualifier = Objects.requireNonNull( qualifier );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _contextProp = contextProp;
    _shouldUpdateOnChange = shouldUpdateOnChange;
    _observable = observable;
    _disposable = disposable;
    _immutablePropKeyStrategy = immutablePropKeyStrategy;
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
    return null != _immutablePropKeyStrategy;
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

  void suppressMutablePropAccessedInPostConstruct()
  {
    _suppressMutablePropAccessedInPostConstruct = true;
  }

  boolean needsMutablePropAccessedInPostConstructInvariant()
  {
    return !_suppressMutablePropAccessedInPostConstruct && mayNeedMutablePropAccessedInPostConstructInvariant();
  }

  boolean mayNeedMutablePropAccessedInPostConstructInvariant()
  {
    return !isImmutable() && !_onChangePresent && _descriptor.hasPostConstruct();
  }

  @Nonnull
  ImmutablePropKeyStrategy getImmutablePropKeyStrategy()
  {
    assert null != _immutablePropKeyStrategy;
    return _immutablePropKeyStrategy;
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
      throw new ProcessorException( "@PropValidate target duplicates existing method named " +
                                    _validateMethod.getSimpleName(), method );
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
    if ( isContextProp() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                          "be specified for a @Prop method that specifies source=CONTEXT" ),
                                    field );
    }
    if ( null != _defaultMethod )
    {
      throw new ProcessorException( "@PropDefault target duplicates existing method named " +
                                    _defaultMethod.getSimpleName(), field );
    }
    else if ( null != _defaultField )
    {
      throw new ProcessorException( "@PropDefault target duplicates existing field named " +
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
    if ( isContextProp() )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                          "be specified for a @Prop method that specifies source=CONTEXT" ),
                                    method );
    }
    if ( null != _defaultMethod )
    {
      throw new ProcessorException( "@PropDefault target duplicates existing method named " +
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

  boolean isContextProp()
  {
    return _contextProp;
  }

  boolean isSpecialChildrenProp()
  {
    return getName().equals( "children" ) || getName().equals( "child" );
  }
}
