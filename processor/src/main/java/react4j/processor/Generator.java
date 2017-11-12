package react4j.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import elemental2.core.JsObject;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactNode;

final class Generator
{
  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildEnhancedComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getEnhancedName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    builder.superclass( descriptor.getClassName() );

    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( ClassName.get( "org.realityforge.arez.annotations", "ArezComponent" ) ).
          addMember( "type", "$S", descriptor.getName() );
      builder.addAnnotation( annotation.build() );
    }

    ProcessorUtil.copyAccessModifiers( element, builder );

    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    final FieldSpec.Builder field =
      FieldSpec.builder( getJsConstructorFnType( descriptor ),
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL,
                         Modifier.PRIVATE ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addField( buildEventHandlerField( eventHandler ).build() );
    }

    builder.addMethod( buildFactoryMethod().build() );
    builder.addMethod( buildFactory2Method( descriptor ).build() );
    builder.addMethod( buildFactory3Method( descriptor ).build() );

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addMethod( buildStaticEventHandlerMethod( descriptor, eventHandler ).build() );
    }

    final MethodDescriptor renderMethod = descriptor.getRenderMethod();
    if ( !renderMethod.getMethod().getSimpleName().toString().equals( "render" ) )
    {
      builder.addMethod( buildRenderAdapterMethod( renderMethod ).build() );
    }

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addMethod( buildEventHandlerBuilderMethod( descriptor, eventHandler ).build() );
    }
    if ( descriptor.isArezComponent() )
    {
      for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
      {
        final AnnotationMirror nonActionAnnotation = eventHandler.getMethod().getAnnotationMirrors().stream().
          filter( m -> m.getAnnotationType().toString().equals( "react4j.arez.NoAutoAction" ) ).
          findAny().orElse( null );
        if ( null == nonActionAnnotation )
        {
          builder.addMethod( buildEventHandlerActionMethod( eventHandler ).build() );
        }
      }
    }

    if ( !descriptor.getLifecycleMethods().isEmpty() )
    {
      builder.addType( buildNativeLifecycleInterface( descriptor ) );
    }
    builder.addType( buildNativeComponent( descriptor ) );

    return builder.build();
  }

  @Nonnull
  private static ParameterizedTypeName getJsConstructorFnType( @Nonnull final ComponentDescriptor descriptor )
  {
    return ParameterizedTypeName.get( ClassName.get( ComponentConstructorFunction.class ),
                                      TypeName.get( descriptor.getPropsType().asType() ),
                                      TypeName.get( descriptor.getStateType().asType() ),
                                      ClassName.bestGuess( "NativeReactComponent" ) );
  }

  @Nonnull
  private static FieldSpec.Builder buildEventHandlerField( @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final String handlerName = "_" + eventHandler.getMethod().getSimpleName().toString();
    return FieldSpec.builder( handlerType, handlerName, Modifier.FINAL, Modifier.PRIVATE ).
      addAnnotation( Nonnull.class ).
      initializer( "create$N()", handlerName );
  }

  @Nonnull
  private static MethodSpec.Builder buildStaticEventHandlerMethod( @Nonnull final ComponentDescriptor descriptor,
                                                                   @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final String handlerName = "_" + eventHandler.getMethod().getSimpleName();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( handlerName ).
        addAnnotation( Nonnull.class ).
        returns( handlerType );

    method.addModifiers( Modifier.STATIC );

    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( descriptor.getElement().asType() ), "component", Modifier.FINAL ).
        addAnnotation( Nonnull.class );
    method.addParameter( parameter.build() );

    method.addStatement( "return (($T) component).$N", descriptor.getEnhancedClassName(), handlerName );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildRenderAdapterMethod( @Nonnull final MethodDescriptor renderMethod )
  {
    return MethodSpec.methodBuilder( "render" ).
      addModifiers( Modifier.PROTECTED ).
      addAnnotation( Override.class ).
      addAnnotation( Nullable.class ).
      returns( ReactNode.class ).
      addStatement( "return $T.of( $N() )", ReactNode.class, renderMethod.getMethod().getSimpleName().toString() );
  }

  @Nonnull
  private static MethodSpec.Builder buildEventHandlerBuilderMethod( @Nonnull final ComponentDescriptor descriptor,
                                                                    @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "create_" + eventHandler.getMethod().getSimpleName() ).
        addModifiers( Modifier.PRIVATE ).
        addAnnotation( Nonnull.class ).
        returns( handlerType );

    final ExecutableElement target = eventHandler.getEventHandlerMethod();
    final int targetParameterCount = target.getParameters().size();
    String args =
      0 == targetParameterCount ?
      "()" :
      IntStream.range( 0, targetParameterCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );
    if ( 1 < targetParameterCount )
    {
      args = "(" + args + ")";
    }

    final int paramCount = eventHandler.getMethod().getParameters().size();
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );

    method.addStatement( "final $T handler = " + args + " -> this.$N(" + params + ")",
                         handlerType,
                         eventHandler.getMethod().getSimpleName() );

    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if( $T.enableComponentNames() )", ReactConfig.class );
    final String code =
      "$T.defineProperty( $T.cast( handler ), \"name\", $T.cast( JsPropertyMap.of( \"value\", $S ) ) )";
    block.addStatement( code,
                        JsObject.class,
                        Js.class,
                        Js.class,
                        descriptor.getName() + "." + eventHandler.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    method.addStatement( "return handler" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildEventHandlerActionMethod( @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( eventHandler.getMethod().getSimpleName().toString() ).
        returns( TypeName.get( eventHandler.getMethodType().getReturnType() ) );
    ProcessorUtil.copyTypeParameters( eventHandler.getMethodType(), method );
    ProcessorUtil.copyAccessModifiers( eventHandler.getMethod(), method );
    ProcessorUtil.copyDocumentedAnnotations( eventHandler.getMethod(), method );

    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( ClassName.get( "org.realityforge.arez.annotations", "Action" ) ).
        addMember( "reportParameters", "false" );
    method.addAnnotation( annotation.build() );

    final int paramCount = eventHandler.getMethod().getParameters().size();
    for ( int i = 0; i < paramCount; i++ )
    {
      final TypeMirror paramType = eventHandler.getMethodType().getParameterTypes().get( i );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( paramType ), "arg" + i, Modifier.FINAL );
      ProcessorUtil.copyDocumentedAnnotations( eventHandler.getMethod().getParameters().get( i ), parameter );
      method.addParameter( parameter.build() );
    }
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );

    final boolean isVoid = eventHandler.getMethodType().getReturnType().getKind() == TypeKind.VOID;

    method.addStatement( ( isVoid ? "" : "return " ) + "super.$N(" + params + ")",
                         eventHandler.getMethod().getSimpleName() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildFactoryMethod()
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( Nonnull.class ).
      addModifiers( Modifier.STATIC ).
      returns( ClassName.get( ReactNode.class ) ).
      addStatement( "return $T.createElement( TYPE )", React.class );
  }

  @Nonnull
  private static MethodSpec.Builder buildFactory2Method( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( Nonnull.class ).
      addModifiers( Modifier.STATIC ).
      returns( ClassName.get( ReactNode.class ) ).
      addParameter( ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
        addAnnotation( Nullable.class ).build() ).
      addStatement( "return $T.createElement( TYPE, props )", React.class );
  }

  @Nonnull
  private static MethodSpec.Builder buildFactory3Method( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( Nonnull.class ).
      addModifiers( Modifier.STATIC ).
      returns( ClassName.get( ReactNode.class ) ).
      addParameter( ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
        addAnnotation( Nullable.class ).build() ).
      addParameter( ParameterSpec.builder( ReactNode.class, "child", Modifier.FINAL ).
        addAnnotation( Nullable.class ).build() ).
      addStatement( "return $T.createElement( TYPE, props, child )", React.class );
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final ParameterizedTypeName constructorType = getJsConstructorFnType( descriptor );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( Nonnull.class ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( constructorType );

    method.addStatement( "final $T componentConstructor = $T::new",
                         constructorType,
                         ClassName.bestGuess( "NativeReactComponent" ) );
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", ReactConfig.class );
    codeBlock.addStatement( "$T.of( componentConstructor ).set( \"displayName\", $S )",
                            JsPropertyMap.class,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );
    method.addStatement( "return componentConstructor" );
    return method;
  }

  @Nonnull
  private static TypeSpec buildNativeComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "NativeReactComponent" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    final TypeName superType =
      ParameterizedTypeName.get( ClassName.get( NativeAdapterComponent.class ),
                                 ClassName.get( descriptor.getPropsType().asType() ),
                                 ClassName.get( descriptor.getStateType().asType() ),
                                 ClassName.get( descriptor.getElement() ) );

    builder.superclass( superType );

    if ( !descriptor.getLifecycleMethods().isEmpty() )
    {
      builder.addSuperinterface( ClassName.bestGuess( "Lifecycle" ) );
    }

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
          addAnnotation( Nonnull.class );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addParameter( props.build() );
      method.addStatement( "super( props )" );
      builder.addMethod( method.build() );
    }

    // build createComponent
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createComponent" ).
          addAnnotation( Override.class ).
          addModifiers( Modifier.PROTECTED ).
          returns( ClassName.get( descriptor.getElement() ) );
      method.addStatement( "return new $T()", descriptor.getClassNameToConstruct() );
      builder.addMethod( method.build() );
    }

    // Lifecycle methods
    {

      for ( final MethodDescriptor lifecycleMethod : descriptor.getLifecycleMethods() )
      {
        final String methodName = lifecycleMethod.getMethod().getSimpleName().toString();
        final MethodSpec.Builder method =
          MethodSpec.methodBuilder( methodName ).
            addModifiers( Modifier.PUBLIC ).
            addAnnotation( Override.class ).
            returns( ClassName.get( lifecycleMethod.getMethodType().getReturnType() ) );

        ProcessorUtil.copyTypeParameters( lifecycleMethod.getMethodType(), method );

        final StringJoiner params = new StringJoiner( "," );
        final List<? extends VariableElement> sourceParameters = lifecycleMethod.getMethod().getParameters();
        final List<? extends TypeMirror> sourceParameterTypes = lifecycleMethod.getMethodType().getParameterTypes();
        final int parameterCount = sourceParameters.size();
        for ( int i = 0; i < parameterCount; i++ )
        {
          final VariableElement parameter = sourceParameters.get( i );
          final TypeMirror parameterType = sourceParameterTypes.get( i );
          final String parameterName = parameter.getSimpleName().toString();
          final ParameterSpec.Builder parameterSpec =
            ParameterSpec.builder( TypeName.get( parameterType ), parameterName, Modifier.FINAL ).
              addAnnotation( Nonnull.class );
          method.addParameter( parameterSpec.build() );
          params.add( parameterName );
        }

        final StringBuilder sb = new StringBuilder();
        if ( TypeKind.VOID != lifecycleMethod.getMethodType().getReturnType().getKind() )
        {
          sb.append( "return " );
        }

        sb.append( "perform" );
        sb.append( Character.toUpperCase( methodName.charAt( 0 ) ) );
        sb.append( methodName.substring( 1 ) );
        sb.append( "(" );
        sb.append( params.toString() );
        sb.append( ")" );

        method.addStatement( sb.toString() );
        builder.addMethod( method.build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Lifecycle" );

    builder.addAnnotation( AnnotationSpec.builder( JsType.class ).
      addMember( "isNative", "true" ).
      build() );

    builder.addModifiers( Modifier.STATIC );

    // Lifecycle methods
    {

      for ( final MethodDescriptor lifecycleMethod : descriptor.getLifecycleMethods() )
      {
        final String methodName = lifecycleMethod.getMethod().getSimpleName().toString();
        final MethodSpec.Builder method =
          MethodSpec.methodBuilder( methodName ).
            addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
            returns( ClassName.get( lifecycleMethod.getMethodType().getReturnType() ) );

        ProcessorUtil.copyTypeParameters( lifecycleMethod.getMethodType(), method );

        final List<? extends VariableElement> sourceParameters = lifecycleMethod.getMethod().getParameters();
        final List<? extends TypeMirror> sourceParameterTypes = lifecycleMethod.getMethodType().getParameterTypes();
        final int parameterCount = sourceParameters.size();
        for ( int i = 0; i < parameterCount; i++ )
        {
          final VariableElement parameter = sourceParameters.get( i );
          final TypeMirror parameterType = sourceParameterTypes.get( i );
          final String parameterName = parameter.getSimpleName().toString();
          final ParameterSpec.Builder parameterSpec =
            ParameterSpec.builder( TypeName.get( parameterType ), parameterName ).addAnnotation( Nonnull.class );
          method.addParameter( parameterSpec.build() );
        }

        builder.addMethod( method.build() );
      }
    }

    return builder.build();
  }
}
