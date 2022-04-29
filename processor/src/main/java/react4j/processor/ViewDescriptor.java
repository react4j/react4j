package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

final class ViewDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final TypeElement _element;
  @Nonnull
  private final ViewType _type;
  private final boolean _sting;
  private final boolean _hasConstructor;
  private final boolean _hasPostConstruct;
  private final boolean _shouldSetDefaultPriority;
  @Nonnull
  private final ExecutableElement _constructor;
  @Nullable
  private ExecutableElement _render;
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
   * Methods that are inputs accessors.
   * These should be implemented as accesses to the underlying inputs value.
   */
  @Nullable
  private List<InputDescriptor> _inputs;
  /**
   * Methods that are inputs accessors.
   * These should be implemented as accesses to the underlying inputs value.
   */
  @Nullable
  private List<OnInputChangeDescriptor> _onInputChangeDescriptors;
  /**
   * Descriptors for methods annotated by @ScheduleRender.
   */
  @Nullable
  private List<ScheduleRenderDescriptor> _scheduleRenderDescriptors;
  /**
   * Descriptors for methods annotated by @Publish.
   */
  @Nullable
  private List<PublishDescriptor> _publishDescriptors;
  @Nullable
  private Boolean _validateInputs;
  @Nullable
  private Boolean _viewAccessesDeprecatedElements;
  @Nullable
  private Boolean _builderAccessesDeprecatedElements;
  @Nullable
  private Boolean _hasDependencyInput;

  ViewDescriptor( @Nonnull final String name,
                  @Nonnull final TypeElement element,
                  @Nonnull final ExecutableElement constructor,
                  @Nonnull final ViewType type,
                  final boolean sting,
                  final boolean hasConstructor,
                  final boolean hasPostConstruct,
                  final boolean shouldSetDefaultPriority )
  {
    _name = Objects.requireNonNull( name );
    _element = Objects.requireNonNull( element );
    _constructor = Objects.requireNonNull( constructor );
    _type = Objects.requireNonNull( type );
    _sting = sting;
    _hasConstructor = hasConstructor;
    _hasPostConstruct = hasPostConstruct;
    _shouldSetDefaultPriority = shouldSetDefaultPriority;
  }

  @Nonnull
  String keySuffix()
  {
    return "_" + _element.getSimpleName() + "_" + shortSha( _element.getQualifiedName().toString() );
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

  boolean enableSting()
  {
    return _sting;
  }

  boolean hasConstructor()
  {
    return _hasConstructor;
  }

  boolean hasPostConstruct()
  {
    return _hasPostConstruct;
  }

  boolean shouldSetDefaultPriority()
  {
    return _shouldSetDefaultPriority;
  }

  boolean requireRender()
  {
    return ViewType.NO_RENDER != _type;
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
  ClassName getFactoryClassName()
  {
    return GeneratorUtil.getGeneratedClassName( _element, "", "Factory" );
  }

  @Nonnull
  ClassName getArezClassName()
  {
    final String simpleName = "Arez_" + GeneratorUtil.getGeneratedSimpleClassName( _element, "React4j_", "" );
    return ClassName.get( getPackageName(), simpleName );
  }

  @Nonnull
  TypeName getViewType()
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
    return ( ViewType.MAYBE_TRACKING == _type || ViewType.TRACKING == _type ) && hasRender();
  }

  @Nonnull
  ViewType getType()
  {
    return _type;
  }

  int syntheticKeyParts()
  {
    return (int) getInputs().stream().filter( InputDescriptor::isImmutable ).count();
  }

  boolean hasDependencyInput()
  {
    if ( null == _hasDependencyInput )
    {
      assert null != _inputs;
      _hasDependencyInput = _inputs.stream().anyMatch( InputDescriptor::isDependency );
    }
    return _hasDependencyInput;
  }

  @Nonnull
  List<InputDescriptor> getImmutableInputs()
  {
    assert null != _inputs;
    return _inputs.stream().filter( InputDescriptor::isImmutable ).collect( Collectors.toList() );
  }

  @Nonnull
  List<InputDescriptor> getInputs()
  {
    assert null != _inputs;
    return _inputs;
  }

  void setInputs( @Nonnull final List<InputDescriptor> events )
  {
    _inputs = Objects.requireNonNull( events );
  }

  @Nullable
  InputDescriptor findInputNamed( @Nonnull final String name )
  {
    return getInputs().stream().filter( p -> p.getName().equals( name ) ).findAny().orElse( null );
  }

  /**
   * Needs to be invoked after all the inputs have been completely constructed.
   */
  void sortInputs()
  {
    assert null != _inputs;
    _inputs.sort( InputComparator.COMPARATOR );
  }

  @Nonnull
  List<OnInputChangeDescriptor> getPreUpdateOnInputChangeDescriptors()
  {
    return getOnInputChangeDescriptors()
      .stream()
      .filter( OnInputChangeDescriptor::isPreUpdate )
      .collect( Collectors.toList() );
  }

  @Nonnull
  List<OnInputChangeDescriptor> getPostUpdateOnInputChangeDescriptors()
  {
    return getOnInputChangeDescriptors().stream().filter( o -> !o.isPreUpdate() ).collect( Collectors.toList() );
  }

  @Nonnull
  private List<OnInputChangeDescriptor> getOnInputChangeDescriptors()
  {
    assert null != _onInputChangeDescriptors;
    return _onInputChangeDescriptors;
  }

  void setOnInputChangeDescriptors( @Nonnull List<OnInputChangeDescriptor> onInputChangeDescriptors )
  {
    _onInputChangeDescriptors = Objects.requireNonNull( onInputChangeDescriptors );
  }

  @Nonnull
  List<ScheduleRenderDescriptor> getScheduleRenderDescriptors()
  {
    assert null != _scheduleRenderDescriptors;
    return _scheduleRenderDescriptors;
  }

  void setScheduleRenderDescriptors( @Nonnull final List<ScheduleRenderDescriptor> scheduleRenderDescriptors )
  {
    _scheduleRenderDescriptors = Objects.requireNonNull( scheduleRenderDescriptors );
  }

  @Nonnull
  List<PublishDescriptor> getPublishDescriptors()
  {
    assert null != _publishDescriptors;
    return _publishDescriptors;
  }

  void setPublishDescriptors( @Nonnull final List<PublishDescriptor> publishDescriptors )
  {
    _publishDescriptors = Objects.requireNonNull( publishDescriptors );
  }

  boolean hasObservableInputs()
  {
    return getInputs().stream().anyMatch( InputDescriptor::isObservable );
  }

  @Nullable
  ExecutableElement getPreUpdate()
  {
    return _preUpdate;
  }

  void setPreUpdate( @Nonnull final ExecutableElement preUpdate )
    throws ProcessorException
  {
    if ( null != _preUpdate )
    {
      throw new ProcessorException( "@PreUpdate target duplicates existing method named " + _preUpdate.getSimpleName(),
                                    preUpdate );
    }
    else
    {
      _preUpdate = preUpdate;
    }
  }

  boolean hasRender()
  {
    return null != _render;
  }

  @Nonnull
  ExecutableElement getRender()
  {
    assert null != _render;
    return _render;
  }

  void setRender( @Nonnull final ExecutableElement render )
  {
    if ( null != _render )
    {
      throw new ProcessorException( MemberChecks.mustNot( Constants.RENDER_CLASSNAME,
                                                          "be present when another method named " +
                                                          _render.getSimpleName() +
                                                          " exists with the same annotation" ), render );
    }
    _render = Objects.requireNonNull( render );
  }

  @Nullable
  ExecutableElement getPostRender()
  {
    return _postRender;
  }

  void setPostRender( @Nonnull final ExecutableElement postRender )
    throws ProcessorException
  {
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
    if ( null != _postMount )
    {
      throw new ProcessorException( "@PostMount target duplicates existing method named " + _postMount.getSimpleName(),
                                    postMount );
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
    return generateShouldComponentUpdateInLiteLifecycle() || shouldValidateInputs();
  }

  boolean generateShouldComponentUpdateInLiteLifecycle()
  {
    return true; // type != STATELESS || hasObservableInputs() || hasUpdateOnChangeInputs();
  }

  boolean generateComponentDidCatch()
  {
    return null != _onError;
  }

  boolean generateComponentWillUnmountInLiteLifecycle()
  {
    return true; // type == STATELESS || type == TRACKING || MAYBE_TRACKING
  }

  boolean generateComponentWillUnmount()
  {
    return true; // type == STATELESS || type == TRACKING || MAYBE_TRACKING
  }

  boolean generateComponentPreUpdate()
  {
    return hasPreUpdateOnInputChange() || null != _preUpdate;
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

  boolean hasPreUpdateOnInputChange()
  {
    return !getPreUpdateOnInputChangeDescriptors().isEmpty();
  }

  boolean hasPostUpdateOnInputChange()
  {
    return !getPostUpdateOnInputChangeDescriptors().isEmpty();
  }

  boolean generateComponentDidUpdate()
  {
    return generateComponentDidUpdateInLiteLifecycle() ||
           // We do it when tracking render so we can store debug information in state
           trackRender();
  }

  boolean generateComponentDidUpdateInLiteLifecycle()
  {
    return hasPostUpdateOnInputChange() || null != _postUpdate || null != _postRender;
  }

  boolean shouldValidateInputs()
  {
    if ( null == _validateInputs )
    {
      _validateInputs = getInputs()
        .stream()
        .anyMatch( input -> input.hasValidateMethod() ||
                            ( input.isNonNull() && ( input.isRequired() || input.isContextSource() ) ) );
    }
    return _validateInputs;
  }

  boolean viewAccessesDeprecatedElements()
  {
    if ( null == _viewAccessesDeprecatedElements )
    {
      _viewAccessesDeprecatedElements =
        isDeprecated( _element ) ||
        isDeprecated( _constructor ) ||
        isDeprecated( _preUpdate ) ||
        isDeprecated( _postRender ) ||
        isDeprecated( _postMount ) ||
        isDeprecated( _postUpdate ) ||
        isDeprecated( _onError ) ||
        getInputs().stream()
          .anyMatch( p -> isDeprecated( p.getMethod() ) ||
                          p.hasValidateMethod() && isDeprecated( p.getValidateMethod() ) ) ||
        getPostUpdateOnInputChangeDescriptors().stream().anyMatch( d -> isDeprecated( d.getMethod() ) );
    }
    return _viewAccessesDeprecatedElements;
  }

  boolean builderAccessesDeprecatedElements()
  {
    if ( null == _builderAccessesDeprecatedElements )
    {
      _builderAccessesDeprecatedElements =
        isDeprecated( _element ) ||
        getInputs().stream().anyMatch( p -> p.hasDefaultMethod() && isDeprecated( p.getDefaultMethod() ) ||
                                            p.hasDefaultField() && isDeprecated( p.getDefaultField() ) );
    }
    return _builderAccessesDeprecatedElements;
  }

  private boolean isDeprecated( @Nullable final Element element )
  {
    return null != element && null != element.getAnnotation( Deprecated.class );
  }

  @Nonnull
  private String shortSha( @Nonnull final String text )
  {
    try
    {
      final MessageDigest algo = MessageDigest.getInstance( "SHA-1" );
      final byte[] digest = algo.digest( text.getBytes() );
      final StringBuilder sb = new StringBuilder();
      for ( int i = 0; i < 4; i++ )
      {
        final byte value = digest[ i ];
        sb.append( Integer.toString( ( value & 0xff ) + 0x100, 16 ).substring( 1 ) );
      }
      return sb.toString();
    }
    catch ( final NoSuchAlgorithmException e )
    {
      throw new IllegalStateException( e );
    }
  }
}
