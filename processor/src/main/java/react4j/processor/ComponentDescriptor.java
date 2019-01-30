package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

final class ComponentDescriptor
{
  @Nonnull
  private final Elements _elements;
  @Nonnull
  private final SourceVersion _sourceVersion;
  @Nonnull
  private final String _name;
  @Nonnull
  private final PackageElement _packageElement;
  @Nonnull
  private final TypeElement _element;
  @Nonnull
  private final ComponentType _type;
  private final boolean _nonConstructorInjections;
  @Nonnull
  private final ExecutableElement _constructor;
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
  private boolean _needsInjection;
  /**
   * Methods that are props accessors.
   * These should be implemented as accesses to the underlying props value.
   */
  @Nullable
  private List<PropDescriptor> _props;
  /**
   * Methods that are props accessors.
   * These should be implemented as accesses to the underlying props value.
   */
  @Nullable
  private List<OnPropChangeDescriptor> _onPropChangeDescriptors;
  /**
   * Methods annotated with arez's @Memoize annotation. Should be null if not an arez component
   */
  @Nullable
  private List<MethodDescriptor> _memoizeMethods;
  private Boolean _hasValidatedProps;
  private boolean _hasArezElements;

  ComponentDescriptor( @Nonnull final Elements elements,
                       @Nonnull final SourceVersion sourceVersion,
                       @Nonnull final String name,
                       @Nonnull final PackageElement packageElement,
                       @Nonnull final TypeElement element,
                       @Nonnull final ComponentType type,
                       final boolean nonConstructorInjections )
  {
    _elements = Objects.requireNonNull( elements );
    _sourceVersion = Objects.requireNonNull( sourceVersion );
    _name = Objects.requireNonNull( name );
    _packageElement = Objects.requireNonNull( packageElement );
    _element = Objects.requireNonNull( element );
    _type = Objects.requireNonNull( type );
    _nonConstructorInjections = nonConstructorInjections;

    if ( ElementKind.CLASS != element.getKind() )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a class", element );
    }
    else if ( element.getModifiers().contains( Modifier.FINAL ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must not be final", element );
    }
    else if ( !element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must be abstract", element );
    }
    else if ( NestingKind.TOP_LEVEL != element.getNestingKind() &&
              !element.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must not be a non-static nested class", element );
    }

    final List<ExecutableElement> constructors = element.getEnclosedElements().stream().
      filter( m -> m.getKind() == ElementKind.CONSTRUCTOR ).
      map( m -> (ExecutableElement) m ).
      collect( Collectors.toList() );
    if ( 1 != constructors.size() )
    {
      throw new ReactProcessorException( "@ReactComponent target must have a single constructor or the " +
                                         "default constructor", element );
    }
    _constructor = constructors.get( 0 );
    if ( !isConstructorValid( _constructor ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must have a single package access constructor " +
                                         "or the default constructor", element );
    }
    for ( final VariableElement parameter : _constructor.getParameters() )
    {
      if ( ProcessorUtil.hasAnnotationOfType( parameter, Constants.PER_INSTANCE_ANNOTATION_CLASSNAME ) )
      {
        throw new ReactProcessorException( "@ReactComponent target has a constructor with a parameter named '" +
                                           parameter.getSimpleName().toString() + "' that is incorrectly annotated " +
                                           "with the " + Constants.PER_INSTANCE_ANNOTATION_CLASSNAME + " annotation.",
                                           element );
      }
    }
  }

  private boolean isConstructorValid( @Nonnull final ExecutableElement ctor )
  {
    final List<? extends VariableElement> parameters = ctor.getParameters();
    final Set<Modifier> modifiers = ctor.getModifiers();
    if ( parameters.isEmpty() )
    {
      return !modifiers.contains( Modifier.PROTECTED ) &&
             !modifiers.contains( Modifier.PRIVATE );
    }
    else
    {
      return
        !modifiers.contains( Modifier.PRIVATE ) &&
        !modifiers.contains( Modifier.PUBLIC ) &&
        !modifiers.contains( Modifier.PROTECTED );
    }
  }

  @Nonnull
  Elements getElements()
  {
    return _elements;
  }

  @Nonnull
  ExecutableElement getConstructor()
  {
    return _constructor;
  }

  boolean hasConstructorParams()
  {
    return !_constructor.getParameters().isEmpty();
  }

  @Nonnull
  SourceVersion getSourceVersion()
  {
    return _sourceVersion;
  }

  @Nonnull
  String getPackageName()
  {
    return _packageElement.getQualifiedName().toString();
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  boolean nonConstructorInjections()
  {
    return _nonConstructorInjections;
  }

  @Nonnull
  ClassName getClassName()
  {
    return ClassName.get( getElement() );
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  DeclaredType getDeclaredType()
  {
    return (DeclaredType) _element.asType();
  }

  @Nonnull
  ClassName getEnhancedClassName()
  {
    return ClassName.get( getPackageName(), getNestedClassPrefix() + "React4j_" + _element.getSimpleName() );
  }

  @Nonnull
  ClassName getBuilderClassName()
  {
    return ClassName.get( getPackageName(), getNestedClassPrefix() + _element.getSimpleName() + "Builder" );
  }

  @Nonnull
  ClassName getDaggerComponentExtensionClassName()
  {
    return ClassName.get( getPackageName(),
                          getNestedClassPrefix() + _element.getSimpleName() + "DaggerComponentExtension" );
  }

  @Nonnull
  ClassName getArezDaggerExtensionClassName()
  {
    return ClassName.get( getPackageName(),
                          getNestedClassPrefix() + "React4j_" + _element.getSimpleName() + "DaggerComponentExtension" );
  }

  @Nonnull
  ClassName getArezClassName()
  {
    final String simpleName = "Arez_" + getNestedClassPrefix() + "React4j_" + _element.getSimpleName();
    return ClassName.get( getPackageName(), simpleName );
  }

  @Nonnull
  TypeName getComponentType()
  {
    final List<TypeName> typeNames =
      getDeclaredType().getTypeArguments().stream().map( TypeName::get ).collect( Collectors.toList() );
    if ( !typeNames.isEmpty() )
    {
      return ParameterizedTypeName.get( ClassName.get( getElement() ), typeNames.toArray( new TypeName[ 0 ] ) );
    }
    else
    {
      return ClassName.get( getElement() );
    }
  }

  boolean needsInjection()
  {
    return _needsInjection;
  }

  void setNeedsInjection( final boolean needsInjection )
  {
    _needsInjection = needsInjection;
  }

  boolean trackRender()
  {
    return ComponentType.MAYBE_TRACKING == _type || ComponentType.TRACKING == _type;
  }

  void setHasArezElements( final boolean hasArezElements )
  {
    _hasArezElements = hasArezElements;
  }

  public ComponentType getType()
  {
    return _type;
  }

  @Nonnull
  private String getNestedClassPrefix()
  {
    final StringBuilder name = new StringBuilder();
    TypeElement t = getElement();
    while ( NestingKind.TOP_LEVEL != t.getNestingKind() )
    {
      t = (TypeElement) t.getEnclosingElement();
      name.insert( 0, t.getSimpleName() + "_" );
    }
    return name.toString();
  }

  @Nonnull
  List<MethodDescriptor> getMemoizeMethods()
  {
    assert null != _memoizeMethods;
    return _memoizeMethods;
  }

  void setMemoizeMethods( @Nonnull final List<MethodDescriptor> memoizeMethods )
  {
    _memoizeMethods = Objects.requireNonNull( memoizeMethods );
  }

  boolean hasSyntheticKey()
  {
    return getProps().stream().anyMatch( PropDescriptor::isImmutable );
  }

  int syntheticKeyComponents()
  {
    return (int) getProps().stream().filter( PropDescriptor::isImmutable ).count();
  }

  @Nonnull
  List<PropDescriptor> getProps()
  {
    assert null != _props;
    return _props;
  }

  @Nullable
  PropDescriptor findPropNamed( @Nonnull final String name )
  {
    return getProps().stream().filter( p -> p.getName().equals( name ) ).findAny().orElse( null );
  }

  void setProps( @Nonnull final List<PropDescriptor> events )
  {
    _props = Objects.requireNonNull( events );
  }

  /**
   * Needs to be invoked after all the props have been completely constructed.
   */
  void sortProps()
  {
    assert null != _props;
    _props.sort( PropComparator.COMPARATOR );
  }

  @Nonnull
  List<OnPropChangeDescriptor> getPreUpdateOnPropChangeDescriptors()
  {
    return getOnPropChangeDescriptors()
      .stream()
      .filter( OnPropChangeDescriptor::isPreUpdate )
      .collect( Collectors.toList() );
  }

  @Nonnull
  List<OnPropChangeDescriptor> getPostUpdateOnPropChangeDescriptors()
  {
    return getOnPropChangeDescriptors().stream().filter( o -> !o.isPreUpdate() ).collect( Collectors.toList() );
  }

  @Nonnull
  private List<OnPropChangeDescriptor> getOnPropChangeDescriptors()
  {
    assert null != _onPropChangeDescriptors;
    return _onPropChangeDescriptors;
  }

  void setOnPropChangeDescriptors( @Nonnull List<OnPropChangeDescriptor> onPropChangeDescriptors )
  {
    _onPropChangeDescriptors = Objects.requireNonNull( onPropChangeDescriptors );
  }

  boolean hasObservableProps()
  {
    return getProps().stream().anyMatch( PropDescriptor::isObservable );
  }

  private boolean hasUpdateOnChangeProps()
  {
    return getProps().stream().anyMatch( PropDescriptor::shouldUpdateOnChange );
  }

  @Nullable
  ExecutableElement getPreUpdate()
  {
    return _preUpdate;
  }

  void setPreUpdate( @Nonnull final ExecutableElement preUpdate )
    throws ReactProcessorException
  {
    MethodChecks.mustBeLifecycleHook( getElement(), Constants.PRE_UPDATE_ANNOTATION_CLASSNAME, preUpdate );

    if ( null != _preUpdate )
    {
      throw new ReactProcessorException( "@PreUpdate target duplicates existing method named " +
                                         _preUpdate.getSimpleName(), preUpdate );
    }
    else
    {
      _preUpdate = preUpdate;
    }
  }

  @Nullable
  ExecutableElement getPostRender()
  {
    return _postRender;
  }

  void setPostRender( @Nonnull final ExecutableElement postRender )
    throws ReactProcessorException
  {
    MethodChecks.mustBeLifecycleHook( getElement(), Constants.POST_RENDER_ANNOTATION_CLASSNAME, postRender );

    if ( null != _postRender )
    {
      throw new ReactProcessorException( "@PostRender target duplicates existing method named " +
                                         _postRender.getSimpleName(), postRender );
    }
    else
    {
      _postRender = postRender;
    }
  }

  @Nullable
  ExecutableElement getPostUpdate()
  {
    return _postUpdate;
  }

  void setPostUpdate( @Nonnull final ExecutableElement postUpdate )
    throws ReactProcessorException
  {
    MethodChecks.mustBeLifecycleHook( getElement(), Constants.POST_UPDATE_ANNOTATION_CLASSNAME, postUpdate );

    if ( null != _postUpdate )
    {
      throw new ReactProcessorException( "@PostUpdate target duplicates existing method named " +
                                         _postUpdate.getSimpleName(), postUpdate );
    }
    else
    {
      _postUpdate = postUpdate;
    }
  }

  @Nullable
  ExecutableElement getPostMount()
  {
    return _postMount;
  }

  void setPostMount( @Nonnull final ExecutableElement postMount )
    throws ReactProcessorException
  {
    MethodChecks.mustBeLifecycleHook( getElement(), Constants.POST_MOUNT_ANNOTATION_CLASSNAME, postMount );

    if ( null != _postMount )
    {
      throw new ReactProcessorException( "@PostMount target duplicates existing method named " +
                                         _postMount.getSimpleName(), postMount );
    }
    else
    {
      _postMount = postMount;
    }
  }

  @Nullable
  ExecutableElement getOnError()
  {
    return _onError;
  }

  void setOnError( @Nonnull final ExecutableElement onError )
    throws ReactProcessorException
  {
    MethodChecks.mustNotBeAbstract( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MethodChecks.mustNotBePublic( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MethodChecks.mustBeSubclassCallable( getElement(), Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MethodChecks.mustNotReturnAValue( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MethodChecks.mustNotThrowAnyExceptions( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );

    boolean infoFound = false;
    boolean errorFound = false;
    for ( final VariableElement parameter : onError.getParameters() )
    {
      final TypeName typeName = TypeName.get( parameter.asType() );
      if ( typeName.toString().equals( Constants.ERROR_INFO_CLASSNAME ) )
      {
        if ( infoFound )
        {
          throw new ReactProcessorException( "@OnError target has multiple parameters of type " +
                                             Constants.ERROR_INFO_CLASSNAME,
                                             onError );
        }
        infoFound = true;
      }
      else if ( typeName.toString().equals( Constants.JS_ERROR_CLASSNAME ) )
      {
        if ( errorFound )
        {
          throw new ReactProcessorException( "@OnError target has multiple parameters of type " +
                                             Constants.JS_ERROR_CLASSNAME,
                                             onError );
        }
        errorFound = true;
      }
      else
      {
        throw new ReactProcessorException( "@OnError target has parameter of invalid type named " +
                                           parameter.getSimpleName().toString(),
                                           parameter );
      }
    }

    if ( null != _onError )
    {
      throw new ReactProcessorException( "@OnError target duplicates existing method named " + _onError.getSimpleName(),
                                         onError );
    }
    else
    {
      _onError = onError;
    }
  }

  private boolean shouldGenerateLifecycle()
  {
    return generateComponentDidMount() ||
           generateShouldComponentUpdate() ||
           generateComponentPreUpdate() ||
           generateComponentDidUpdate() ||
           generateComponentWillUnmount() ||
           generateComponentDidCatch();
  }

  boolean shouldGenerateLiteLifecycle()
  {
    return ( generateComponentDidUpdateInLiteLifecycle() != generateComponentDidUpdate() ||
             generateComponentWillUnmountInLiteLifecycle() != generateComponentWillUnmount() ||
             generateShouldComponentUpdateInLiteLifecycle() != generateShouldComponentUpdate() ||
             generateComponentDidMountInLiteLifecycle() != generateComponentDidMount() ) &&
           shouldGenerateLifecycle();
  }

  boolean generateShouldComponentUpdate()
  {
    //noinspection ConstantConditions
    return generateShouldComponentUpdateInLiteLifecycle() || hasValidatedProps();
  }

  boolean generateShouldComponentUpdateInLiteLifecycle()
  {
    //noinspection PointlessBooleanExpression,ConstantConditions
    return /* type != STATELESS */ true ||
                                   hasObservableProps() ||
                                   hasUpdateOnChangeProps();
  }

  boolean generateComponentDidCatch()
  {
    return null != _onError;
  }

  boolean generateComponentWillUnmountInLiteLifecycle()
  {
    return _hasArezElements;
  }

  boolean generateComponentWillUnmount()
  {
    return true;
  }

  boolean generateComponentPreUpdate()
  {
    return hasPreUpdateOnPropChange() || null != _preUpdate;
  }

  boolean generateComponentDidMount()
  {
    return generateComponentDidMountInLiteLifecycle() ||
           // We do it when tracking render so we can store debug information in state
           trackRender();
  }

  boolean generateComponentDidMountInLiteLifecycle()
  {
    return null != _postMount || null != _postRender;
  }

  boolean hasPreUpdateOnPropChange()
  {
    return !getPreUpdateOnPropChangeDescriptors().isEmpty();
  }

  boolean hasPostUpdateOnPropChange()
  {
    return !getPostUpdateOnPropChangeDescriptors().isEmpty();
  }

  boolean generateComponentDidUpdate()
  {
    return generateComponentDidUpdateInLiteLifecycle() ||
           // We do it when tracking render so we can store debug information in state
           trackRender();
  }

  boolean generateComponentDidUpdateInLiteLifecycle()
  {
    return hasPostUpdateOnPropChange() || null != _postUpdate || null != _postRender;
  }

  boolean hasValidatedProps()
  {
    if ( null == _hasValidatedProps )
    {
      _hasValidatedProps = getProps().stream().anyMatch( PropDescriptor::hasValidateMethod );
    }
    return _hasValidatedProps;
  }
}
