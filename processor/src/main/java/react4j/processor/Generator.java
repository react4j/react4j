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
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
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
  private static final ClassName NONNULL_CLASSNAME = ClassName.get( "javax.annotation", "Nonnull" );
  private static final ClassName NULLABLE_CLASSNAME = ClassName.get( "javax.annotation", "Nullable" );
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  private static final ClassName AREZ_CLASSNAME = ClassName.get( "arez", "Arez" );
  private static final ClassName OBSERVER_FLAGS_CLASSNAME = ClassName.get( "arez", "Observer", "Flags" );
  private static final ClassName OBSERVER_CLASSNAME = ClassName.get( "arez", "Observer" );
  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "ObservableValue" );
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName AREZ_INJECT_MODE_CLASSNAME =
    ClassName.get( "arez.annotations", "InjectMode" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
  private static final ClassName PER_INSTANCE_CLASSNAME = ClassName.get( "arez.annotations", "PerInstance" );
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  private static final ClassName PRIORITY_OVERRIDE_CLASSNAME = ClassName.get( "arez.annotations", "PriorityOverride" );
  private static final ClassName EXECUTOR_CLASSNAME = ClassName.get( "arez.annotations", "Executor" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observe" );
  private static final ClassName OBSERVER_REF_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "ObserverRef" );
  private static final ClassName COMPONENT_STATE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentStateRef" );
  private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableValueRef" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );
  private static final ClassName IDENTIFIABLE_CLASSNAME =
    ClassName.get( "arez.component", "Identifiable" );
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );
  private static final ClassName JS_ERROR_CLASSNAME = ClassName.get( "elemental2.core", "JsError" );
  private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsConstructor" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName KEYED_CLASSNAME = ClassName.get( "react4j", "Keyed" );
  private static final ClassName REACT_ELEMENT_CLASSNAME = ClassName.get( "react4j", "ReactElement" );
  private static final ClassName REACT_ERROR_INFO_CLASSNAME = ClassName.get( "react4j", "ReactErrorInfo" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
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
    ClassName.get( "react4j.internal", "OnComponentDidCatch" );
  private static final ClassName REACT_NATIVE_COMPONENT_CLASSNAME =
    ClassName.get( "react4j.internal", "NativeComponent" );
  private static final ClassName COMPONENT_STATE_CLASSNAME = ClassName.get( "react4j.internal.arez", "ComponentState" );
  private static final ClassName SCHEDULER_UTIL_CLASSNAME = ClassName.get( "react4j.internal.arez", "SchedulerUtil" );
  private static final ClassName INTROSPECT_UTIL_CLASSNAME = ClassName.get( "react4j.internal.arez", "IntrospectUtil" );
  private static final String FRAMEWORK_INTERNAL_PREFIX = "$$react4j$$_";
  private static final String SHOULD_COMPONENT_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "shouldComponentUpdate";
  private static final String COMPONENT_PRE_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentPreUpdate";
  private static final String COMPONENT_DID_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidUpdate";
  private static final String COMPONENT_DID_MOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidMount";
  private static final String COMPONENT_WILL_UNMOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentWillUnmount";
  private static final String VALIDATE_PROPS_METHOD = FRAMEWORK_INTERNAL_PREFIX + "validatePropValues";
  private static final String COMPONENT_STATE_FIELD = FRAMEWORK_INTERNAL_PREFIX + "state";
  private static final String COMPONENT_FIELD = FRAMEWORK_INTERNAL_PREFIX + "component";
  private static final String IS_READY_METHOD = FRAMEWORK_INTERNAL_PREFIX + "isReady";

  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildComponentBuilder( @Nonnull final ProcessingEnvironment processingEnv,
                                         @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getBuilderClassName() );
    addOriginatingTypes( descriptor.getElement(), builder );
    addGeneratedAnnotation( processingEnv, builder );
    builder.addModifiers( Modifier.FINAL );
    ProcessorUtil.copyAccessModifiers( descriptor.getElement(), builder );

    // Private constructor so can not instantiate
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final BuilderDescriptor builderDescriptor = buildBuilderDescriptor( descriptor );

    final List<Step> steps = builderDescriptor.getSteps();
    for ( final Step step : steps )
    {
      builder.addType( buildBuilderStepInterface( descriptor, step ) );
    }

    // first step which may be required prop, optional props, or build terminal step.
    buildStaticStepMethodMethods( descriptor, builder, steps.get( 0 ) );

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
          else if ( stepMethod.isChildrenStreamIntrinsic() )
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

    final PropDescriptor prop = stepMethod.getProp();
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
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      parameter.addAnnotation( NONNULL_CLASSNAME );
    }
    method.addParameter( parameter.build() );

    if ( null != prop && prop.isImmutable() && 1 == descriptor.syntheticKeyComponents() )
    {
      final ImmutablePropKeyStrategy strategy = prop.getImmutablePropKeyStrategy();
      if ( ImmutablePropKeyStrategy.KEYED == strategy )
      {
        method.addStatement( "_element.setKey( $T.class.getName() + $T.getKey( $N ) )",
                             descriptor.getClassName(),
                             KEYED_CLASSNAME,
                             stepMethod.getName() );
      }
      else if ( ImmutablePropKeyStrategy.IS_STRING == strategy )
      {
        method.addStatement( "_element.setKey( $T.class.getName() + $N )",
                             descriptor.getClassName(),
                             stepMethod.getName() );
      }
      else if ( ImmutablePropKeyStrategy.TO_STRING == strategy )
      {
        method.addStatement( "_element.setKey( $T.class.getName() + $N )",
                             descriptor.getClassName(),
                             stepMethod.getName() );
      }
      else if ( ImmutablePropKeyStrategy.ENUM == strategy )
      {
        method.addStatement( "_element.setKey( $T.class.getName() + $N.name() )",
                             descriptor.getClassName(),
                             stepMethod.getName() );
      }
      else
      {
        assert ImmutablePropKeyStrategy.AREZ_IDENTIFIABLE == strategy;
        method.addStatement( "_element.setKey( $T.class.getName() + $T.<Object>getArezId( $N ) )",
                             descriptor.getClassName(),
                             IDENTIFIABLE_CLASSNAME,
                             stepMethod.getName() );
      }
    }

    if ( stepMethod.isChildrenIntrinsic() )
    {
      method.varargs();
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
      assert null != prop;
      if ( ProcessorUtil.isNonnull( propMethod ) )
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
    else
    {
      if ( ( null != propMethod && ProcessorUtil.isNonnull( propMethod ) ) && !stepMethod.getType().isPrimitive() )
      {
        method.addStatement( "$T.requireNonNull( $N )", Objects.class, stepMethod.getName() );
      }
      assert null != prop;
      method.addStatement( "_element.props().set( $T.Props.$N, $N )",
                           descriptor.getEnhancedClassName(),
                           prop.getConstantName(),
                           stepMethod.getName() );
    }

    if ( StepMethodType.TERMINATE == stepMethod.getStepMethodType() )
    {
      method.addStatement( "return build()" );
    }
    else
    {
      method.addStatement( "return this" );
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );

    return method.build();
  }

  @Nonnull
  private static MethodSpec buildBuildStepImpl( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "build" )
      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
      .addAnnotation( NONNULL_CLASSNAME );
    final List<PropDescriptor> syntheticProps =
      descriptor.getProps().stream().filter( PropDescriptor::isImmutable ).collect( Collectors.toList() );
    if ( syntheticProps.size() > 1 )
    {
      method.addStatement( "final $T props = _element.props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );

      final StringBuilder sb = new StringBuilder();
      sb.append( "_element.setKey( $T.class.getName()" );
      final ArrayList<Object> params = new ArrayList<>();
      params.add( descriptor.getClassName() );
      for ( final PropDescriptor prop : syntheticProps )
      {
        sb.append( " + \"-\" + " );
        final ImmutablePropKeyStrategy strategy = prop.getImmutablePropKeyStrategy();
        if ( ImmutablePropKeyStrategy.KEYED == strategy )
        {
          sb.append( "$T.getKey( ($T) props.get( $T.Props.$N ) )" );
          params.add( KEYED_CLASSNAME );
          params.add( prop.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( prop.getConstantName() );
        }
        else if ( ImmutablePropKeyStrategy.IS_STRING == strategy || ImmutablePropKeyStrategy.ENUM == strategy )
        {
          sb.append( "( ($T) props.get( $T.Props.$N ) )" );
          params.add( prop.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( prop.getConstantName() );
        }
        else if ( ImmutablePropKeyStrategy.TO_STRING == strategy )
        {
          sb.append( "$T.valueOf( ($T) props.get( $T.Props.$N ) )" );
          params.add( String.class );
          params.add( prop.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( prop.getConstantName() );
        }
        else
        {
          assert ImmutablePropKeyStrategy.AREZ_IDENTIFIABLE == strategy;
          sb.append( "$T.valueOf( $T.<Object>getArezId( ($T) props.get( $T.Props.$N ) ) )" );
          params.add( String.class );
          params.add( IDENTIFIABLE_CLASSNAME );
          params.add( prop.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( prop.getConstantName() );
        }
      }
      sb.append( " )" );
      method.addStatement( sb.toString(), params.toArray() );
    }
    method
      .addStatement( "_element.complete()" )
      .addStatement( "return _element" )
      .returns( REACT_NODE_CLASSNAME );
    return method.build();
  }

  @Nonnull
  private static TypeSpec buildBuilder( @Nonnull final ComponentDescriptor descriptor,
                                        @Nonnull final BuilderDescriptor builderDescriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Builder" );
    ProcessorUtil.copyTypeParameters( descriptor.getElement(), builder );
    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );
    final List<Step> steps = builderDescriptor.getSteps();
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
      method.addStatement( "final $T props = _element.props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      for ( final PropDescriptor prop : propsWithDefaults )
      {
        method.addStatement( "props.set( $T.Props.$N, $T.$N" +
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
  static TypeSpec buildEnhancedComponent( @Nonnull final ProcessingEnvironment processingEnv,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getEnhancedClassName() );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );
    ProcessorUtil.copyWhitelistedAnnotations( descriptor.getElement(), builder );

    builder.superclass( descriptor.getComponentType() );

    final AnnotationSpec.Builder arezAnnotation =
      AnnotationSpec.builder( AREZ_COMPONENT_CLASSNAME ).
        addMember( "name", "$S", descriptor.getName() ).
        addMember( "disposeNotifier", "$T.DISABLE", AREZ_FEATURE_CLASSNAME );
    if ( !descriptor.trackRender() )
    {
      arezAnnotation.addMember( "allowEmpty", "true" );
    }
    if ( descriptor.needsInjection() )
    {
      arezAnnotation.addMember( "inject", "$T.CONSUME", AREZ_INJECT_MODE_CLASSNAME );
    }
    else
    {
      arezAnnotation.addMember( "inject", "$T.NONE", AREZ_INJECT_MODE_CLASSNAME );
    }
    builder.addAnnotation( arezAnnotation.build() );
    builder.addModifiers( Modifier.ABSTRACT );

    addGeneratedAnnotation( processingEnv, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

    builder.addMethod( buildConstructor( descriptor ).build() );

    if ( descriptor.trackRender() )
    {
      builder.addField( FieldSpec.builder( TypeName.INT, COMPONENT_STATE_FIELD, Modifier.PRIVATE ).build() );
    }

    builder.addType( buildFactory() );
    if ( !descriptor.getProps().isEmpty() )
    {
      builder.addType( buildPropsType( descriptor ) );
    }

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    if ( descriptor.getProps().stream().anyMatch( PropDescriptor::needsMutablePropAccessedInPostConstructInvariant ) )
    {
      builder.addMethod( buildIsReadyMethod().build() );
    }

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      builder.addMethod( buildPropMethod( prop ).build() );
      if ( prop.isObservable() )
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

    if ( descriptor.trackRender() || descriptor.getProps().stream().anyMatch( PropDescriptor::isDisposable ) )
    {
      builder.addMethod( buildRender( descriptor ).build() );
    }

    if ( descriptor.trackRender() )
    {
      builder.addMethod( buildOnRenderDepsChange( descriptor ).build() );
      builder.addMethod( buildGetRenderObserver( descriptor ).build() );
      builder.addMethod( buildPopulateDebugData( descriptor ).build() );

      for ( final String priorityOverride : descriptor.getPriorityOverrides() )
      {
        builder.addMethod( buildPriorityOverrideMethod( priorityOverride ).build() );
      }
    }

    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      builder.addType( buildNativeComponent( descriptor, true ) );
    }
    builder.addType( buildNativeComponent( descriptor, false ) );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildPriorityOverrideMethod( @Nonnull final String priorityOverride )
  {
    return MethodSpec.methodBuilder( priorityOverride + "Priority" )
      .addAnnotation( PRIORITY_OVERRIDE_CLASSNAME )
      .addModifiers( Modifier.FINAL )
      .returns( TypeName.INT )
      .addStatement( "return $T.PRIORITY_LOWEST", OBSERVER_FLAGS_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructor( @Nonnull final ComponentDescriptor descriptor )
  {
    final String componentParameterName = FRAMEWORK_INTERNAL_PREFIX + "nativeComponent";
    final ParameterSpec.Builder componentParameter =
      ParameterSpec
        .builder( REACT_NATIVE_COMPONENT_CLASSNAME, componentParameterName, Modifier.FINAL )
        .addAnnotation( NONNULL_CLASSNAME );
    if ( descriptor.needsInjection() )
    {
      componentParameter.addAnnotation( PER_INSTANCE_CLASSNAME );
    }
    final MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
    ctor.addParameter( componentParameter.build() );

    final List<? extends VariableElement> parameters = descriptor.getConstructor().getParameters();
    if ( !parameters.isEmpty() )
    {
      final StringBuilder sb = new StringBuilder();
      final ArrayList<Object> params = new ArrayList<>();
      sb.append( "super( " );
      boolean first = true;
      for ( final VariableElement element : parameters )
      {
        if ( !first )
        {
          sb.append( ", " );
        }
        first = false;
        sb.append( "$N" );
        final String name = element.getSimpleName().toString();
        params.add( name );
        final ParameterSpec.Builder ctorParameter =
          ParameterSpec.builder( TypeName.get( element.asType() ), name, Modifier.FINAL );
        ProcessorUtil.copyWhitelistedAnnotations( element, ctorParameter );
        ctor.addParameter( ctorParameter.build() );
      }
      sb.append( " )" );
      ctor.addStatement( sb.toString(), params.toArray() );
    }

    ctor.addStatement( "bindComponent( $N )", componentParameterName );
    return ctor;
  }

  @Nonnull
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
  private static MethodSpec.Builder buildPropMethod( @Nonnull final PropDescriptor prop )
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

    if ( prop.isObservable() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", prop.getName() ).
          addMember( "expectSetter", "false" ).
          addMember( "readOutsideTransaction", "true" );
      method.addAnnotation( annotation.build() );
    }

    if ( prop.needsMutablePropAccessedInPostConstructInvariant() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
      block.addStatement( "$T.apiInvariant( () -> $N(), " +
                          "() -> \"The component '\" + this + \"' accessed the prop named '" + prop.getName() +
                          "' before the component is ready (possibly in a @PostConstruct annotated method?) and " +
                          "does not have a @OnPropChange annotated method to cover the prop and reflect changes " +
                          "of the prop onto the component. This is considered a likely bug and the @Prop should be " +
                          "made immutable or an @OnPropChange method added to cover the prop. Alternatively this " +
                          "invariant check can be suppressed by adding @SuppressWarnings( \\\"" +
                          Constants.MUTABLE_PROP_ACCESSED_IN_POST_CONSTRUCT_SUPPRESSION + "\\\" ) or " +
                          "@SuppressReact4jWarnings( \\\"" +
                          Constants.MUTABLE_PROP_ACCESSED_IN_POST_CONSTRUCT_SUPPRESSION + "\\\" ) to the method.\" )",
                          GUARDS_CLASSNAME,
                          IS_READY_METHOD );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    final String convertMethodName = getConverter( returnType, methodElement );
    final TypeKind resultKind = methodElement.getReturnType().getKind();
    if ( !resultKind.isPrimitive() && !ProcessorUtil.isNonnull( methodElement ) )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
      block.addStatement( "return null != props().getAsAny( Props.$N ) ? props().getAsAny( Props.$N ).$N() : null",
                          prop.getConstantName(),
                          prop.getConstantName(),
                          convertMethodName );
      block.nextControlFlow( "else" );
      block.addStatement( "return $T.uncheckedCast( props().getAsAny( Props.$N ) )",
                          JS_CLASSNAME,
                          prop.getConstantName() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "return props().getAsAny( Props.$N ).$N()", prop.getConstantName(), convertMethodName );
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
        throw new ProcessorException( "Return type of @Prop method is not yet " +
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
      returns( ParameterizedTypeName.get( OBSERVABLE_CLASSNAME, WildcardTypeName.subtypeOf( TypeName.OBJECT ) ) );
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
        if ( !resultKind.isPrimitive() && !ProcessorUtil.isNonnull( prop.getMethod() ) )
        {
          sb.append( "$T.uncheckedCast( props.getAsAny( Props.$N ) )" );
          params.add( JS_CLASSNAME );
          params.add( prop.getConstantName() );
        }
        else
        {
          sb.append( "props.getAsAny( Props.$N ).$N()" );
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
      validateBlock.addStatement( "$N( nextProps )", VALIDATE_PROPS_METHOD );
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
      if ( descriptor.trackRender() )
      {
        method.addStatement( "return $T.SCHEDULED == $N", COMPONENT_STATE_CLASSNAME, COMPONENT_STATE_FIELD );
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
        if ( descriptor.trackRender() )
        {
          method.addStatement( "return modified || $T.SCHEDULED == $N",
                               COMPONENT_STATE_CLASSNAME,
                               COMPONENT_STATE_FIELD );
        }
        else
        {
          method.addStatement( "return modified" );
        }
      }
      else
      {
        if ( descriptor.trackRender() )
        {
          method.addStatement( "return $T.SCHEDULED == $N", COMPONENT_STATE_CLASSNAME, COMPONENT_STATE_FIELD );
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

    if ( descriptor.trackRender() )
    {
      method.addStatement( "$N = $T.UNMOUNTED", COMPONENT_STATE_FIELD, COMPONENT_STATE_CLASSNAME );
    }
    // We always dispose here rather than checking hasArezElements()
    // as this code path is only invoked when there are Arez elements, when we are in non-production
    // mode (and thus this makes debugging easier). Thus no need to have a guard
    method.addStatement( "(($T) this).dispose()", descriptor.getArezClassName() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildRender( @Nonnull final ComponentDescriptor descriptor )
  {
    assert descriptor.trackRender() || descriptor.getProps().stream().anyMatch( PropDescriptor::isDisposable );
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "render" )
      .addAnnotation( Override.class )
      .addAnnotation( NULLABLE_CLASSNAME )
      .addModifiers( Modifier.PROTECTED )
      .returns( REACT_NODE_CLASSNAME );

    if ( descriptor.trackRender() )
    {
      final AnnotationSpec.Builder observe =
        AnnotationSpec
          .builder( OBSERVE_ANNOTATION_CLASSNAME )
          .addMember( "name", "$S", "render" )
          .addMember( "priority", "$T.LOW", PRIORITY_CLASSNAME )
          .addMember( "executor", "$T.EXTERNAL", EXECUTOR_CLASSNAME )
          // Needs AREZ_OR_NONE in scenario where all props are disposed and component
          // thus accesses no dependencies before exiting render
          .addMember( "depType", "$T.AREZ_OR_NONE", DEP_TYPE_CLASSNAME )
          .addMember( "observeLowerPriorityDependencies", "true" )
          .addMember( "reportResult", "false" );
      method.addAnnotation( observe.build() );

      method.addStatement( "$N = $T.IDLE", COMPONENT_STATE_FIELD, COMPONENT_STATE_CLASSNAME );
      method.addStatement( "$T.pauseUntilRenderLoopComplete()", SCHEDULER_UTIL_CLASSNAME );
    }
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

    if ( ComponentType.TRACKING == descriptor.getType() )
    {
      method.addStatement( "final $T result = super.render()", REACT_NODE_CLASSNAME );

      final CodeBlock.Builder depCheckBlock = CodeBlock.builder();
      depCheckBlock.beginControlFlow( "if ( $T.shouldCheckInvariants() && $T.areSpiesEnabled() )",
                                      AREZ_CLASSNAME,
                                      AREZ_CLASSNAME );
      depCheckBlock.addStatement( "$T.invariant( () -> !getRenderObserver().getContext().getSpy()." +
                                  "asObserverInfo( getRenderObserver() ).getDependencies().isEmpty(), " +
                                  "() -> \"Component render completed on '\" + this + \"' without accessing " +
                                  "any Arez dependencies but has a type set to TRACKING. The render method " +
                                  "needs to access an Arez dependency or the type should be changed to " +
                                  "STATEFUL or MAYBE_TRACKING.\" )",
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
    assert descriptor.trackRender();
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
    assert descriptor.trackRender();
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
    assert descriptor.trackRender();
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "onRenderDepsChange" )
      .addModifiers( Modifier.FINAL );

    final CodeBlock.Builder outer = CodeBlock.builder();
    outer.beginControlFlow( "if ( $T.IDLE == $N )", COMPONENT_STATE_CLASSNAME, COMPONENT_STATE_FIELD );
    outer.addStatement( "$N = $T.SCHEDULED", COMPONENT_STATE_FIELD, COMPONENT_STATE_CLASSNAME );
    if ( descriptor.hasObservableProps() )
    {
      outer.addStatement( "scheduleRender( false )" );
    }
    else
    {
      outer.addStatement( "scheduleRender()" );
    }
    outer.endControlFlow();
    method.addCode( outer.build() );
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildIsReadyMethod()
  {
    return MethodSpec.methodBuilder( IS_READY_METHOD ).
      addModifiers( Modifier.ABSTRACT ).
      returns( TypeName.BOOLEAN ).
      addAnnotation( COMPONENT_STATE_REF_ANNOTATION_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildPropValidatorMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( VALIDATE_PROPS_METHOD ).
        addModifiers( Modifier.PRIVATE ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NONNULL_CLASSNAME ).build() );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final String name = prop.getName();
      final String rawName = "raw$" + name;
      final String typedName = "typed$" + name;
      method.addStatement( "final $T $N = props.get( Props.$N )", Object.class, rawName, prop.getConstantName() );
      final boolean isNonNull = ProcessorUtil.isNonnull( prop.getMethod() );
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
        .builder( REACT_NATIVE_COMPONENT_CLASSNAME, "nativeComponent", Modifier.FINAL )
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
      FieldSpec
        .builder( COMPONENT_CONSTRUCTOR_FUNCTION_CLASSNAME, "TYPE", Modifier.STATIC, Modifier.FINAL )
        .addAnnotation( NONNULL_CLASSNAME )
        .initializer( "getConstructorFunction()" );
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

    builder.superclass( REACT_NATIVE_COMPONENT_CLASSNAME );
    builder.addTypeVariables( ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    final TypeName componentFieldType;
    if ( descriptor.getElement().getTypeParameters().isEmpty() )
    {
      componentFieldType = descriptor.getEnhancedClassName();
    }
    else
    {
      final TypeName[] typeNames =
        ProcessorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ).toArray( new TypeName[ 0 ] );
      componentFieldType = ParameterizedTypeName.get( descriptor.getEnhancedClassName(), typeNames );
    }

    builder.addField( FieldSpec
                        .builder( componentFieldType, COMPONENT_FIELD, Modifier.PRIVATE, Modifier.FINAL )
                        .addAnnotation( NONNULL_CLASSNAME )
                        .build() );

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
      if ( descriptor.generateComponentWillUnmountInLiteLifecycle() )
      {
        builder.addSuperinterface( ON_COMPONENT_WILL_UNMOUNT_CLASSNAME );
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
      if ( descriptor.generateComponentWillUnmount() )
      {
        builder.addSuperinterface( ON_COMPONENT_WILL_UNMOUNT_CLASSNAME );
      }
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addSuperinterface( ON_GET_SNAPSHOT_BEFORE_UPDATE_CLASSNAME );
    }
    if ( descriptor.generateComponentDidCatch() )
    {
      builder.addSuperinterface( ON_COMPONENT_DID_CATCH_CLASSNAME );
    }

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "props", Modifier.FINAL ).
          addAnnotation( NULLABLE_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().addParameter( props.build() ).addAnnotation( JS_CONSTRUCTOR_CLASSNAME );
      method.addStatement( "super( props )" );
      if ( descriptor.needsInjection() )
      {
        method.addStatement( "$N = $T.InjectSupport.create( this )",
                             COMPONENT_FIELD,
                             descriptor.getDaggerComponentExtensionClassName() );
      }
      else
      {
        final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
        method.addStatement( "$N = new $T" + infix + "( this )", COMPONENT_FIELD, descriptor.getArezClassName() );
      }

      if ( descriptor.hasValidatedProps() )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldValidatePropValues() )", REACT_CLASSNAME );
        block.addStatement( "assert null != props" );
        block.addStatement( "$N.$N( props )", COMPONENT_FIELD, VALIDATE_PROPS_METHOD );
        block.endControlFlow();
        method.addCode( block.build() );
      }

      builder.addMethod( method.build() );
    }

    if ( lite ? descriptor.generateComponentDidMountInLiteLifecycle() : descriptor.generateComponentDidMount() )
    {
      // We add this so the DevTool sees any debug data saved
      builder.addMethod( buildNativeComponentDidMount().build() );
    }
    if ( lite ? descriptor.generateShouldComponentUpdateInLiteLifecycle() : descriptor.generateShouldComponentUpdate() )
    {
      builder.addMethod( buildNativeShouldComponentUpdate().build() );
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addMethod( buildNativeComponentPreUpdate().build() );
    }
    if ( lite ? descriptor.generateComponentDidUpdateInLiteLifecycle() : descriptor.generateComponentDidUpdate() )
    {
      // We add this for Arez components so the DevTool sees any debug data saved
      builder.addMethod( buildNativeComponentDidUpdate( descriptor ).build() );
    }
    if ( lite ? descriptor.generateComponentWillUnmountInLiteLifecycle() : descriptor.generateComponentWillUnmount() )
    {
      builder.addMethod( buildNativeComponentWillUnmount().build() );
    }
    if ( descriptor.generateComponentDidCatch() )
    {
      builder.addMethod( buildNativeComponentDidCatch( descriptor ).build() );
    }

    builder.addMethod( buildNativeRender().build() );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeRender()
  {
    return MethodSpec
      .methodBuilder( "render" )
      .addAnnotation( Override.class )
      .addAnnotation( NULLABLE_CLASSNAME )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( REACT_NODE_CLASSNAME )
      .addStatement( "return $N.render()", COMPONENT_FIELD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentDidMount()
  {
    return MethodSpec
      .methodBuilder( "componentDidMount" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addStatement( "$N.$N()", COMPONENT_FIELD, COMPONENT_DID_MOUNT_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeShouldComponentUpdate()
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
      .addStatement( "return $N.$N( nextProps )", COMPONENT_FIELD, SHOULD_COMPONENT_UPDATE_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentPreUpdate()
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
      .addStatement( "$N.$N( prevProps )", COMPONENT_FIELD, COMPONENT_PRE_UPDATE_METHOD )
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
      return method.addStatement( "$N.$N( prevProps )", COMPONENT_FIELD, COMPONENT_DID_UPDATE_METHOD );
    }
    else
    {
      return method.addStatement( "$N.$N()", COMPONENT_FIELD, COMPONENT_DID_UPDATE_METHOD );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentWillUnmount()
  {
    return MethodSpec
      .methodBuilder( "componentWillUnmount" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addStatement( "$N.$N()", COMPONENT_FIELD, COMPONENT_WILL_UNMOUNT_METHOD );
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeComponentDidCatch( @Nonnull final ComponentDescriptor descriptor )
  {
    final ExecutableElement onError = descriptor.getOnError();
    assert null != onError;
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "componentDidCatch" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addParameter( ParameterSpec.builder( JS_ERROR_CLASSNAME, "error", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() )
      .addParameter( ParameterSpec.builder( REACT_ERROR_INFO_CLASSNAME, "info", Modifier.FINAL )
                       .addAnnotation( NONNULL_CLASSNAME )
                       .build() );

    final List<? extends VariableElement> parameters = onError.getParameters();
    final String args =
      parameters.isEmpty() ?
      "()" :
      "( " +
      parameters.stream()
        .map( p -> TypeName.get( p.asType() ).toString().equals( Constants.JS_ERROR_CLASSNAME ) ? "error" : "info" )
        .collect(
          Collectors.joining( ", " ) ) +
      " )";

    method.addStatement( "$N.$N" + args, COMPONENT_FIELD, onError.getSimpleName() );
    return method;
  }

  private static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
  }

  @Nonnull
  static TypeSpec buildDaggerComponentExtension( @Nonnull final ProcessingEnvironment processingEnv,
                                                 @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( descriptor.getDaggerComponentExtensionClassName() );
    final ClassName superClassName = descriptor.getArezDaggerExtensionClassName();
    builder.addSuperinterface( superClassName );
    addGeneratedAnnotation( processingEnv, builder );
    addOriginatingTypes( descriptor.getElement(), builder );

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

  @Nonnull
  private static BuilderDescriptor buildBuilderDescriptor( @Nonnull final ComponentDescriptor descriptor )
  {
    final BuilderDescriptor builder = new BuilderDescriptor();

    Step optionalPropStep = null;
    final List<PropDescriptor> props = descriptor.getProps();

    final int propsSize = props.size();

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
    step.addTerminalMethod( "build", "build", REACT_NODE_CLASSNAME );
  }

  /**
   * A helper intrinsic that converts children streams.
   */
  private static void addChildrenStreamPropStepMethod( @Nonnull final Step step )
  {
    final ParameterizedTypeName typeName =
      ParameterizedTypeName.get( ClassName.get( Stream.class ), WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );

    //TODO: Replace this with prop enhancer
    step.addTerminalMethod( "children",
                            "*children_stream*",
                            typeName
    );
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

  private static void addGeneratedAnnotation( @Nonnull final ProcessingEnvironment processingEnv,
                                              @Nonnull final TypeSpec.Builder builder )
  {
    GeneratedAnnotationSpecs
      .generatedAnnotationSpec( processingEnv.getElementUtils(),
                                processingEnv.getSourceVersion(),
                                ReactProcessor.class )
      .ifPresent( builder::addAnnotation );
  }
}
