package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class FactoryGenerator
{
  @Nonnull
  private static final ClassName NATIVE_VIEW_CLASSNAME = ClassName.get( "react4j.internal", "NativeView" );
  @Nonnull
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  @Nonnull
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );

  private FactoryGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final ViewDescriptor descriptor )
  {
    final var builder = TypeSpec.classBuilder( descriptor.getFactoryClassName() );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, React4jProcessor.class.getName() );
    GeneratorUtil.addOriginatingTypes( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PUBLIC, Modifier.FINAL );

    if ( descriptor.enableSting() )
    {
      builder.addAnnotation( ClassName.bestGuess( Constants.STING_INJECTABLE_CLASSNAME ) );
      builder.addAnnotation( ClassName.bestGuess( Constants.STING_EAGER_CLASSNAME ) );
      GeneratorUtil.copyWhitelistedAnnotations( descriptor.getElement(),
                                                builder,
                                                Arrays.asList( Constants.STING_NAMED_CLASSNAME,
                                                               Constants.STING_CONTRIBUTE_TO_CLASSNAME ) );
    }

    final var constructor = ElementsUtil.getConstructors( descriptor.getElement() ).get( 0 );

    buildFields( processingEnv, builder, constructor );
    buildConstructor( processingEnv, descriptor, builder, constructor );
    buildCreateMethod( descriptor, builder );
    builder.addType( buildInjectSupport( descriptor ) );

    return builder.build();
  }

  private static void buildConstructor( @Nonnull final ProcessingEnvironment processingEnv,
                                        @Nonnull final ViewDescriptor descriptor,
                                        @Nonnull final TypeSpec.Builder builder,
                                        @Nonnull final ExecutableElement constructor )
  {
    final var ctor = MethodSpec.constructorBuilder();

    final var sting = descriptor.enableSting();
    final var additionalSuppressions = new ArrayList<String>();

    SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv,
                                                        ctor,
                                                        additionalSuppressions,
                                                        Collections.singletonList( constructor.asType() ) );
    GeneratorUtil.copyWhitelistedAnnotations( constructor, ctor );

    if ( !sting )
    {
      ctor.addModifiers( Modifier.PUBLIC );
    }

    final var whitelistedAnnotations = new ArrayList<String>( GeneratorUtil.ANNOTATION_WHITELIST );
    whitelistedAnnotations.add( "sting.Named" );
    whitelistedAnnotations.add( "javax.inject.Named" );
    for ( final var parameter : constructor.getParameters() )
    {
      final var name = parameter.getSimpleName().toString();
      final var param = ParameterSpec.builder( TypeName.get( parameter.asType() ), name, Modifier.FINAL );

      GeneratorUtil.copyWhitelistedAnnotations( parameter, param, whitelistedAnnotations );
      ctor.addParameter( param.build() );
      if ( AnnotationsUtil.hasNonnullAnnotation( parameter ) )
      {
        ctor.addStatement( "this.$N = $T.requireNonNull( $N )", name, Objects.class, name );
      }
      else
      {
        ctor.addStatement( "this.$N = $N", name, name );
      }
    }

    ctor.addStatement( "InjectSupport.setFactory( this )" );

    builder.addMethod( ctor.build() );
  }

  private static void buildFields( @Nonnull final ProcessingEnvironment processingEnv,
                                   @Nonnull final TypeSpec.Builder builder,
                                   @Nonnull final ExecutableElement constructor )
  {
    for ( final var parameter : constructor.getParameters() )
    {
      final var field = FieldSpec
        .builder( TypeName.get( parameter.asType() ),
                  parameter.getSimpleName().toString(),
                  Modifier.PRIVATE,
                  Modifier.FINAL );
      final var types = Arrays.asList( constructor.asType(), parameter.asType() );
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, field, types );
      GeneratorUtil.copyWhitelistedAnnotations( parameter, field );
      builder.addField( field.build() );
    }
  }

  private static void buildCreateMethod( @Nonnull final ViewDescriptor descriptor,
                                         @Nonnull final TypeSpec.Builder builder )
  {
    builder.addMethod( MethodSpec
                         .methodBuilder( "create" )
                         .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .addParameter( ParameterSpec
                                          .builder( NATIVE_VIEW_CLASSNAME, "view", Modifier.FINAL )
                                          .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                                          .build() )
                         .returns( descriptor.getEnhancedClassName() )
                         .addStatement( "return InjectSupport.create( view )", descriptor.getArezClassName() )
                         .build() );
  }

  @Nonnull
  private static TypeSpec buildInjectSupport( @Nonnull final ViewDescriptor descriptor )
  {
    // We create a separate InjectSupport class to avoid generating a clinit for the factory class
    // which can block optimizations in GWT
    final var builder =
      TypeSpec.classBuilder( "InjectSupport" ).addModifiers( Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL );

    builder.addField( buildFactoryField( descriptor ).build() );
    builder.addMethod( buildSetFactoryMethod( descriptor ).build() );
    builder.addMethod( buildInjectCreateMethod( descriptor ).build() );

    return builder.build();
  }

  @Nonnull
  private static FieldSpec.Builder buildFactoryField( @Nonnull final ViewDescriptor descriptor )
  {
    return FieldSpec.builder( descriptor.getFactoryClassName(), "c_factory", Modifier.STATIC, Modifier.PRIVATE );
  }

  @Nonnull
  private static MethodSpec.Builder buildSetFactoryMethod( @Nonnull final ViewDescriptor descriptor )
  {
    final var method = MethodSpec
      .methodBuilder( "setFactory" )
      .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
      .addParameter( ParameterSpec
                       .builder( descriptor.getFactoryClassName(), "factory", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME ).build() );
    final var block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
    block.addStatement( "$T.invariant( () -> null == c_factory, () -> \"Attempted to instantiate the React4j " +
                        "view factory for the view named '$N' a second time\" )",
                        GUARDS_CLASSNAME,
                        descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    method.addStatement( "c_factory = factory" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildInjectCreateMethod( @Nonnull final ViewDescriptor descriptor )
  {
    final var method =
      MethodSpec
        .methodBuilder( "create" )
        .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
        .addParameter( ParameterSpec
                         .builder( NATIVE_VIEW_CLASSNAME, "view", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME ).build() )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .returns( descriptor.getEnhancedClassName() );
    final var block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
    block.addStatement(
      "$T.invariant( () -> null != c_factory, () -> \"Attempted to create an instance of the React4j " +
      "view named '$N' before the view factory has been initialized. Please see " +
      "the documentation at https://react4j.github.io/dependency_injection for directions how to " +
      "configure dependency injection.\" )",
      GUARDS_CLASSNAME,
      descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    final var constructor = ElementsUtil.getConstructors( descriptor.getElement() ).get( 0 );
    return method.addStatement( "return new $T( view" +
                                constructor.getParameters()
                                  .stream()
                                  .map( p -> ", c_factory." + p.getSimpleName() )
                                  .collect( Collectors.joining() ) +
                                " )", descriptor.getArezClassName() );
  }
}
