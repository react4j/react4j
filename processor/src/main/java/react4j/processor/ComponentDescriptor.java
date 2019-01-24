package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Objects;
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
  @Nullable
  private ExecutableElement _preUpdate;
  @Nullable
  private ExecutableElement _postRender;
  @Nullable
  private ExecutableElement _postUpdate;
  @Nullable
  private ExecutableElement _postMount;
  @Nullable
  private ExecutableElement _preUnmount;
  private boolean _arezComponent;
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
  private boolean _allowNoArezDeps;
  private final boolean _needsEnhancer;

  ComponentDescriptor( @Nonnull final Elements elements,
                       @Nonnull final SourceVersion sourceVersion,
                       @Nonnull final String name,
                       @Nonnull final PackageElement packageElement,
                       @Nonnull final TypeElement element,
                       final boolean needsEnhancer )
  {
    _elements = Objects.requireNonNull( elements );
    _sourceVersion = Objects.requireNonNull( sourceVersion );
    _name = Objects.requireNonNull( name );
    _packageElement = Objects.requireNonNull( packageElement );
    _element = Objects.requireNonNull( element );
    _needsEnhancer = needsEnhancer;

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
    if ( !( 1 == constructors.size() &&
            constructors.get( 0 ).getParameters().isEmpty() &&
            !constructors.get( 0 ).getModifiers().contains( Modifier.PRIVATE ) ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must have a single non-private, no-argument " +
                                         "constructor or the default constructor", element );
    }
  }

  @Nonnull
  Elements getElements()
  {
    return _elements;
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

  boolean needsEnhancer()
  {
    return _needsEnhancer;
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

  boolean isArezComponent()
  {
    return _arezComponent;
  }

  boolean allowNoArezDeps()
  {
    return _allowNoArezDeps;
  }

  void setArezComponent( final boolean arezComponent, final boolean allowNoArezDeps )
  {
    _arezComponent = arezComponent;
    _allowNoArezDeps = allowNoArezDeps;
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
  ExecutableElement getPreUnmount()
  {
    return _preUnmount;
  }

  void setPreUnmount( @Nonnull final ExecutableElement preUnmount )
    throws ReactProcessorException
  {
    MethodChecks.mustBeLifecycleHook( getElement(), Constants.PRE_UNMOUNT_ANNOTATION_CLASSNAME, preUnmount );

    if ( null != _preUnmount )
    {
      throw new ReactProcessorException( "@PreUnmount target duplicates existing method named " +
                                         _preUnmount.getSimpleName(), preUnmount );
    }
    else
    {
      _preUnmount = preUnmount;
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

  private boolean shouldGenerateLifecycle()
  {
    return isArezComponent() ||
           generateComponentDidMount() ||
           generateShouldComponentUpdate() ||
           generateComponentPreUpdate() ||
           generateComponentDidUpdate() ||
           generateComponentWillUnmount() ||
           generateComponentDidCatch();
  }

  boolean shouldGenerateLiteLifecycle()
  {
    return ( generateComponentDidUpdateInLiteLifecycle() != generateComponentDidUpdate() ||
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
    /*
     * We do not need to implement this when we are an arez component with no observable props
     * and all "update on change" props as any prop change will cause a re-render which is effectively
     * the same behaviour. Hence why isArezComponent() has been removed from this list.
     */
    return hasObservableProps() ||
           // If any prop change requires a re-render then there is no need for SCU but if then
           // there is at least one that need not cause a re-render then we need to implement SCU so we
           // can skip that scenario
           ( hasUpdateOnChangeProps() && !getProps().stream().allMatch( PropDescriptor::shouldUpdateOnChange ) );
  }

  boolean generateComponentDidCatch()
  {
    //TODO: Implement this
    return false;
  }

  boolean generateComponentWillUnmount()
  {
    return isArezComponent() || null != _preUnmount;
  }

  boolean generateComponentPreUpdate()
  {
    return hasPreUpdateOnPropChange() || null != _preUpdate;
  }

  boolean generateComponentDidMount()
  {
    return generateComponentDidMountInLiteLifecycle() || isArezComponent();
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
    return generateComponentDidUpdateInLiteLifecycle() || isArezComponent();
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
