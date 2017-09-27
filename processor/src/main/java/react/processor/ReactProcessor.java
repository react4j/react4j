package react.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
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
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
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
    if ( descriptor.isStateless() )
    {
      //Start generating stateless components. Once this is done move on to others
      emitTypeSpec( descriptor.getPackageName(), Generator.buildNativeComponent( descriptor ) );
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
    final ReactComponent component = typeElement.getAnnotation( ReactComponent.class );
    assert null != component;
    final String name =
      ProcessorUtil.isSentinelName( component.name() ) ? typeElement.getSimpleName().toString() : component.name();
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentDescriptor descriptor = new ComponentDescriptor( name, packageElement, typeElement );

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
    final TypeElement arezComponentType = processingEnv.getElementUtils().getTypeElement( "react.arez.ArezComponent" );
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

    descriptor.setStateless( isStatelessComponent );

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

    return descriptor;
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
