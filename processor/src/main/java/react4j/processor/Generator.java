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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
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
  private static final ClassName AREZ_CLASSNAME = ClassName.get( "arez", "Arez" );
  private static final ClassName OBSERVER_CLASSNAME = ClassName.get( "arez", "Observer" );
  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "ObservableValue" );
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName AREZ_INJECT_MODE_CLASSNAME =
    ClassName.get( "arez.annotations", "InjectMode" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
  private static final ClassName MEMOIZE_CLASSNAME = ClassName.get( "arez.annotations", "Memoize" );
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  private static final ClassName EXECUTOR_CLASSNAME = ClassName.get( "arez.annotations", "Executor" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observe" );
  private static final ClassName OBSERVER_REF_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "ObserverRef" );
  private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableValueRef" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );
  private static final ClassName JS_ERROR_CLASSNAME = ClassName.get( "elemental2.core", "JsError" );
  private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsConstructor" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName REACT_ELEMENT_CLASSNAME = ClassName.get( "react4j", "ReactElement" );
  private static final ClassName REACT_ERROR_INFO_CLASSNAME = ClassName.get( "react4j", "ReactErrorInfo" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
  private static final ClassName COMPONENT_CLASSNAME = ClassName.get( "react4j", "Component" );
  private static final ClassName COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j.internal", "ComponentConstructorFunction" );
  private static final ClassName ON_COMPONENT_DID_MOUNT_CLASSNAME =
    ClassName.get( "react4j.internal", "OnComponentDidMount" );
  private static final ClassName ON_COMPONENT_DID_UPDATE_CLASSNAME =
    ClassName.get( "react4j.internal", "OnComponentDidUpdate" );
  private static final ClassName ON_COMPONENT_WILL_UNMOUNT_CLASSNAME =
    ClassName.get( "react4j.internal", "OnComponentWillUnmount" );
  private static final ClassName ON_GET_SNAPSHOT_BEFORE_UPDATE_CLASSNAME =
    ClassName.get( "react4j.internal", "OnGetSnapshotBeforeUpdate" );
  private static final ClassName ON_COMPONENT_SHOULD_UPDATE_CLASSNAME =
    ClassName.get( "react4j.internal", "OnShouldComponentUpdate" );
  private static final ClassName ON_COMPONENT_DID_CATCH_CLASSNAME =
    ClassName.get( "react4j.internal", "componentDidCatch" );
  private static final ClassName REACT_NATIVE_ADAPTER_COMPONENT_CLASSNAME =
    ClassName.get( "react4j.internal", "NativeAdapterComponent" );
  private static final ClassName REACT_NATIVE_COMPONENT_CLASSNAME =
    ClassName.get( "react4j.internal", "NativeComponent" );
  private static final ClassName SCHEDULER_UTIL_CLASSNAME = ClassName.get( "react4j.internal.arez", "SchedulerUtil" );
  private static final ClassName INTROSPECT_UTIL_CLASSNAME = ClassName.get( "react4j.internal.arez", "IntrospectUtil" );
  private static final String FRAMEWORK_INTERNAL_PREFIX = "$$react4j$$_";
  private static final String SHOULD_COMPONENT_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "shouldComponentUpdate";
  private static final String COMPONENT_PRE_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentPreUpdate";
  private static final String COMPONENT_DID_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidUpdate";
  private static final String COMPONENT_DID_MOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidMount";
  private static final String COMPONENT_WILL_UNMOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentWillUnmount";
  private static final String UNMOUNTED_FIELD = FRAMEWORK_INTERNAL_PREFIX + "unmounted";
  private static final String RENDER_DEPS_CHANGED_FIELD = FRAMEWORK_INTERNAL_PREFIX + "renderDepsChanged";

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
      final ClassName className = ClassName.bestGuess( "Step" + returnIndex );
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
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Step" + stepIndex );
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
    method.addParameter( parameter.build() );

    boolean returnHandled = false;

    if ( stepMethod.isChildrenIntrinsic() )
    {
      method.varargs();
      final PropDescriptor prop = stepMethod.getProp();
      assert null != prop;
      method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $N ) )",
                           descriptor.getEnhancedClassName(),
                           prop.getConstantName(),
                           JS_ARRAY_CLASSNAME,
                           stepMethod.getName() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      method.addStatement( "children( $N.toArray( $T[]::new ) )", stepMethod.getName(), REACT_NODE_CLASSNAME );
    }
    else if ( stepMethod.isChildIntrinsic() )
    {
      assert null != propMethod;
      final PropDescriptor prop = stepMethod.getProp();
      assert null != prop;
      if ( isNonnull( propMethod ) )
      {
        method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $T.requireNonNull( $N ) ) )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             Objects.class,
                             stepMethod.getName() );
      }
      else
      {
        method.addStatement( "_element.props().set( $T.Props.$N, $T.of( $N ) )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             stepMethod.getName() );
      }
    }
    else if ( stepMethod.getKey().equals( "*key_int*" ) )
    {
      returnHandled = true;
      // TODO: Handle this by prop enhancer ?
      method.addStatement( "return key( $T.valueOf( $N ) )", TypeName.get( String.class ), stepMethod.getName() );
    }
    else
    {
      if ( stepMethod.isKeyIntrinsic() )
      {
        method.addStatement( "_element.setKey( $T.requireNonNull( $N ) )",
                             TypeName.get( Objects.class ),
                             stepMethod.getName() );
      }
      else
      {
        if ( ( null != propMethod && isNonnull( propMethod ) ) && !stepMethod.getType().isPrimitive() )
        {
          method.addStatement( "$T.requireNonNull( $N )", Objects.class, stepMethod.getName() );
        }
        final PropDescriptor prop = stepMethod.getProp();
        assert null != prop;
        method.addStatement( "_element.props().set( $T.Props.$N, $N )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             stepMethod.getName() );
      }
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

  private static boolean isNonnull( @Nonnull final ExecutableElement method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.NONNULL_ANNOTATION_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec buildBuildStepImpl()
  {
    return MethodSpec.methodBuilder( "build" )
      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
      .addAnnotation( NONNULL_CLASSNAME )
      .addStatement( "_element.complete()" )
      .addStatement( "return _element" )
      .returns( REACT_NODE_CLASSNAME )
      .build();
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
      builder.addSuperinterface( getParameterizedTypeName( descriptor, ClassName.bestGuess( "Step" + ( i + 1 ) ) ) );
    }

    final List<PropDescriptor> propsWithDefaults = descriptor.getProps()
      .stream()
      .filter( p -> p.hasDefaultField() || p.hasDefaultMethod() )
      .collect( Collectors.toList() );
    if ( !propsWithDefaults.isEmpty() )
    {
      final MethodSpec.Builder method = MethodSpec.constructorBuilder();
      //TODO: Should we enforce the non-presence of _element field so we guarantee the wrapper class is eliminated?
      method.addStatement( "_element = $T.createComponentElement( $T.Factory.TYPE )",
                           REACT_ELEMENT_CLASSNAME,
                           descriptor.getEnhancedClassName() );
      for ( final PropDescriptor prop : propsWithDefaults )
      {

        method.addStatement( "_element.props().set( $T.Props.$N, $T.$N" +
                             ( prop.hasDefaultField() ? "" : "()" ) + " )",
                             descriptor.getEnhancedClassName(),
                             prop.getConstantName(),
                             descriptor.getClassName(),
                             prop.hasDefaultField() ?
                             prop.getDefaultField().getSimpleName() :
                             prop.getDefaultMethod().getSimpleName() );
      }

      builder.addMethod( method.build() );
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
          }
        }
      }
    }

    final FieldSpec.Builder field =
      FieldSpec.builder( REACT_ELEMENT_CLASSNAME, "_element", Modifier.PRIVATE, Modifier.FINAL );
    if ( propsWithDefaults.isEmpty() )
    {
      field.initializer( "$T.createComponentElement( $T.Factory.TYPE )",
                         REACT_ELEMENT_CLASSNAME,
                         descriptor.getEnhancedClassName() );
    }
    builder.addField( field.build() );

    builder.addMethod( buildBuildStepImpl() );

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
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getEnhancedClassName() );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    builder.superclass( descriptor.getComponentType() );

    final AnnotationSpec.Builder arezAnnotation =
      AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
        addMember( "name", "$S", descriptor.getName() ).
        addMember( "disposeTrackable", "$T.DISABLE", AREZ_FEATURE_CLASSNAME );
    if ( !descriptor.isArezComponent() )
    {
      arezAnnotation.addMember( "allowEmpty", "true" );
    }
    if ( descriptor.needsInjection() )
    {
      arezAnnotation.addMember( "inject", "$T.CONSUME", AREZ_INJECT_MODE_CLASSNAME );
      arezAnnotation.addMember( "dagger", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
    }
    builder.addAnnotation( arezAnnotation.build() );
    builder.addModifiers( Modifier.ABSTRACT );

    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    if ( !descriptor.needsInjection() )
    {
      final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
      ctor.addParameter( ParameterSpec.builder( REACT_NATIVE_COMPONENT_CLASSNAME, "nativeComponent", Modifier.FINAL )
                           .addAnnotation( NONNULL_CLASSNAME )
                           .build() );
      ctor.addStatement( "bindComponent( nativeComponent )" );
      builder.addMethod( ctor.build() );
    }
    /*
    if ( descriptor.needsInjection() && !descriptor.isArezComponent() )
    {
      builder.addMethod( MethodSpec.constructorBuilder().addAnnotation( INJECT_CLASSNAME ).build() );
    }
    */

    if ( descriptor.isArezComponent() )
    {
      builder.addField( FieldSpec.builder( TypeName.BOOLEAN, RENDER_DEPS_CHANGED_FIELD, Modifier.PRIVATE ).build() );
      builder.addField( FieldSpec.builder( TypeName.BOOLEAN, UNMOUNTED_FIELD, Modifier.PRIVATE ).build() );
    }

    builder.addType( buildFactory() );
    if ( descriptor.needsInjection() )
    {
      builder.addType( buildInjectSupport( descriptor ) );
    }
    if ( !descriptor.getProps().isEmpty() )
    {
      builder.addType( buildPropsType( descriptor ) );
    }

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      builder.addMethod( buildPropMethod( descriptor, prop ).build() );
      if ( descriptor.isArezComponent() && prop.isObservable() )
      {
        builder.addMethod( buildPropObservableValueRefMethod( prop ).build() );
      }
    }

    if ( descriptor.hasValidatedProps() )
    {
      builder.addMethod( buildPropValidatorMethod( descriptor ).build() );
    }

    if ( descriptor.generateShouldComponentUpdate() )
    {
      builder.addMethod( buildShouldComponentUpdate( descriptor ).build() );
    }
    if ( descriptor.generateComponentDidMount() )
    {
      builder.addMethod( buildComponentDidMount( descriptor ).build() );
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addMethod( buildComponentPreUpdate( descriptor ).build() );
    }
    if ( descriptor.generateComponentDidUpdate() )
    {
      builder.addMethod( buildComponentDidUpdate( descriptor ).build() );
    }
    if ( descriptor.generateComponentWillUnmount() )
    {
      builder.addMethod( buildComponentWillUnmount( descriptor ).build() );
    }

    if ( descriptor.isArezComponent() )
    {
      builder.addMethod( buildOnRenderDepsChange( descriptor ).build() );
      builder.addMethod( buildRender( descriptor ).build() );
      builder.addMethod( buildGetRenderObserver( descriptor ).build() );
      builder.addMethod( buildPopulateDebugData( descriptor ).build() );
    }

    if ( descriptor.isArezComponent() )
    {
      for ( final MethodDescriptor method : descriptor.getMemoizeMethods() )
      {
        builder.addMethod( buildMemoizeWrapperMethod( method ).build() );
      }
    }

    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      builder.addType( buildNativeComponent( descriptor, true ) );
    }
    builder.addType( buildNativeComponent( descriptor, false ) );

    return builder.build();
  }

  private static FieldSpec.Builder buildPropKeyConstantField( @Nonnull final PropDescriptor descriptor,
                                                              final int index )
  {
    final String name = descriptor.getName();

    final FieldSpec.Builder field =
      FieldSpec.builder( TypeName.get( String.class ),
                         descriptor.getConstantName(),
                         Modifier.STATIC,
                         Modifier.FINAL );
    if ( descriptor.isSpecialChildrenProp() )
    {
      return field.initializer( "$S", "children" );
    }
    else
    {
      return field.initializer( "$T.shouldMinimizePropKeys() ? $S : $S",
                                REACT_CLASSNAME,
                                Character.toString( (char) ( 'a' + index ) ),
                                name );
    }
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
    final AnnotationValue keepAliveValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "keepAlive" );
    if ( null != keepAliveValue )
    {
      annotation.addMember( "keepAlive", "$N", keepAliveValue.getValue().toString() );
    }
    final AnnotationValue reportResultValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "reportResult" );
    if ( null != reportResultValue )
    {
      annotation.addMember( "reportResult", "$N", reportResultValue.getValue().toString() );
    }

    final AnnotationValue depTypeValue =
      ProcessorUtil.findDeclaredAnnotationValue( descriptor.getMethod(),
                                                 Constants.MEMOIZE_ANNOTATION_CLASSNAME,
                                                 "depType" );
    if ( null != depTypeValue )
    {
      annotation.addMember( "depType", "$T.$N", DEP_TYPE_CLASSNAME, depTypeValue.getValue().toString() );
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

    if ( descriptor.isArezComponent() && prop.isObservable() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", prop.getName() ).
          addMember( "expectSetter", "false" ).
          addMember( "readOutsideTransaction", "true" );
      method.addAnnotation( annotation.build() );
    }
    final String convertMethodName = getConverter( returnType, methodElement );
    final TypeKind resultKind = methodElement.getReturnType().getKind();
    if ( !resultKind.isPrimitive() && !isNonnull( methodElement ) )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
      block.addStatement( "return null != props().getAny( Props.$N ) ? props().getAny( Props.$N ).$N() : null",
                          prop.getConstantName(),
                          prop.getConstantName(),
                          convertMethodName );
      block.nextControlFlow( "else" );
      block.addStatement( "return $T.uncheckedCast( props().getAny( Props.$N ) )",
                          JS_CLASSNAME,
                          prop.getConstantName() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "return props().getAny( Props.$N ).$N()", prop.getConstantName(), convertMethodName );
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
        throw new ReactProcessorException( "Return type of @Prop method is not yet " +
                                           "handled. Type: " + type.getKind(), element );
    }
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

  private static void buildOnPropChangeInvocations( @Nonnull final CodeBlock.Builder code,
                                                    @Nonnull final List<OnPropChangeDescriptor> onPropChanges )
  {
    // The list of props we need to check for changes
    final List<PropDescriptor> props =
      onPropChanges.stream().flatMap( d -> d.getProps().stream() ).distinct().collect( Collectors.toList() );

    for ( final PropDescriptor prop : props )
    {
      code.addStatement( "final boolean $N = !$T.isTripleEqual( props.get( Props.$N ), prevProps.get( Props.$N ) )",
                         prop.getName(),
                         JS_CLASSNAME,
                         prop.getConstantName(),
                         prop.getConstantName() );
    }
    for ( final OnPropChangeDescriptor onPropChange : onPropChanges )
    {
      final CodeBlock.Builder onChangeBlock = CodeBlock.builder();
      onChangeBlock.beginControlFlow( "if ( " +
                                      onPropChange.getProps()
                                        .stream()
                                        .map( PropDescriptor::getName )
                                        .collect( Collectors.joining( " && " ) ) + " )" );
      final StringBuilder sb = new StringBuilder();
      final ArrayList<Object> params = new ArrayList<>();
      sb.append( "$N( " );
      params.add( onPropChange.getMethod().getSimpleName().toString() );
      boolean requireComma = false;
      for ( final PropDescriptor prop : onPropChange.getProps() )
      {
        if ( requireComma )
        {
          sb.append( ", " );
        }
        requireComma = true;
        final String convertMethodName = getConverter( prop.getMethod().getReturnType(), prop.getMethod() );
        final TypeKind resultKind = prop.getMethod().getReturnType().getKind();
        if ( !resultKind.isPrimitive() && !isNonnull( prop.getMethod() ) )
        {
          sb.append( "$T.uncheckedCast( props.getAny( Props.$N ) )" );
          params.add( JS_CLASSNAME );
          params.add( prop.getConstantName() );
        }
        else
        {
          sb.append( "props.getAny( Props.$N ).$N()" );
          params.add( prop.getConstantName() );
          params.add( convertMethodName );
        }
      }

      sb.append( " )" );
      onChangeBlock.addStatement( sb.toString(), params.toArray() );
      onChangeBlock.endControlFlow();
      code.add( onChangeBlock.build() );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentDidMount( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_DID_MOUNT_METHOD )
        .addModifiers( Modifier.PRIVATE );
    final ExecutableElement postRender = descriptor.getPostRender();
    if ( null != postRender )
    {
      method.addStatement( "$N()", postRender.getSimpleName().toString() );
    }
    final ExecutableElement postMount = descriptor.getPostMount();
    if ( null != postMount )
    {
      method.addStatement( "$N()", postMount.getSimpleName().toString() );
    }
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldStoreDebugDataAsState() )", REACT_CLASSNAME );
    block.addStatement( "storeDebugDataAsState()" );
    block.endControlFlow();
    method.addCode( block.build() );

    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildShouldComponentUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( SHOULD_COMPONENT_UPDATE_METHOD )
        .returns( TypeName.BOOLEAN )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL )
                         .addAnnotation( NULLABLE_CLASSNAME )
                         .build() );

    final List<PropDescriptor> observableProps =
      descriptor.getProps()
        .stream()
        .filter( PropDescriptor::isObservable )
        .collect( Collectors.toList() );

    if ( !observableProps.isEmpty() )
    {
      method.addAnnotation( AnnotationSpec.builder( ACTION_CLASSNAME ).addMember( "verifyRequired", "false" ).build() );
    }
    else
    {
      method.addModifiers( Modifier.PRIVATE );
    }

    method.addStatement( "assert null != nextProps" );

    if ( descriptor.hasValidatedProps() )
    {
      final CodeBlock.Builder validateBlock = CodeBlock.builder();
      validateBlock.beginControlFlow( "if ( $T.shouldValidatePropValues() )", REACT_CLASSNAME );
      validateBlock.addStatement( "validatePropValues( nextProps )" );
      validateBlock.endControlFlow();
      method.addCode( validateBlock.build() );
    }

    final List<PropDescriptor> updateOnChangeProps =
      descriptor
        .getProps()
        .stream()
        .filter( PropDescriptor::shouldUpdateOnChange )
        // Observable properties already checked above
        .filter( p -> !p.isObservable() )
        .collect( Collectors.toList() );

    if ( observableProps.isEmpty() && updateOnChangeProps.isEmpty() )
    {
      if ( descriptor.isArezComponent() )
      {
        method.addStatement( "return $N", RENDER_DEPS_CHANGED_FIELD );
      }
      else
      {
        method.addStatement( "return false" );
      }
    }
    else
    {
      method.addStatement( "final $T props = props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );

      final boolean hasObservablePropsToUpdateOnChange =
        observableProps.stream().anyMatch( PropDescriptor::shouldUpdateOnChange );

      if ( hasObservablePropsToUpdateOnChange )
      {
        method.addStatement( "boolean modified = false" );
      }

      for ( final PropDescriptor prop : observableProps )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( !$T.isTripleEqual( props.get( Props.$N ), nextProps.get( Props.$N ) ) )",
                                JS_CLASSNAME,
                                prop.getConstantName(),
                                prop.getConstantName() );
        block.addStatement( "$N().reportChanged()", toObservableValueRefMethodName( prop ) );
        if ( prop.shouldUpdateOnChange() )
        {
          block.addStatement( "modified = true" );
        }
        block.endControlFlow();
        method.addCode( block.build() );
      }

      for ( final PropDescriptor prop : updateOnChangeProps )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( !$T.isTripleEqual( props.get( Props.$N ), nextProps.get( Props.$N ) ) )",
                                JS_CLASSNAME,
                                prop.getConstantName(),
                                prop.getConstantName() );
        block.addStatement( "return true" );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      if ( hasObservablePropsToUpdateOnChange )
      {
        if ( descriptor.isArezComponent() )
        {
          method.addStatement( "return modified || $N", RENDER_DEPS_CHANGED_FIELD );
        }
        else
        {
          method.addStatement( "return modified" );
        }
      }
      else
      {
        if ( descriptor.isArezComponent() )
        {
          method.addStatement( "return $N", RENDER_DEPS_CHANGED_FIELD );
        }
        else
        {
          method.addStatement( "return false" );
        }
      }
    }

    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentPreUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_PRE_UPDATE_METHOD )
        .addModifiers( Modifier.PRIVATE )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                         .addAnnotation( NULLABLE_CLASSNAME )
                         .build() );
    final boolean hasPreUpdateOnPropChange = descriptor.hasPreUpdateOnPropChange();
    if ( hasPreUpdateOnPropChange )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevProps )" );
      block.addStatement( "final $T props = props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      buildOnPropChangeInvocations( block, descriptor.getPreUpdateOnPropChangeDescriptors() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    final ExecutableElement preUpdate = descriptor.getPreUpdate();
    if ( null != preUpdate )
    {
      method.addStatement( "$N()", preUpdate.getSimpleName().toString() );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentDidUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_DID_UPDATE_METHOD )
        .addModifiers( Modifier.PRIVATE );

    if ( descriptor.hasPostUpdateOnPropChange() )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                             .addAnnotation( NULLABLE_CLASSNAME )
                             .build() );
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevProps )" );
      block.addStatement( "final $T props = props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      buildOnPropChangeInvocations( block, descriptor.getPostUpdateOnPropChangeDescriptors() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    final ExecutableElement postRender = descriptor.getPostRender();
    if ( null != postRender )
    {
      method.addStatement( "$N()", postRender.getSimpleName().toString() );
    }
    final ExecutableElement postUpdate = descriptor.getPostUpdate();
    if ( null != postUpdate )
    {
      method.addStatement( "$N()", postUpdate.getSimpleName().toString() );
    }
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldStoreDebugDataAsState() )", REACT_CLASSNAME );
    block.addStatement( "storeDebugDataAsState()" );
    block.endControlFlow();
    method.addCode( block.build() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentWillUnmount( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_WILL_UNMOUNT_METHOD )
        .addModifiers( Modifier.PRIVATE );

    final ExecutableElement preUnmount = descriptor.getPreUnmount();
    if ( null != preUnmount )
    {
      method.addStatement( "$N()", preUnmount.getSimpleName().toString() );
    }
    if ( descriptor.isArezComponent() )
    {
      method.addStatement( "$N = true", UNMOUNTED_FIELD );
      method.addStatement( "$T.dispose( this )", DISPOSABLE_CLASSNAME );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildRender( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.isArezComponent();
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "render" )
      .addAnnotation( Override.class )
      .addAnnotation( NULLABLE_CLASSNAME )
      .addModifiers( Modifier.PROTECTED )
      .returns( REACT_NODE_CLASSNAME );

    final AnnotationSpec.Builder observe =
      AnnotationSpec
        .builder( OBSERVE_ANNOTATION_CLASSNAME )
        .addMember( "name", "$S", "render" )
        .addMember( "priority", "$T.LOW", PRIORITY_CLASSNAME )
        .addMember( "executor", "$T.EXTERNAL", EXECUTOR_CLASSNAME )
        .addMember( "observeLowerPriorityDependencies", "true" )
        .addMember( "reportResult", "false" );
    if ( descriptor.allowNoArezDeps() )
    {
      observe.addMember( "depType", "$T.AREZ_OR_NONE", DEP_TYPE_CLASSNAME );
    }
    method.addAnnotation( observe.build() );

    method.addStatement( "$N = false", RENDER_DEPS_CHANGED_FIELD );
    method.addStatement( "$T.pauseUntilRenderLoopComplete()", SCHEDULER_UTIL_CLASSNAME );
    method.addStatement( "assert $T.isNotDisposed( this )", DISPOSABLE_CLASSNAME );

    final List<PropDescriptor> disposableProps =
      descriptor.getProps().stream().filter( PropDescriptor::isDisposable ).collect( Collectors.toList() );

    for ( final PropDescriptor prop : disposableProps )
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
      block.addStatement( "return null" );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    if ( !descriptor.allowNoArezDeps() )
    {
      method.addStatement( "final $T result = super.render()", REACT_NODE_CLASSNAME );

      final CodeBlock.Builder depCheckBlock = CodeBlock.builder();
      depCheckBlock.beginControlFlow( "if ( $T.shouldCheckInvariants() && $T.areSpiesEnabled() )",
                                      AREZ_CLASSNAME,
                                      AREZ_CLASSNAME );
      depCheckBlock.addStatement(
        "$T.invariant( () -> !getRenderObserver().getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), () -> \"ReactArezComponent render completed on '\" + this + \"' but the component does not have any Arez dependencies. This component should extend react4j.Component instead.\" )",
        GUARDS_CLASSNAME );
      depCheckBlock.endControlFlow();
      method.addCode( depCheckBlock.build() );
      method.addStatement( "return result" );
    }
    else
    {
      method.addStatement( "return super.render()" );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildGetRenderObserver( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.isArezComponent();
    return MethodSpec
      .methodBuilder( "getRenderObserver" )
      .addAnnotation( NONNULL_CLASSNAME )
      .addAnnotation( OBSERVER_REF_ANNOTATION_CLASSNAME )
      .addModifiers( Modifier.ABSTRACT )
      .returns( OBSERVER_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildPopulateDebugData( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.isArezComponent();
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "populateDebugData" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PROTECTED )
      .addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "data", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() );
    final CodeBlock.Builder block = CodeBlock.builder();
    block.beginControlFlow( "if ( $T.shouldStoreDebugDataAsState() && $T.areSpiesEnabled() )",
                            REACT_CLASSNAME,
                            AREZ_CLASSNAME );
    block.addStatement( "$T.collectDependencyDebugData( getRenderObserver(), data )", INTROSPECT_UTIL_CLASSNAME );
    block.endControlFlow();
    method.addCode( block.build() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildOnRenderDepsChange( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.isArezComponent();
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "onRenderDepsChange" )
      .addModifiers( Modifier.FINAL );

    final CodeBlock.Builder outer = CodeBlock.builder();
    outer.beginControlFlow( "if ( !$N )", RENDER_DEPS_CHANGED_FIELD );
    outer.addStatement( "$N = true",RENDER_DEPS_CHANGED_FIELD );
    final CodeBlock.Builder inner = CodeBlock.builder();
    inner.beginControlFlow( "if ( !$N )", UNMOUNTED_FIELD );
    if( descriptor.hasObservableProps() )
    {
      inner.addStatement( "scheduleRender( false )" );
    }
    else
    {
      inner.addStatement( "scheduleRender()" );
    }
    inner.endControlFlow();
    outer.add( inner.build() );
    outer.endControlFlow();
    method.addCode( outer.build() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildPropValidatorMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "validatePropValues" ).
        addAnnotation( Override.class ).
        addModifiers( Modifier.PROTECTED, Modifier.FINAL ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final String name = prop.getName();
      final String rawName = "raw$" + name;
      final String typedName = "typed$" + name;
      method.addStatement( "final $T $N = props.get( Props.$N )", Object.class, rawName, prop.getConstantName() );
      final boolean isNonNull = isNonnull( prop.getMethod() );
      if ( !prop.isOptional() && isNonNull )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
        block.addStatement( "$T.apiInvariant( () -> null != $N, () -> \"Required prop named '$N' is missing from " +
                            "component named '$N' so it was either incorrectly omitted or a null value has been " +
                            "incorrectly specified.\" ) ",
                            GUARDS_CLASSNAME,
                            rawName,
                            prop.getName(),
                            descriptor.getName() );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != $N )", rawName );
      final TypeMirror returnType = prop.getMethodType().getReturnType();
      block.addStatement( "final $T $N = $T.$N( $N )",
                          returnType,
                          typedName,
                          JS_CLASSNAME,
                          getConverter( returnType, prop.getMethod() ),
                          rawName );
      if ( prop.hasValidateMethod() )
      {
        block.addStatement( "$N( $N )", prop.getValidateMethod().getSimpleName().toString(), typedName );
      }
      block.endControlFlow();
      method.addCode( block.build() );
    }
    return method;
  }

  @Nonnull
  private static TypeSpec buildInjectSupport( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.needsInjection();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "InjectSupport" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    builder.addField( buildProviderField( descriptor ).build() );
    builder.addMethod( buildSetProviderMethod( descriptor ).build() );
    builder.addMethod( buildGetProviderMethod( descriptor ).build() );

    return builder.build();
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
    block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
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

    final boolean shouldGenerateLiteLifecycle = descriptor.shouldGenerateLiteLifecycle();
    if ( shouldGenerateLiteLifecycle )
    {
      method.addStatement( "final $T componentConstructor = ( $T.shouldStoreDebugDataAsState() || " +
                           "$T.shouldValidatePropValues() ) ? $T::new : $T::new",
                           COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           REACT_CLASSNAME,
                           REACT_CLASSNAME,
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
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", REACT_CLASSNAME );
    codeBlock.addStatement( "$T.asPropertyMap( componentConstructor ).set( \"displayName\", $S )",
                            JS_CLASSNAME,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );

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

    // These fields have been moved to a separate class to avoid a <clinit> on containing class

    final List<PropDescriptor> props = descriptor.getProps();
    final int propCount = props.size();
    for ( int i = 0; i < propCount; i++ )
    {
      builder.addField( buildPropKeyConstantField( props.get( i ), i ).build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildFactory()
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Factory" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    // This field has been moved to a separate class to avoid a <clinit> on containing class as that forces
    // every call to React_MyComponent to first check <clinit> has been invoked.
    final FieldSpec.Builder field =
      FieldSpec.builder( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME,
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL ).
        initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

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

    if ( lite )
    {
      if ( descriptor.generateComponentDidMountInLiteLifecycle() )
      {
        builder.addSuperinterface( ON_COMPONENT_DID_MOUNT_CLASSNAME );
      }
      if ( descriptor.generateComponentDidUpdateInLiteLifecycle() )
      {
        builder.addSuperinterface( ON_COMPONENT_DID_UPDATE_CLASSNAME );
      }
      if ( descriptor.generateShouldComponentUpdateInLiteLifecycle() )
      {
        builder.addSuperinterface( ON_COMPONENT_SHOULD_UPDATE_CLASSNAME );
      }
    }
    else
    {
      if ( descriptor.generateComponentDidMount() )
      {
        builder.addSuperinterface( ON_COMPONENT_DID_MOUNT_CLASSNAME );
      }
      if ( descriptor.generateComponentDidUpdate() )
      {
        builder.addSuperinterface( ON_COMPONENT_DID_UPDATE_CLASSNAME );
      }
      if ( descriptor.generateShouldComponentUpdate() )
      {
        builder.addSuperinterface( ON_COMPONENT_SHOULD_UPDATE_CLASSNAME );
      }
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addSuperinterface( ON_GET_SNAPSHOT_BEFORE_UPDATE_CLASSNAME );
    }
    if ( descriptor.generateComponentWillUnmount() )
    {
      builder.addSuperinterface( ON_COMPONENT_WILL_UNMOUNT_CLASSNAME );
    }
    if ( descriptor.generateComponentDidCatch() )
    {
      builder.addSuperinterface( ON_COMPONENT_WILL_UNMOUNT_CLASSNAME );
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
        method.addStatement( "return InjectSupport.getProvider().get()" );
      }
      else
      {
        final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
        method.addStatement( "return new $T" + infix + "( this )", descriptor.getClassNameToConstruct() );
      }
      builder.addMethod( method.build() );
    }

    if ( lite ? descriptor.generateComponentDidMountInLiteLifecycle() : descriptor.generateComponentDidMount() )
    {
      // We add this so the DevTool sees any debug data saved
      builder.addMethod( buildNativeComponentDidMount( descriptor ).build() );
    }
    if ( lite ? descriptor.generateShouldComponentUpdateInLiteLifecycle() : descriptor.generateShouldComponentUpdate() )
    {
      builder.addMethod( buildNativeShouldComponentUpdate( descriptor ).build() );
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addMethod( buildNativeComponentPreUpdate( descriptor ).build() );
    }
    if ( lite ? descriptor.generateComponentDidUpdateInLiteLifecycle() : descriptor.generateComponentDidUpdate() )
    {
      // We add this for Arez components so the DevTool sees any debug data saved
      builder.addMethod( buildNativeComponentDidUpdate( descriptor ).build() );
    }
    if ( descriptor.generateComponentWillUnmount() )
    {
      builder.addMethod( buildNativeComponentWillUnmount( descriptor ).build() );
    }
    if ( descriptor.generateComponentDidCatch() )
    {
      builder.addMethod( buildNativeComponentDidCatch().build() );
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentDidMount( @Nonnull final ComponentDescriptor componentDescriptor )
  {
    return MethodSpec
      .methodBuilder( "componentDidMount" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addStatement( "(($T) component() ).$N()",
                     componentDescriptor.getEnhancedClassName(),
                     COMPONENT_DID_MOUNT_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeShouldComponentUpdate( @Nonnull final ComponentDescriptor componentDescriptor )
  {
    return MethodSpec
      .methodBuilder( "shouldComponentUpdate" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( TypeName.BOOLEAN )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextProps", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addStatement( "return (($T) component() ).$N( nextProps )",
                     componentDescriptor.getEnhancedClassName(),
                     SHOULD_COMPONENT_UPDATE_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentPreUpdate( @Nonnull final ComponentDescriptor componentDescriptor )
  {
    return MethodSpec
      .methodBuilder( "getSnapshotBeforeUpdate" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( TypeName.get( Object.class ) )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevState", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addStatement( "(($T) component() ).$N( prevProps )",
                     componentDescriptor.getEnhancedClassName(),
                     COMPONENT_PRE_UPDATE_METHOD )
      .addStatement( "return null" );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentDidUpdate( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "componentDidUpdate" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevProps", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() );
    if ( descriptor.hasPostUpdateOnPropChange() )
    {
      return method.addStatement( "(($T) component() ).$N( prevProps )",
                                  descriptor.getEnhancedClassName(),
                                  COMPONENT_DID_UPDATE_METHOD );
    }
    else
    {
      return method.addStatement( "(($T) component() ).$N()",
                                  descriptor.getEnhancedClassName(),
                                  COMPONENT_DID_UPDATE_METHOD );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentWillUnmount( @Nonnull final ComponentDescriptor componentDescriptor )
  {
    return MethodSpec
      .methodBuilder( "componentWillUnmount" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addStatement( "(($T) component() ).$N()",
                     componentDescriptor.getEnhancedClassName(),
                     COMPONENT_WILL_UNMOUNT_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentDidCatch()
  {
    return MethodSpec
      .methodBuilder( "componentDidCatch" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( TypeName.get( Object.class ) )
      .addParameter( ParameterSpec.builder( JS_ERROR_CLASSNAME, "error", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addParameter( ParameterSpec.builder( REACT_ERROR_INFO_CLASSNAME, "info", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addStatement( "performComponentDidCatch( error, info )" );
  }

  private static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
  }

  @Nonnull
  static TypeSpec buildArezDaggerComponentExtension( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerComponentExtensionClassName() );
    final ClassName superClassName = descriptor.getArezDaggerExtensionClassName();
    builder.addSuperinterface( superClassName );
    addGeneratedAnnotation( descriptor, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PUBLIC );

    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "bind" + descriptor.getName() )
        .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
        .addStatement( "$T.super.$N()", superClassName, "bind" + descriptor.getName() )
        .addStatement( "$T.InjectSupport.setProvider( () -> ($T) $N().createProvider().get() )",
                       descriptor.getEnhancedClassName(),
                       descriptor.getClassName(),
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

    return builder.build();
  }

  @Nonnull
  static TypeSpec buildDaggerComponentExtension( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerComponentExtensionClassName() );
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
          addModifiers( Modifier.PUBLIC, Modifier.DEFAULT );
      if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
      {
        method.addStatement( "$T.InjectSupport.setProvider( $N().createProvider() )",
                             descriptor.getEnhancedClassName(),
                             "get" + descriptor.getName() + "DaggerSubcomponent" );
      }
      else
      {
        method.addStatement( "$T.InjectSupport.setProvider( () -> ($T) $N().createProvider().get() )",
                             descriptor.getEnhancedClassName(),
                             descriptor.getClassName(),
                             "get" + descriptor.getName() + "DaggerSubcomponent" );
      }
      builder.addMethod( method.build() );
    }

    builder.addType( buildDaggerModule( descriptor ) );
    builder.addType( buildDaggerComponent( descriptor ) );

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

    if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createProvider" ).
          addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
          returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, descriptor.getClassName() ) );
      builder.addMethod( method.build() );
    }
    else
    {
      final MethodSpec.Builder method =
        MethodSpec.methodBuilder( "createProvider" ).
          addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
          returns( ParameterizedTypeName.get( PROVIDER_CLASSNAME, COMPONENT_CLASSNAME ) );
      builder.addMethod( method.build() );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildDaggerModule( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "DaggerModule" );

    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addAnnotation( ClassName.bestGuess( Constants.DAGGER_MODULE_CLASSNAME ) );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "bindComponent" ).
        addAnnotation( ClassName.bestGuess( Constants.DAGGER_BINDS_CLASSNAME ) ).
        addModifiers( Modifier.ABSTRACT, Modifier.PUBLIC ).
        addParameter( descriptor.getClassNameToConstruct(), "component" ).
        returns( descriptor.getClassName() );
    if ( descriptor.getElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.returns( descriptor.getClassName() );
    }
    else
    {
      method.returns( COMPONENT_CLASSNAME );
    }

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
    keyStep.addMethod( "key", "key", TypeName.get( String.class ), keyStepMethodType );
    keyStep.addMethod( "key", "*key_int*", TypeName.INT, keyStepMethodType );

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
            addChildrenStreamPropStepMethod( optionalPropStep );
          }
          hasRequiredAfterOptional = true;
        }
        // Single method step
        final Step step = builder.addStep();
        addPropStepMethod( step, prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
        if ( prop.getName().equals( "children" ) )
        {
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
    step.addMethod( "build", "build", REACT_NODE_CLASSNAME, StepMethodType.TERMINATE );
  }

  /**
   * A helper intrinsic that converts children streams.
   */
  private static void addChildrenStreamPropStepMethod( @Nonnull final Step step )
  {
    final ParameterizedTypeName typeName =
      ParameterizedTypeName.get( ClassName.get( Stream.class ), WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );

    //TODO: Replace this with prop enhancer
    step.addMethod( "children",
                    "*children_stream*",
                    typeName,
                    StepMethodType.TERMINATE );
  }

  private static void addPropStepMethod( @Nonnull final Step step,
                                         @Nonnull final PropDescriptor prop,
                                         @Nonnull final StepMethodType stepMethodType )
  {
    step.addMethod( prop, stepMethodType );
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
