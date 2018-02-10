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
  private final ComponentDescriptor _component;
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  @Nullable
  private VariableElement _defaultField;
  @Nullable
  private ExecutableElement _defaultMethod;
  @Nullable
  private ExecutableType _defaultMethodType;
  /**
   * Flag set to true if prop is the last prop and thus by necessity completes configuration in generated builder.
   */
  private boolean _isTerminalProp;

  PropDescriptor( @Nonnull final ComponentDescriptor component,
                  @Nonnull final String name,
                  @Nonnull final ExecutableElement method,
                  @Nonnull final ExecutableType methodType )
  {
    _component = Objects.requireNonNull( component );
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
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

  boolean isOptional()
  {
    return hasDefaultField() || hasDefaultMethod();
  }

  void setDefaultMethod( @Nonnull final ExecutableElement method,
                         @Nonnull final ExecutableType methodType )
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
    else if ( null != _defaultField )
    {
      throw new ReactProcessorException( "@PropDefault target duplicates existing field named " +
                                         _defaultField.getSimpleName(), method );
    }
    else
    {
      _defaultMethod = Objects.requireNonNull( method );
      _defaultMethodType = Objects.requireNonNull( methodType );
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

  @Nonnull
  ExecutableType getDefaultMethodType()
  {
    assert null != _defaultMethodType;
    return _defaultMethodType;
  }

  boolean isTerminalProp()
  {
    return _isTerminalProp;
  }

  void markAsTerminalProp()
  {
    _isTerminalProp = true;
  }

  boolean isSpecialChildrenProp()
  {
    return getName().equals( "children" ) || getName().equals( "child" );
  }
}
