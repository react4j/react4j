package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

final class ComponentDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final PackageElement _packageElement;
  @Nonnull
  private final TypeElement _element;
  private boolean _arezComponent;
  private boolean _needsInjection;
  private boolean _needsDaggerIntegration;
  private boolean _runArezScheduler;
  @Nullable
  private TypeElement _stateType;
  @Nullable
  private TypeElement _contextType;
  /**
   * A map of field name => TypeMirror for each context variable
   */
  @Nullable
  private Map<String, TypeMirror> _contextTypeFields;
  /**
   * A map of field name => TypeMirror for each context variable provided.
   */
  @Nullable
  private Map<String, TypeMirror> _childContextTypeFields;
  /**
   * Render method overridden by the user.
   */
  @Nullable
  private MethodDescriptor _renderMethod;
  /**
   * Lifecycle methods that are overridden by the user and need to be proxied from the native object.
   */
  @Nullable
  private List<MethodDescriptor> _lifecycleMethods;
  /**
   * Methods that are designated as event handlers. A wrapper method will be generated to simplify
   * the use of the method in React DevTools by naming handler.
   */
  @Nullable
  private List<EventHandlerDescriptor> _eventHandlers;
  /**
   * Methods that are props accessors.
   * These should be implemented as accesses to the underlying props value.
   */
  @Nullable
  private List<PropDescriptor> _props;
  /**
   * This is a static method that retrieves the default properties for a component.
   */
  @Nullable
  private ExecutableElement _defaultPropsMethod;

  ComponentDescriptor( @Nonnull final String name,
                       @Nonnull final PackageElement packageElement,
                       @Nonnull final TypeElement element )
  {
    _name = Objects.requireNonNull( name );
    _packageElement = Objects.requireNonNull( packageElement );
    _element = Objects.requireNonNull( element );

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
  String getPackageName()
  {
    return _packageElement.getQualifiedName().toString();
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
  String getEnhancedName()
  {
    return _element.getSimpleName() + "_";
  }

  @Nonnull
  String getBuilderName()
  {
    return _element.getSimpleName() + "Builder";
  }

  @Nonnull
  ClassName getEnhancedClassName()
  {
    return ClassName.get( getPackageName(), getNestedClassPrefix() + getEnhancedName() );
  }

  @Nonnull
  String getDaggerFactory()
  {
    return _element.getSimpleName() + "DaggerFactory";
  }

  @Nonnull
  ClassName getDaggerFactoryClassName()
  {
    return ClassName.get( getPackageName(), getNestedClassPrefix() + getDaggerFactory() );
  }

  @Nonnull
  ClassName getClassNameToConstruct()
  {
    final String cname = ( isArezComponent() ? "Arez_" : "" ) + getElement().getSimpleName() + "_";
    return ClassName.get( getPackageName(), getNestedClassPrefix() + cname );
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

  boolean needsDaggerIntegration()
  {
    return _needsDaggerIntegration;
  }

  void setNeedsDaggerIntegration( final boolean needsDaggerIntegration )
  {
    _needsDaggerIntegration = needsDaggerIntegration;
  }

  boolean isArezComponent()
  {
    return _arezComponent;
  }

  void setArezComponent( final boolean arezComponent )
  {
    _arezComponent = arezComponent;
  }

  boolean shouldRunArezScheduler()
  {
    return _runArezScheduler;
  }

  void setRunArezScheduler( final boolean runArezScheduler )
  {
    _runArezScheduler = runArezScheduler;
  }

  @Nonnull
  TypeElement getStateType()
  {
    assert null != _stateType;
    return _stateType;
  }

  void setStateType( @Nonnull final TypeElement stateType )
  {
    _stateType = Objects.requireNonNull( stateType );
  }

  @Nonnull
  TypeElement getContextType()
  {
    assert null != _contextType;
    return _contextType;
  }

  @Nonnull
  Map<String, TypeMirror> getContextTypeFields()
  {
    assert null != _contextTypeFields;
    return _contextTypeFields;
  }

  void setContextType( @Nonnull final TypeElement contextType,
                       @Nonnull final Map<String, TypeMirror> contextTypeFields )
  {
    _contextType = Objects.requireNonNull( contextType );
    _contextTypeFields = Objects.requireNonNull( contextTypeFields );
  }

  boolean hasChildContextFields()
  {
    return null != _childContextTypeFields;
  }

  @Nonnull
  Map<String, TypeMirror> getChildContextTypeFields()
  {
    assert null != _childContextTypeFields;
    return _childContextTypeFields;
  }

  void setChildContextTypeFields( @Nonnull final Map<String, TypeMirror> childContextTypeFields )
  {
    _childContextTypeFields = Objects.requireNonNull( childContextTypeFields );
  }

  @Nonnull
  String getNestedClassPrefix()
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
  MethodDescriptor getRenderMethod()
  {
    assert null != _renderMethod;
    return _renderMethod;
  }

  void setRenderMethod( @Nonnull final MethodDescriptor renderMethod )
  {
    _renderMethod = Objects.requireNonNull( renderMethod );
  }

  @Nonnull
  List<MethodDescriptor> getLifecycleMethods()
  {
    assert null != _lifecycleMethods;
    return _lifecycleMethods;
  }

  void setLifecycleMethods( @Nonnull final List<MethodDescriptor> lifecycleMethods )
  {
    _lifecycleMethods = Objects.requireNonNull( lifecycleMethods );
    for ( final MethodDescriptor method : _lifecycleMethods )
    {
      final ExecutableElement m = method.getMethod();
      if ( null != ProcessorUtil.findAnnotationByType( m, Constants.EVENT_HANDLER_ANNOTATION_CLASSNAME ) )
      {
        throw new ReactProcessorException( "@EventHandler target must not be a lifecycle method", m );
      }
    }
  }

  @Nonnull
  List<EventHandlerDescriptor> getEventHandlers()
  {
    assert null != _eventHandlers;
    return _eventHandlers;
  }

  void setEventHandlers( @Nonnull final List<EventHandlerDescriptor> eventHandlers )
  {
    _eventHandlers = Objects.requireNonNull( eventHandlers );
  }

  @Nonnull
  List<PropDescriptor> getProps()
  {
    assert null != _props;
    return _props;
  }

  void setProps( @Nonnull final List<PropDescriptor> events )
  {
    _props = Objects.requireNonNull( events );
  }

  boolean hasDefaultPropsMethod()
  {
    return null != _defaultPropsMethod;
  }

  @Nonnull
  ExecutableElement getDefaultPropsMethod()
  {
    assert null != _defaultPropsMethod;
    return _defaultPropsMethod;
  }

  void setDefaultPropsMethod( @Nonnull final ExecutableElement defaultPropsMethod )
  {
    _defaultPropsMethod = Objects.requireNonNull( defaultPropsMethod );
  }
}
