package react4j.processor;

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
import java.util.Map;
import java.util.Objects;
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
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes React annotated source and generates models from the annotations.
 */
@SuppressWarnings( "Duplicates" )
@AutoService( Processor.class )
@SupportedAnnotationTypes( { "react4j.annotations.*" } )
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
                   "componentDidCatch",
                   "getChildContext",
                   "shouldComponentUpdate" );
  private static final List<String> RENDER_METHODS =
    Arrays.asList( "render",
                   "renderAsArray",
                   "renderAsJsArray" );

  /**
   * Cache of lifecycle names to methods as defined by Component.
   */
  private final HashMap<String, ExecutableElement> _componentLifecycleMethods = new HashMap<>();
  /**
   * Cache of render method names to methods as defined by Component.
   */
  private HashMap<String, ExecutableElement> _componentRenderMethods = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear method caches to avoid potential inter-run problems
    _componentLifecycleMethods.clear();
    _componentRenderMethods.clear();

    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME );
    final Set<? extends Element> elements = env.getElementsAnnotatedWith( annotation );
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
          " Report the error at: https://github.com/react4j/react4j/issues\n" +
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
    emitTypeSpec( descriptor.getPackageName(), Generator.buildEnhancedComponent( descriptor ) );
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
    final String name = deriveComponentName( typeElement );
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentDescriptor descriptor = new ComponentDescriptor( name, packageElement, typeElement );

    determineComponentType( descriptor, typeElement );
    determinePropsAndStateTypes( descriptor );
    determineLifecycleMethods( typeElement, descriptor );
    determineChildContextTypes( descriptor );
    determineRenderMethod( typeElement, descriptor );
    determineEventHandlers( descriptor );
    determineDefaultPropsMethod( descriptor );

    return descriptor;
  }

  private void determineDefaultPropsMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> defaultPropsMethods =
      ProcessorUtil.getMethods( descriptor.getElement(), processingEnv.getTypeUtils() ).stream()
        .filter( m -> m.getSimpleName().toString().equals( "getInitialProps" ) )
        .collect( Collectors.toList() );

    if ( !defaultPropsMethods.isEmpty() )
    {
      for ( final ExecutableElement method : defaultPropsMethods )
      {
        final ExecutableType methodType =
          (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

        if ( methodType.getThrownTypes().isEmpty() &&
             methodType.getParameterTypes().isEmpty() &&
             method.getModifiers().contains( Modifier.STATIC ) &&
             !method.getModifiers().contains( Modifier.PRIVATE ) &&
             processingEnv.getTypeUtils()
               .isAssignable( methodType.getReturnType(), descriptor.getPropsType().asType() ) )
        {
          descriptor.setDefaultPropsMethod( method );
          return;
        }
      }
      throw new ReactProcessorException( "The getInitialProps method does not satisfy constraints. The method must " +
                                         "be static, non-private, have no parameters, throw no exceptions and must " +
                                         "return a value that is compatible with the prop type for the component.",
                                         descriptor.getElement() );
    }
  }

  private void determineEventHandlers( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<EventHandlerDescriptor> eventHandlers =
      ProcessorUtil.getMethods( descriptor.getElement(), processingEnv.getTypeUtils() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.EVENT_HANDLER_ANNOTATION_CLASSNAME ) )
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
      if ( null == ProcessorUtil.findAnnotationByType( handlerType, Constants.JS_FUNCTION_CLASSNAME ) )
      {
        throw new ReactProcessorException( "The @EventHandler specified an invalid type that is not annotated " +
                                           "with the annotation jsinterop.annotations.JsFunction.",
                                           eventHandler.getMethod() );
      }
      final EventHandlerDescriptor matched = eventHandlers.stream()
        .filter( h -> h != eventHandler && h.getName().equals( eventHandler.getName() ) )
        .findAny().orElse( null );
      if ( null != matched )
      {
        throw new ReactProcessorException( "The @EventHandler has the same name as the event handler defined by " +
                                           matched.getMethod() + ".", eventHandler.getMethod() );
      }
      final EventHandlerDescriptor matched2 = eventHandlers.stream()
        .filter( h -> h != eventHandler &&
                      h.getMethod().getSimpleName().equals( eventHandler.getMethod().getSimpleName() ) )
        .findAny().orElse( null );
      if ( null != matched2 )
      {
        throw new ReactProcessorException( "The @EventHandler has the same method name as the event handler defined " +
                                           "by " + matched2.getMethod() + ".", eventHandler.getMethod() );
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
    final String name = deriveEventHandlerName( method );
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

    if ( descriptor.isArezComponent() )
    {
      final AnnotationMirror nonActionAnnotation = method.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "react4j.arez.NoAutoAction" ) ).
        findAny().orElse( null );
      if ( null == nonActionAnnotation )
      {
        final AnnotationMirror actionAnnotation = method.getAnnotationMirrors().stream().
          filter( m -> m.getAnnotationType().toString().equals( "org.realityforge.arez.annotations.Action" ) ).
          findAny().orElse( null );
        if ( null != actionAnnotation )
        {
          throw new ReactProcessorException( "Method annotated with @EventHandler is also annotated with " +
                                             "@org.realityforge.arez.annotations.Action but is not annotated with " +
                                             "@react4j.arez.NoAutoAction which would stop react4j from also " +
                                             "annotating the method with @Action. Please remove @Action or add " +
                                             "@NoAutoAction annotation.", method );
        }
      }
    }

    return new EventHandlerDescriptor( name, method, methodType, eventHandlerType, eventHandlerMethods.get( 0 ) );
  }

  @Nonnull
  private TypeElement getEventHandlerType( @Nonnull final ExecutableElement method )
  {
    final DeclaredType typeMirror =
      ProcessorUtil.getTypeMirrorAnnotationParameter( processingEnv.getElementUtils(),
                                                      method,
                                                      Constants.EVENT_HANDLER_ANNOTATION_CLASSNAME,
                                                      "value" );
    if ( null != typeMirror )
    {
      return (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror );
    }
    else
    {
      return processingEnv.getElementUtils().getTypeElement( Constants.PROCEDURE_CLASSNAME );
    }
  }

  @Nonnull
  private String deriveEventHandlerName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.EVENT_HANDLER_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return method.getSimpleName().toString();
    }
    else
    {
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
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
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

  private void determineRenderMethod( @Nonnull final TypeElement typeElement,
                                      @Nonnull final ComponentDescriptor descriptor )
  {
    /*
     * Get the list of render methods that have been overridden by typeElement
     * a parent class, or by a default method method implemented by typeElement or
     * a parent class.
     */
    final Collection<ExecutableElement> renderMethods = getComponentRenderMethods().values();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
    final List<MethodDescriptor> overriddenRenderMethods =
      // Get all methods on type parent classes, and default methods from interfaces
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).stream()
        // Only keep methods that override the render methods
        .filter( m -> renderMethods.stream().anyMatch( l -> elementUtils.overrides( m, l, typeElement ) ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .collect( Collectors.toList() );

    // The assumption is that overriddenRenderMethods is the list of candidate methods.
    // The developer should only override one so we assume that if the list contains multiple
    // we select the last one and verify no other render methods are defined on the declared type that
    // defines this method. This means that the search will walk interfaces and superclasses appropriately
    // and select the "correct" render method. If it can not select the correct method then we assume the user
    // has done something wrong and warn them

    if ( overriddenRenderMethods.isEmpty() )
    {
      throw new ReactProcessorException( "The react component does not override any render methods.", typeElement );
    }
    else
    {
      final int size = overriddenRenderMethods.size();
      final MethodDescriptor candidate = overriddenRenderMethods.get( size - 1 );
      if ( size > 1 )
      {
        final TypeElement enclosingElement = (TypeElement) candidate.getMethod().getEnclosingElement();
        final TypeMirror candidateType = candidate.getMethodType().getReceiverType();
        final MethodDescriptor other = overriddenRenderMethods.get( size - 2 );
        final TypeMirror otherType = other.getMethodType().getReceiverType();
        if ( Objects.equals( otherType, candidateType ) )
        {
          throw new ReactProcessorException( "The react component has two candidate render methods " +
                                             candidate.getMethod().getSimpleName() + " and " +
                                             other.getMethod().getSimpleName() + " defined on the same type " +
                                             enclosingElement.getQualifiedName() + ".", typeElement );
        }
      }
      descriptor.setRenderMethod( candidate );
    }
  }

  @Nonnull
  private String deriveComponentName( @Nonnull final TypeElement typeElement )
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 typeElement,
                                                 Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      if ( name.isEmpty() || !ProcessorUtil.isJavaIdentifier( name ) )
      {
        throw new ReactProcessorException( "The @ReactComponent specified an invalid name. Name should be follow " +
                                           "the rules of a java identifier.", typeElement );
      }
      return name;
    }
  }

  /**
   * Return the set of lifecycle methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private HashMap<String, ExecutableElement> getComponentLifecycleMethods()
  {
    if ( _componentLifecycleMethods.isEmpty() )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
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

  /**
   * Return the set of render methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private HashMap<String, ExecutableElement> getComponentRenderMethods()
  {
    if ( _componentRenderMethods.isEmpty() )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : ProcessorUtil.getMethods( componentType, processingEnv.getTypeUtils() ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( RENDER_METHODS.contains( methodName ) )
        {
          _componentRenderMethods.put( methodName, method );
        }
      }

    }
    return _componentRenderMethods;
  }

  private void determineComponentType( @Nonnull final ComponentDescriptor descriptor,
                                       @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    /*
     * Arez need not be on the classpath in which case this will return a null value to arezComponentType.
     * Our code should just gracefully handle this and perform none of the arez specific checks or generation.
     */
    @Nullable
    final TypeElement arezComponentType =
      processingEnv.getElementUtils().getTypeElement( "react4j.arez.ReactArezComponent" );
    @Nullable
    final TypeMirror rawArezComponentType =
      null == arezComponentType ? null : processingEnv.getTypeUtils().erasure( arezComponentType.asType() );

    final TypeMirror type = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( type, rawComponentType );
    final boolean isArezComponent =
      null != rawArezComponentType && processingEnv.getTypeUtils().isSubtype( type, rawArezComponentType );

    if ( !isComponent )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a subclass of react4j.core.Component",
                                         typeElement );
    }
    else if ( isArezComponent )
    {
      final AnnotationMirror arezAnnotation = typeElement.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "org.realityforge.arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null != arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target extends react4j.arez.ReactArezComponent and should " +
                                           "not be annotated with org.realityforge.arez.annotations.ArezComponent as " +
                                           "React4j will add annotation", typeElement );
      }
    }

    final boolean needsInjection =
      ProcessorUtil.getFieldElements( typeElement ).stream().anyMatch( this::hasInjectAnnotation ) ||
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).
        stream().anyMatch( this::hasInjectAnnotation );

    final boolean runArezScheduler =
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).
        stream().anyMatch( this::hasAutorunAnnotation );

    final boolean isDaggerPresent =
      needsInjection &&
      null != processingEnv.getElementUtils().getTypeElement( Constants.DAGGER_MODULE_CLASSNAME );

    descriptor.setNeedsInjection( needsInjection );
    descriptor.setNeedsDaggerIntegration( isDaggerPresent );
    descriptor.setArezComponent( isArezComponent );
    descriptor.setRunArezScheduler( runArezScheduler );
  }

  private boolean hasAutorunAnnotation( final Element method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.AUTORUN_ANNOTATION_CLASSNAME );
  }

  private boolean hasInjectAnnotation( final Element method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.INJECT_ANNOTATION_CLASSNAME );
  }

  private void determinePropsAndStateTypes( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final List<? extends TypeParameterElement> typeParameters = componentType.getTypeParameters();
    assert 3 == typeParameters.size();

    final TypeParameterElement propsTypeParameter = typeParameters.get( 0 );
    assert propsTypeParameter.getSimpleName().toString().equals( "P" );
    final TypeElement propsType = resolveToElement( descriptor, propsTypeParameter );
    descriptor.setPropsType( propsType );

    final TypeParameterElement stateTypeParameter = typeParameters.get( 1 );
    assert stateTypeParameter.getSimpleName().toString().equals( "S" );
    final TypeElement stateType = resolveToElement( descriptor, stateTypeParameter );
    descriptor.setStateType( stateType );

    final TypeParameterElement contextTypeParameter = typeParameters.get( 2 );
    assert contextTypeParameter.getSimpleName().toString().equals( "C" );
    final TypeElement contextType = resolveToElement( descriptor, contextTypeParameter );

    final Map<String, TypeMirror> contextTypeFields =
      ProcessorUtil.getFields( contextType, processingEnv.getTypeUtils() );

    descriptor.setContextType( contextType, contextTypeFields );
  }

  private void determineChildContextTypes( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodDescriptor getChildContext = descriptor.getLifecycleMethods().stream().
      filter( m -> m.getMethod().getSimpleName().toString().equals( "getChildContext" ) ).findFirst().orElse( null );
    if ( null != getChildContext )
    {
      final DeclaredType returnType = (DeclaredType) getChildContext.getMethodType().getReturnType();
      final Map<String, TypeMirror> childContextTypeFields =
        ProcessorUtil.getFields( (TypeElement) returnType.asElement(), processingEnv.getTypeUtils() );
      descriptor.setChildContextTypeFields( childContextTypeFields );
    }
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
