package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import org.realityforge.proton.GeneratorUtil;

final class DaggerComponentExtensionGenerator
{
  private static final ClassName NONNULL_CLASSNAME = ClassName.get( "javax.annotation", "Nonnull" );
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );

  private DaggerComponentExtensionGenerator()
  {
  }

  @Nonnull
  private static TypeSpec buildInjectSupport( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.needsInjection();
    final TypeSpec.Builder builder =
      TypeSpec.classBuilder( "InjectSupport" ).addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL );

    builder.addField( buildFactoryField( descriptor ).build() );
    builder.addMethod( buildSetFactoryMethod( descriptor ).build() );
    builder.addMethod( buildInjectCreateMethod( descriptor ).build() );

    return builder.build();
  }

  @Nonnull
  private static FieldSpec.Builder buildFactoryField( @Nonnull final ComponentDescriptor descriptor )
  {
    return FieldSpec.builder( descriptor.getArezClassName().nestedClass( "Factory" ),
                              "c_factory",
                              Modifier.STATIC,
                              Modifier.PRIVATE );
  }

  @Nonnull
  private static MethodSpec.Builder buildSetFactoryMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "setFactory" )
      .addModifiers( Modifier.STATIC )
      .addParameter( ParameterSpec
                       .builder( descriptor.getArezClassName().nestedClass( "Factory" ), "factory", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME ).build() );
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
    block.addStatement( "$T.invariant( () -> null == c_factory, () -> \"Attempted to re-initialize the React4j " +
                        "dependency injection provider for the component named '$N'. Initialization should only " +
                        "occur a single time.\" )", GUARDS_CLASSNAME, descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    method.addStatement( "c_factory = factory" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildInjectCreateMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final ParameterSpec.Builder parameter =
      ParameterSpec
        .builder( ClassName.get( "react4j.internal", "NativeComponent" ), "nativeComponent", Modifier.FINAL )
        .addAnnotation( NONNULL_CLASSNAME );
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "create" )
        .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
        .addParameter( parameter.build() )
        .addAnnotation( NONNULL_CLASSNAME )
        .returns( descriptor.getEnhancedClassName() );
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
    block.addStatement(
      "$T.invariant( () -> null != c_factory, () -> \"Attempted to create an instance of the React4j " +
      "component named '$N' before the dependency injection provider has been initialized. Please see " +
      "the documentation at https://react4j.github.io/dependency_injection for directions how to " +
      "configure dependency injection.\" )",
      GUARDS_CLASSNAME,
      descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    return method.addStatement( "return c_factory.create( nativeComponent )" );
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerComponentExtensionClassName() );
    final ClassName superClassName = descriptor.getArezDaggerExtensionClassName();
    builder.addSuperinterface( superClassName );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, React4jProcessor.class.getName() );
    GeneratorUtil.addOriginatingTypes( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PUBLIC );

    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "bind" + descriptor.getName() )
        .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT );

    method.addStatement( "InjectSupport.setFactory( $N().createFactory() )",
                         "get" + descriptor.getName() + "DaggerSubcomponent" );

    builder.addMethod( method.build() );

    /*
     * This overridden method is required as Dagger is unable to see subcomponent methods from superclass
     */
    builder.addMethod( MethodSpec
                         .methodBuilder( "get" + descriptor.getName() + "DaggerSubcomponent" )
                         .addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC )
                         .addAnnotation( Override.class )
                         .returns( ClassName.bestGuess( "DaggerSubcomponent" ) )
                         .build() );

    if ( descriptor.needsInjection() )
    {
      builder.addType( buildInjectSupport( descriptor ) );
    }

    return builder.build();
  }
}
