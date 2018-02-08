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
import javax.lang.model.type.TypeKind;
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
                   "componentWillReceiveProps",
                   "componentWillUnmount",
                   "componentWillUpdate",
                   "componentDidCatch",
                   "getChildContext",
                   "shouldComponentUpdate" );

  /**
   * Cache of lifecycle names to methods as defined by Component.
   */
  private final HashMap<String, ExecutableElement> _componentLifecycleMethods = new HashMap<>();
  /**
   * Cache of render method method as defined by Component.
   */
  private ExecutableElement _componentRenderMethod;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear method caches to avoid potential inter-run problems
    _componentLifecycleMethods.clear();
    _componentRenderMethod = null;

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
    emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentBuilder( descriptor ) );
    if ( descriptor.needsDaggerIntegration() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildDaggerFactory( descriptor ) );
    }
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
    determineProps( descriptor );
    determineDefaultPropsMethod( descriptor );

    verifyNoUnexpectedAbstractMethod( descriptor );

    return descriptor;
  }

  private void verifyNoUnexpectedAbstractMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    if ( !descriptor.isArezComponent() )
    {
      final ExecutableElement abstractMethod =
        ProcessorUtil.getMethods( descriptor.getElement(), processingEnv.getTypeUtils() )
          .stream()
          .filter( m -> m.getModifiers().contains( Modifier.ABSTRACT ) )
          // Props are expected to be null methods
          .filter( m -> descriptor.getProps().stream().noneMatch( p -> p.getMethod() == m ) )
          .findAny()
          .orElse( null );
      if ( null != abstractMethod )
      {
        throw new ReactProcessorException( "@ReactComponent target has an unexpected abstract method",
                                           abstractMethod );
      }
    }
  }

  private void determineDefaultPropsMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final Types typeUtils = processingEnv.getTypeUtils();
    final List<ExecutableElement> defaultPropsMethods =
      ProcessorUtil.getMethods( descriptor.getElement(), typeUtils ).stream()
        .filter( m -> m.getSimpleName().toString().equals( "getInitialProps" ) )
        .collect( Collectors.toList() );

    if ( !defaultPropsMethods.isEmpty() )
    {
      for ( final ExecutableElement method : defaultPropsMethods )
      {
        final ExecutableType methodType =
          (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), method );

        final TypeElement typeElement =
          processingEnv.getElementUtils().getTypeElement( Constants.JS_PROPERTY_MAP_CLASSNAME );
        final TypeElement objectType = processingEnv.getElementUtils().getTypeElement( Object.class.getName() );
        final DeclaredType expectedType = typeUtils.getDeclaredType( typeElement, objectType.asType() );

        if ( methodType.getThrownTypes().isEmpty() &&
             methodType.getParameterTypes().isEmpty() &&
             method.getModifiers().contains( Modifier.STATIC ) &&
             !method.getModifiers().contains( Modifier.PRIVATE ) &&
             typeUtils.isAssignable( methodType.getReturnType(), expectedType ) )
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
    verifyNoDuplicateAnnotations( method );
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
          filter( m -> m.getAnnotationType().toString().equals( "arez.annotations.Action" ) ).
          findAny().orElse( null );
        if ( null != actionAnnotation )
        {
          throw new ReactProcessorException( "Method annotated with @EventHandler is also annotated with " +
                                             "@arez.annotations.Action but is not annotated with " +
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
    assert null != typeMirror;
    return (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror );
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
      if ( !ProcessorUtil.isJavaIdentifier( name ) )
      {
        throw new ReactProcessorException( "Method annotated with @EventHandler specified invalid name " + name,
                                           method );
      }
      return name;
    }
  }

  private void determineProps( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      ProcessorUtil.getMethods( descriptor.getElement(), processingEnv.getTypeUtils() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_ANNOTATION_CLASSNAME ) )
        .map( m -> createPropDescriptor( descriptor, m ) )
        .sorted( this::sortChildrenToEnd )
        .collect( Collectors.toList() );

    final PropDescriptor childrenProp =
      props.stream().filter( p -> p.getName().equals( "children" ) ).findAny().orElse( null );
    final PropDescriptor childProp =
      props.stream().filter( p -> p.getName().equals( "child" ) ).findAny().orElse( null );
    if ( null != childrenProp && null != childProp )
    {
      throw new ReactProcessorException( "Multiple candidate children @Prop annotated methods: " +
                                         childrenProp.getMethod().getSimpleName() + " and " +
                                         childProp.getMethod().getSimpleName(),
                                         childrenProp.getMethod() );
    }

    descriptor.setProps( props );
  }

  private int sortChildrenToEnd( @Nonnull final PropDescriptor o1, @Nonnull final PropDescriptor o2 )
  {
    final String name1 = o1.getName();
    final String name2 = o2.getName();

    //children moves to the end, key moves to the start

    if ( name1.equals( "children" ) || name1.equals( "child" ) || name2.equals( "key" ) )
    {
      return 1;
    }
    else if ( name2.equals( "children" ) || name2.equals( "child" ) || name1.equals( "key" ) )
    {
      return -1;
    }
    else
    {
      return 0;
    }
  }

  @Nonnull
  private PropDescriptor createPropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final ExecutableElement method )
  {
    final String name = derivePropName( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

    verifyNoDuplicateAnnotations( method );
    MethodChecks.mustBeAbstract( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotHaveAnyParameters( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustReturnAValue( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_ANNOTATION_CLASSNAME, method );

    if ( "key".equals( name ) )
    {
      throw new ReactProcessorException(
        "@Prop named 'key' is invalid as references value used this key in reconciliation process. This value can be accessed via Component.getKey()",
        method );
    }
    else if ( "child".equals( name ) &&
              (
                methodType.getReturnType().getKind() != TypeKind.DECLARED &&
                !"react4j.core.ReactNode".equals( methodType.getReturnType().toString() )
              ) )
    {
      throw new ReactProcessorException( "@Prop named 'child' should be of type react4j.core.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              (
                methodType.getReturnType().getKind() != TypeKind.DECLARED &&
                !"react4j.core.ReactNode[]".equals( methodType.getReturnType().toString() )
              ) )
    {
      throw new ReactProcessorException( "@Prop named 'children' should be of type react4j.core.ReactNode[]", method );
    }

    return new PropDescriptor( name, method, methodType );
  }

  @Nonnull
  private String derivePropName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String specifiedName =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.PROP_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    final String name = ProcessorUtil.getPropertyAccessorName( method, specifiedName );
    if ( !ProcessorUtil.isJavaIdentifier( name ) )
    {
      throw new ReactProcessorException( "Method annotated with @Prop specified invalid name " + specifiedName,
                                         method );
    }
    else
    {
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
     * Get the render method that has been overridden by the typeElement, a parent class, or by a default
     * method method implemented by the typeElement or a parent class.
     */
    final ExecutableElement renderMethod = getComponentRenderMethod();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
    final MethodDescriptor overriddenRenderMethod =
      // Get all methods on type parent classes, and default methods from interfaces
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).stream()
        // Only keep method if they override the render method
        .filter( m -> elementUtils.overrides( m, renderMethod, typeElement ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .findAny().
        orElse( null );

    if ( null == overriddenRenderMethod )
    {
      throw new ReactProcessorException( "The react component does not override any render methods.", typeElement );
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
      if ( !ProcessorUtil.isJavaIdentifier( name ) )
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
  private ExecutableElement getComponentRenderMethod()
  {
    if ( null == _componentRenderMethod )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : ProcessorUtil.getMethods( componentType, processingEnv.getTypeUtils() ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( "render".equals( methodName ) )
        {
          _componentRenderMethod = method;
          break;
        }
      }
    }
    return _componentRenderMethod;
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
        filter( m -> m.getAnnotationType().toString().equals( "arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null != arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target extends react4j.arez.ReactArezComponent and should " +
                                           "not be annotated with arez.annotations.ArezComponent as " +
                                           "React4j will add annotation", typeElement );
      }
    }

    final boolean runArezScheduler =
      ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).
        stream().anyMatch( this::hasAutorunAnnotation );

    final boolean needsInjection = isInjectionRequired( typeElement );
    final boolean isDaggerPresent = needsInjection && isDaggerRequired( typeElement );

    descriptor.setNeedsInjection( needsInjection );
    descriptor.setNeedsDaggerIntegration( isDaggerPresent );
    descriptor.setArezComponent( isArezComponent );
    descriptor.setRunArezScheduler( runArezScheduler );
  }

  private boolean isInjectionRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "inject" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return ProcessorUtil.getFieldElements( typeElement ).stream().anyMatch( this::hasInjectAnnotation ) ||
               ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() ).
                 stream().anyMatch( this::hasInjectAnnotation );
    }
  }

  private boolean isDaggerRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "dagger" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return null != processingEnv.getElementUtils().getTypeElement( Constants.DAGGER_MODULE_CLASSNAME );
    }
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
    assert 2 == typeParameters.size();

    final TypeParameterElement stateTypeParameter = typeParameters.get( 0 );
    assert stateTypeParameter.getSimpleName().toString().equals( "S" );
    final TypeElement stateType = resolveToElement( descriptor, stateTypeParameter );
    descriptor.setStateType( stateType );

    final TypeParameterElement contextTypeParameter = typeParameters.get( 1 );
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

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String[] annotationTypes =
      new String[]{ Constants.EVENT_HANDLER_ANNOTATION_CLASSNAME,
                    Constants.PROP_ANNOTATION_CLASSNAME };
    for ( int i = 0; i < annotationTypes.length; i++ )
    {
      final String type1 = annotationTypes[ i ];
      final Object annotation1 = ProcessorUtil.findAnnotationByType( method, type1 );
      if ( null != annotation1 )
      {
        for ( int j = i + 1; j < annotationTypes.length; j++ )
        {
          final String type2 = annotationTypes[ j ];
          final Object annotation2 = ProcessorUtil.findAnnotationByType( method, type2 );
          if ( null != annotation2 )
          {
            final String message =
              "Method can not be annotated with both @" + ProcessorUtil.toSimpleName( type1 ) +
              " and @" + ProcessorUtil.toSimpleName( type2 );
            throw new ReactProcessorException( message, method );
          }
        }
      }
    }
  }
}
