package react4j.processor;

import java.util.List;
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
  private final ComponentDescriptor _descriptor;
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  private final boolean _shouldUpdateOnChange;
  private final boolean _observable;
  private final boolean _disposable;
  @Nullable
  private VariableElement _defaultField;
  @Nullable
  private ExecutableElement _defaultMethod;
  @Nullable
  private ExecutableElement _validateMethod;
  @Nullable
  private ExecutableElement _onPropChanged;
  /**
   * Flag set to true if prop is optional.
   */
  private boolean _optional;

  PropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                  @Nonnull final String name,
                  @Nonnull final ExecutableElement method,
                  @Nonnull final ExecutableType methodType,
                  final boolean shouldUpdateOnChange,
                  final boolean observable,
                  final boolean disposable )
  {
    _descriptor = Objects.requireNonNull( descriptor );
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _shouldUpdateOnChange = shouldUpdateOnChange;
    _observable = observable;
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

  boolean isObservable()
  {
    return _observable;
  }

  boolean isDisposable()
  {
    return _disposable;
  }

  void setDefaultMethod( @Nonnull final ExecutableElement method )
  {
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

  boolean hasOnPropChangedMethod()
  {
    return null != _onPropChanged;
  }

  @Nonnull
  ExecutableElement getPropChangedMethod()
  {
    assert null != _onPropChanged;
    return _onPropChanged;
  }

  void setOnPropChangedMethod( @Nonnull final ExecutableElement method )
  {
    MethodChecks.mustBeSubclassCallable( _descriptor.getElement(),
                                         Constants.ON_PROP_CHANGED_ANNOTATION_CLASSNAME,
                                         method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.ON_PROP_CHANGED_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotReturnAValue( Constants.ON_PROP_CHANGED_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePublic( Constants.ON_PROP_CHANGED_ANNOTATION_CLASSNAME, method );

    final List<? extends VariableElement> parameters = method.getParameters();
    if ( !parameters.isEmpty() )
    {
      final VariableElement param1 = parameters.get( 0 );
      final boolean mismatchedNullability =
        (
          null != ProcessorUtil.findAnnotationByType( param1, Constants.NONNULL_ANNOTATION_CLASSNAME ) &&
          null != ProcessorUtil.findAnnotationByType( _method, Constants.NULLABLE_ANNOTATION_CLASSNAME )
        ) ||
        (
          null != ProcessorUtil.findAnnotationByType( param1, Constants.NULLABLE_ANNOTATION_CLASSNAME ) &&
          null != ProcessorUtil.findAnnotationByType( _method, Constants.NONNULL_ANNOTATION_CLASSNAME ) );

      if ( mismatchedNullability )
      {
        throw new ReactProcessorException( "@OnPropChanged target has a parameter that has a nullability annotation " +
                                           "incompatible with the associated @Prop method named " +
                                           _method.getSimpleName(), method );
      }
    }

    if ( null != _onPropChanged )
    {
      throw new ReactProcessorException( "@OnPropChanged target duplicates existing method named " +
                                         _onPropChanged.getSimpleName(), method );
    }
    else
    {
      _onPropChanged = Objects.requireNonNull( method );
    }
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
    MethodChecks.mustBeSubclassCallable( _descriptor.getElement(),
                                         Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                                         method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotReturnAValue( Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePublic( Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME, method );

    final VariableElement param = method.getParameters().get( 0 );
    final boolean mismatchedNullability =
      (
        null != ProcessorUtil.findAnnotationByType( param, Constants.NONNULL_ANNOTATION_CLASSNAME ) &&
        null != ProcessorUtil.findAnnotationByType( _method, Constants.NULLABLE_ANNOTATION_CLASSNAME )
      ) ||
      (
        null != ProcessorUtil.findAnnotationByType( param, Constants.NULLABLE_ANNOTATION_CLASSNAME ) &&
        null != ProcessorUtil.findAnnotationByType( _method, Constants.NONNULL_ANNOTATION_CLASSNAME ) );

    if ( mismatchedNullability )
    {
      throw new ReactProcessorException( "@PropValidate target has a parameter that has a nullability annotation " +
                                         "incompatible with the associated @Prop method named " +
                                         _method.getSimpleName(), method );
    }

    if ( null != _validateMethod )
    {
      throw new ReactProcessorException( "@PropValidate target duplicates existing method named " +
                                         _validateMethod.getSimpleName(), method );
    }
    else
    {
      _validateMethod = Objects.requireNonNull( method );
    }
  }

  void setDefaultField( @Nonnull final VariableElement field )
  {
    MethodChecks.mustBeFinal( Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME, field );

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

  @Nonnull
  String getConstantName()
  {
    return "PROP_" + getName();
  }

  boolean isSpecialChildrenProp()
  {
    return getName().equals( "children" ) || getName().equals( "child" );
  }
}
