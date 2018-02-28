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
import com.squareup.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

@SuppressWarnings( "Duplicates" )
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
  private static final ClassName AREZ_DEPENDENCY_CLASSNAME =
    ClassName.get( "arez.annotations", "Dependency" );

  private static final ClassName JS_OBJECT_CLASSNAME = ClassName.get( "elemental2.core", "JsObject" );
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );

  private static final ClassName JS_TYPE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsType" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );

  private static final ClassName COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j.core", "ComponentConstructorFunction" );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j.core", "ReactNode" );
  private static final ClassName REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME =
    ClassName.get( "react4j.core", "NativeAdapterComponent" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j.core", "React" );
  private static final ClassName REACT_CONFIG_CLASSNAME = ClassName.get( "react4j.core", "ReactConfig" );
  private static final ClassName COMPONENT_CLASSNAME = ClassName.get( "react4j.core", "Component" );

  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildComponentBuilder( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getBuilderClassName() );
    markTypeAsGenerated( builder );

    ProcessorUtil.copyAccessModifiers( descriptor.getElement(), builder );

    // Private constructor so can not instantiate
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final BuilderDescriptor builderDescriptor = buildBuilderDescriptor( descriptor );

    final ArrayList<Step> steps = builderDescriptor.getSteps();
    for ( final Step step : steps )
    {
      builder.addType( buildBuilderStepInterface( descriptor, step ) );
    }

    // key step
    buildStaticStepMethodMethods( descriptor, builder, steps.get( 0 ) );

    // first step which may be required prop, optional props, or build terminal step.
    buildStaticStepMethodMethods( descriptor, builder, steps.get( 1 ) );

    builder.addType( buildBuilder( descriptor, builderDescriptor ) );

    return builder.build();
  }

  private static void buildStaticStepMethodMethods( @Nonnull final ComponentDescriptor descriptor,
                                                    @Nonnull final TypeSpec.Builder builder,
                                                    @Nonnull final Step step )
  {
    final ArrayList<StepMethod> methods = step.getMethods();
    for ( final StepMethod method : methods )
    {
      builder.addMethod( buildStaticStepMethodMethod( descriptor, step, method ) );
    }
  }

  @Nonnull
  private static MethodSpec buildStaticStepMethodMethod( @Nonnull final ComponentDescriptor descriptor,
                                                         @Nonnull final Step step,
                                                         @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( stepMethod.getName() ).
        addAnnotation( NONNULL_CLASSNAME );

    method.addModifiers( Modifier.STATIC );
    if ( descriptor.getDeclaredType().asElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.addModifiers( Modifier.PUBLIC );
    }
    final ExecutableType propMethodType = stepMethod.getPropMethodType();
    if ( null != propMethodType )
    {
      ProcessorUtil.copyTypeParameters( propMethodType, method );
    }
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), method );

    if ( !stepMethod.isBuildIntrinsic() )
    {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
      final ExecutableElement propMethod = stepMethod.getPropMethod();
      if ( null != propMethod )
      {
        ProcessorUtil.copyDocumentedAnnotations( propMethod, parameter );
      }
      else if ( stepMethod.isKeyIntrinsic() || stepMethod.isChildrenStreamIntrinsic() )
      {
        parameter.addAnnotation( NONNULL_CLASSNAME );
      }
      else if ( stepMethod.isChildOfChildrenIntrinsic() )
      {
        parameter.addAnnotation( NULLABLE_CLASSNAME );
      }
      method.addParameter( parameter.build() );
    }

    final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
    if ( stepMethod.isBuildIntrinsic() )
    {
      method.addStatement( "return new $T" + infix + "().build()", ClassName.bestGuess( "Builder" ) );
    }
    else
    {
      method.addStatement( "return new $T" + infix + "().$N( $N )",
                           ClassName.bestGuess( "Builder" ),
                           stepMethod.getName(),
                           stepMethod.getName() );
    }
    configureStepMethodReturns( method, step, stepMethod.getStepMethodType() );
    return method.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildStepInterfaceMethod( @Nonnull final String name,
                                                              @Nonnull final Step step,
                                                              @Nonnull final StepMethodType stepMethodType,
                                                              @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT );
    method.addAnnotation( NONNULL_CLASSNAME );
    action.accept( method );
    configureStepMethodReturns( method, step, stepMethodType );
    return method;
  }

  private static void configureStepMethodReturns( @Nonnull final MethodSpec.Builder method,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethodType stepMethodType )
  {
    if ( StepMethodType.TERMINATE == stepMethodType )
    {
      method.returns( REACT_NODE_CLASSNAME );
    }
    else
    {
      final int returnIndex = step.getIndex() + ( StepMethodType.STAY == stepMethodType ? 0 : 1 );
      method.returns( ClassName.bestGuess( "Builder" + returnIndex ) );
    }
  }

  @Nonnull
  private static TypeSpec buildBuilderStepInterface( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final Step step )
  {
    final int stepIndex = step.getIndex();
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Builder" + stepIndex );
    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    if ( !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      builder.addAnnotation( AnnotationSpec.builder( SuppressWarnings.class )
                               .addMember( "value", "$S", "unused" )
                               .build() );
    }

    for ( final StepMethod stepMethod : step.getMethods() )
    {
      final StepMethodType stepMethodType = stepMethod.getStepMethodType();
      // Magically handle the step method named build
      if ( stepMethod.isBuildIntrinsic() )
      {
        builder.addMethod( buildStepInterfaceMethod( "build", step, stepMethodType, m -> {
        } ).build() );
      }
      else
      {
        builder.addMethod( buildStepInterfaceMethod( stepMethod.getName(), step, stepMethodType, m -> {
          final ExecutableType propMethodType = stepMethod.getPropMethodType();
          if ( null != propMethodType )
          {
            ProcessorUtil.copyTypeParameters( propMethodType, m );
          }
          if ( stepMethod.isChildrenIntrinsic() )
          {
            m.varargs();
          }
          final ParameterSpec.Builder parameter = ParameterSpec.builder( stepMethod.getType(), stepMethod.getName() );
          final ExecutableElement propMethod = stepMethod.getPropMethod();
          if ( null != propMethod )
          {
            ProcessorUtil.copyDocumentedAnnotations( propMethod, parameter );
          }
          else if ( stepMethod.isKeyIntrinsic() || stepMethod.isChildrenStreamIntrinsic() )
          {
            parameter.addAnnotation( NONNULL_CLASSNAME );
          }
          else if ( stepMethod.isChildOfChildrenIntrinsic() )
          {
            parameter.addAnnotation( NULLABLE_CLASSNAME );
          }
          m.addParameter( parameter.build() );
        } ).build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildBuilderStepImpl( @Nonnull final Step step, @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( stepMethod.getName() );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( Override.class );
    method.addAnnotation( NONNULL_CLASSNAME );

    final ExecutableType propMethodType = stepMethod.getPropMethodType();
    if ( null != propMethodType )
    {
      ProcessorUtil.copyTypeParameters( propMethodType, method );
    }
    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
    final ExecutableElement propMethod = stepMethod.getPropMethod();
    if ( null != propMethod )
    {
      ProcessorUtil.copyDocumentedAnnotations( propMethod, parameter );
    }
    else if ( stepMethod.isKeyIntrinsic() || stepMethod.isChildrenStreamIntrinsic() )
    {
      parameter.addAnnotation( NONNULL_CLASSNAME );
    }
    else if ( stepMethod.isChildOfChildrenIntrinsic() )
    {
      parameter.addAnnotation( NULLABLE_CLASSNAME );
    }
    method.addParameter( parameter.build() );

    if ( stepMethod.isChildrenIntrinsic() )
    {
      method.varargs();
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "for ( final $T child : $N )", REACT_NODE_CLASSNAME, stepMethod.getName() );
      block.addStatement( "child( child )" );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else if ( stepMethod.isChildOfChildrenIntrinsic() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != $N )", stepMethod.getName() );
      block.addStatement( "_children.push( $N )", stepMethod.getName() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      method.addStatement( "children( $N.toArray( $T[]::new ) )", stepMethod.getName(), REACT_NODE_CLASSNAME );
    }
    else if ( stepMethod.isChildIntrinsic() )
    {
      assert null != propMethod;
      if ( null != ProcessorUtil.findAnnotationByType( propMethod, Constants.NONNULL_ANNOTATION_CLASSNAME ) )
      {
        method.addStatement( "_child = $T.requireNonNull( $N )", Objects.class, stepMethod.getName() );
      }
      else
      {
        method.addStatement( "_child = $N", stepMethod.getName() );
      }
    }
    else if ( stepMethod.isKeyIntrinsic() ||
              ( null != propMethod &&
                null != ProcessorUtil.findAnnotationByType( propMethod, Constants.NONNULL_ANNOTATION_CLASSNAME ) ) )
    {
      method.addStatement( "_props.set( $S, $T.requireNonNull( $N ) )",
                           stepMethod.getName(),
                           Objects.class,
                           stepMethod.getName() );
    }
    else
    {
      method.addStatement( "_props.set( $S, $N )", stepMethod.getName(), stepMethod.getName() );
    }

    if ( StepMethodType.TERMINATE == stepMethod.getStepMethodType() )
    {
      method.addStatement( "return build()" );
    }
    else
    {
      method.addStatement( "return this" );
    }
    configureStepMethodReturns( method, step, stepMethod.getStepMethodType() );

    return method.build();
  }

  @Nonnull
  private static MethodSpec buildBuildStepImpl( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( "build" );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( NONNULL_CLASSNAME );
    if ( null != descriptor.findPropNamed( "children" ) )
    {
      method.addStatement( "return $T.createElement( $T.TYPE, $T.uncheckedCast( _props ), _children )",
                           REACT_CLASSNAME,
                           descriptor.getEnhancedClassName(),
                           JS_CLASSNAME );
    }
    else if ( null != descriptor.findPropNamed( "child" ) )
    {
      method.addStatement( "return $T.createElement( $T.TYPE, $T.uncheckedCast( _props ), _child )",
                           REACT_CLASSNAME,
                           descriptor.getEnhancedClassName(),
                           JS_CLASSNAME );
    }
    else
    {
      method.addStatement( "return $T.createElement( $T.TYPE, $T.uncheckedCast( _props ) )",
                           REACT_CLASSNAME,
                           descriptor.getEnhancedClassName(),
                           JS_CLASSNAME );
    }
    method.returns( REACT_NODE_CLASSNAME );
    return method.build();
  }

  @Nonnull
  private static TypeSpec buildBuilder( @Nonnull final ComponentDescriptor descriptor,
                                        @Nonnull final BuilderDescriptor builderDescriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Builder" );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), builder );
    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );
    final ArrayList<Step> steps = builderDescriptor.getSteps();
    for ( int i = 0; i < steps.size(); i++ )
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

    final HashSet<String> stepMethodsAdded = new HashSet<>();
    for ( final Step step : steps )
    {
      for ( final StepMethod stepMethod : step.getMethods() )
      {
        if ( stepMethodsAdded.add( stepMethod.getName() + stepMethod.getType().toString() ) )
        {
          if ( !stepMethod.isBuildIntrinsic() )
          {
            builder.addMethod( buildBuilderStepImpl( step, stepMethod ) );
            if ( stepMethod.isChildrenIntrinsic() )
            {
              final ParameterizedTypeName type =
                ParameterizedTypeName.get( JS_ARRAY_CLASSNAME, REACT_NODE_CLASSNAME );
              final FieldSpec.Builder field = FieldSpec.builder( type, "_children", Modifier.PRIVATE, Modifier.FINAL );
              field.initializer( "new $T<>()", JS_ARRAY_CLASSNAME );
              builder.addField( field.build() );
            }
            else if ( stepMethod.isChildIntrinsic() )
            {
              final FieldSpec.Builder field =
                FieldSpec.builder( REACT_NODE_CLASSNAME, "_child", Modifier.PRIVATE );
              builder.addField( field.build() );
            }
          }
        }
      }
    }
    builder.addMethod( buildBuildStepImpl( descriptor ) );

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
  static TypeSpec buildComponentHelper( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.needsHelper();

    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getHelperClassName() );

    markTypeAsGenerated( builder );

    // Private constructor to block instantiation
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    for ( final CallbackDescriptor callback : descriptor.getCallbacks() )
    {
      builder.addMethod( buildStaticCallbackMethod( descriptor, callback ).build() );
    }

    return builder.build();
  }

  @Nonnull
  static TypeSpec buildEnhancedComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getEnhancedClassName() );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    builder.superclass( descriptor.getComponentType() );

    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
          addMember( "name", "$S", descriptor.getName() ).
          addMember( "deferSchedule", "true" );
      if ( descriptor.needsInjection() )
      {
        annotation.addMember( "inject", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
      }
      builder.addAnnotation( annotation.build() );
      builder.addModifiers( Modifier.ABSTRACT );
    }

    markTypeAsGenerated( builder );

    final FieldSpec.Builder field =
      FieldSpec.builder( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    if ( descriptor.needsInjection() )
    {
      builder.addField( buildProviderField( descriptor ).build() );
    }

    for ( final CallbackDescriptor callback : descriptor.getCallbacks() )
    {
      builder.addField( buildCallbackField( callback ).build() );
    }

    if ( descriptor.needsInjection() )
    {
      builder.addMethod( buildSetProviderMethod( descriptor ).build() );
      builder.addMethod( buildGetProviderMethod( descriptor ).build() );
    }

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      builder.addMethod( buildPropMethod( descriptor, prop ).build() );
      if ( descriptor.isArezComponent() )
      {
        builder.addMethod( buildPropObservableRefMethod( prop ).build() );
      }
    }

    for ( final StateValueDescriptor stateValue : descriptor.getStateValues() )
    {
      builder.addMethod( buildStateGetterMethod( descriptor, stateValue ).build() );
      builder.addMethod( buildStateSetterMethod( descriptor, stateValue ).build() );
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

    for ( final CallbackDescriptor callback : descriptor.getCallbacks() )
    {
      builder.addMethod( buildCallbackBuilderMethod( descriptor, callback ).build() );
    }
    if ( descriptor.isArezComponent() )
    {
      for ( final CallbackDescriptor callback : descriptor.getCallbacks() )
      {
        if ( callback.shouldInitCallbackContext() )
        {
          builder.addMethod( buildCallbackActionMethod( callback ).build() );
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

  private static void markTypeAsGenerated( final TypeSpec.Builder builder )
  {
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );
  }

  private static MethodSpec.Builder buildPropMethod( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final PropDescriptor prop )
  {
    final ExecutableElement methodElement = prop.getMethod();
    final ExecutableType methodType = prop.getMethodType();
    final TypeMirror returnType = methodType.getReturnType();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( methodElement.getSimpleName().toString() ).
        returns( TypeName.get( returnType ) );
    ProcessorUtil.copyTypeParameters( methodType, method );
    ProcessorUtil.copyAccessModifiers( methodElement, method );
    ProcessorUtil.copyDocumentedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    final String name = prop.getName();
    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", name ).
          addMember( "expectSetter", "false" );
      method.addAnnotation( annotation.build() );
    }
    final Element propType = prop.getPropType();
    if ( null != propType && descriptor.isArezComponent() && ElementKind.CLASS == propType.getKind() )
    {
      if ( null != ProcessorUtil.findAnnotationByType( propType, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) )
      {
        method.addAnnotation( AnnotationSpec.builder( AREZ_DEPENDENCY_CLASSNAME ).build() );
      }
    }
    final String convertMethodName = getConverter( returnType, methodElement, "Prop" );
    final String key = "child".equals( name ) ? "children" : name;
    method.addStatement( "return props().getAny( $S ).$N()", key, convertMethodName );
    return method;
  }

  @Nonnull
  private static String getConverter( @Nonnull final TypeMirror type,
                                      @Nonnull final Element element,
                                      @Nonnull final String key )
  {
    switch ( type.getKind() )
    {
      case BOOLEAN:
        return "asBoolean";
      case BYTE:
        return "asByte";
      case CHAR:
        return "asChar";
      case DOUBLE:
        return "asDouble";
      case FLOAT:
        return "asFloat";
      case INT:
        return "asInt";
      case LONG:
        return "asLong";
      case SHORT:
        return "asShort";
      case TYPEVAR:
        return "cast";
      case DECLARED:
        if ( type.toString().equals( "java.lang.String" ) )
        {
          return "asString";
        }
        else
        {
          return "cast";
        }
      case ARRAY:
        return "cast";
      default:
        throw new ReactProcessorException( "Return type of @" + key + " method is not yet " +
                                           "handled. Type: " + type.getKind(), element );
    }
  }

  private static MethodSpec.Builder buildStateSetterMethod( @Nonnull final ComponentDescriptor descriptor,
                                                            @Nonnull final StateValueDescriptor stateValue )
  {
    final ExecutableElement methodElement = stateValue.getSetter();
    final MethodSpec.Builder method = MethodSpec.methodBuilder( methodElement.getSimpleName().toString() );
    ProcessorUtil.copyTypeParameters( stateValue.getSetterType(), method );
    ProcessorUtil.copyAccessModifiers( methodElement, method );
    ProcessorUtil.copyDocumentedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    final String name = stateValue.getName();
    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", name );
      method.addAnnotation( annotation.build() );
    }

    final TypeMirror parameterType = stateValue.getSetterType().getParameterTypes().get( 0 );
    final VariableElement element = stateValue.getSetter().getParameters().get( 0 );
    final String paramName = element.getSimpleName().toString();
    final TypeName type = TypeName.get( parameterType );
    final ParameterSpec.Builder param =
      ParameterSpec.builder( type, paramName, Modifier.FINAL );
    ProcessorUtil.copyDocumentedAnnotations( element, param );
    method.addParameter( param.build() );

    method.addStatement(
      "scheduleStateUpdate( ( ( previousState, currentProps ) -> $T.of( $S, $N ) ) )",
      JS_PROPERTY_MAP_CLASSNAME,
      name,
      paramName );
    return method;
  }

  private static MethodSpec.Builder buildStateGetterMethod( @Nonnull final ComponentDescriptor descriptor,
                                                            @Nonnull final StateValueDescriptor stateValue )
  {
    final TypeMirror returnType = stateValue.getGetterType().getReturnType();
    final ExecutableElement methodElement = stateValue.getGetter();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( methodElement.getSimpleName().toString() ).
        returns( TypeName.get( returnType ) );
    ProcessorUtil.copyTypeParameters( stateValue.getGetterType(), method );
    ProcessorUtil.copyAccessModifiers( methodElement, method );
    ProcessorUtil.copyDocumentedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    final String name = stateValue.getName();
    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", name );
      method.addAnnotation( annotation.build() );
    }
    final String convertMethodName = getConverter( returnType, methodElement, "State" );
    method.addStatement( "return state().getAny( $S ).$N()", name, convertMethodName );
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
      addModifiers( Modifier.PROTECTED ).
      addAnnotation( Override.class ).
      addAnnotation( ACTION_CLASSNAME ).
      addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL ).
        addAnnotation( NULLABLE_CLASSNAME ).build() );
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      final String code =
        "if ( !$T.isTripleEqual( props().get( $S ), null == nextProps ? null : nextProps.get( $S ) ) )";
      block.beginControlFlow( code, JS_CLASSNAME, prop.getName(), prop.getName() );
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
  private static FieldSpec.Builder buildCallbackField( @Nonnull final CallbackDescriptor callback )
  {
    final TypeName handlerType = TypeName.get( callback.getCallbackType().asType() );
    final String handlerName = "_" + callback.getMethod().getSimpleName().toString();
    return FieldSpec.builder( handlerType, handlerName, Modifier.FINAL ).
      addAnnotation( NONNULL_CLASSNAME ).
      initializer( "create$N()", handlerName );
  }

  @Nonnull
  private static MethodSpec.Builder buildStaticCallbackMethod( @Nonnull final ComponentDescriptor descriptor,
                                                               @Nonnull final CallbackDescriptor callback )
  {
    final TypeName handlerType = TypeName.get( callback.getCallbackType().asType() );
    final String handlerName = "_" + callback.getMethod().getSimpleName();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( handlerName ).
        addAnnotation( NONNULL_CLASSNAME ).
        returns( handlerType );

    method.addModifiers( Modifier.STATIC );

    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( TypeName.get( descriptor.getElement().asType() ), "component", Modifier.FINAL ).
        addAnnotation( NONNULL_CLASSNAME );
    method.addParameter( parameter.build() );

    if ( !descriptor.getElement().getTypeParameters().isEmpty() )
    {
      method.addAnnotation( AnnotationSpec.builder( SuppressWarnings.class )
                              .addMember( "value", "$S", "unused" )
                              .build() );
      ProcessorUtil.copyTypeParameters( descriptor.getElement(), method );
    }

    method.addStatement( "return (($T) component).$N", descriptor.getEnhancedClassName(), handlerName );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildCallbackBuilderMethod( @Nonnull final ComponentDescriptor descriptor,
                                                                @Nonnull final CallbackDescriptor callback )
  {
    final TypeName handlerType = TypeName.get( callback.getCallbackType().asType() );
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "create_" + callback.getMethod().getSimpleName() ).
        addModifiers( Modifier.PRIVATE ).
        addAnnotation( NONNULL_CLASSNAME ).
        returns( handlerType );

    final ExecutableElement target = callback.getCallbackMethod();
    final int targetParameterCount = target.getParameters().size();
    String args =
      0 == targetParameterCount ?
      "()" :
      IntStream.range( 0, targetParameterCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );
    if ( 1 < targetParameterCount )
    {
      args = "(" + args + ")";
    }

    final int paramCount = callback.getMethod().getParameters().size();
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );

    method.addStatement( "final $T handler = " + args + " -> this.$N(" + params + ")",
                         handlerType,
                         callback.getMethod().getSimpleName() );

    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if( $T.enableComponentNames() )", REACT_CONFIG_CLASSNAME );
    final String code =
      "$T.defineProperty( $T.cast( handler ), \"name\", $T.cast( $T.of( \"value\", $S ) ) )";
    block.addStatement( code,
                        JS_OBJECT_CLASSNAME,
                        JS_CLASSNAME,
                        JS_CLASSNAME,
                        JS_PROPERTY_MAP_CLASSNAME,
                        descriptor.getName() + "." + callback.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    method.addStatement( "return handler" );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildCallbackActionMethod( @Nonnull final CallbackDescriptor callback )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( callback.getMethod().getSimpleName().toString() ).
        returns( TypeName.get( callback.getMethodType().getReturnType() ) );
    ProcessorUtil.copyTypeParameters( callback.getMethodType(), method );
    ProcessorUtil.copyAccessModifiers( callback.getMethod(), method );
    ProcessorUtil.copyDocumentedAnnotations( callback.getMethod(), method );

    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( ACTION_CLASSNAME ).
        addMember( "reportParameters", "false" );
    method.addAnnotation( annotation.build() );

    final int paramCount = callback.getMethod().getParameters().size();
    for ( int i = 0; i < paramCount; i++ )
    {
      final TypeMirror paramType = callback.getMethodType().getParameterTypes().get( i );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( paramType ), "arg" + i, Modifier.FINAL );
      ProcessorUtil.copyDocumentedAnnotations( callback.getMethod().getParameters().get( i ), parameter );
      method.addParameter( parameter.build() );
    }
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount ).mapToObj( i -> "arg" + i ).collect( Collectors.joining( "," ) );

    final boolean isVoid = callback.getMethodType().getReturnType().getKind() == TypeKind.VOID;

    method.addStatement( ( isVoid ? "" : "return " ) + "super.$N(" + params + ")",
                         callback.getMethod().getSimpleName() );
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
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( NONNULL_CLASSNAME ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME );

    method.addStatement( "final $T componentConstructor = $T::new",
                         COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                         ClassName.bestGuess( "NativeReactComponent" ) );
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", REACT_CONFIG_CLASSNAME );
    codeBlock.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"displayName\", $S )",
                            JS_CLASSNAME,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );

    final List<PropDescriptor> propsWithDefaults = descriptor.getProps()
      .stream()
      .filter( p -> p.hasDefaultField() || p.hasDefaultMethod() )
      .collect( Collectors.toList() );
    if ( !propsWithDefaults.isEmpty() )
    {
      method.addStatement( "final $T<$T> defaultProps = $T.of()",
                           JS_PROPERTY_MAP_CLASSNAME,
                           Object.class,
                           JS_PROPERTY_MAP_CLASSNAME );
      for ( final PropDescriptor prop : propsWithDefaults )
      {

        method.addStatement( "defaultProps.set( $S, $T.$N" + ( prop.hasDefaultField() ? "" : "()" ) + " )",
                             prop.getName(),
                             descriptor.getClassName(),
                             prop.hasDefaultField() ?
                             prop.getDefaultField().getSimpleName() :
                             prop.getDefaultMethod().getSimpleName()
        );
      }

      method.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"defaultProps\", defaultProps )",
                           JS_CLASSNAME );
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
      ParameterizedTypeName.get( REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME, descriptor.getComponentType() );

    builder.superclass( superType );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    if ( !descriptor.getLifecycleMethods().isEmpty() )
    {
      builder.addSuperinterface( ClassName.bestGuess( "Lifecycle" ) );
    }

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME );
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
    markTypeAsGenerated( builder );

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

  @Nonnull
  private static BuilderDescriptor buildBuilderDescriptor( @Nonnull final ComponentDescriptor descriptor )
  {
    final BuilderDescriptor builder = new BuilderDescriptor();

    Step optionalPropStep = null;
    final List<PropDescriptor> props = descriptor.getProps();

    final int propsSize = props.size();

    // Key step
    builder.addStep().
      addStep( "key",
               "key",
               TypeName.get( String.class ),
               null,
               null,
               0 == propsSize ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );

    boolean hasRequiredAfterOptional = false;
    for ( int i = 0; i < propsSize; i++ )
    {
      final PropDescriptor prop = props.get( i );
      final boolean isLast = i == propsSize - 1;
      if ( prop.isOptional() )
      {
        if ( null == optionalPropStep )
        {
          optionalPropStep = builder.addStep();
        }
        if ( prop.getName().equals( "children" ) )
        {
          addChildPropStepMethod( optionalPropStep, StepMethodType.STAY );
          addChildrenStreamPropStepMethod( optionalPropStep );
        }
        addPropStepMethod( optionalPropStep, prop, StepMethodType.STAY );
      }
      else
      {
        if ( null != optionalPropStep )
        {
          // Need this when we have children magic prop that is required that follows the optional props.
          addPropStepMethod( optionalPropStep, prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
          // This is when children are built up using child steps
          if ( prop.getName().equals( "children" ) )
          {
            addChildPropStepMethod( optionalPropStep, StepMethodType.ADVANCE );
            addChildrenStreamPropStepMethod( optionalPropStep );
          }
          hasRequiredAfterOptional = true;
        }
        // Single method step
        final Step step = builder.addStep();
        addPropStepMethod( step, prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
        if ( prop.getName().equals( "children" ) )
        {
          addChildPropStepMethod( step, StepMethodType.STAY );
          addChildrenStreamPropStepMethod( step );
          addBuildStep( step );
        }
      }
    }
    if ( null != optionalPropStep && !hasRequiredAfterOptional )
    {
      addBuildStep( optionalPropStep );
    }
    if ( props.isEmpty() )
    {
      addBuildStep( builder.addStep() );
    }

    return builder;
  }

  /**
   * Setup the "build" intrinsic step.
   */
  private static void addBuildStep( @Nonnull final Step step )
  {
    step.addStep( "build", "build", REACT_NODE_CLASSNAME, null, null, StepMethodType.TERMINATE );
  }

  /**
   * The assumption is that a chain of "child(...)" methods will write to the array that will eventually generate children array.
   */
  private static void addChildPropStepMethod( @Nonnull final Step step, @Nonnull final StepMethodType stepMethodType )
  {
    step.addStep( "child",
                  "*children_child*",
                  REACT_NODE_CLASSNAME,
                  null,
                  null,
                  stepMethodType );
  }

  /**
   * A helper intrinsic that converts children streams.
   */
  private static void addChildrenStreamPropStepMethod( @Nonnull final Step step )
  {
    final ParameterizedTypeName typeName =
      ParameterizedTypeName.get( ClassName.get( Stream.class ), WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );
    step.addStep( "children",
                  "*children_stream*",
                  typeName,
                  null,
                  null,
                  StepMethodType.TERMINATE );
  }

  private static void addPropStepMethod( @Nonnull final Step step,
                                         @Nonnull final PropDescriptor prop,
                                         @Nonnull final StepMethodType stepMethodType )
  {
    step.addStep( prop.getName(),
                  prop.getName(),
                  TypeName.get( prop.getMethodType().getReturnType() ),
                  prop.getMethod(),
                  prop.getMethodType(),
                  stepMethodType );
  }
}
