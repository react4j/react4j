package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.SuppressWarningsUtil;

final class FactoryGenerator
{
  @Nonnull
  private static final ClassName NATIVE_VIEW_CLASSNAME = ClassName.get( "react4j.internal", "NativeView" );
  @Nonnull
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  @Nonnull
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
  @Nonnull
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );

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

    buildFields( processingEnv, builder, descriptor, constructor );
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

    final var whitelistedAnnotations = new ArrayList<>( GeneratorUtil.ANNOTATION_WHITELIST );
    whitelistedAnnotations.add( "sting.Named" );
    whitelistedAnnotations.add( "javax.inject.Named" );
    for ( final var parameter : descriptor.getInjectableConstructorParameters() )
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
                                   @Nonnull final ViewDescriptor descriptor,
                                   @Nonnull final ExecutableElement constructor )
  {
    for ( final var parameter : descriptor.getInjectableConstructorParameters() )
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
                         .addStatement( "return InjectSupport.create( view )" )
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
    for ( final var input : descriptor.getConstructorInputs() )
    {
      final var parameter = input.getParameter();
      assert null != parameter;
      final var localName = parameter.getSimpleName().toString();
      final var convertMethodName = getConverter( input );
      if ( !input.getType().getKind().isPrimitive() && !input.isNonNull() )
      {
        final var extraction = CodeBlock.builder();
        extraction.addStatement( "$T $N", TypeName.get( input.getType() ), localName );
        extraction.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
        extraction.addStatement( "$N = null != view.inputs().getAsAny( $T.Inputs.$N ) ? " +
                                 "view.inputs().getAsAny( $T.Inputs.$N ).$N() : null",
                                 localName,
                                 descriptor.getEnhancedClassName(),
                                 input.getConstantName(),
                                 descriptor.getEnhancedClassName(),
                                 input.getConstantName(),
                                 convertMethodName );
        extraction.nextControlFlow( "else" );
        extraction.addStatement( "$N = $T.uncheckedCast( view.inputs().getAsAny( $T.Inputs.$N ) )",
                                 localName,
                                 JS_CLASSNAME,
                                 descriptor.getEnhancedClassName(),
                                 input.getConstantName() );
        extraction.endControlFlow();
        method.addCode( extraction.build() );
      }
      else
      {
        method.addStatement( "final $T $N = view.inputs().getAsAny( $T.Inputs.$N ).$N()",
                             TypeName.get( input.getType() ),
                             localName,
                             descriptor.getEnhancedClassName(),
                             input.getConstantName(),
                             convertMethodName );
      }
    }

    final var constructor = ElementsUtil.getConstructors( descriptor.getElement() ).get( 0 );
    final var args = new StringBuilder( "return new $T( view" );
    final var params = new ArrayList<>();
    params.add( descriptor.getArezClassName() );
    for ( final VariableElement parameter : constructor.getParameters() )
    {
      args.append( ", " );
      final var input = descriptor.getConstructorInputs()
        .stream()
        .filter( d -> parameter.equals( d.getParameter() ) )
        .findAny()
        .orElse( null );
      if ( null != input )
      {
        args.append( "$N" );
        params.add( parameter.getSimpleName().toString() );
      }
      else
      {
        args.append( "c_factory.$N" );
        params.add( parameter.getSimpleName().toString() );
      }
    }
    args.append( " )" );
    return method.addStatement( args.toString(), params.toArray() );
  }

  @Nonnull
  private static String getConverter( @Nonnull final InputDescriptor input )
  {
    return switch ( input.getType().getKind() )
    {
      case BOOLEAN -> "asBoolean";
      case BYTE -> "asByte";
      case CHAR -> "asChar";
      case DOUBLE -> "asDouble";
      case FLOAT -> "asFloat";
      case INT -> "asInt";
      case LONG -> "asLong";
      case SHORT -> "asShort";
      case TYPEVAR, ARRAY -> "cast";
      case DECLARED -> input.getType().toString().equals( "java.lang.String" ) ? "asString" : "cast";
      default -> throw new ProcessorException( "Return type of @Input method is not yet handled. Type: " +
                                               input.getType().getKind(),
                                               input.getElement() );
    };
  }
}
