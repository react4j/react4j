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
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class Generator
{
  private static final ClassName INJECT_CLASSNAME = ClassName.get( "javax.inject", "Inject" );
  private static final ClassName PROVIDER_CLASSNAME = ClassName.get( "javax.inject", "Provider" );
  private static final ClassName NONNULL_CLASSNAME = ClassName.get( "javax.annotation", "Nonnull" );
  private static final ClassName NULLABLE_CLASSNAME = ClassName.get( "javax.annotation", "Nullable" );

  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );

  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "Observable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVABLE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableRef" );
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
  static TypeSpec buildComponentBuilder( @Nonnull final ComponentDescriptor descriptor )
  {
    final String name = descriptor.getNestedClassPrefix() + descriptor.getBuilderName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    ProcessorUtil.copyAccessModifiers( descriptor.getElement(), builder );

    // Private constructor so can not instantiate
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    int stepCount = 1;

    final List<PropDescriptor> props = descriptor.getProps();
    final int propCount = props.size();

    final boolean isKeyLastStep = 0 == propCount;
    builder.addType( buildKeyBuilderStepInterface( descriptor, isKeyLastStep ) );
    builder.addMethod( buildStaticKeyMethod( descriptor, isKeyLastStep ) );

    for ( int i = 0; i < propCount; i++ )
    {
      final PropDescriptor prop = props.get( i );
      // Step 1 is the key
      final int step = ++stepCount;
      final boolean isPropLastStep = propCount == i + 1;
      builder.addType( buildPropBuilderStepInterface( descriptor, prop, step, isPropLastStep ) );
      builder.addMethod( buildStaticPropMethod( descriptor, prop, step, isPropLastStep ) );
    }

    builder.addType( buildBuilder( descriptor, stepCount ) );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildStaticKeyMethod( @Nonnull final ComponentDescriptor descriptor,
                                                  final boolean isLastStep )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "key" ).
        addAnnotation( NONNULL_CLASSNAME );

    method.addModifiers( Modifier.STATIC );
    if ( descriptor.getDeclaredType().asElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.addModifiers( Modifier.PUBLIC );
    }
    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( String.class ), "key", Modifier.FINAL ).
        addAnnotation( NONNULL_CLASSNAME );
    method.addParameter( parameter.build() );

    ProcessorUtil.copyTypeParameters( descriptor.getElement(), method );

    final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
    method.addStatement( "return new $T" + infix + "().key( key )", ClassName.bestGuess( "Builder" ) );
    if ( isLastStep )
    {
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      method.returns( ClassName.bestGuess( "Builder2" ) );
    }
    return method.build();
  }

  private static TypeSpec buildKeyBuilderStepInterface( @Nonnull final ComponentDescriptor descriptor,
                                                        final boolean isLastStep )
  {
    return buildBuilderStepInterface( descriptor, 1, isLastStep, "key", method -> {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( String.class ), "key" ).addAnnotation( NONNULL_CLASSNAME );
      method.addParameter( parameter.build() );
    } );
  }

  private static MethodSpec buildKeyBuilderStepImpl( @Nonnull final ComponentDescriptor descriptor,
                                                     final boolean isLastStep )
  {
    return buildBuilderStepImpl( descriptor, 1, isLastStep, "key", method -> {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( String.class ), "key", Modifier.FINAL ).addAnnotation( NONNULL_CLASSNAME );
      method.addParameter( parameter.build() );
      method.addStatement( "_props.set( $S, $T.requireNonNull( key ) )", "key", Objects.class );
    } );
  }

  @Nonnull
  private static MethodSpec buildStaticPropMethod( @Nonnull final ComponentDescriptor descriptor,
                                                   @Nonnull final PropDescriptor prop,
                                                   final int step,
                                                   final boolean isLastStep )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( prop.getName() ).
        addAnnotation( NONNULL_CLASSNAME );

    method.addModifiers( Modifier.STATIC );
    if ( descriptor.getDeclaredType().asElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.addModifiers( Modifier.PUBLIC );
    }
    ProcessorUtil.copyTypeParameters( prop.getMethodType(), method );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), method );

    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( prop.getMethodType().getReturnType() ), prop.getName(), Modifier.FINAL ).
        addAnnotation( NONNULL_CLASSNAME );
    method.addParameter( parameter.build() );

    final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
    method.addStatement( "return new $T" + infix + "().$N( $N )",
                         ClassName.bestGuess( "Builder" ),
                         prop.getName(),
                         prop.getName() );
    if ( isLastStep )
    {
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      method.returns( ClassName.bestGuess( "Builder" + ( step + 1 ) ) );
    }
    return method.build();
  }

  @Nonnull
  private static TypeSpec buildPropBuilderStepInterface( @Nonnull final ComponentDescriptor descriptor,
                                                         @Nonnull final PropDescriptor prop,
                                                         final int step,
                                                         final boolean isLastStep )
  {
    return buildBuilderStepInterface( descriptor, step, isLastStep, prop.getName(), method -> {
      ProcessorUtil.copyTypeParameters( prop.getMethodType(), method );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( prop.getMethodType().getReturnType() ), prop.getName() )
          .addAnnotation( NONNULL_CLASSNAME );
      method.addParameter( parameter.build() );
    } );
  }

  private static MethodSpec buildPropBuilderStepImpl( @Nonnull final ComponentDescriptor descriptor,
                                                      @Nonnull final PropDescriptor prop,
                                                      final int step,
                                                      final boolean isLastStep )
  {
    return buildBuilderStepImpl( descriptor, step, isLastStep, prop.getName(), method -> {
      ProcessorUtil.copyTypeParameters( prop.getMethodType(), method );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( prop.getMethodType().getReturnType() ), prop.getName(), Modifier.FINAL )
          .addAnnotation( NONNULL_CLASSNAME );
      method.addParameter( parameter.build() );
      method.addStatement( "_props.set( $S, $T.requireNonNull( $N ) )", prop.getName(), Objects.class, prop.getName() );
    } );
  }

  @Nonnull
  private static TypeSpec buildBuilderStepInterface( @Nonnull final ComponentDescriptor descriptor,
                                                     final int step,
                                                     final boolean isLastStep,
                                                     @Nonnull final String name,
                                                     @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Builder" + step );
    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    if ( !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      builder.addAnnotation( AnnotationSpec.builder( SuppressWarnings.class )
                               .addMember( "value", "$S", "unused" )
                               .build() );
    }

    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT );
    action.accept( method );
    if ( isLastStep )
    {
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      method.returns( ClassName.bestGuess( "Builder" + ( step + 1 ) ) );
    }

    builder.addMethod( method.build() );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildBuilderStepImpl( @Nonnull final ComponentDescriptor descriptor,
                                                  final int step,
                                                  final boolean isLastStep,
                                                  @Nonnull final String name,
                                                  @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( Override.class );
    method.addAnnotation( NONNULL_CLASSNAME );
    action.accept( method );
    if ( isLastStep )
    {
      method.addStatement( "return $T.createElement( $T.TYPE, $T.uncheckedCast( _props ) )",
                           REACT_CLASSNAME,
                           descriptor.getEnhancedClassName(),
                           JS_CLASSNAME );
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      method.addStatement( "return this" );
      method.returns( ClassName.bestGuess( "Builder" + ( step + 1 ) ) );
    }

    return method.build();
  }

  @Nonnull
  private static TypeSpec buildBuilder( @Nonnull final ComponentDescriptor descriptor, final int stepCount )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Builder" );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), builder );
    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );
    for ( int i = 0; i < stepCount; i++ )
    {
      builder.addSuperinterface( getParameterizedTypeName( descriptor, ClassName.bestGuess( "Builder" + ( i + 1 ) ) ) );
    }

    {
      final ParameterizedTypeName type =
        ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.get( Object.class ) );
      final FieldSpec.Builder field = FieldSpec.builder( type, "_props", Modifier.PRIVATE, Modifier.FINAL );
      field.initializer( "$T.of()", JS_PROPERTY_MAP_CLASSNAME );
      builder.addField( field.build() );
    }

    builder.addMethod( buildKeyBuilderStepImpl( descriptor, 1 == stepCount ) );

    final List<PropDescriptor> props = descriptor.getProps();
    final int propCount = props.size();

    for ( int i = 0; i < propCount; i++ )
    {
      final PropDescriptor prop = props.get( i );
      // 1 - based
      final int initialIndex = 1;
      final int keyOffset = 1;
      final int step = i + keyOffset + initialIndex;
      final boolean isLastStep = propCount == i + 1;
      builder.addMethod( buildPropBuilderStepImpl( descriptor, prop, step, isLastStep ) );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeName getParameterizedTypeName( @Nonnull final ComponentDescriptor descriptor,
                                                    @Nonnull final ClassName baseName )
  {
    final List<? extends TypeMirror> arguments = descriptor.getDeclaredType().getTypeArguments();
    if ( arguments.isEmpty() )
    {
      return baseName;
    }
    else
    {
      return ParameterizedTypeName.get( baseName, arguments.stream().map( TypeName::get ).toArray( TypeName[]::new ) );
    }
  }

  @Nonnull
  static TypeSpec buildEnhancedComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getEnhancedName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    builder.superclass( descriptor.getComponentType() );

    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
          addMember( "type", "$S", descriptor.getName() ).
          addMember( "deferSchedule", "true" );
      if ( descriptor.needsInjection() )
      {
        annotation.addMember( "inject", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
      }
      builder.addAnnotation( annotation.build() );
      builder.addModifiers( Modifier.ABSTRACT );
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
                         Modifier.FINAL ).
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

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      builder.addMethod( buildPropMethod( descriptor, prop ).build() );
      if ( descriptor.isArezComponent() )
      {
        builder.addMethod( buildPropObservableRefMethod( prop ).build() );
      }
    }

    if ( descriptor.isArezComponent() )
    {
      builder.addMethod( buildReportPropsChangedMethod( descriptor ).build() );
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

  private static MethodSpec.Builder buildPropMethod( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final PropDescriptor prop )
  {
    final TypeMirror returnType = prop.getMethodType().getReturnType();
    final ExecutableElement methodElement = prop.getMethod();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( methodElement.getSimpleName().toString() ).
        returns( TypeName.get( returnType ) );
    ProcessorUtil.copyTypeParameters( prop.getMethodType(), method );
    ProcessorUtil.copyAccessModifiers( methodElement, method );
    ProcessorUtil.copyDocumentedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", prop.getName() ).
          addMember( "expectSetter", "false" );
      method.addAnnotation( annotation.build() );
    }
    final String convertMethodName;
    switch ( returnType.getKind() )
    {
      case BOOLEAN:
        convertMethodName = "asBoolean";
        break;
      case BYTE:
        convertMethodName = "asByte";
        break;
      case CHAR:
        convertMethodName = "asChar";
        break;
      case DOUBLE:
        convertMethodName = "asDouble";
        break;
      case FLOAT:
        convertMethodName = "asFloat";
        break;
      case INT:
        convertMethodName = "asInt";
        break;
      case LONG:
        convertMethodName = "asLong";
        break;
      case SHORT:
        convertMethodName = "asShort";
        break;
      case TYPEVAR:
        convertMethodName = "cast";
        break;
      case DECLARED:
        if ( returnType.toString().equals( "java.lang.String" ) )
        {
          convertMethodName = "asString";
        }
        else
        {
          convertMethodName = "cast";
        }
        break;
      case ARRAY:
        convertMethodName = "cast";
        break;
      default:
        throw new ReactProcessorException( "Return type of @Prop method named '" + prop.getName() +
                                           "' is not yet handled. Return type: " + returnType, methodElement );
    }
    method.addStatement( "return $T.asPropertyMap( props() ).getAny( $S ).$N()",
                         JS_CLASSNAME,
                         prop.getName(),
                         convertMethodName );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPropObservableRefMethod( @Nonnull final PropDescriptor prop )
  {
    return MethodSpec.methodBuilder( toObservableRefMethodName( prop ) ).
      addModifiers( Modifier.PROTECTED, Modifier.ABSTRACT ).
      addAnnotation( NONNULL_CLASSNAME ).
      addAnnotation( OBSERVABLE_REF_ANNOTATION_CLASSNAME ).
      returns( OBSERVABLE_CLASSNAME );
  }

  @Nonnull
  private static String toObservableRefMethodName( @Nonnull final PropDescriptor prop )
  {
    final String name = prop.getName();
    return "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 ) + "Observable";
  }

  @Nonnull
  private static MethodSpec.Builder buildReportPropsChangedMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( "reportPropsChanged" ).
      addModifiers( Modifier.PROTECTED, Modifier.FINAL ).
      addAnnotation( Override.class ).
      addParameter( ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "nextProps", Modifier.FINAL ).
        addAnnotation( NULLABLE_CLASSNAME ).build() );
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      final String code =
        "if ( !$T.isTripleEqual( $T.asPropertyMap( props() ).get( $S ), $T.asPropertyMap( nextProps ).get( $S ) ) )";
      block.beginControlFlow( code, JS_CLASSNAME, JS_CLASSNAME, prop.getName(), JS_CLASSNAME, prop.getName() );
      block.addStatement( "$N().reportChanged()", toObservableRefMethodName( prop ) );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    return method;
  }

  private static FieldSpec.Builder buildProviderField( @Nonnull final ComponentDescriptor descriptor )
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
    builder.addModifiers( Modifier.PRIVATE );

    final TypeName superType =
      ParameterizedTypeName.get( REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME,
                                 ClassName.get( descriptor.getPropsType().asType() ),
                                 ClassName.get( descriptor.getStateType().asType() ),
                                 ClassName.get( descriptor.getContextType().asType() ),
                                 descriptor.getComponentType() );

    builder.superclass( superType );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

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
          returns( descriptor.getComponentType() );
      if ( descriptor.needsInjection() )
      {
        method.addStatement( "return getProvider().get()" );
      }
      else
      {
        final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
        method.addStatement( "return new $T" + infix + "()", descriptor.getClassNameToConstruct() );
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

  private static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
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
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerFactoryClassName() );

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
