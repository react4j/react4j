package react.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import jsinterop.annotations.JsFunction;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.Component;
import react.core.Procedure;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes React annotated source and generates models from the annotations.
 */
@SuppressWarnings( "Duplicates" )
@AutoService( Processor.class )
@SupportedAnnotationTypes( { "react.annotations.*" } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public final class ReactProcessor
  extends AbstractProcessor
{
  private static final List<String> LIFECYCLE_METHODS =
    Arrays.asList( "componentDidMount",
                   "componentDidUpdate",
                   "componentWillMount",
                   "componentWillReceiveProps",
                   "componentWillUnmount",
                   "componentWillUpdate",
                   "shouldComponentUpdate" );

  /**
   * Cache of lifecycle names to methods as defined by Component.
   */
  private final HashMap<String, ExecutableElement> _componentLifecycleMethods = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear lifecycle method cache to avoid potential inter-run problems
    _componentLifecycleMethods.clear();

    final Set<? extends Element> elements = env.getElementsAnnotatedWith( ReactComponent.class );
    processElements( elements );
    return false;
  }

  private void processElements( @Nonnull final Set<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      try
      {
        process( (TypeElement) element );
      }
      catch ( final IOException ioe )
      {
        processingEnv.getMessager().printMessage( ERROR, ioe.getMessage(), element );
      }
      catch ( final ReactProcessorException e )
      {
        processingEnv.getMessager().printMessage( ERROR, e.getMessage(), e.getElement() );
      }
      catch ( final Throwable e )
      {
        final StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter( sw ) );
        sw.flush();

        final String message =
          "Unexpected error will running the " + getClass().getName() + " processor. This has " +
          "resulted in a failure to process the code and has left the compiler in an invalid " +
          "state. Please report the failure to the developers so that it can be fixed.\n" +
          " Report the error at: https://github.com/realityforge/gwt-react-playground/issues\n" +
          "\n\n" +
          sw.toString();
        processingEnv.getMessager().printMessage( ERROR, message, element );
      }
    }
  }

  private void process( @Nonnull final TypeElement element )
    throws IOException, ReactProcessorException
  {
    final ComponentDescriptor descriptor = parse( element );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildConstructorFactory( descriptor ) );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildNativeComponent( descriptor ) );
  }

  private void emitTypeSpec( @Nonnull final String packageName, @Nonnull final TypeSpec typeSpec )
    throws IOException
  {
    JavaFile.builder( packageName, typeSpec ).
      skipJavaLangImports( true ).
      build().
      writeTo( processingEnv.getFiler() );
  }

  @Nonnull
  private ComponentDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final ReactComponent component = typeElement.getAnnotation( ReactComponent.class );
    assert null != component;
    final String name = deriveComponentName( typeElement, component );
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentDescriptor descriptor = new ComponentDescriptor( name, packageElement, typeElement );

    determineComponentType( descriptor, typeElement );
    determinePropsAndStateTypes( descriptor );
    determineLifecycleMethods( typeElement, descriptor );
    determineEventHandlers( descriptor );

    return descriptor;
  }

  private void determineEventHandlers( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<EventHandlerDescriptor> eventHandlers =
      ProcessorUtil.getMethods( descriptor.getElement(), processingEnv.getTypeUtils() ).stream()
        .filter( m -> null != m.getAnnotation( EventHandler.class ) )
        .map( m -> createEventHandlerDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );
    for ( final EventHandlerDescriptor eventHandler : eventHandlers )
    {
      final ExecutableElement method = eventHandler.getMethod();
      final TypeElement handlerType = getEventHandlerType( method );
      if ( ElementKind.INTERFACE != handlerType.getKind() )
      {
        throw new ReactProcessorException( "The @EventHandler specified an invalid type that is not an interface.",
                                           eventHandler.getMethod() );
      }
      if ( null == handlerType.getAnnotation( JsFunction.class ) )
      {
        throw new ReactProcessorException( "The @EventHandler specified an invalid type that is not annotated " +
                                           "with the annotation jsinterop.annotations.JsFunction.",
                                           eventHandler.getMethod() );
      }
      final ExecutableType methodType = eventHandler.getMethodType();
      final List<? extends TypeMirror> parameters = methodType.getParameterTypes();
      if ( !parameters.isEmpty() )
      {
        // Our annotated handler method has parameters so they should exactly align
        // in count and type with the parameters in the event handler method
        final ExecutableElement target = eventHandler.getEventHandlerMethod();
        final List<? extends VariableElement> targetParameters = target.getParameters();
        if ( targetParameters.size() != parameters.size() )
        {
          throw new ReactProcessorException( "The @EventHandler target has " + parameters.size() + " parameters " +
                                             "but the type parameter specified a handler with method type " +
                                             eventHandler.getEventHandlerType().getQualifiedName() + " that has " +
                                             "handler method with " + targetParameters.size() + " parameters. The " +
                                             "@EventHandler target should have zero parameters or match the number " +
                                             "of parameter in the target method " + target.getSimpleName() + ".",
                                             eventHandler.getMethod() );
        }
        for ( int i = 0; i < parameters.size(); i++ )
        {
          final TypeMirror parameterType = parameters.get( i );
          final VariableElement element = targetParameters.get( i );
          final TypeMirror targetParameterType = element.asType();
          final TypeMirror targetErased = processingEnv.getTypeUtils().erasure( targetParameterType );
          final TypeMirror parameterErased = processingEnv.getTypeUtils().erasure( parameterType );
          if ( !processingEnv.getTypeUtils().isAssignable( targetErased, parameterErased ) )
          {
            throw new ReactProcessorException( "The @EventHandler target parameter named " +
                                               eventHandler.getMethod().getParameters().get( i ).getSimpleName() +
                                               " of type " + parameterType + " is not assignable from target type " +
                                               targetParameterType + " of parameter " + element.getSimpleName() +
                                               " in method " + eventHandler.getEventHandlerType().getQualifiedName() +
                                               "." + target.getSimpleName() + ".",
                                               eventHandler.getMethod() );
          }
        }
      }
    }

    descriptor.setEventHandlers( eventHandlers );
  }

  @Nonnull
  private EventHandlerDescriptor createEventHandlerDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                                               @Nonnull final ExecutableElement method )
  {
    final String name = deriveEventHandlerName( method, method.getAnnotation( EventHandler.class ) );
    final TypeElement eventHandlerType = getEventHandlerType( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
    final List<ExecutableElement> eventHandlerMethods =
      ProcessorUtil.getMethods( eventHandlerType, processingEnv.getTypeUtils() ).stream().
        filter( m11 -> m11.getModifiers().contains( Modifier.ABSTRACT ) ).
        collect( Collectors.toList() );
    if ( eventHandlerMethods.isEmpty() )
    {
      throw new ReactProcessorException( "Method annotated with @EventHandler specified type " +
                                         eventHandlerType.getQualifiedName() + " that has no abstract method and " +
                                         "thus is not a functional interface", method );
    }
    else if ( eventHandlerMethods.size() > 1 )
    {
      throw new ReactProcessorException( "Method annotated with @EventHandler specified type " +
                                         eventHandlerType.getQualifiedName() + " that has more than 1 abstract " +
                                         "method and thus is not a functional interface", method );
    }
    return new EventHandlerDescriptor( name, method, methodType, eventHandlerType, eventHandlerMethods.get( 0 ) );
  }

  @Nonnull
  private TypeElement getEventHandlerType( @Nonnull final ExecutableElement method )
  {
    final DeclaredType typeMirror =
      ProcessorUtil.getTypeMirrorAnnotationParameter( method, "value", EventHandler.class );
    if ( null != typeMirror )
    {
      return (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror );
    }
    else
    {
      return processingEnv.getElementUtils().getTypeElement( Procedure.class.getName() );
    }
  }

  @Nonnull
  private String deriveEventHandlerName( @Nonnull final ExecutableElement method,
                                         @Nonnull final EventHandler annotation )
    throws ReactProcessorException
  {
    if ( ProcessorUtil.isSentinelName( annotation.name() ) )
    {
      return method.getSimpleName().toString();
    }
    else
    {
      final String name = annotation.name();
      if ( name.isEmpty() || !ProcessorUtil.isJavaIdentifier( name ) )
      {
        throw new ReactProcessorException( "Method annotated with @EventHandler specified invalid name " + name,
                                           method );
      }
      return name;
    }
  }

  private void determineLifecycleMethods( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    /*
     * Get the list of lifecycle methods that have been overridden by typeElement
     * a parent class, or by a default method method implemented by typeElement or
     * a parent class.
     */
    final Collection<ExecutableElement> lifecycleMethods = getComponentLifecycleMethods().values();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Component.class.getName() );
    final List<MethodDescriptor> overriddenLifecycleMethods =
      // Get all methods on type parent classes, and default methods from interfaces
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).stream()
        // Only keep methods that override the lifecycle methods
        .filter( m -> lifecycleMethods.stream().anyMatch( l -> elementUtils.overrides( m, l, typeElement ) ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .collect( Collectors.toList() );

    descriptor.setLifecycleMethods( overriddenLifecycleMethods );
  }

  @Nonnull
  private String deriveComponentName( @Nonnull final TypeElement typeElement, @Nonnull final ReactComponent component )
  {
    if ( ProcessorUtil.isSentinelName( component.name() ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      final String name = component.name();
      if ( name.isEmpty() || !ProcessorUtil.isJavaIdentifier( name ) )
      {
        throw new ReactProcessorException( "The @ReactComponent specified an invalid name. Name should be follow " +
                                           "the rules of a java identifier.", typeElement );
      }
      return name;
    }
  }

  @Nonnull
  private HashMap<String, ExecutableElement> getComponentLifecycleMethods()
  {
    if ( _componentLifecycleMethods.isEmpty() )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Component.class.getName() );
      for ( final ExecutableElement method : ProcessorUtil.getMethods( componentType, processingEnv.getTypeUtils() ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( LIFECYCLE_METHODS.contains( methodName ) )
        {
          _componentLifecycleMethods.put( methodName, method );
        }
      }

    }
    return _componentLifecycleMethods;
  }

  private void determineComponentType( @Nonnull final ComponentDescriptor descriptor,
                                       @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Component.class.getName() );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    /*
     * Arez need not be on the classpath in which case this will return a null value to arezComponentType.
     * Our code should just gracefully handle this and perform none of the arez specific checks or generation.
     */
    @Nullable
    final TypeElement arezComponentType =
      processingEnv.getElementUtils().getTypeElement( "react.arez.ReactArezComponent" );
    @Nullable
    final TypeMirror rawArezComponentType =
      null == arezComponentType ? null : processingEnv.getTypeUtils().erasure( arezComponentType.asType() );

    final TypeMirror type = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( type, rawComponentType );
    final boolean isArezComponent =
      null != rawArezComponentType && processingEnv.getTypeUtils().isSubtype( type, rawArezComponentType );

    if ( !isComponent )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a subclass of react.core.Component",
                                         typeElement );
    }
    else if ( isArezComponent )
    {
      final AnnotationMirror arezAnnotation = typeElement.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "org.realityforge.arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null == arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target extends react.arez.ReactArezComponent and should " +
                                           "be annotated with org.realityforge.arez.annotations.ArezComponent but " +
                                           "is not", typeElement );
      }
      final ExecutableElement nameKey = arezAnnotation.getElementValues().keySet().stream().
        filter( k -> "name".equals( k.getSimpleName().toString() ) ).
        findAny().orElse( null );
      final AnnotationValue nameAnnotation = arezAnnotation.getElementValues().get( nameKey );
      if ( null == nameAnnotation &&
           !descriptor.getName().equals( descriptor.getElement().getSimpleName().toString() ) )
      {
        throw new ReactProcessorException( "@ArezComponent annotation does not specify a name value but " +
                                           "@ReactComponent annotation specified a name that does not match " +
                                           "default name value for @ArezComponent", typeElement );
      }
      else if ( null != nameAnnotation && !nameAnnotation.getValue().equals( descriptor.getName() ) )
      {
        throw new ReactProcessorException( "@ArezComponent annotation specified name parameter but it does not " +
                                           "match the name of the @ReactComponent", typeElement );
      }
    }

    descriptor.setArezComponent( isArezComponent );
  }

  private void determinePropsAndStateTypes( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Component.class.getName() );
    final List<? extends TypeParameterElement> typeParameters = componentType.getTypeParameters();
    assert 2 == typeParameters.size();

    final TypeParameterElement propsTypeParameter = typeParameters.get( 0 );
    assert propsTypeParameter.getSimpleName().toString().equals( "P" );
    final TypeElement propsType = resolveToElement( descriptor, propsTypeParameter );
    descriptor.setPropsType( propsType );

    final TypeParameterElement stateTypeParameter = typeParameters.get( 1 );
    assert stateTypeParameter.getSimpleName().toString().equals( "S" );
    final TypeElement stateType = resolveToElement( descriptor, stateTypeParameter );
    descriptor.setStateType( stateType );
  }

  @Nonnull
  private TypeElement resolveToElement( @Nonnull final ComponentDescriptor descriptor,
                                        @Nonnull final TypeParameterElement typeParameter )
  {
    final TypeMirror propsType =
      processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), typeParameter );
    return (TypeElement) processingEnv.getTypeUtils().asElement( propsType );
  }
}
