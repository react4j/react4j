package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;

@SuppressWarnings( "Duplicates" )
final class PropDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  private final boolean _shouldUpdateOnChange;
  private final boolean _disposable;
  @Nullable
  private VariableElement _defaultField;
  @Nullable
  private ExecutableElement _defaultMethod;
  /**
   * Flag set to true if prop is optional.
   */
  private boolean _optional;

  PropDescriptor( @Nonnull final String name,
                  @Nonnull final ExecutableElement method,
                  @Nonnull final ExecutableType methodType,
                  final boolean shouldUpdateOnChange,
                  final boolean disposable )
  {
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _shouldUpdateOnChange = shouldUpdateOnChange;
    _disposable = disposable;
  }

  @Nonnull
  String getName()
  {
    return _name;
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

  boolean isDisposable()
  {
    return _disposable;
  }

  void setDefaultMethod( @Nonnull final ExecutableElement method )
  {
    MethodChecks.mustBeStatic( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePrivate( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotHaveAnyParameters( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustReturnAValue( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, method );

    if ( null != _defaultMethod )
    {
      throw new ReactProcessorException( "@PropDefault target duplicates existing method named " +
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

  void setDefaultField( @Nonnull final VariableElement field )
  {
    MethodChecks.mustBeStatic( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, field );
    MethodChecks.mustBeFinal( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, field );
    MethodChecks.mustNotBePrivate( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, field );

    if ( null != _defaultMethod )
    {
      throw new ReactProcessorException( "@PropDefault target duplicates existing method named " +
                                         _defaultMethod.getSimpleName(), field );
    }
    else if ( null != _defaultField )
    {
      throw new ReactProcessorException( "@PropDefault target duplicates existing field named " +
                                         _defaultField.getSimpleName(), field );
    }
    else
    {
      _defaultField = Objects.requireNonNull( field );
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

  boolean isOptional()
  {
    return _optional;
  }

  void markAsOptional()
  {
    _optional = true;
  }

  boolean isSpecialChildrenProp()
  {
    return getName().equals( "children" ) || getName().equals( "child" );
  }
}
