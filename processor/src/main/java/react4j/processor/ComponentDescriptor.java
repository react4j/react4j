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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

final class ComponentDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final TypeElement _element;
  @Nonnull
  private final ComponentType _type;
  private final boolean _hasPostConstruct;
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
   * Names of elements that require a priority override to be generated.
   */
  @Nullable
  private List<String> _priorityOverrides;
  private Boolean _hasValidatedProps;
  private boolean _hasArezElements;

  ComponentDescriptor( @Nonnull final String name,
                       @Nonnull final TypeElement element,
                       @Nonnull final ComponentType type,
                       final boolean hasPostConstruct )
  {
    _name = Objects.requireNonNull( name );
    _element = Objects.requireNonNull( element );
    _type = Objects.requireNonNull( type );
    _hasPostConstruct = hasPostConstruct;

    if ( ElementKind.CLASS != element.getKind() )
    {
      throw new ProcessorException( "@ReactComponent target must be a class", element );
    }
    else if ( element.getModifiers().contains( Modifier.FINAL ) )
    {
      throw new ProcessorException( "@ReactComponent target must not be final", element );
    }
    else if ( !element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ProcessorException( "@ReactComponent target must be abstract", element );
    }
    else if ( NestingKind.TOP_LEVEL != element.getNestingKind() &&
              !element.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ProcessorException( "@ReactComponent target must not be a non-static nested class", element );
    }

    final List<ExecutableElement> constructors = element.getEnclosedElements().stream().
      filter( m -> m.getKind() == ElementKind.CONSTRUCTOR ).
      map( m -> (ExecutableElement) m ).
      collect( Collectors.toList() );
    if ( 1 != constructors.size() || !isConstructorValid( constructors.get( 0 ) ) )
    {
      throw new ProcessorException( "@ReactComponent target must have a single, package-access constructor " +
                                    "or the default constructor", element );
    }
    _constructor = constructors.get( 0 );
    for ( final VariableElement parameter : _constructor.getParameters() )
    {
      if ( AnnotationsUtil.hasAnnotationOfType( parameter, Constants.PER_INSTANCE_ANNOTATION_CLASSNAME ) )
      {
        throw new ProcessorException( "@ReactComponent target has a constructor with a parameter named '" +
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
  ExecutableElement getConstructor()
  {
    return _constructor;
  }

  private boolean hasConstructorParams()
  {
    return !_constructor.getParameters().isEmpty();
  }

  boolean hasPostConstruct()
  {
    return _hasPostConstruct;
  }

  @Nonnull
  String getPackageName()
  {
    return GeneratorUtil.getQualifiedPackageName( _element );
  }

  @Nonnull
  String getName()
  {
    return _name;
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
    return GeneratorUtil.getGeneratedClassName( _element, "React4j_", "" );
  }

  @Nonnull
  ClassName getBuilderClassName()
  {
    return GeneratorUtil.getGeneratedClassName( _element, "", "Builder" );
  }

  @Nonnull
  ClassName getDaggerComponentExtensionClassName()
  {
    return GeneratorUtil.getGeneratedClassName( _element, "", "DaggerComponentExtension" );
  }

  @Nonnull
  ClassName getArezDaggerExtensionClassName()
  {
    return GeneratorUtil.getGeneratedClassName( _element, "React4j_", "DaggerComponentExtension" );
  }

  @Nonnull
  ClassName getArezClassName()
  {
    final String simpleName = "Arez_" + GeneratorUtil.getGeneratedSimpleClassName( _element, "React4j_", "" );
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
    return hasConstructorParams();
  }

  boolean trackRender()
  {
    return ComponentType.MAYBE_TRACKING == _type || ComponentType.TRACKING == _type;
  }

  void setHasArezElements( final boolean hasArezElements )
  {
    _hasArezElements = hasArezElements;
  }

  @Nonnull
  ComponentType getType()
  {
    return _type;
  }

  @Nonnull
  List<String> getPriorityOverrides()
  {
    assert null != _priorityOverrides;
    return _priorityOverrides;
  }

  void setPriorityOverrides( @Nonnull final List<String> priorityOverrides )
  {
    _priorityOverrides = Objects.requireNonNull( priorityOverrides );
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

  @Nullable
  ExecutableElement getPreUpdate()
  {
    return _preUpdate;
  }

  void setPreUpdate( @Nonnull final ExecutableElement preUpdate )
    throws ProcessorException
  {
    MemberChecks.mustBeLifecycleHook( getElement(),
                                      Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                      Constants.PRE_UPDATE_ANNOTATION_CLASSNAME,
                                      preUpdate );
    MemberChecks.mustNotBePublic( Constants.PRE_UPDATE_ANNOTATION_CLASSNAME, preUpdate );

    if ( null != _preUpdate )
    {
      throw new ProcessorException( "@PreUpdate target duplicates existing method named " +
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
    throws ProcessorException
  {
    MemberChecks.mustBeLifecycleHook( getElement(),
                                      Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                      Constants.POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME,
                                      postRender );
    MemberChecks.mustNotBePublic( Constants.POST_MOUNT_OR_UPDATE_ANNOTATION_CLASSNAME, postRender );

    if ( null != _postRender )
    {
      throw new ProcessorException( "@PostMountOrUpdate target duplicates existing method named " +
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
    throws ProcessorException
  {
    MemberChecks.mustBeLifecycleHook( getElement(),
                                      Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                      Constants.POST_UPDATE_ANNOTATION_CLASSNAME,
                                      postUpdate );
    MemberChecks.mustNotBePublic( Constants.POST_UPDATE_ANNOTATION_CLASSNAME, postUpdate );

    if ( null != _postUpdate )
    {
      throw new ProcessorException( "@PostUpdate target duplicates existing method named " +
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
    throws ProcessorException
  {
    MemberChecks.mustBeLifecycleHook( getElement(),
                                      Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                      Constants.POST_MOUNT_ANNOTATION_CLASSNAME,
                                      postMount );
    MemberChecks.mustNotBePublic( Constants.POST_MOUNT_ANNOTATION_CLASSNAME, postMount );

    if ( null != _postMount )
    {
      throw new ProcessorException( "@PostMount target duplicates existing method named " +
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
    throws ProcessorException
  {
    MemberChecks.mustNotBeAbstract( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MemberChecks.mustNotBePublic( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MemberChecks.mustBeSubclassCallable( getElement(),
                                         Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                         Constants.ON_ERROR_ANNOTATION_CLASSNAME,
                                         onError );
    MemberChecks.mustNotReturnAnyValue( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );
    MemberChecks.mustNotThrowAnyExceptions( Constants.ON_ERROR_ANNOTATION_CLASSNAME, onError );

    boolean infoFound = false;
    boolean errorFound = false;
    for ( final VariableElement parameter : onError.getParameters() )
    {
      final TypeName typeName = TypeName.get( parameter.asType() );
      if ( typeName.toString().equals( Constants.ERROR_INFO_CLASSNAME ) )
      {
        if ( infoFound )
        {
          throw new ProcessorException( "@OnError target has multiple parameters of type " +
                                        Constants.ERROR_INFO_CLASSNAME,
                                        onError );
        }
        infoFound = true;
      }
      else if ( typeName.toString().equals( Constants.JS_ERROR_CLASSNAME ) )
      {
        if ( errorFound )
        {
          throw new ProcessorException( "@OnError target has multiple parameters of type " +
                                        Constants.JS_ERROR_CLASSNAME,
                                        onError );
        }
        errorFound = true;
      }
      else
      {
        throw new ProcessorException( "@OnError target has parameter of invalid type named " +
                                      parameter.getSimpleName().toString(),
                                      parameter );
      }
    }

    if ( null != _onError )
    {
      throw new ProcessorException( "@OnError target duplicates existing method named " + _onError.getSimpleName(),
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
    return generateShouldComponentUpdateInLiteLifecycle() || hasValidatedProps();
  }

  boolean generateShouldComponentUpdateInLiteLifecycle()
  {
    return true; // type != STATELESS || hasObservableProps() || hasUpdateOnChangeProps();
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
