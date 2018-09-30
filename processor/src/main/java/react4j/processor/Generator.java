package react4j.processor;

import com.google.auto.common.GeneratedAnnotationSpecs;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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
  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "ObservableValue" );
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName COMPUTED_CLASSNAME = ClassName.get( "arez.annotations", "Computed" );
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
  private static final ClassName MEMOIZE_CLASSNAME = ClassName.get( "arez.annotations", "Memoize" );
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableValueRef" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );
  private static final ClassName JS_OBJECT_CLASSNAME = ClassName.get( "elemental2.core", "JsObject" );
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );
  private static final ClassName JS_TYPE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsType" );
  private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsConstructor" );
  private static final ClassName JS_PACKAGE_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsPackage" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );
  private static final ClassName COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j", "ComponentConstructorFunction" );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME =
    ClassName.get( "react4j", "NativeAdapterComponent" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
  private static final ClassName REACT_CONFIG_CLASSNAME = ClassName.get( "react4j", "ReactConfig" );
  private static final ClassName COMPONENT_CLASSNAME = ClassName.get( "react4j", "Component" );
  private static final ClassName KEY_CLASSNAME = ClassName.get( "react4j", "Key" );
  private static final ClassName REACT_AREZ_CONFIG_CLASSNAME = ClassName.get( "react4j.arez", "ReactArezConfig" );

  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildComponentBuilder( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getBuilderClassName() );
    addGeneratedAnnotation( descriptor, builder );

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

    if ( stepMethod.isBuildIntrinsic() )
    {
      final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
      method.addStatement( "return new $T" + infix + "().build()", ClassName.bestGuess( "Builder" ) );
    }
    else
    {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
      final ExecutableElement propMethod = stepMethod.getPropMethod();
      if ( null != propMethod )
      {
        ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
      }
      else if ( stepMethod.isChildrenStreamIntrinsic() )
      {
        parameter.addAnnotation( NONNULL_CLASSNAME );
      }
      else if ( stepMethod.isKeyIntrinsic() && !stepMethod.getKey().equals( "*key_int*" ) )
      {
        parameter.addAnnotation( NONNULL_CLASSNAME );
      }
      else if ( stepMethod.isChildOfChildrenIntrinsic() )
      {
        parameter.addAnnotation( NULLABLE_CLASSNAME );
      }
      method.addParameter( parameter.build() );
      final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
      method.addStatement( "return new $T" + infix + "().$N( $N )",
                           ClassName.bestGuess( "Builder" ),
                           stepMethod.getName(),
                           stepMethod.getName() );
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );
    return method.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildStepInterfaceMethod( @Nonnull final ComponentDescriptor descriptor,
                                                              @Nonnull final String name,
                                                              @Nonnull final Step step,
                                                              @Nonnull final StepMethodType stepMethodType,
                                                              @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT );
    method.addAnnotation( NONNULL_CLASSNAME );
    action.accept( method );
    configureStepMethodReturns( descriptor, method, step, stepMethodType );
    return method;
  }

  private static void configureStepMethodReturns( @Nonnull final ComponentDescriptor descriptor,
                                                  @Nonnull final MethodSpec.Builder method,
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
      final ClassName className = ClassName.bestGuess( "Builder" + returnIndex );
      final List<TypeVariableName> variableNames =
        ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() );
      if ( variableNames.isEmpty() )
      {
        method.returns( className );
      }
      else
      {
        method.returns( ParameterizedTypeName.get( className, variableNames.toArray( new TypeName[ 0 ] ) ) );
      }
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
        builder.addMethod( buildStepInterfaceMethod( descriptor, "build", step, stepMethodType, m -> {
        } ).build() );
      }
      else
      {
        builder.addMethod( buildStepInterfaceMethod( descriptor, stepMethod.getName(), step, stepMethodType, m -> {
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
            ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
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
  private static MethodSpec buildBuilderStepImpl( @Nonnull final ComponentDescriptor descriptor,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethod stepMethod )
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
      ProcessorUtil.copyWhitelistedAnnotations( propMethod, parameter );
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

    boolean returnHandled = false;

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
    else if ( stepMethod.getKey().equals( "*key_int*" ) || stepMethod.getKey().equals( "*key_string*" ) )
    {
      returnHandled = true;
      method.addStatement( "return key( $T.of( $N ) )", KEY_CLASSNAME, stepMethod.getName() );
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

    if ( !returnHandled )
    {
      if ( StepMethodType.TERMINATE == stepMethod.getStepMethodType() )
      {
        method.addStatement( "return build()" );
      }
      else
      {
        method.addStatement( "return this" );
      }
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );

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
            builder.addMethod( buildBuilderStepImpl( descriptor, step, stepMethod ) );
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

    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

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
          addMember( "name", "$S", descriptor.getName() );
      if ( descriptor.shouldRunArezScheduler() )
      {
        annotation.addMember( "deferSchedule", "true" );
      }
      if ( descriptor.needsInjection() )
      {
        annotation.addMember( "inject", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
      }
      builder.addAnnotation( annotation.build() );
      builder.addModifiers( Modifier.ABSTRACT );
    }

    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    final FieldSpec.Builder field =
      FieldSpec.builder( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    builder.addType( buildPropsType( descriptor ) );

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

    if ( descriptor.isArezComponent() )
    {
      final List<PropDescriptor> props =
        descriptor.getProps().stream().filter( PropDescriptor::isDisposable ).collect( Collectors.toList() );
      if ( !props.isEmpty() )
      {
        builder.addMethod( buildAnyPropsDisposedMethod( props ).build() );
      }
    }

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      builder.addMethod( buildPropMethod( descriptor, prop ).build() );
      if ( descriptor.isArezComponent() )
      {
        builder.addMethod( buildPropObservableValueRefMethod( prop ).build() );
      }
    }

    for ( final StateValueDescriptor stateValue : descriptor.getStateValues() )
    {
      builder.addMethod( buildStateGetterMethod( descriptor, stateValue ).build() );
      builder.addMethod( buildStateSetterMethod( descriptor, stateValue ).build() );
    }

    if ( descriptor.isArezComponent() )
    {
      final MethodSpec.Builder method = buildShouldComponentUpdateMethod( descriptor );
      if ( null != method )
      {
        builder.addMethod( method.build() );
      }
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
    if ( descriptor.isArezComponent() )
    {
      for ( final MethodDescriptor method : descriptor.getComputedMethods() )
      {
        builder.addMethod( buildComputedWrapperMethod( method ).build() );
      }
      for ( final MethodDescriptor method : descriptor.getMemoizeMethods() )
      {
        builder.addMethod( buildMemoizeWrapperMethod( method ).build() );
      }
    }

    if ( !descriptor.getLifecycleMethods().isEmpty() )
    {
      if ( descriptor.shouldGenerateLiteLifecycle() )
      {
        builder.addType( buildNativeLiteLifecycleInterface( descriptor ) );
      }
      builder.addType( buildNativeLifecycleInterface( descriptor ) );
    }
    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      builder.addType( buildNativeComponent( descriptor, true ) );
    }
    builder.addType( buildNativeComponent( descriptor, false ) );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildComputedWrapperMethod( @Nonnull final MethodDescriptor descriptor )
  {
    return generateOverrideMethod( descriptor ).addAnnotation( buildComputedAnnotation( descriptor ).build() );
  }

  @Nonnull
  private static MethodSpec.Builder buildMemoizeWrapperMethod( @Nonnull final MethodDescriptor descriptor )
  {
    return generateOverrideMethod( descriptor ).addAnnotation( buildMemoizeAnnotation( descriptor ).build() );
  }

  @Nonnull
  private static MethodSpec.Builder generateOverrideMethod( final @Nonnull MethodDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( descriptor.getMethod().getSimpleName().toString() ).
        addAnnotation( Override.class ).
        returns( TypeName.get( descriptor.getMethodType().getReturnType() ) );
    ProcessorUtil.copyTypeParameters( descriptor.getMethodType(), method );
    ProcessorUtil.copyAccessModifiers( descriptor.getMethod(), method );
    ProcessorUtil.copyWhitelistedAnnotations( descriptor.getMethod(), method );

    final int paramCount = descriptor.getMethod().getParameters().size();
    for ( int i = 0; i < paramCount; i++ )
    {
      final TypeMirror paramType = descriptor.getMethodType().getParameterTypes().get( i );
      final VariableElement param = descriptor.getMethod().getParameters().get( i );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( paramType ), param.getSimpleName().toString(), Modifier.FINAL );
      ProcessorUtil.copyWhitelistedAnnotations( param, parameter );
      method.addParameter( parameter.build() );
    }
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount )
        .mapToObj( i -> descriptor.getMethod().getParameters().get( i ).getSimpleName().toString() )
        .collect( Collectors.joining( "," ) );

    final boolean isVoid = descriptor.getMethodType().getReturnType().getKind() == TypeKind.VOID;

    method.addStatement( ( isVoid ? "" : "return " ) + "super.$N(" + params + ")",
                         descriptor.getMethod().getSimpleName() );
    return method;
  }

  @Nonnull
  private static AnnotationSpec.Builder buildComputedAnnotation( @Nonnull final MethodDescriptor descriptor )
  {
    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( COMPUTED_CLASSNAME ).
        addMember( "priority", "$T.LOWEST", PRIORITY_CLASSNAME );
    final AnnotationValue nameValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                                 "name" );
    if ( null != nameValue )
    {
      annotation.addMember( "name", "$S", nameValue.getValue().toString() );
    }
    final AnnotationValue keepAliveValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                                 "keepAlive" );
    if ( null != keepAliveValue )
    {
      annotation.addMember( "keepAlive", "$N", keepAliveValue.getValue().toString() );
    }
    final AnnotationValue depTypeValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                                 "depType" );
    if ( null != depTypeValue )
    {
      annotation.addMember( "depType", "$T.$N", DEP_TYPE_CLASSNAME, depTypeValue.getValue().toString() );
    }

    final AnnotationValue observeLowerPriorityDependenciesValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                                 "observeLowerPriorityDependencies" );
    if ( null != observeLowerPriorityDependenciesValue )
    {
      annotation.addMember( "observeLowerPriorityDependencies",
                            "$N",
                            observeLowerPriorityDependenciesValue.getValue().toString() );
    }
    return annotation;
  }

  @Nonnull
  private static AnnotationSpec.Builder buildMemoizeAnnotation( @Nonnull final MethodDescriptor descriptor )
  {
    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( MEMOIZE_CLASSNAME ).
        addMember( "priority", "$T.LOWEST", PRIORITY_CLASSNAME );
    final AnnotationValue nameValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "name" );
    if ( null != nameValue )
    {
      annotation.addMember( "name", "$S", nameValue.getValue().toString() );
    }
    final AnnotationValue observeLowerPriorityDependenciesValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "observeLowerPriorityDependencies" );
    if ( null != observeLowerPriorityDependenciesValue )
    {
      annotation.addMember( "observeLowerPriorityDependencies",
                            "$N",
                            observeLowerPriorityDependenciesValue.getValue().toString() );
    }
    return annotation;
  }

  @Nonnull
  private static MethodSpec.Builder buildAnyPropsDisposedMethod( @Nonnull final List<PropDescriptor> props )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "anyPropsDisposed" )
        .addModifiers( Modifier.PROTECTED, Modifier.FINAL )
        .addAnnotation( Override.class )
        .returns( TypeName.BOOLEAN );

    for ( final PropDescriptor prop : props )
    {
      final String varName = "$$react4jv$$_" + prop.getMethod().getSimpleName();
      method.addStatement( "final $T $N = $N()",
                           prop.getMethodType().getReturnType(),
                           varName,
                           prop.getMethod().getSimpleName().toString() );
      final CodeBlock.Builder block = CodeBlock.builder();
      if ( prop.isOptional() )
      {
        block.beginControlFlow( "if ( null != $N && $T.isDisposed( $N ) )", varName, DISPOSABLE_CLASSNAME, varName );
      }
      else
      {
        block.beginControlFlow( "if ( $T.isDisposed( $N ) )", DISPOSABLE_CLASSNAME, varName );
      }
      block.addStatement( "return true" );
      block.endControlFlow();
      method.addCode( block.build() );

    }
    method.addStatement( "return false" );

    return method;
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
    ProcessorUtil.copyWhitelistedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    final String name = prop.getName();
    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", name ).
          addMember( "expectSetter", "false" ).
          addMember( "readOutsideTransaction", "true" );
      method.addAnnotation( annotation.build() );
    }
    final String key = "child".equals( name ) ? "children" : name;
    final TypeName propsType;
    List<? extends TypeParameterElement> typeParameters = descriptor.getElement().getTypeParameters();
    if ( typeParameters.isEmpty() )
    {
      propsType = ClassName.bestGuess( "Props" );
    }
    else
    {
      final TypeName[] bounds =
        typeParameters
          .stream()
          .map( Element::asType )
          .map( TypeName::get )
          .toArray( TypeName[]::new );
      propsType = ParameterizedTypeName.get( ClassName.bestGuess( "Props" ), bounds );
    }
    if ( prop.isSpecialChildrenProp() )
    {
      method.addStatement( "return $T.uncheckedCast( $T.<$T>uncheckedCast( props() ).$N )",
                           JS_CLASSNAME,
                           JS_CLASSNAME,
                           propsType,
                           key );
    }
    else
    {
      method.addStatement( "return $T.<$T>uncheckedCast( props() ).$N", JS_CLASSNAME, propsType, key );
    }
    return method;
  }

  @Nonnull
  private static String getConverter( @Nonnull final TypeMirror type, @Nonnull final Element element )
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
        throw new ReactProcessorException( "Return type of @State method is not yet " +
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
    ProcessorUtil.copyWhitelistedAnnotations( methodElement, method );

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
    ProcessorUtil.copyWhitelistedAnnotations( element, param );
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
    ProcessorUtil.copyWhitelistedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    final String name = stateValue.getName();
    if ( descriptor.isArezComponent() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", name );
      method.addAnnotation( annotation.build() );
    }
    final String convertMethodName = getConverter( returnType, methodElement );
    method.addStatement( "return state().getAny( $S ).$N()", name, convertMethodName );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPropObservableValueRefMethod( @Nonnull final PropDescriptor prop )
  {
    return MethodSpec.methodBuilder( toObservableValueRefMethodName( prop ) ).
      addModifiers( Modifier.PROTECTED, Modifier.ABSTRACT ).
      addAnnotation( NONNULL_CLASSNAME ).
      addAnnotation( OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME ).
      returns( OBSERVABLE_CLASSNAME );
  }

  @Nonnull
  private static String toObservableValueRefMethodName( @Nonnull final PropDescriptor prop )
  {
    final String name = prop.getName();
    return "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 ) + "ObservableValue";
  }

  @Nullable
  private static MethodSpec.Builder buildShouldComponentUpdateMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      descriptor.getProps().stream().filter( PropDescriptor::shouldUpdateOnChange ).collect( Collectors.toList() );
    if ( props.isEmpty() )
    {
      return null;
    }
    else
    {
      final MethodSpec.Builder method = MethodSpec.methodBuilder( "shouldComponentUpdate" ).
        addModifiers( Modifier.PROTECTED ).
        addAnnotation( Override.class ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME ).build() )
        .returns( TypeName.BOOLEAN );
      method.addAnnotation( AnnotationSpec.builder( ACTION_CLASSNAME ).addMember( "verifyRequired", "false" ).build() );
      method.addStatement( "boolean modified = false" );
      for ( final PropDescriptor prop : descriptor.getProps() )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        final String code =
          "if ( !$T.isTripleEqual( props().get( $S ), null == nextProps ? null : nextProps.get( $S ) ) )";
        final String key = "child".equals( prop.getName() ) ? "children" : prop.getName();
        block.beginControlFlow( code, JS_CLASSNAME, key, key );
        block.addStatement( "modified = true" );
        block.addStatement( "$N().reportChanged()", toObservableValueRefMethodName( prop ) );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      method.addStatement( "return modified" );
      return method;
    }
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
      IntStream.range( 0, targetParameterCount )
        .mapToObj( i -> target.getParameters().get( i ).getSimpleName().toString() )
        .collect( Collectors.joining( "," ) );
    if ( 1 < targetParameterCount )
    {
      args = "(" + args + ")";
    }

    final int paramCount = callback.getMethod().getParameters().size();
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount )
        .mapToObj( i -> target.getParameters().get( i ).getSimpleName().toString() )
        .collect( Collectors.joining( "," ) );

    if ( callback.isJsFunction() )
    {
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
    }
    else
    {
      method.addStatement( "return " + args + " -> this.$N(" + params + ")", callback.getMethod().getSimpleName() );
    }
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
    ProcessorUtil.copyWhitelistedAnnotations( callback.getMethod(), method );

    final AnnotationSpec.Builder annotation =
      AnnotationSpec.builder( ACTION_CLASSNAME ).
        addMember( "reportParameters", "false" );
    method.addAnnotation( annotation.build() );

    final int paramCount = callback.getMethod().getParameters().size();
    for ( int i = 0; i < paramCount; i++ )
    {
      final TypeMirror paramType = callback.getMethodType().getParameterTypes().get( i );
      final VariableElement param = callback.getMethod().getParameters().get( i );
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( TypeName.get( paramType ), param.getSimpleName().toString(), Modifier.FINAL );
      ProcessorUtil.copyWhitelistedAnnotations( param, parameter );
      method.addParameter( parameter.build() );
    }
    final String params =
      0 == paramCount ?
      "" :
      IntStream.range( 0, paramCount )
        .mapToObj( i -> callback.getMethod().getParameters().get( i ).getSimpleName().toString() )
        .collect( Collectors.joining( "," ) );

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
    final MethodSpec.Builder method = MethodSpec.methodBuilder( "getProvider" ).
      addModifiers( Modifier.PRIVATE, Modifier.STATIC ).
      returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, TypeName.get( descriptor.getDeclaredType() ) ) );
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CONFIG_CLASSNAME );
    block.addStatement(
      "$T.invariant( () -> null != c_provider, () -> \"Attempted to create an instance of the React4j " +
      "component named '$N' before the dependency injection provider has been initialized. Please see " +
      "the documentation at https://react4j.github.io/dependency_injection for directions how to " +
      "configure dependency injection.\" )",
      GUARDS_CLASSNAME,
      descriptor.getName() );
    block.endControlFlow();
    method.addCode( block.build() );
    return method.addStatement( "return c_provider" );
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( NONNULL_CLASSNAME ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME );

    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      method.addStatement( "final $T componentConstructor = $T.shouldStoreArezDataAsState() ? $T::new : $T::new",
                           COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           REACT_AREZ_CONFIG_CLASSNAME,
                           ClassName.bestGuess( "NativeReactComponent" ),
                           ClassName.bestGuess( "LiteNativeReactComponent" ) );
    }
    else
    {
      method.addStatement( "final $T componentConstructor = $T::new",
                           COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           ClassName.bestGuess( "NativeReactComponent" ) );
    }
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
  private static TypeSpec buildPropsType( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Props" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), builder );

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
      addMember( "isNative", "true" ).
      addMember( "namespace", "$T.GLOBAL", JS_PACKAGE_CLASSNAME ).
      addMember( "name", "$S", "Object" ).
      build() );

    // Key can be a string or a number
    builder.addField( FieldSpec.builder( TypeName.OBJECT, "key" ).addAnnotation( NULLABLE_CLASSNAME ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final boolean isChildren = prop.isSpecialChildrenProp();
      final TypeName type = isChildren ? TypeName.OBJECT : TypeName.get( prop.getMethod().getReturnType() );
      final String name = isChildren ? "children" : prop.getName();
      final FieldSpec.Builder field = FieldSpec.builder( type, name );
      if ( null != ProcessorUtil.findAnnotationByType( prop.getMethod(), Constants.NONNULL_ANNOTATION_CLASSNAME ) )
      {
        field.addAnnotation( NONNULL_CLASSNAME );
      }
      if ( null != ProcessorUtil.findAnnotationByType( prop.getMethod(), Constants.NULLABLE_ANNOTATION_CLASSNAME ) )
      {
        field.addAnnotation( NULLABLE_CLASSNAME );
      }
      builder.addField( field.build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeComponent( @Nonnull final ComponentDescriptor descriptor, final boolean lite )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( ( lite ? "Lite" : "" ) + "NativeReactComponent" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );
    builder.addModifiers( Modifier.PRIVATE );

    final TypeName superType =
      ParameterizedTypeName.get( REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME, descriptor.getComponentType() );

    builder.superclass( superType );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    final List<MethodDescriptor> lifecycleMethods =
      lite ? descriptor.getLiteLifecycleMethods() : descriptor.getLifecycleMethods();
    if ( !lifecycleMethods.isEmpty() )
    {
      builder.addSuperinterface( ClassName.bestGuess( ( lite ? "Lite" : "" ) + "Lifecycle" ) );
    }

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addParameter( props.build() );
      method.addAnnotation( JS_CONSTRUCTOR_CLASSNAME );
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
      for ( final MethodDescriptor lifecycleMethod : lifecycleMethods )
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
  private static TypeSpec buildNativeLiteLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "LiteLifecycle" );

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
      addMember( "isNative", "true" ).
      addMember( "namespace", "$T.GLOBAL", JS_PACKAGE_CLASSNAME ).
      addMember( "name", "$S", "?" ).
      build() );

    builder.addModifiers( Modifier.STATIC );

    for ( final MethodDescriptor method : descriptor.getLiteLifecycleMethods() )
    {
      buildLfecycleMethod( builder, method );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeLifecycleInterface( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Lifecycle" );

    builder.addAnnotation( AnnotationSpec.builder( JS_TYPE_CLASSNAME ).
      addMember( "isNative", "true" ).
      addMember( "namespace", "$T.GLOBAL", JS_PACKAGE_CLASSNAME ).
      addMember( "name", "$S", "?" ).
      build() );

    builder.addModifiers( Modifier.STATIC );

    descriptor.getLifecycleMethods().forEach( method -> buildLfecycleMethod( builder, method ) );

    return builder.build();
  }

  private static void buildLfecycleMethod( @Nonnull final TypeSpec.Builder builder,
                                           @Nonnull final MethodDescriptor lifecycleMethod )
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

  @Nonnull
  static TypeSpec buildDaggerFactory( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerFactoryClassName() );
    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

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
    final Step keyStep = builder.addStep();
    final StepMethodType keyStepMethodType = 0 == propsSize ? StepMethodType.TERMINATE : StepMethodType.ADVANCE;
    keyStep.addMethod( "key", "key", KEY_CLASSNAME, null, null, keyStepMethodType );
    keyStep.addMethod( "key", "*key_int*", TypeName.INT, null, null, keyStepMethodType );
    keyStep.addMethod( "key", "*key_string*", TypeName.get( String.class ), null, null, keyStepMethodType );

    final boolean hasSingleOptional = props.stream().filter( PropDescriptor::isOptional ).count() == 1;
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
        addPropStepMethod( optionalPropStep, prop, hasSingleOptional ? StepMethodType.TERMINATE : StepMethodType.STAY );
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
    step.addMethod( "build", "build", REACT_NODE_CLASSNAME, null, null, StepMethodType.TERMINATE );
  }

  /**
   * The assumption is that a chain of "child(...)" methods will write to the array that will eventually generate children array.
   */
  private static void addChildPropStepMethod( @Nonnull final Step step, @Nonnull final StepMethodType stepMethodType )
  {
    step.addMethod( "child",
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
    step.addMethod( "children",
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
    step.addMethod( prop.getName(),
                    prop.getName(),
                    TypeName.get( prop.getMethodType().getReturnType() ),
                    prop.getMethod(),
                    prop.getMethodType(),
                    stepMethodType );
  }

  private static void addOriginatingTypes( @Nonnull final TypeElement element, @Nonnull final TypeSpec.Builder builder )
  {
    builder.addOriginatingElement( element );
    ProcessorUtil.getSuperTypes( element ).forEach( builder::addOriginatingElement );
  }

  private static void addGeneratedAnnotation( @Nonnull final ComponentDescriptor descriptor,
                                              @Nonnull final TypeSpec.Builder builder )
  {
    GeneratedAnnotationSpecs
      .generatedAnnotationSpec( descriptor.getElements(), descriptor.getSourceVersion(), ReactProcessor.class )
      .ifPresent( builder::addAnnotation );
  }
}
