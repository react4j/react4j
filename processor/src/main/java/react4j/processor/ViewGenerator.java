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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;
import org.realityforge.proton.SuppressWarningsUtil;

@SuppressWarnings( "Duplicates" )
final class ViewGenerator
{
  private static final ClassName GUARDS_CLASSNAME = ClassName.get( "org.realityforge.braincheck", "Guards" );
  private static final ClassName AREZ_CLASSNAME = ClassName.get( "arez", "Arez" );
  private static final ClassName OBSERVER_CLASSNAME = ClassName.get( "arez", "Observer" );
  private static final ClassName OBSERVABLE_CLASSNAME = ClassName.get( "arez", "ObservableValue" );
  private static final ClassName DISPOSABLE_CLASSNAME = ClassName.get( "arez", "Disposable" );
  private static final ClassName AREZ_FEATURE_CLASSNAME =
    ClassName.get( "arez.annotations", "Feature" );
  private static final ClassName SUPPRESS_AREZ_WARNINGS_CLASSNAME =
    ClassName.get( "arez.annotations", "SuppressArezWarnings" );
  private static final ClassName ACTION_CLASSNAME = ClassName.get( "arez.annotations", "Action" );
  private static final ClassName DEP_TYPE_CLASSNAME = ClassName.get( "arez.annotations", "DepType" );
  private static final ClassName PRIORITY_CLASSNAME = ClassName.get( "arez.annotations", "Priority" );
  private static final ClassName EXECUTOR_CLASSNAME = ClassName.get( "arez.annotations", "Executor" );
  private static final ClassName OBSERVABLE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observable" );
  private static final ClassName OBSERVE_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "Observe" );
  private static final ClassName OBSERVER_REF_ANNOTATION_CLASSNAME = ClassName.get( "arez.annotations", "ObserverRef" );
  private static final ClassName COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentDependency" );
  private static final ClassName COMPONENT_NAME_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentNameRef" );
  private static final ClassName COMPONENT_ID_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentIdRef" );
  private static final ClassName COMPONENT_STATE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ComponentStateRef" );
  private static final ClassName OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME =
    ClassName.get( "arez.annotations", "ObservableValueRef" );
  private static final ClassName AREZ_COMPONENT_CLASSNAME =
    ClassName.get( "arez.annotations", "ArezComponent" );
  private static final ClassName JS_ERROR_CLASSNAME = ClassName.get( "akasha.core", "JsError" );
  private static final ClassName JS_CONSTRUCTOR_CLASSNAME = ClassName.get( "jsinterop.annotations", "JsConstructor" );
  private static final ClassName JS_CLASSNAME = ClassName.get( "jsinterop.base", "Js" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName REACT_ERROR_INFO_CLASSNAME = ClassName.get( "react4j", "ReactErrorInfo" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
  private static final ClassName CONTEXT_CLASSNAME = ClassName.get( "react4j", "Context" );
  private static final ClassName CONTEXTS_CLASSNAME = ClassName.get( "react4j", "Contexts" );
  private static final ClassName VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME =
    ClassName.get( "react4j.internal", "ViewConstructorFunction" );
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
  private static final ClassName NATIVE_VIEW_CLASSNAME = ClassName.get( "react4j.internal", "NativeView" );
  private static final ClassName VIEW_STATE_CLASSNAME = ClassName.get( "react4j.internal", "ViewState" );
  private static final ClassName SCHEDULER_UTIL_CLASSNAME = ClassName.get( "react4j.internal", "SchedulerUtil" );
  private static final ClassName INTROSPECT_UTIL_CLASSNAME = ClassName.get( "react4j.internal", "IntrospectUtil" );
  private static final String FRAMEWORK_INTERNAL_PREFIX = "$$react4j$$_";
  private static final String RENDER_METHOD = FRAMEWORK_INTERNAL_PREFIX + "render";
  private static final String SHOULD_COMPONENT_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "shouldComponentUpdate";
  private static final String COMPONENT_PRE_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentPreUpdate";
  private static final String COMPONENT_DID_UPDATE_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidUpdate";
  private static final String COMPONENT_DID_MOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentDidMount";
  private static final String COMPONENT_WILL_UNMOUNT_METHOD = FRAMEWORK_INTERNAL_PREFIX + "componentWillUnmount";
  private static final String VALIDATE_INPUTS_METHOD = FRAMEWORK_INTERNAL_PREFIX + "validateInputValues";
  private static final String STATE_FIELD = FRAMEWORK_INTERNAL_PREFIX + "state";
  private static final String VIEW_FIELD = FRAMEWORK_INTERNAL_PREFIX + "view";
  private static final String IS_READY_METHOD = FRAMEWORK_INTERNAL_PREFIX + "isReady";
  private static final String NATIVE_VIEW_FIELD = FRAMEWORK_INTERNAL_PREFIX + "nativeView";
  private static final String FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX = "$$react4j_immutable_input$$_";

  private ViewGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final ViewDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getEnhancedClassName() );
    builder.addTypeVariables( GeneratorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );
    final TypeElement typeElement = descriptor.getElement();
    GeneratorUtil.copyWhitelistedAnnotations( typeElement,
                                              builder,
                                              Collections.singletonList( Deprecated.class.getName() ) );

    builder.superclass( descriptor.getViewType() );

    final AnnotationSpec suppression =
      SuppressWarningsUtil.maybeSuppressWarningsAnnotation( descriptor.viewAccessesDeprecatedElements() ?
                                                            "deprecation" :
                                                            null );
    if ( null != suppression )
    {
      builder.addAnnotation( suppression );
    }
    if ( !descriptor.trackRender() )
    {
      builder.addAnnotation( AnnotationSpec
                               .builder( SUPPRESS_AREZ_WARNINGS_CLASSNAME )
                               .addMember( "value", "$S", "Arez:UnnecessaryAllowEmpty" )
                               .build() );
    }
    final AnnotationSpec.Builder arezAnnotation =
      AnnotationSpec
        .builder( AREZ_COMPONENT_CLASSNAME ).
        addMember( "name", "$S", typeElement.getQualifiedName().toString().replace( ".", "_" ) ).
        addMember( "disposeNotifier", "$T.DISABLE", AREZ_FEATURE_CLASSNAME ).
        addMember( "sting", "$T.DISABLE", AREZ_FEATURE_CLASSNAME );
    if ( !descriptor.trackRender() )
    {
      arezAnnotation.addMember( "allowEmpty", "true" );
    }
    else if ( descriptor.shouldSetDefaultPriority() )
    {
      arezAnnotation.addMember( "defaultPriority", "$T.LOWEST", PRIORITY_CLASSNAME );
    }
    builder.addAnnotation( arezAnnotation.build() );
    builder.addModifiers( Modifier.ABSTRACT );

    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, React4jProcessor.class.getName() );
    GeneratorUtil.addOriginatingTypes( typeElement, builder );

    if ( !descriptor.getPublishDescriptors().isEmpty() )
    {
      builder.addType( buildContextHolder( descriptor ) );
    }

    builder.addField( FieldSpec
                        .builder( NATIVE_VIEW_CLASSNAME,
                                  NATIVE_VIEW_FIELD,
                                  Modifier.PRIVATE,
                                  Modifier.FINAL )
                        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                        .build() );

    for ( final InputDescriptor input : descriptor.getImmutableInputs() )
    {
      builder.addField( buildImmutableField( input ) );
    }

    builder.addMethod( buildConstructor( processingEnv, descriptor ).build() );

    for ( final ScheduleRenderDescriptor element : descriptor.getScheduleRenderDescriptors() )
    {
      final MethodSpec.Builder method = GeneratorUtil.overrideMethod( processingEnv, typeElement, element.getMethod() );
      if ( element.skipShouldViewUpdate() )
      {
        method.addStatement( "$N.forceUpdate()", NATIVE_VIEW_FIELD );
      }
      else
      {
        method.addStatement( "$N.setState( $T.of() )", NATIVE_VIEW_FIELD, JS_PROPERTY_MAP_CLASSNAME );
      }
      builder.addMethod( method.build() );
    }
    if ( descriptor.trackRender() )
    {
      builder.addField( FieldSpec.builder( TypeName.INT, STATE_FIELD, Modifier.PRIVATE ).build() );
    }

    builder.addType( buildFactory() );
    if ( !descriptor.getInputs().isEmpty() )
    {
      builder.addType( buildInputsType( descriptor ) );
    }

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    if ( descriptor.getInputs()
      .stream()
      .anyMatch( InputDescriptor::needsMutableInputAccessedInPostConstructInvariant ) )
    {
      builder.addMethod( buildIsReadyMethod().build() );
    }

    for ( final InputDescriptor input : descriptor.getInputs() )
    {
      builder.addMethod( buildInputMethod( input ).build() );
      if ( input.isObservable() )
      {
        builder.addMethod( buildInputObservableValueRefMethod( input ).build() );
      }
    }

    if ( descriptor.shouldValidateInputs() )
    {
      builder.addMethod( buildInputValidatorMethod( descriptor ).build() );
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

    if ( descriptor.shouldGenerateRender() )
    {
      builder.addMethod( buildRender( descriptor ).build() );
    }

    if ( descriptor.trackRender() )
    {
      builder.addField( FieldSpec.builder( TypeName.BOOLEAN,
                                           FRAMEWORK_INTERNAL_PREFIX + "scheduledDebugStateUpdate",
                                           Modifier.PRIVATE ).build() );
      builder.addMethod( buildOnRenderDepsChange( descriptor ).build() );
      builder.addMethod( buildGetRenderObserver( descriptor ).build() );
      builder.addMethod( buildGetComponentId().build() );
      builder.addMethod( buildGetComponentName().build() );
      builder.addMethod( buildStoreDebugDataAsState().build() );
    }

    if ( descriptor.shouldGenerateLiteLifecycle() )
    {
      builder.addType( buildNativeView( descriptor, true ) );
    }
    builder.addType( buildNativeView( descriptor, false ) );

    return builder.build();
  }

  @Nonnull
  private static FieldSpec buildImmutableField( @Nonnull final InputDescriptor input )
  {
    final FieldSpec.Builder field =
      FieldSpec.builder( TypeName.get( input.getMethodType().getReturnType() ),
                         FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX + input.getName(),
                         Modifier.FINAL );

    if ( input.isDependency() )
    {
      final Element inputType = input.getInputType();
      final boolean isTypeCompatible =
        null != inputType &&
        (
          (
            ElementKind.CLASS == inputType.getKind() &&
            AnnotationsUtil.hasAnnotationOfType( inputType, Constants.AREZ_COMPONENT_CLASSNAME )
          ) ||
          (
            ( ElementKind.CLASS == inputType.getKind() || ElementKind.INTERFACE == inputType.getKind() ) &&
            AnnotationsUtil.hasAnnotationOfType( inputType, Constants.ACT_AS_COMPONENT_CLASSNAME )
          )
        );
      final AnnotationSpec.Builder annotation = AnnotationSpec.builder( COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME );
      if ( !isTypeCompatible )
      {
        annotation.addMember( "validateTypeAtRuntime", "true" );
      }
      field.addAnnotation( annotation.build() );
    }
    else
    {
      field.addModifiers( Modifier.PRIVATE );
      field.addAnnotation( AnnotationSpec.builder( SuppressWarnings.class )
                             .addMember( "value", "$S", "Arez:UnmanagedComponentReference" )
                             .build() );
    }

    GeneratorUtil.copyWhitelistedAnnotations( input.getMethod(), field );
    return field.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructor( @Nonnull final ProcessingEnvironment processingEnv,
                                                      @Nonnull final ViewDescriptor descriptor )
  {
    final ParameterSpec.Builder componentParameter =
      ParameterSpec
        .builder( NATIVE_VIEW_CLASSNAME, NATIVE_VIEW_FIELD, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
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
        GeneratorUtil.copyWhitelistedAnnotations( element, ctorParameter );
        SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, ctor, element.asType() );

        ctor.addParameter( ctorParameter.build() );
      }
      sb.append( " )" );
      ctor.addStatement( sb.toString(), params.toArray() );
    }

    ctor.addStatement( "this.$N = $T.requireNonNull( $N )",
                       NATIVE_VIEW_FIELD,
                       Objects.class,
                       NATIVE_VIEW_FIELD );

    for ( final InputDescriptor input : descriptor.getImmutableInputs() )
    {
      final ExecutableType methodType = input.getMethodType();
      final ExecutableElement methodElement = input.getMethod();
      final String convertMethodName = getConverter( methodType.getReturnType(), methodElement );
      final TypeKind resultKind = methodElement.getReturnType().getKind();
      if ( !resultKind.isPrimitive() && !AnnotationsUtil.hasNonnullAnnotation( methodElement ) )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
        block.addStatement( "$N = null != $N.inputs().getAsAny( Inputs.$N ) ? " +
                            "$N.inputs().getAsAny( Inputs.$N ).$N() : null",
                            FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX + input.getName(),
                            NATIVE_VIEW_FIELD,
                            input.getConstantName(),
                            NATIVE_VIEW_FIELD,
                            input.getConstantName(),
                            convertMethodName );
        block.nextControlFlow( "else" );
        block.addStatement( "$N = $T.uncheckedCast( $N.inputs().getAsAny( Inputs.$N ) )",
                            FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX + input.getName(),
                            JS_CLASSNAME,
                            NATIVE_VIEW_FIELD,
                            input.getConstantName() );
        block.endControlFlow();
        ctor.addCode( block.build() );
      }
      else
      {
        ctor.addStatement( "$N = $N.inputs().getAsAny( Inputs.$N ).$N()",
                           FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX + input.getName(),
                           NATIVE_VIEW_FIELD,
                           input.getConstantName(),
                           convertMethodName );
      }
    }
    return ctor;
  }

  @Nonnull
  private static FieldSpec.Builder buildInputKeyConstantField( @Nonnull final InputDescriptor descriptor,
                                                               final int index )
  {
    final String name = descriptor.getName();

    final FieldSpec.Builder field =
      FieldSpec.builder( TypeName.get( String.class ),
                         descriptor.getConstantName(),
                         Modifier.STATIC,
                         Modifier.FINAL );
    if ( descriptor.isSpecialChildrenInput() )
    {
      return field.initializer( "$S", "children" );
    }
    else
    {
      return field.initializer( "$T.shouldMinimizeInputKeys() ? $S : $S",
                                REACT_CLASSNAME,
                                Character.toString( (char) ( 'a' + index ) ),
                                name );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildInputMethod( @Nonnull final InputDescriptor input )
  {
    final ExecutableElement methodElement = input.getMethod();
    final ExecutableType methodType = input.getMethodType();
    final TypeMirror returnType = methodType.getReturnType();
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( methodElement.getSimpleName().toString() ).
        returns( TypeName.get( returnType ) );
    GeneratorUtil.copyTypeParameters( methodType, method );
    GeneratorUtil.copyAccessModifiers( methodElement, method );
    GeneratorUtil.copyWhitelistedAnnotations( methodElement, method );

    method.addAnnotation( Override.class );

    if ( input.isObservable() )
    {
      final AnnotationSpec.Builder annotation =
        AnnotationSpec.builder( OBSERVABLE_ANNOTATION_CLASSNAME ).
          addMember( "name", "$S", input.getName() ).
          addMember( "expectSetter", "false" ).
          addMember( "readOutsideTransaction", "$T.ENABLE", AREZ_FEATURE_CLASSNAME );
      method.addAnnotation( annotation.build() );
    }

    if ( input.needsMutableInputAccessedInPostConstructInvariant() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
      block.addStatement( "$T.apiInvariant( () -> $N(), " +
                          "() -> \"The view '\" + this + \"' accessed the input named '" + input.getName() +
                          "' before the view is ready (possibly in a @PostConstruct annotated method?) and " +
                          "does not have a @OnInputChange annotated method to cover the input and reflect changes " +
                          "of the input onto the view. This is considered a likely bug and the @Input should be " +
                          "made immutable or an @OnInputChange method added to cover the input. " +
                          MemberChecks.suppressedBy( Constants.WARNING_MUTABLE_INPUT_ACCESSED_IN_POST_CONSTRUCT,
                                                     Constants.SUPPRESS_REACT4J_WARNINGS_CLASSNAME ).
                            replace( "\"", "\\\"" ) + " to the @Input annotated method.\" )",
                          GUARDS_CLASSNAME,
                          IS_READY_METHOD );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    if ( input.isImmutable() )
    {
      method.addStatement( "return $N", FRAMEWORK_INTERNAL_IMMUTABLE_INPUT_PREFIX + input.getName() );
    }
    else
    {
      final String convertMethodName = getConverter( returnType, methodElement );
      final TypeKind resultKind = methodElement.getReturnType().getKind();
      if ( !resultKind.isPrimitive() && !AnnotationsUtil.hasNonnullAnnotation( methodElement ) )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );
        block.addStatement( "return null != $N.inputs().getAsAny( Inputs.$N ) ? " +
                            "$N.inputs().getAsAny( Inputs.$N ).$N() : null",
                            NATIVE_VIEW_FIELD,
                            input.getConstantName(),
                            NATIVE_VIEW_FIELD,
                            input.getConstantName(),
                            convertMethodName );
        block.nextControlFlow( "else" );
        block.addStatement( "return $T.uncheckedCast( $N.inputs().getAsAny( Inputs.$N ) )",
                            JS_CLASSNAME,
                            NATIVE_VIEW_FIELD,
                            input.getConstantName() );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      else
      {
        method.addStatement( "return $N.inputs().getAsAny( Inputs.$N ).$N()",
                             NATIVE_VIEW_FIELD,
                             input.getConstantName(),
                             convertMethodName );
      }
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
      case ARRAY:
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
      default:
        throw new ProcessorException( "Return type of @Input method is not yet " +
                                      "handled. Type: " + type.getKind(), element );
    }
  }

  @Nonnull
  private static MethodSpec.Builder buildInputObservableValueRefMethod( @Nonnull final InputDescriptor input )
  {
    return MethodSpec.methodBuilder( toObservableValueRefMethodName( input ) ).
      addModifiers( Modifier.ABSTRACT ).
      addAnnotation( GeneratorUtil.NONNULL_CLASSNAME ).
      addAnnotation( OBSERVABLE_VALUE_REF_ANNOTATION_CLASSNAME ).
      returns( ParameterizedTypeName.get( OBSERVABLE_CLASSNAME, WildcardTypeName.subtypeOf( TypeName.OBJECT ) ) );
  }

  @Nonnull
  private static String toObservableValueRefMethodName( @Nonnull final InputDescriptor input )
  {
    final String name = input.getName();
    return "get" + Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 ) + "ObservableValue";
  }

  private static void buildOnInputChangeInvocations( @Nonnull final CodeBlock.Builder code,
                                                     @Nonnull final List<OnInputChangeDescriptor> onInputChanges )
  {
    // The list of inputs we need to check for changes
    final List<InputDescriptor> inputs =
      onInputChanges.stream().flatMap( d -> d.getInputs().stream() ).distinct().toList();

    for ( final InputDescriptor input : inputs )
    {
      code.addStatement( "final boolean $N = !$T.isTripleEqual( inputs.get( Inputs.$N ), prevInputs.get( Inputs.$N ) )",
                         input.getName(),
                         JS_CLASSNAME,
                         input.getConstantName(),
                         input.getConstantName() );
    }
    for ( final OnInputChangeDescriptor onInputChange : onInputChanges )
    {
      final CodeBlock.Builder onChangeBlock = CodeBlock.builder();
      onChangeBlock.beginControlFlow( "if ( " +
                                      onInputChange.getInputs()
                                        .stream()
                                        .map( InputDescriptor::getName )
                                        .collect( Collectors.joining( " && " ) ) + " )" );
      final StringBuilder sb = new StringBuilder();
      final ArrayList<Object> params = new ArrayList<>();
      sb.append( "$N( " );
      params.add( onInputChange.getMethod().getSimpleName().toString() );
      boolean requireComma = false;
      for ( final InputDescriptor input : onInputChange.getInputs() )
      {
        if ( requireComma )
        {
          sb.append( ", " );
        }
        requireComma = true;
        final String convertMethodName = getConverter( input.getMethod().getReturnType(), input.getMethod() );
        final TypeKind resultKind = input.getMethod().getReturnType().getKind();
        if ( !resultKind.isPrimitive() && !input.isNonNull() )
        {
          sb.append( "$T.uncheckedCast( inputs.getAsAny( Inputs.$N ) )" );
          params.add( JS_CLASSNAME );
          params.add( input.getConstantName() );
        }
        else
        {
          sb.append( "inputs.getAsAny( Inputs.$N ).$N()" );
          params.add( input.getConstantName() );
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
  private static MethodSpec.Builder buildComponentDidMount( @Nonnull final ViewDescriptor descriptor )
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
    if ( descriptor.trackRender() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldStoreDebugDataAsState() && $T.areSpiesEnabled() )",
                              REACT_CLASSNAME,
                              AREZ_CLASSNAME );
      block.addStatement( "$N()", FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState" );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildShouldComponentUpdate( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( SHOULD_COMPONENT_UPDATE_METHOD )
        .returns( TypeName.BOOLEAN )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextInputs", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
                         .build() );

    final List<InputDescriptor> observableInputs =
      descriptor.getInputs()
        .stream()
        .filter( InputDescriptor::isObservable )
        .toList();

    if ( !observableInputs.isEmpty() )
    {
      method.addAnnotation( AnnotationSpec.builder( ACTION_CLASSNAME ).addMember( "verifyRequired", "false" ).build() );
    }
    else
    {
      method.addModifiers( Modifier.PRIVATE );
    }

    method.addStatement( "assert null != nextInputs" );

    if ( descriptor.shouldValidateInputs() )
    {
      final CodeBlock.Builder validateBlock = CodeBlock.builder();
      validateBlock.beginControlFlow( "if ( $T.shouldValidateInputValues() )", REACT_CLASSNAME );
      validateBlock.addStatement( "$N( nextInputs )", VALIDATE_INPUTS_METHOD );
      validateBlock.endControlFlow();
      method.addCode( validateBlock.build() );
    }

    final List<InputDescriptor> updateOnChangeInputs =
      descriptor
        .getInputs()
        .stream()
        .filter( InputDescriptor::shouldUpdateOnChange )
        // Observable properties already checked above
        .filter( p -> !p.isObservable() )
        .toList();

    if ( observableInputs.isEmpty() && updateOnChangeInputs.isEmpty() )
    {
      if ( descriptor.trackRender() )
      {
        method.addStatement( "return $T.SCHEDULED == $N", VIEW_STATE_CLASSNAME, STATE_FIELD );
      }
      else
      {
        method.addStatement( "return false" );
      }
    }
    else
    {
      method.addStatement( "final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD );

      final boolean hasObservableInputsToUpdateOnChange =
        observableInputs.stream().anyMatch( InputDescriptor::shouldUpdateOnChange );

      if ( hasObservableInputsToUpdateOnChange )
      {
        method.addStatement( "boolean modified = false" );
      }

      for ( final InputDescriptor input : observableInputs )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( !$T.isTripleEqual( inputs.get( Inputs.$N ), nextInputs.get( Inputs.$N ) ) )",
                                JS_CLASSNAME,
                                input.getConstantName(),
                                input.getConstantName() );
        block.addStatement( "$N().reportChanged()", toObservableValueRefMethodName( input ) );
        if ( input.shouldUpdateOnChange() )
        {
          block.addStatement( "modified = true" );
        }
        block.endControlFlow();
        method.addCode( block.build() );
      }

      for ( final InputDescriptor input : updateOnChangeInputs )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( !$T.isTripleEqual( inputs.get( Inputs.$N ), nextInputs.get( Inputs.$N ) ) )",
                                JS_CLASSNAME,
                                input.getConstantName(),
                                input.getConstantName() );
        block.addStatement( "return true" );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      if ( hasObservableInputsToUpdateOnChange )
      {
        if ( descriptor.trackRender() )
        {
          method.addStatement( "return modified || $T.SCHEDULED == $N",
                               VIEW_STATE_CLASSNAME,
                               STATE_FIELD );
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
          method.addStatement( "return $T.SCHEDULED == $N", VIEW_STATE_CLASSNAME, STATE_FIELD );
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
  private static MethodSpec.Builder buildComponentPreUpdate( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_PRE_UPDATE_METHOD )
        .addModifiers( Modifier.PRIVATE )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
                         .build() );
    final boolean hasPreUpdateOnInputChange = descriptor.hasPreUpdateOnInputChange();
    if ( hasPreUpdateOnInputChange )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevInputs )" );
      block.addStatement( "final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD );
      buildOnInputChangeInvocations( block, descriptor.getPreUpdateOnInputChangeDescriptors() );
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
  private static MethodSpec.Builder buildComponentDidUpdate( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_DID_UPDATE_METHOD )
        .addModifiers( Modifier.PRIVATE );

    if ( descriptor.hasPostUpdateOnInputChange() )
    {
      method.addParameter( ParameterSpec
                             .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL )
                             .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
                             .build() );
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( null != prevInputs )" );
      block.addStatement( "final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, NATIVE_VIEW_FIELD );
      buildOnInputChangeInvocations( block, descriptor.getPostUpdateOnInputChangeDescriptors() );
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
    if ( descriptor.trackRender() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.shouldStoreDebugDataAsState() && $T.areSpiesEnabled() )",
                              REACT_CLASSNAME,
                              AREZ_CLASSNAME );
      block.addStatement( "$N()", FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState" );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildComponentWillUnmount( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( COMPONENT_WILL_UNMOUNT_METHOD )
        .addModifiers( Modifier.PRIVATE );

    // We always dispose here rather than checking hasArezElements()
    // as this code path is only invoked when there are Arez elements, when we are in non-production
    // mode (and thus this makes debugging easier). Thus no need to have a guard
    method.addStatement( "(($T) this).dispose()", descriptor.getArezClassName() );

    if ( descriptor.trackRender() )
    {
      method.addStatement( "$N = $T.UNMOUNTED", STATE_FIELD, VIEW_STATE_CLASSNAME );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildRender( @Nonnull final ViewDescriptor descriptor )
  {
    final ExecutableElement render = descriptor.hasRender() ? descriptor.getRender() : null;

    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( RENDER_METHOD )
        .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
        .returns( REACT_NODE_CLASSNAME );

    if ( descriptor.trackRender() )
    {
      final AnnotationSpec.Builder observe =
        AnnotationSpec
          .builder( OBSERVE_ANNOTATION_CLASSNAME )
          .addMember( "name", "$S", "render" )
          .addMember( "priority", "$T.LOW", PRIORITY_CLASSNAME )
          .addMember( "executor", "$T.EXTERNAL", EXECUTOR_CLASSNAME )
          // Needs AREZ_OR_NONE in scenario where all inputs are disposed and view
          // thus accesses no dependencies before exiting render
          .addMember( "depType", "$T.AREZ_OR_NONE", DEP_TYPE_CLASSNAME )
          .addMember( "observeLowerPriorityDependencies", "true" )
          .addMember( "reportResult", "false" );
      method.addAnnotation( observe.build() );

      method.addStatement( "$N = $T.IDLE", STATE_FIELD, VIEW_STATE_CLASSNAME );
    }
    method.addStatement( "assert $T.isNotDisposed( this )", DISPOSABLE_CLASSNAME );

    final List<InputDescriptor> disposableInputs =
      descriptor
        .getInputs()
        .stream()
        .filter( InputDescriptor::isDisposable )
        .filter( input -> !input.isDependency() )
        .toList();

    for ( final InputDescriptor input : disposableInputs )
    {
      final String varName = "$$react4jv$$_" + input.getMethod().getSimpleName();
      method.addStatement( "final $T $N = $N()",
                           input.getMethodType().getReturnType(),
                           varName,
                           input.getMethod().getSimpleName().toString() );
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isDisposed( $N ) )", DISPOSABLE_CLASSNAME, varName );
      block.addStatement( "return null" );
      block.endControlFlow();
      method.addCode( block.build() );
    }

    if ( descriptor.trackRender() )
    {
      method.addStatement( "$T.pauseUntilRenderLoopComplete()", SCHEDULER_UTIL_CLASSNAME );
    }

    final StringBuilder sb = new StringBuilder();
    final List<Object> args = new ArrayList<>();
    final List<PublishDescriptor> publishDescriptors = descriptor.getPublishDescriptors();
    for ( final PublishDescriptor publish : publishDescriptors )
    {
      sb.append( "$T.$N.provide( $N(), " );
      args.add( ClassName.bestGuess( "ContextHolder" ) );
      args.add( "CONTEXT_" + publish.getMethod().getSimpleName() );
      args.add( publish.getMethod().getSimpleName() );
    }
    for ( final RenderHookDescriptor hook : descriptor.getPreRenderDescriptors() )
    {
      method.addStatement( "$N()", hook.getMethod().getSimpleName().toString() );
    }

    final boolean isTrackingType = ViewType.TRACKING == descriptor.getType();
    final List<RenderHookDescriptor> postRenderDescriptors = descriptor.getPostRenderDescriptors();
    final boolean explicitReturnRequired = isTrackingType || !postRenderDescriptors.isEmpty();

    if ( null != render )
    {
      sb.append( "$N()" );
      args.add( render.getSimpleName().toString() );
    }
    else
    {
      sb.append( "null" );
    }

    final int publishCount = publishDescriptors.size();
    sb.append( " )".repeat( publishCount ) );

    if ( !explicitReturnRequired )
    {
      method.addStatement( "return " + sb, args.toArray() );
    }
    else
    {
      args.add( 0, REACT_NODE_CLASSNAME );
      method.addStatement( "final $T result = " + sb, args.toArray() );

      if ( isTrackingType )
      {
        final CodeBlock.Builder depCheckBlock = CodeBlock.builder();
        depCheckBlock.beginControlFlow( "if ( $T.shouldCheckInvariants() && $T.areSpiesEnabled() )",
                                        AREZ_CLASSNAME,
                                        AREZ_CLASSNAME );
        final String getObserverMethodName = FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver";
        depCheckBlock.addStatement( "$T.invariant( () -> !$N().getContext().getSpy()." +
                                    "asObserverInfo( $N() ).getDependencies().isEmpty(), " +
                                    "() -> \"View render completed on '\" + this + \"' without accessing " +
                                    "any Arez dependencies but has a type set to TRACKING. The render method " +
                                    "needs to access an Arez dependency or the type should be changed to " +
                                    "STATEFUL or MAYBE_TRACKING.\" )",
                                    GUARDS_CLASSNAME,
                                    getObserverMethodName,
                                    getObserverMethodName );
        depCheckBlock.endControlFlow();
        method.addCode( depCheckBlock.build() );
      }

      for ( final RenderHookDescriptor hook : postRenderDescriptors )
      {
        method.addStatement( "$N()", hook.getMethod().getSimpleName().toString() );
      }

      method.addStatement( "return result" );
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildGetComponentId()
  {
    return MethodSpec
      .methodBuilder( FRAMEWORK_INTERNAL_PREFIX + "getComponentId" )
      .addAnnotation( COMPONENT_ID_REF_ANNOTATION_CLASSNAME )
      .addModifiers( Modifier.ABSTRACT )
      .returns( TypeName.INT );
  }

  @Nonnull
  private static MethodSpec.Builder buildGetComponentName()
  {
    return MethodSpec
      .methodBuilder( FRAMEWORK_INTERNAL_PREFIX + "getComponentName" )
      .addAnnotation( COMPONENT_NAME_REF_ANNOTATION_CLASSNAME )
      .addModifiers( Modifier.ABSTRACT )
      .returns( ClassName.get( String.class ) );
  }

  @Nonnull
  private static MethodSpec.Builder buildGetRenderObserver(
    @Nonnull final ViewDescriptor descriptor )
  {
    assert descriptor.trackRender();
    return MethodSpec
      .methodBuilder( FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver" )
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
      .addAnnotation( AnnotationSpec.builder( OBSERVER_REF_ANNOTATION_CLASSNAME )
                        .addMember( "name", "$S", "render" )
                        .build() )
      .addModifiers( Modifier.ABSTRACT )
      .returns( OBSERVER_CLASSNAME );
  }

  @Nonnull
  private static MethodSpec.Builder buildStoreDebugDataAsState()
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( FRAMEWORK_INTERNAL_PREFIX + "storeDebugDataAsState" )
      .addModifiers( Modifier.PRIVATE );

    final CodeBlock.Builder block = CodeBlock.builder();
    final String flag = FRAMEWORK_INTERNAL_PREFIX + "scheduledDebugStateUpdate";
    block.beginControlFlow( "if ( $N )", flag );
    block.addStatement( "$N = false", flag );
    block.nextControlFlow( "else" );
    block.addStatement( "final $T newState = $T.of()",
                        JS_PROPERTY_MAP_T_OBJECT_CLASSNAME,
                        JS_PROPERTY_MAP_CLASSNAME );
    // Present id as state. Useful to track when instance ids change.
    block.addStatement( "newState.set( $S, $N() )", "Arez.id", FRAMEWORK_INTERNAL_PREFIX + "getComponentId" );
    block.addStatement( "newState.set( $S, $N() )", "Arez.name", FRAMEWORK_INTERNAL_PREFIX + "getComponentName" );

    block.addStatement( "$T.collectDependencyDebugData( $N(), newState )",
                        INTROSPECT_UTIL_CLASSNAME,
                        FRAMEWORK_INTERNAL_PREFIX + "getRenderObserver" );

    final CodeBlock.Builder onUpdateBlock = CodeBlock.builder();
    onUpdateBlock.beginControlFlow( "if ( $T.prepareStateUpdate( newState, $N.state() ) )",
                                    INTROSPECT_UTIL_CLASSNAME,
                                    NATIVE_VIEW_FIELD );
    onUpdateBlock.addStatement( "$N.setState( newState )", NATIVE_VIEW_FIELD );
    // Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
    onUpdateBlock.addStatement( "$N.forceUpdate()", NATIVE_VIEW_FIELD );
    onUpdateBlock.addStatement( "$N = true", flag );
    onUpdateBlock.endControlFlow();
    block.add( onUpdateBlock.build() );
    block.endControlFlow();

    method.addCode( block.build() );

    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildOnRenderDepsChange(
    @Nonnull final ViewDescriptor descriptor )
  {
    assert descriptor.trackRender();
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "onRenderDepsChange" );

    final CodeBlock.Builder outer = CodeBlock.builder();
    outer.beginControlFlow( "if ( $T.IDLE == $N )", VIEW_STATE_CLASSNAME, STATE_FIELD );
    outer.addStatement( "$N = $T.SCHEDULED", STATE_FIELD, VIEW_STATE_CLASSNAME );
    if ( descriptor.hasObservableInputs() )
    {
      outer.addStatement( "$N.setState( $T.of() )", NATIVE_VIEW_FIELD, JS_PROPERTY_MAP_CLASSNAME );
    }
    else
    {
      outer.addStatement( "$N.forceUpdate()", NATIVE_VIEW_FIELD );
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
  private static MethodSpec.Builder buildInputValidatorMethod(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( VALIDATE_INPUTS_METHOD ).
        addModifiers( Modifier.PRIVATE ).
        addParameter( ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "inputs", Modifier.FINAL ).
                        addAnnotation( GeneratorUtil.NONNULL_CLASSNAME ).build() );

    for ( final InputDescriptor input : descriptor.getInputs() )
    {
      final boolean requiresNonnullInvariant = input.isNonNull() && ( input.isRequired() || input.isContextSource() );
      if ( requiresNonnullInvariant || input.hasValidateMethod() )
      {
        final String name = input.getName();
        final String rawName = "raw$" + name;
        final String typedName = "typed$" + name;
        method.addStatement( "final $T $N = inputs.get( Inputs.$N )",
                             Object.class,
                             rawName,
                             input.getConstantName() );
        if ( requiresNonnullInvariant )
        {
          final CodeBlock.Builder block = CodeBlock.builder();
          block.beginControlFlow( "if ( $T.shouldCheckInvariants() )", REACT_CLASSNAME );

          if ( input.isContextSource() )
          {
            final String qualifier = input.getQualifier();
            if ( qualifier.isEmpty() )
            {
              block.addStatement( "$T.apiInvariant( () -> null != $N, () -> \"Context value of type $N is " +
                                  "missing when constructing view named '$N'. Ensure a parent view publishes " +
                                  "the value to the context.\" ) ",
                                  GUARDS_CLASSNAME,
                                  rawName,
                                  input.getMethodType().getReturnType().toString(),
                                  descriptor.getName() );
            }
            else
            {
              block.addStatement(
                "$T.apiInvariant( () -> null != $N, () -> \"Context value of type $N with qualifier " +
                "'$N' is missing when constructing view named '$N'. Ensure a parent view publishes " +
                "the value to the context.\" ) ",
                GUARDS_CLASSNAME,
                rawName,
                input.getMethodType().getReturnType().toString(),
                qualifier,
                descriptor.getName() );
            }
          }
          else
          {
            block.addStatement(
              "$T.apiInvariant( () -> null != $N, () -> \"Required input named '$N' is missing from " +
              "view named '$N' so it was either incorrectly omitted or a null value has been " +
              "incorrectly specified.\" ) ",
              GUARDS_CLASSNAME,
              rawName,
              input.getName(),
              descriptor.getName() );
          }
          block.endControlFlow();
          method.addCode( block.build() );
        }
        if ( input.hasValidateMethod() )
        {
          final CodeBlock.Builder block = CodeBlock.builder();
          block.beginControlFlow( "if ( null != $N )", rawName );
          final TypeMirror returnType = input.getMethodType().getReturnType();
          block.addStatement( "final $T $N = $T.$N( $N )",
                              returnType,
                              typedName,
                              JS_CLASSNAME,
                              getConverter( returnType, input.getMethod() ),
                              rawName );
          block.addStatement( "$N( $N )", input.getValidateMethod().getSimpleName().toString(), typedName );
          block.endControlFlow();
          method.addCode( block.build() );
        }
      }
    }
    return method;
  }

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstructorFunction" ).
        addAnnotation( GeneratorUtil.NONNULL_CLASSNAME ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME );

    final boolean shouldGenerateLiteLifecycle = descriptor.shouldGenerateLiteLifecycle();
    if ( shouldGenerateLiteLifecycle )
    {
      method.addStatement( "final $T viewConstructor = ( $T.shouldStoreDebugDataAsState() || " +
                           "$T.shouldValidateInputValues() ) ? $T::new : $T::new",
                           VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           REACT_CLASSNAME,
                           REACT_CLASSNAME,
                           ClassName.bestGuess( "NativeView" ),
                           ClassName.bestGuess( "LiteNativeView" ) );
    }
    else
    {
      method.addStatement( "final $T viewConstructor = $T::new",
                           VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME,
                           ClassName.bestGuess( "NativeView" ) );
    }
    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableViewNames() )", REACT_CLASSNAME );
    codeBlock.addStatement( "$T.asPropertyMap( viewConstructor ).set( \"displayName\", $S )",
                            JS_CLASSNAME,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );

    method.addStatement( "return viewConstructor" );
    return method;
  }

  @Nonnull
  private static TypeSpec buildInputsType(
    @Nonnull final ViewDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Inputs" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );

    // These fields have been moved to a separate class to avoid a <clinit> on containing class

    final List<InputDescriptor> inputs = descriptor.getInputs();
    final int inputCount = inputs.size();
    for ( int i = 0; i < inputCount; i++ )
    {
      builder.addField( buildInputKeyConstantField( inputs.get( i ), i ).build() );
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
    // every call to React_MyView to first check <clinit> has been invoked.
    final FieldSpec.Builder field =
      FieldSpec
        .builder( VIEW_CONSTRUCTOR_FUNCTION_CLASSNAME, "TYPE", Modifier.STATIC, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
        .initializer( "getConstructorFunction()" );
    builder.addField( field.build() );

    return builder.build();
  }

  @Nonnull
  private static TypeSpec buildNativeView(
    @Nonnull final ViewDescriptor descriptor, final boolean lite )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( ( lite ? "Lite" : "" ) + "NativeView" );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );
    builder.addModifiers( Modifier.STATIC );
    builder.addModifiers( Modifier.PRIVATE );

    builder.superclass( NATIVE_VIEW_CLASSNAME );
    builder.addTypeVariables( GeneratorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    final TypeName viewFieldType;
    if ( descriptor.getElement().getTypeParameters().isEmpty() )
    {
      viewFieldType = descriptor.getEnhancedClassName();
    }
    else
    {
      final TypeName[] typeNames =
        GeneratorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ).toArray( new TypeName[ 0 ] );
      viewFieldType = ParameterizedTypeName.get( descriptor.getEnhancedClassName(), typeNames );
    }

    builder.addField( FieldSpec
                        .builder( viewFieldType, VIEW_FIELD, Modifier.PRIVATE, Modifier.FINAL )
                        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
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
      final ParameterSpec.Builder inputs =
        ParameterSpec.builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "inputs", Modifier.FINAL ).
          addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().addParameter( inputs.build() ).addAnnotation( JS_CONSTRUCTOR_CLASSNAME );
      method.addStatement( "super( inputs )" );
      if ( descriptor.needsInjection() )
      {
        method.addStatement( "$N = $T.create( this )", VIEW_FIELD, descriptor.getFactoryClassName() );
      }
      else
      {
        final String infix = BuilderGenerator.asTypeArgumentsInfix( descriptor.getDeclaredType() );
        method.addStatement( "$N = new $T" + infix + "( this )", VIEW_FIELD, descriptor.getArezClassName() );
      }

      if ( descriptor.shouldValidateInputs() )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.shouldValidateInputValues() )", REACT_CLASSNAME );
        block.addStatement( "assert null != inputs" );
        block.addStatement( "$N.$N( inputs )", VIEW_FIELD, VALIDATE_INPUTS_METHOD );
        block.endControlFlow();
        method.addCode( block.build() );
      }

      builder.addMethod( method.build() );
    }

    if ( lite ? descriptor.generateComponentDidMountInLiteLifecycle() : descriptor.generateComponentDidMount() )
    {
      // We add this so the DevTool sees any debug data saved
      builder.addMethod( buildNativeViewDidMount( descriptor ) );
    }
    if ( lite ?
         descriptor.generateShouldComponentUpdateInLiteLifecycle() :
         descriptor.generateShouldComponentUpdate() )
    {
      builder.addMethod( buildNativeShouldComponentUpdate( descriptor ) );
    }
    if ( descriptor.generateComponentPreUpdate() )
    {
      builder.addMethod( buildNativeViewPreUpdate( descriptor ) );
    }
    if ( lite ? descriptor.generateComponentDidUpdateInLiteLifecycle() : descriptor.generateComponentDidUpdate() )
    {
      // We add this for Arez views so the DevTool sees any debug data saved
      builder.addMethod( buildNativeViewDidUpdate( descriptor ) );
    }
    if ( lite ? descriptor.generateComponentWillUnmountInLiteLifecycle() : descriptor.generateComponentWillUnmount() )
    {
      builder.addMethod( buildNativeViewWillUnmount( descriptor ) );
    }
    if ( descriptor.generateComponentDidCatch() )
    {
      builder.addMethod( buildNativeViewDidCatch( descriptor ).build() );
    }

    builder.addMethod( buildNativeRender( descriptor ) );

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildNativeRender(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "render" )
      .addAnnotation( Override.class )
      .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( REACT_NODE_CLASSNAME );
    if ( descriptor.shouldGenerateRender() )
    {
      if ( descriptor.hasDependencyInput() )
      {
        final CodeBlock.Builder block = CodeBlock.builder();
        block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
        block.addStatement( "return $N.$N()", VIEW_FIELD, RENDER_METHOD );
        block.nextControlFlow( "else" );
        block.addStatement( "return null" );
        block.endControlFlow();
        method.addCode( block.build() );
      }
      else
      {
        method.addStatement( "return $N.$N()", VIEW_FIELD, RENDER_METHOD );
      }
    }
    else
    {
      method.addStatement( "return null" );
    }
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildNativeViewDidMount(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "componentDidMount" )
        .addAnnotation( Override.class )
        .addModifiers( Modifier.FINAL, Modifier.PUBLIC );
    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      block.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_DID_MOUNT_METHOD );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_DID_MOUNT_METHOD );
    }
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildNativeShouldComponentUpdate(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "shouldComponentUpdate" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .returns( TypeName.BOOLEAN )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "nextInputs", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                       .build() );
    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      block.addStatement( "return $N.$N( nextInputs )", VIEW_FIELD, SHOULD_COMPONENT_UPDATE_METHOD );
      block.nextControlFlow( "else" );
      block.addStatement( "return false" );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "return $N.$N( nextInputs )", VIEW_FIELD, SHOULD_COMPONENT_UPDATE_METHOD );
    }
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildNativeViewPreUpdate(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "getSnapshotBeforeUpdate" )
        .addAnnotation( Override.class )
        .addAnnotation( GeneratorUtil.NULLABLE_CLASSNAME )
        .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
        .returns( TypeName.get( Object.class ) )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .build() )
        .addParameter( ParameterSpec
                         .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevState", Modifier.FINAL )
                         .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                         .build() );
    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      block.addStatement( "$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_PRE_UPDATE_METHOD );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_PRE_UPDATE_METHOD );
    }
    method.addStatement( "return null" );
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildNativeViewDidUpdate(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "componentDidUpdate" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addParameter( ParameterSpec
                       .builder( JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, "prevInputs", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                       .build() );
    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      if ( descriptor.hasPostUpdateOnInputChange() )
      {
        block.addStatement( "$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD );
      }
      else
      {
        block.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD );
      }
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      if ( descriptor.hasPostUpdateOnInputChange() )
      {
        method.addStatement( "$N.$N( prevInputs )", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD );
      }
      else
      {
        method.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_DID_UPDATE_METHOD );
      }
    }
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildNativeViewWillUnmount(
    @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "componentWillUnmount" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC );
    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      block.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_WILL_UNMOUNT_METHOD );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "$N.$N()", VIEW_FIELD, COMPONENT_WILL_UNMOUNT_METHOD );
    }
    return method.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildNativeViewDidCatch(
    @Nonnull final ViewDescriptor descriptor )
  {
    final ExecutableElement onError = descriptor.getOnError();
    assert null != onError;
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "componentDidCatch" )
      .addAnnotation( Override.class )
      .addModifiers( Modifier.FINAL, Modifier.PUBLIC )
      .addParameter( ParameterSpec.builder( JS_ERROR_CLASSNAME, "error", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                       .build() )
      .addParameter( ParameterSpec.builder( REACT_ERROR_INFO_CLASSNAME, "info", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
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

    if ( descriptor.hasDependencyInput() )
    {
      final CodeBlock.Builder block = CodeBlock.builder();
      block.beginControlFlow( "if ( $T.isNotDisposed( $N ) )", DISPOSABLE_CLASSNAME, VIEW_FIELD );
      block.addStatement( "$N.$N" + args, VIEW_FIELD, onError.getSimpleName() );
      block.endControlFlow();
      method.addCode( block.build() );
    }
    else
    {
      method.addStatement( "$N.$N" + args, VIEW_FIELD, onError.getSimpleName() );
    }
    return method;
  }

  @Nonnull
  private static TypeSpec buildContextHolder(
    @Nonnull final ViewDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "ContextHolder" );
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL );

    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    for ( final PublishDescriptor publish : descriptor.getPublishDescriptors() )
    {
      final TypeName type = TypeName.get( publish.getMethodType().getReturnType() ).box();
      final FieldSpec.Builder field = FieldSpec
        .builder( ParameterizedTypeName.get( CONTEXT_CLASSNAME, type ),
                  "CONTEXT_" + publish.getMethod().getSimpleName(),
                  Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
      final String qualifier = publish.getQualifier();
      if ( qualifier.isEmpty() )
      {
        field.initializer( "$T.get( $T.class )", CONTEXTS_CLASSNAME, type );
      }
      else
      {
        field.initializer( "$T.get( $T.class, $S )", CONTEXTS_CLASSNAME, type, qualifier );
      }
      builder.addField( field.build() );
    }

    return builder.build();
  }
}
