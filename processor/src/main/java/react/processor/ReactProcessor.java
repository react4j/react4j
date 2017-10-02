package react.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import react.annotations.ReactComponent;
import react.core.Component;
import react.core.StatelessComponent;
import static javax.tools.Diagnostic.Kind.ERROR;

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

    return descriptor;
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
    final TypeElement statelessComponentType = elementUtils.getTypeElement( StatelessComponent.class.getName() );
    final List<MethodDescriptor> overriddenLifecycleMethods =
      // Get all methods on type parent classes, and default methods from interfaces
      ProcessorUtil.getMethods( typeElement ).stream()
        // Only keep methods that override the lifecycle methods
        .filter( m -> lifecycleMethods.stream().anyMatch( l -> elementUtils.overrides( m, l, typeElement ) ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType && m.getEnclosingElement() != statelessComponentType )
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
      for ( final ExecutableElement method : ProcessorUtil.getMethods( componentType ) )
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

    final TypeElement statelessComponentType =
      processingEnv.getElementUtils().getTypeElement( StatelessComponent.class.getName() );
    final TypeMirror rawStatelessComponentType =
      processingEnv.getTypeUtils().erasure( statelessComponentType.asType() );

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
    final boolean isStatelessComponent = processingEnv.getTypeUtils().isSubtype( type, rawStatelessComponentType );
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

    descriptor.setStateless( isStatelessComponent );
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
