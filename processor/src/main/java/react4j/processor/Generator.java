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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class Generator
{
  private static final ClassName INJECT_CLASSNAME = ClassName.get( "javax.inject", "Inject" );
  private static final ClassName PROVIDER_CLASSNAME = ClassName.get( "javax.inject", "Provider" );
  private static final ClassName NONNULL_CLASSNAME = ClassName.get( "javax.annotation", "Nonnull" );
  private static final ClassName NULLABLE_CLASSNAME = ClassName.get( "javax.annotation", "Nullable" );

  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );

  private static final ClassName INJECTIBLE_CLASSNAME =
    ClassName.get( "arez.annotations", "Injectible" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );

  private static final ClassName JS_OBJECT_CLASSNAME = ClassName.get( "elemental2.core", "JsObject" );

  private static final ClassName JS_TYPE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsType" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );

  private static final ClassName COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j.core", "ComponentConstructorFunction" );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j.core", "ReactNode" );
  private static final ClassName REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME =
    ClassName.get( "react4j.core", "NativeAdapterComponent" );
  private static final ClassName REACT_PROP_TYPE_CLASSNAME = ClassName.get( "react4j.core", "PropType" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j.core", "React" );
  private static final ClassName REACT_CONFIG_CLASSNAME = ClassName.get( "react4j.core", "ReactConfig" );
  private static final ClassName COMPONENT_CLASSNAME = ClassName.get( "react4j.core", "Component" );

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
        AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
          addMember( "type", "$S", descriptor.getName() ).
          addMember( "deferSchedule", "true" );
      if ( descriptor.needsInjection() )
      {
        annotation.addMember( "inject", "$T.ENABLED", INJECTIBLE_CLASSNAME );
      }
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

    if ( descriptor.needsInjection() )
    {
      builder.addField( buildProviderField( descriptor ).build() );
    }

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addField( buildEventHandlerField( eventHandler ).build() );
    }

    if ( descriptor.needsInjection() )
    {
      builder.addMethod( buildSetProviderMethod( descriptor ).build() );
      builder.addMethod( buildGetProviderMethod( descriptor ).build() );
    }

    builder.addMethod( buildFactoryMethod().build() );
    builder.addMethod( buildFactory2Method( descriptor ).build() );
    builder.addMethod( buildFactory3Method( descriptor ).build() );

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    for ( final EventHandlerDescriptor eventHandler : descriptor.getEventHandlers() )
    {
      builder.addMethod( buildStaticEventHandlerMethod( descriptor, eventHandler ).build() );
    }

    if ( descriptor.needsInjection() && !descriptor.isArezComponent() )
    {
      builder.addMethod( MethodSpec.constructorBuilder().addAnnotation( INJECT_CLASSNAME ).build() );
    }

    if ( descriptor.shouldRunArezScheduler() && descriptor.isArezComponent() )
    {
      builder.addMethod( MethodSpec.methodBuilder( "triggerScheduler" ).
        addAnnotation( Override.class ).
        addModifiers( Modifier.PROTECTED, Modifier.FINAL ).
        addStatement( "getContext().triggerScheduler()" ).
        build() );
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

  private static FieldSpec.Builder buildProviderField( final @Nonnull ComponentDescriptor descriptor )
  {
    return FieldSpec.builder( ParameterizedTypeName.get( PROVIDER_CLASSNAME,
                                                         TypeName.get( descriptor.getDeclaredType() ) ),
                              "c_provider",
                              Modifier.STATIC,
                              Modifier.PRIVATE );
  }

  @Nonnull
  private static ParameterizedTypeName getJsConstructorFnType( @Nonnull final ComponentDescriptor descriptor )
  {
    return ParameterizedTypeName.get( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                                      TypeName.get( descriptor.getPropsType().asType() ),
                                      TypeName.get( descriptor.getContextType().asType() ) );
  }

  @Nonnull
  private static FieldSpec.Builder buildEventHandlerField( @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final String handlerName = "_" + eventHandler.getMethod().getSimpleName().toString();
    return FieldSpec.builder( handlerType, handlerName, Modifier.FINAL, Modifier.PRIVATE ).
      addAnnotation( NONNULL_CLASSNAME ).
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
        addAnnotation( NONNULL_CLASSNAME ).
        returns( handlerType );

    method.addModifiers( Modifier.STATIC );

    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( descriptor.getElement().asType() ), "component", Modifier.FINAL ).
        addAnnotation( NONNULL_CLASSNAME );
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
      addAnnotation( NULLABLE_CLASSNAME ).
      returns( REACT_NODE_CLASSNAME ).
      addStatement( "return $T.of( $N() )", REACT_NODE_CLASSNAME, renderMethod.getMethod().getSimpleName().toString() );
  }

  @Nonnull
  private static MethodSpec.Builder buildEventHandlerBuilderMethod( @Nonnull final ComponentDescriptor descriptor,
                                                                    @Nonnull final EventHandlerDescriptor eventHandler )
  {
    final TypeName handlerType = TypeName.get( eventHandler.getEventHandlerType().asType() );
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "create_" + eventHandler.getMethod().getSimpleName() ).
        addModifiers( Modifier.PRIVATE ).
        addAnnotation( NONNULL_CLASSNAME ).
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
    block.beginControlFlow( "if( $T.enableComponentNames() )", REACT_CONFIG_CLASSNAME );
    final String code =
      "$T.defineProperty( $T.cast( handler ), \"name\", $T.cast( $T.of( \"value\", $S ) ) )";
    block.addStatement( code,
                        JS_OBJECT_CLASSNAME,
                        JS_CLASSNAME,
                        JS_CLASSNAME,
                        JS_PROPERTY_MAP_CLASSNAME,
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
      AnnotationSpec.builder( ACTION_CLASSNAME ).
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
  private static MethodSpec.Builder buildSetProviderMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "setProvider" ).
      addModifiers( Modifier.STATIC ).
      addParameter( ParameterizedTypeName.get( PROVIDER_CLASSNAME,
                                               TypeName.get( descriptor.getDeclaredType() ) ),
                    "provider",
                    Modifier.FINAL ).
      addStatement( "c_provider = provider" );
  }

  @Nonnull
  private static MethodSpec.Builder buildGetProviderMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "getProvider" ).
      addModifiers( Modifier.PRIVATE, Modifier.STATIC ).
      returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, TypeName.get( descriptor.getDeclaredType() ) ) ).
      addStatement( "$T.invariant( () -> null != c_provider, () -> \"Attempted to create an instance of the React4j " +
                    "component named '$N' before the dependency injection provider has been initialized. Please see " +
                    "the documentation at https://react4j.github.io/dependency_injection for directions how to " +
                    "configure dependency injection.\" )",
                    GUARDS_CLASSNAME,
                    descriptor.getName() ).
      addStatement( "return c_provider" );
  }

  @Nonnull
  private static MethodSpec.Builder buildFactoryMethod()
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( NONNULL_CLASSNAME ).
      addModifiers( Modifier.STATIC ).
      returns( REACT_NODE_CLASSNAME ).
      addStatement( "return $T.createElement( TYPE )", REACT_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildFactory2Method( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( NONNULL_CLASSNAME ).
      addModifiers( Modifier.STATIC ).
      returns( REACT_NODE_CLASSNAME ).
      addParameter( ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
        addAnnotation( NULLABLE_CLASSNAME ).build() ).
      addStatement( "return $T.createElement( TYPE, props )", REACT_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildFactory3Method( @Nonnull final ComponentDescriptor descriptor )
  {
    return MethodSpec.methodBuilder( "_create" ).
      addAnnotation( NONNULL_CLASSNAME ).
      addModifiers( Modifier.STATIC ).
      returns( REACT_NODE_CLASSNAME ).
      addParameter( ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
        addAnnotation( NULLABLE_CLASSNAME ).build() ).
      addParameter( ParameterSpec.builder( REACT_NODE_CLASSNAME, "child", Modifier.FINAL ).
        addAnnotation( NULLABLE_CLASSNAME ).build() ).
      addStatement( "return $T.createElement( TYPE, props, child )", REACT_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final ParameterizedTypeName constructorType = getJsConstructorFnType( descriptor );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( NONNULL_CLASSNAME ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( constructorType );

    method.addStatement( "final $T componentConstructor = $T::new",
                         constructorType,
                         ClassName.bestGuess( "NativeReactComponent" ) );
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", REACT_CONFIG_CLASSNAME );
    codeBlock.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"displayName\", $S )",
                            JS_CLASSNAME,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );

    final Map<String, TypeMirror> childContextTypeFields =
      descriptor.hasChildContextFields() ? descriptor.getChildContextTypeFields() : Collections.emptyMap();
    final Map<String, TypeMirror> contextTypeFields = descriptor.getContextTypeFields();
    if ( !childContextTypeFields.isEmpty() || !contextTypeFields.isEmpty() )
    {
      method.addStatement( "final $T valid = () -> null", REACT_PROP_TYPE_CLASSNAME );
    }
    if ( !contextTypeFields.isEmpty() )
    {
      method.addStatement( "final $T<$T> contextTypes = $T.of()",
                           JS_PROPERTY_MAP_CLASSNAME,
                           Object.class,
                           JS_PROPERTY_MAP_CLASSNAME );
      for ( final String contextKey : contextTypeFields.keySet() )
      {
        method.addStatement( "contextTypes.set( $S, valid )", contextKey );
      }
      method.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"contextTypes\", contextTypes )",
                           JS_CLASSNAME );
    }
    if ( !childContextTypeFields.isEmpty() )
    {
      method.addStatement( "final $T<$T> childContextTypes = $T.of()",
                           JS_PROPERTY_MAP_CLASSNAME,
                           Object.class,
                           JS_PROPERTY_MAP_CLASSNAME );
      for ( final String childContextKey : childContextTypeFields.keySet() )
      {
        method.addStatement( "childContextTypes.set( $S, valid )", childContextKey );
      }
      method.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"childContextTypes\", childContextTypes )",
                           JS_CLASSNAME );
    }
    if ( descriptor.hasDefaultPropsMethod() )
    {
      method.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"defaultProps\", $T.$N() )",
                           JS_CLASSNAME,
                           descriptor.getClassName(),
                           descriptor.getDefaultPropsMethod().getSimpleName().toString() );
    }
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
      ParameterizedTypeName.get( REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME,
                                 ClassName.get( descriptor.getPropsType().asType() ),
                                 ClassName.get( descriptor.getStateType().asType() ),
                                 ClassName.get( descriptor.getContextType().asType() ),
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
          addAnnotation( NULLABLE_CLASSNAME );
      final ParameterSpec.Builder context =
        ParameterSpec.builder( ClassName.get( descriptor.getContextType() ), "context", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addParameter( props.build() ).
          addParameter( context.build() );
      method.addStatement( "super( props, context )" );
      builder.addMethod( method.build() );
    }

    // build createComponent
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createComponent" ).
          addAnnotation( Override.class ).
          addModifiers( Modifier.PROTECTED ).
          returns( ClassName.get( descriptor.getElement() ) );
      if ( descriptor.needsInjection() )
      {
        method.addStatement( "return getProvider().get()" );
      }
      else
      {
        method.addStatement( "return new $T()", descriptor.getClassNameToConstruct() );
      }
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
              addAnnotation( NONNULL_CLASSNAME );
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

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
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
            ParameterSpec.builder( TypeName.get( parameterType ), parameterName ).addAnnotation( NONNULL_CLASSNAME );
          method.addParameter( parameterSpec.build() );
        }

        builder.addMethod( method.build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  static TypeSpec buildDaggerFactory( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder =
      TypeSpec.interfaceBuilder( ClassName.get( descriptor.getPackageName(),
                                                descriptor.getElement().getSimpleName() + "DaggerFactory" ) );

    builder.addModifiers( Modifier.PUBLIC );

    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "get" + descriptor.getName() + "DaggerSubcomponent" ).
          addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT ).
          returns( ClassName.bestGuess( "DaggerSubcomponent" ) );
      builder.addMethod( method.build() );
    }
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "bind" + descriptor.getName() ).
          addModifiers( Modifier.PUBLIC, Modifier.DEFAULT ).
          addStatement( "$T.setProvider( () -> $N().get() )",
                        descriptor.getEnhancedClassName(),
                        "get" + descriptor.getName() + "DaggerSubcomponent" );
      builder.addMethod( method.build() );
    }

    if ( descriptor.needsDaggerIntegration() )
    {
      builder.addType( buildDaggerModule( descriptor ) );
      builder.addType( buildDaggerComponent( descriptor ) );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildDaggerComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "DaggerSubcomponent" );

    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    final AnnotationSpec.Builder subcomponent =
      AnnotationSpec.builder( ClassName.bestGuess( Constants.DAGGER_SUBCOMPONENT_CLASSNAME ) );
    subcomponent.addMember( "modules", "DaggerModule.class" );
    builder.addAnnotation( subcomponent.build() );

    {
      final ParameterizedTypeName typeName =
        ParameterizedTypeName.get( PROVIDER_CLASSNAME, COMPONENT_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createProvider" ).
          addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
          returns( typeName );
      builder.addMethod( method.build() );
    }

    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "get" ).
          addModifiers( Modifier.DEFAULT, Modifier.PUBLIC ).
          returns( descriptor.getClassName() ).
          addStatement( "return ($T) createProvider().get()", descriptor.getClassName() );
      builder.addMethod( method.build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildDaggerModule( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "DaggerModule" );

    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL );
    builder.addAnnotation( ClassName.bestGuess( Constants.DAGGER_MODULE_CLASSNAME ) );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "provideComponent" ).
        addAnnotation( ClassName.bestGuess( Constants.DAGGER_PROVIDES_CLASSNAME ) ).
        addModifiers( Modifier.STATIC ).
        addParameter( descriptor.getClassNameToConstruct(), "component", Modifier.FINAL ).
        returns( COMPONENT_CLASSNAME ).
        addStatement( "return component" );
    builder.addMethod( method.build() );

    return builder.build();
  }
}
