package react4j.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class BuilderGenerator
{
  private static final ClassName JS_ARRAY_CLASSNAME = ClassName.get( "elemental2.core", "JsArray" );
  private static final ClassName IDENTIFIABLE_CLASSNAME = ClassName.get( "arez.component", "Identifiable" );
  private static final ClassName REACT_ELEMENT_CLASSNAME = ClassName.get( "react4j", "ReactElement" );
  private static final ClassName CONTEXT_CLASSNAME = ClassName.get( "react4j", "Context" );
  private static final ClassName CONTEXTS_CLASSNAME = ClassName.get( "react4j", "Contexts" );
  private static final ClassName REACT_CLASSNAME = ClassName.get( "react4j", "React" );
  private static final ClassName KEYED_CLASSNAME = ClassName.get( "react4j", "Keyed" );
  private static final ClassName REACT_NODE_CLASSNAME = ClassName.get( "react4j", "ReactNode" );
  private static final ClassName JS_PROPERTY_MAP_CLASSNAME = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  private static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT_CLASSNAME =
    ParameterizedTypeName.get( JS_PROPERTY_MAP_CLASSNAME, TypeName.OBJECT );

  private BuilderGenerator()
  {
  }

  @Nonnull
  static TypeSpec buildType( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final ViewDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( descriptor.getBuilderClassName() );
    GeneratorUtil.addOriginatingTypes( descriptor.getElement(), builder );
    GeneratorUtil.addGeneratedAnnotation( processingEnv, builder, React4jProcessor.class.getName() );
    builder.addModifiers( Modifier.FINAL );
    GeneratorUtil.copyAccessModifiers( descriptor.getElement(), builder );
    GeneratorUtil.copyWhitelistedAnnotations( descriptor.getElement(), builder,
                                              Collections.singletonList( Deprecated.class.getName() ) );

    if ( descriptor.builderAccessesDeprecatedElements() )
    {
      builder.addAnnotation( SuppressWarningsUtil.suppressWarningsAnnotation( "deprecation" ) );
    }

    // Private constructor so can not instantiate
    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final BuilderDescriptor builderDescriptor = buildBuilderDescriptor( descriptor );

    final List<Step> steps = builderDescriptor.getSteps();

    builder.addMethod( buildStaticNewBuilderMethod( descriptor ) );

    for ( final Step step : steps )
    {
      builder.addType( buildBuilderStepInterface( processingEnv, descriptor, step ) );
    }

    // first step which may be required input, optional inputs, or build terminal step.
    buildStaticStepMethodMethods( processingEnv, descriptor, builder, steps.get( 0 ) );

    if ( descriptor.getInputs().stream().anyMatch( InputDescriptor::isContextSource ) )
    {
      builder.addType( buildContextHolder( descriptor ) );
    }

    builder.addType( buildBuilder( processingEnv, descriptor, builderDescriptor ) );

    return builder.build();
  }

  private static void buildStaticStepMethodMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                                    @Nonnull final ViewDescriptor descriptor,
                                                    @Nonnull final TypeSpec.Builder builder,
                                                    @Nonnull final Step step )
  {
    for ( final StepMethod method : step.getMethods() )
    {
      builder.addMethod( buildStaticStepMethodMethod( processingEnv, descriptor, step, method ) );
    }
  }

  @Nonnull
  private static MethodSpec buildStaticNewBuilderMethod( @Nonnull final ViewDescriptor descriptor )
  {
    final String infix = descriptor.getDeclaredType().getTypeArguments().isEmpty() ? "" : "<>";
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "newBuilder" )
      .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
      .returns( parameterizeIfRequired( descriptor, ClassName.bestGuess( "Step1" ) ) )
      .addStatement( "return new $T" + infix + "()", ClassName.bestGuess( "Builder" ) );
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), method );
    return method.build();
  }

  @Nonnull
  private static MethodSpec buildStaticStepMethodMethod( @Nonnull final ProcessingEnvironment processingEnv,
                                                         @Nonnull final ViewDescriptor descriptor,
                                                         @Nonnull final Step step,
                                                         @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( stepMethod.getName() ).
        addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );

    method.addModifiers( Modifier.STATIC );
    if ( descriptor.getDeclaredType().asElement().getModifiers().contains( Modifier.PUBLIC ) )
    {
      method.addModifiers( Modifier.PUBLIC );
    }
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), method );

    if ( stepMethod.isBuildIntrinsic() )
    {
      method.addStatement( "return newBuilder().build()" );
    }
    else
    {
      final ParameterSpec.Builder parameter =
        ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
      final ExecutableElement inputMethod = stepMethod.getMethod();
      if ( null != inputMethod )
      {
        GeneratorUtil.copyWhitelistedAnnotations( inputMethod, parameter );
        final ExecutableType methodType = stepMethod.getMethodType();
        assert null != methodType;
        SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, parameter, methodType.getReturnType() );
      }
      else if ( stepMethod.isChildrenStreamIntrinsic() )
      {
        parameter.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
      }
      method.addParameter( parameter.build() );

      final String infix = asTypeArgumentsInfix( descriptor.getDeclaredType() );
      if ( infix.isEmpty() )
      {
        // No type parameters
        method.addStatement( "return newBuilder().$N( $N )", stepMethod.getName(), stepMethod.getName() );
      }
      else
      {
        method.addStatement( "return $T." + infix + "newBuilder().$N( $N )",
                             descriptor.getBuilderClassName(),
                             stepMethod.getName(),
                             stepMethod.getName() );
      }
    }
    configureStepMethodReturns( descriptor, method, step, stepMethod.getStepMethodType() );
    return method.build();
  }

  @Nonnull
  private static MethodSpec.Builder buildStepInterfaceMethod( @Nonnull final ViewDescriptor descriptor,
                                                              @Nonnull final String name,
                                                              @Nonnull final Step step,
                                                              @Nonnull final StepMethodType stepMethodType,
                                                              @Nonnull final Consumer<MethodSpec.Builder> action )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( name );
    method.addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT );
    method.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
    action.accept( method );
    configureStepMethodReturns( descriptor, method, step, stepMethodType );
    return method;
  }

  private static void configureStepMethodReturns( @Nonnull final ViewDescriptor descriptor,
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
      method.returns( parameterizeIfRequired( descriptor, ClassName.bestGuess( "Step" + returnIndex ) ) );
    }
  }

  @Nonnull
  private static TypeName parameterizeIfRequired( @Nonnull final ViewDescriptor descriptor,
                                                  @Nonnull final ClassName className )
  {
    final List<TypeVariableName> variableNames =
      GeneratorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() );
    if ( variableNames.isEmpty() )
    {
      return className;
    }
    else
    {
      return ParameterizedTypeName.get( className, variableNames.toArray( new TypeName[ 0 ] ) );
    }
  }

  @Nonnull
  private static TypeSpec buildBuilderStepInterface( @Nonnull final ProcessingEnvironment processingEnv,
                                                     @Nonnull final ViewDescriptor descriptor,
                                                     @Nonnull final Step step )
  {
    final int stepIndex = step.getIndex();
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder( "Step" + stepIndex );
    builder.addModifiers( Modifier.PUBLIC, Modifier.STATIC );
    builder.addTypeVariables( GeneratorUtil.getTypeArgumentsAsNames( descriptor.getDeclaredType() ) );

    if ( !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      builder.addAnnotation( SuppressWarningsUtil.suppressWarningsAnnotation( "unused" ) );
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
          final ExecutableType inputMethodType = stepMethod.getMethodType();
          if ( null != inputMethodType )
          {
            GeneratorUtil.copyTypeParameters( inputMethodType, m );
          }
          if ( stepMethod.isChildrenIntrinsic() )
          {
            m.varargs();
          }
          final ParameterSpec.Builder parameter = ParameterSpec.builder( stepMethod.getType(), stepMethod.getName() );
          final ExecutableElement inputMethod = stepMethod.getMethod();
          if ( null != inputMethod )
          {
            GeneratorUtil.copyWhitelistedAnnotations( inputMethod, parameter );
            final ExecutableType methodType = stepMethod.getMethodType();
            assert null != methodType;
            SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, parameter, methodType.getReturnType() );
          }
          else if ( stepMethod.isChildrenStreamIntrinsic() )
          {
            parameter.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
          }
          m.addParameter( parameter.build() );
        } ).build() );
      }
    }

    return builder.build();
  }

  @Nonnull
  private static MethodSpec buildBuilderStepImpl( @Nonnull final ProcessingEnvironment processingEnv,
                                                  @Nonnull final ViewDescriptor descriptor,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( stepMethod.getName() );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( Override.class );
    method.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );

    final InputDescriptor input = stepMethod.getInput();
    final ExecutableType inputMethodType = stepMethod.getMethodType();
    if ( null != inputMethodType )
    {
      GeneratorUtil.copyTypeParameters( inputMethodType, method );
    }
    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
    final ExecutableElement inputMethod = stepMethod.getMethod();
    if ( null != inputMethod )
    {
      GeneratorUtil.copyWhitelistedAnnotations( inputMethod, parameter );
      final ExecutableType methodType = stepMethod.getMethodType();
      assert null != methodType;
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, parameter, methodType.getReturnType() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      parameter.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
    }
    method.addParameter( parameter.build() );

    if ( null != input && input.isImmutable() && 1 == descriptor.syntheticKeyParts() )
    {
      final ImmutableInputKeyStrategy strategy = input.getImmutableInputKeyStrategy();
      if ( ImmutableInputKeyStrategy.KEYED == strategy )
      {
        method.addStatement( "_element.setKey( $T.getKey( $N ) + " +
                             "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                             KEYED_CLASSNAME,
                             stepMethod.getName(),
                             REACT_CLASSNAME,
                             descriptor.keySuffix(),
                             descriptor.getClassName() );
      }
      else if ( ImmutableInputKeyStrategy.IS_STRING == strategy ||
                ImmutableInputKeyStrategy.TO_STRING == strategy ||
                ImmutableInputKeyStrategy.ENUM == strategy )
      {
        method.addStatement( "_element.setKey( $N + ( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                             stepMethod.getName(),
                             REACT_CLASSNAME,
                             descriptor.keySuffix(),
                             descriptor.getClassName() );
      }
      else if ( ImmutableInputKeyStrategy.DYNAMIC == strategy )
      {
        method.addStatement( "_element.setKey( ( $N instanceof $T ? $T.getKey( $N ) : " +
                             "$N instanceof $T ? $T.<$T>getArezId( $N ) : $T.valueOf( $N ) ) + " +
                             "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                             stepMethod.getName(),
                             KEYED_CLASSNAME,
                             KEYED_CLASSNAME,
                             stepMethod.getName(),
                             stepMethod.getName(),
                             IDENTIFIABLE_CLASSNAME,
                             IDENTIFIABLE_CLASSNAME,
                             Object.class,
                             stepMethod.getName(),
                             String.class,
                             stepMethod.getName(),
                             REACT_CLASSNAME,
                             descriptor.keySuffix(),
                             descriptor.getClassName() );
      }
      else
      {
        assert ImmutableInputKeyStrategy.AREZ_IDENTIFIABLE == strategy;
        method.addStatement( "_element.setKey( $T.<Object>getArezId( $N ) + " +
                             "( $T.enableViewNames() ? $S : $T.class.getName() ) )",
                             IDENTIFIABLE_CLASSNAME,
                             stepMethod.getName(),
                             REACT_CLASSNAME,
                             descriptor.keySuffix(),
                             descriptor.getClassName() );
      }
    }

    if ( stepMethod.isChildrenIntrinsic() )
    {
      method.varargs();
      assert null != input;
      method.addStatement( "_element.input( $T.Inputs.$N, $T.of( $N ) )",
                           descriptor.getEnhancedClassName(),
                           input.getConstantName(),
                           JS_ARRAY_CLASSNAME,
                           stepMethod.getName() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      method.addStatement( "children( $N.toArray( $T[]::new ) )",
                           stepMethod.getName(),
                           REACT_NODE_CLASSNAME );
    }
    else if ( stepMethod.isChildIntrinsic() )
    {
      assert null != inputMethod;
      assert null != input;
      if ( AnnotationsUtil.hasNonnullAnnotation( inputMethod ) )
      {
        method.addStatement( "_element.input( $T.Inputs.$N, $T.of( $T.requireNonNull( $N ) ) )",
                             descriptor.getEnhancedClassName(),
                             input.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             Objects.class,
                             stepMethod.getName() );
      }
      else
      {
        method.addStatement( "_element.input( $T.Inputs.$N, $T.of( $N ) )",
                             descriptor.getEnhancedClassName(),
                             input.getConstantName(),
                             JS_ARRAY_CLASSNAME,
                             stepMethod.getName() );
      }
    }
    else
    {
      if ( ( null != inputMethod && AnnotationsUtil.hasNonnullAnnotation( inputMethod ) ) &&
           !stepMethod.getType().isPrimitive() )
      {
        method.addStatement( "$T.requireNonNull( $N )", Objects.class, stepMethod.getName() );
      }
      assert null != input;
      method.addStatement( "_element.input( $T.Inputs.$N, $N )",
                           descriptor.getEnhancedClassName(),
                           input.getConstantName(),
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
  private static MethodSpec buildBuildStepImpl( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "build" )
      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
    return buildBuildMethodContent( descriptor, method, "_element" );
  }

  @Nonnull
  private static MethodSpec buildContextBuildStepImpl( @Nonnull final ViewDescriptor descriptor )
  {
    final List<InputDescriptor> contextInputs =
      descriptor.getInputs().stream().filter( InputDescriptor::isContextSource ).collect( Collectors.toList() );

    final int count = contextInputs.size();

    final StringBuilder sb = new StringBuilder();
    final List<Object> args = new ArrayList<>();

    sb.append( "return " );

    for ( int i = 0; i < count; i++ )
    {
      sb.append( "$T.$N.consumer().render( $N -> " );
      args.add( ClassName.bestGuess( "ContextHolder" ) );
      args.add( "CONTEXT_" + contextInputs.get( i ).getConstantName() );
      args.add( "v" + i );
    }

    sb.append( "build( _element" );

    for ( int i = 0; i < count; i++ )
    {
      sb.append( ".input( $T.Inputs.$N, $N )" );
      args.add( descriptor.getEnhancedClassName() );
      args.add( contextInputs.get( i ).getConstantName() );
      args.add( "v" + i );
    }

    sb.append( ".dup() )" );

    for ( int i = 0; i < count; i++ )
    {
      sb.append( " )" );
    }
    return MethodSpec
      .methodBuilder( "build" )
      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
      .addStatement( sb.toString(), args.toArray() )
      .returns( REACT_NODE_CLASSNAME )
      .build();
  }

  @Nonnull
  private static MethodSpec buildInternalBuildStepImpl( @Nonnull final ViewDescriptor descriptor )
  {
    final MethodSpec.Builder method = MethodSpec
      .methodBuilder( "build" )
      .addModifiers( Modifier.PRIVATE )
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
      .addParameter( ParameterSpec
                       .builder( REACT_ELEMENT_CLASSNAME, "element", Modifier.FINAL )
                       .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME )
                       .build() );
    return buildBuildMethodContent( descriptor, method, "element" );
  }

  @Nonnull
  private static MethodSpec buildBuildMethodContent( @Nonnull final ViewDescriptor descriptor,
                                                     @Nonnull final MethodSpec.Builder method,
                                                     @Nonnull final String elementName )
  {
    final List<InputDescriptor> syntheticInputs =
      descriptor.getInputs().stream().filter( InputDescriptor::isImmutable ).collect( Collectors.toList() );
    if ( syntheticInputs.size() > 1 )
    {
      method.addStatement( "final $T inputs = $N.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, elementName );

      for ( final InputDescriptor input : syntheticInputs )
      {
        if ( ImmutableInputKeyStrategy.DYNAMIC == input.getImmutableInputKeyStrategy() )
        {
          method.addStatement( "final $T $N = inputs.get( $T.Inputs.$N )",
                               Object.class,
                               "$" + input.getName() + "$",
                               descriptor.getEnhancedClassName(),
                               input.getConstantName() );
        }
      }

      final StringBuilder sb = new StringBuilder();
      sb.append( "$N.setKey( " );
      final List<Object> params = new ArrayList<>();
      params.add( elementName );
      boolean firstInput = true;
      for ( final InputDescriptor input : syntheticInputs )
      {
        if ( !firstInput )
        {
          sb.append( " + \"-\" + " );
        }
        else
        {
          firstInput = false;
        }
        final ImmutableInputKeyStrategy strategy = input.getImmutableInputKeyStrategy();
        if ( ImmutableInputKeyStrategy.KEYED == strategy )
        {
          sb.append( "$T.getKey( ($T) inputs.get( $T.Inputs.$N ) )" );
          params.add( KEYED_CLASSNAME );
          params.add( input.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( input.getConstantName() );
        }
        else if ( ImmutableInputKeyStrategy.IS_STRING == strategy || ImmutableInputKeyStrategy.ENUM == strategy )
        {
          sb.append( "( ($T) inputs.get( $T.Inputs.$N ) )" );
          params.add( input.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( input.getConstantName() );
        }
        else if ( ImmutableInputKeyStrategy.TO_STRING == strategy )
        {
          final TypeMirror inputType = input.getMethodType().getReturnType();
          final TypeKind kind = inputType.getKind();
          if ( TypeKind.INT == kind || TypeKind.SHORT == kind || TypeKind.BYTE == kind || TypeKind.FLOAT == kind )
          {
            sb.append( "$T.valueOf( ($T) (double) inputs.get( $T.Inputs.$N ) )" );
          }
          else
          {
            sb.append( "$T.valueOf( ($T) inputs.get( $T.Inputs.$N ) )" );
          }
          params.add( String.class );
          params.add( inputType );
          params.add( descriptor.getEnhancedClassName() );
          params.add( input.getConstantName() );
        }
        else if ( ImmutableInputKeyStrategy.DYNAMIC == strategy )
        {
          final String name = "$" + input.getName() + "$";
          sb.append( "( $N instanceof $T ? $T.getKey( $N ) : " +
                     "$N instanceof $T ? $T.<$T>getArezId( $N ) : " +
                     "$T.valueOf( $N ) )" );
          params.add( name );
          params.add( KEYED_CLASSNAME );
          params.add( KEYED_CLASSNAME );
          params.add( name );
          params.add( name );
          params.add( IDENTIFIABLE_CLASSNAME );
          params.add( IDENTIFIABLE_CLASSNAME );
          params.add( Object.class );
          params.add( name );
          params.add( String.class );
          params.add( name );
        }
        else
        {
          assert ImmutableInputKeyStrategy.AREZ_IDENTIFIABLE == strategy;
          sb.append( "$T.valueOf( $T.<Object>getArezId( ($T) inputs.get( $T.Inputs.$N ) ) )" );
          params.add( String.class );
          params.add( IDENTIFIABLE_CLASSNAME );
          params.add( input.getMethodType().getReturnType() );
          params.add( descriptor.getEnhancedClassName() );
          params.add( input.getConstantName() );
        }
      }
      sb.append( " + ( $T.enableViewNames() ? $S : $T.class.getName() ) )" );
      params.add( REACT_CLASSNAME );
      params.add( descriptor.keySuffix() );
      params.add( descriptor.getClassName() );
      method.addStatement( sb.toString(), params.toArray() );
    }

    method
      .addStatement( "$N.complete()", elementName )
      .addStatement( "return $N", elementName )
      .returns( REACT_NODE_CLASSNAME );
    return method.build();
  }

  @Nonnull
  private static TypeSpec buildContextHolder( @Nonnull final ViewDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "ContextHolder" );
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );

    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final List<InputDescriptor> contextInputs =
      descriptor.getInputs().stream().filter( InputDescriptor::isContextSource ).collect( Collectors.toList() );

    for ( final InputDescriptor input : contextInputs )
    {
      final TypeName type = TypeName.get( input.getMethodType().getReturnType() ).box();
      final FieldSpec.Builder field = FieldSpec
        .builder( ParameterizedTypeName.get( CONTEXT_CLASSNAME, type ),
                  "CONTEXT_" + input.getConstantName(),
                  Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
      final String qualifier = input.getQualifier();
      if ( "".equals( qualifier ) )
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

  @Nonnull
  private static TypeSpec buildBuilder( @Nonnull final ProcessingEnvironment processingEnv,
                                        @Nonnull final ViewDescriptor descriptor,
                                        @Nonnull final BuilderDescriptor builderDescriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "Builder" );
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), builder );
    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );
    final List<Step> steps = builderDescriptor.getSteps();
    for ( int i = 0; i < steps.size(); i++ )
    {
      builder.addSuperinterface( getParameterizedTypeName( descriptor, ClassName.bestGuess( "Step" + ( i + 1 ) ) ) );
    }

    final List<InputDescriptor> inputsWithDefaults = descriptor.getInputs()
      .stream()
      .filter( p -> p.hasDefaultField() || p.hasDefaultMethod() )
      .collect( Collectors.toList() );
    if ( !inputsWithDefaults.isEmpty() )
    {
      final MethodSpec.Builder method = MethodSpec.constructorBuilder();
      method.addStatement( "_element = $T.createViewElement( $T.Factory.TYPE )",
                           REACT_ELEMENT_CLASSNAME,
                           descriptor.getEnhancedClassName() );
      method.addStatement( "final $T inputs = _element.inputs()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME );
      for ( final InputDescriptor input : inputsWithDefaults )
      {
        method.addStatement( "inputs.set( $T.Inputs.$N, $T.$N" +
                             ( input.hasDefaultField() ? "" : "()" ) + " )",
                             descriptor.getEnhancedClassName(),
                             input.getConstantName(),
                             descriptor.getClassName(),
                             input.hasDefaultField() ?
                             input.getDefaultField().getSimpleName() :
                             input.getDefaultMethod().getSimpleName() );
      }

      builder.addMethod( method.build() );
    }

    final Set<String> stepMethodsAdded = new HashSet<>();
    for ( final Step step : steps )
    {
      for ( final StepMethod stepMethod : step.getMethods() )
      {
        if ( stepMethodsAdded.add( stepMethod.getName() + stepMethod.getType().toString() ) )
        {
          if ( !stepMethod.isBuildIntrinsic() )
          {
            builder.addMethod( buildBuilderStepImpl( processingEnv, descriptor, step, stepMethod ) );
          }
        }
      }
    }

    final FieldSpec.Builder field =
      FieldSpec.builder( REACT_ELEMENT_CLASSNAME, "_element", Modifier.PRIVATE, Modifier.FINAL );
    if ( inputsWithDefaults.isEmpty() )
    {
      field.initializer( "$T.createViewElement( $T.Factory.TYPE )",
                         REACT_ELEMENT_CLASSNAME,
                         descriptor.getEnhancedClassName() );
    }
    builder.addField( field.build() );

    if ( descriptor.getInputs().stream().anyMatch( InputDescriptor::isContextSource ) )
    {
      builder.addMethod( buildInternalBuildStepImpl( descriptor ) );
      builder.addMethod( buildContextBuildStepImpl( descriptor ) );
    }
    else
    {
      builder.addMethod( buildBuildStepImpl( descriptor ) );
    }

    return builder.build();
  }

  @Nonnull
  private static TypeName getParameterizedTypeName( @Nonnull final ViewDescriptor descriptor,
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

  static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
  }

  @Nonnull
  private static BuilderDescriptor buildBuilderDescriptor( @Nonnull final ViewDescriptor descriptor )
  {
    final BuilderDescriptor builder = new BuilderDescriptor();

    Step optionalInputStep = null;
    final List<InputDescriptor> inputs =
      descriptor.getInputs().stream().filter( p -> !p.isContextSource() ).collect( Collectors.toList() );

    final int inputsSize = inputs.size();

    final boolean hasSingleOptional = inputs.stream().filter( InputDescriptor::isOptional ).count() == 1;
    final boolean hasNonOptionalChild =
      inputs.stream().filter( i -> i.isSpecialChildrenInput() && !i.isOptional() ).count() == 1;
    boolean hasRequiredAfterOptional = false;
    for ( int i = 0; i < inputsSize; i++ )
    {
      final InputDescriptor input = inputs.get( i );
      final boolean isLast = i == inputsSize - 1;
      if ( input.isOptional() )
      {
        if ( null == optionalInputStep )
        {
          optionalInputStep = builder.addStep();
        }
        if ( input.getName().equals( "children" ) )
        {
          addChildrenStreamInputStepMethod( optionalInputStep );
        }
        if ( hasNonOptionalChild )
        {
          optionalInputStep.addMethod( input, hasSingleOptional ? StepMethodType.ADVANCE : StepMethodType.STAY );
        }
        else
        {
          optionalInputStep.addMethod( input, hasSingleOptional ? StepMethodType.TERMINATE : StepMethodType.STAY );
        }
      }
      else
      {
        if ( null != optionalInputStep )
        {
          // Need this when we have children magic input that is required that follows the optional inputs.
          optionalInputStep.addMethod( input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
          // This is when children are built up using child steps
          if ( input.getName().equals( "children" ) )
          {
            addChildrenStreamInputStepMethod( optionalInputStep );
          }
          hasRequiredAfterOptional = true;
        }
        // Single method step
        final Step step = builder.addStep();
        step.addMethod( input, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
        if ( input.getName().equals( "children" ) )
        {
          addChildrenStreamInputStepMethod( step );
          addBuildStep( step );
        }
      }
    }
    if ( null != optionalInputStep && !hasRequiredAfterOptional )
    {
      addBuildStep( optionalInputStep );
    }
    if ( inputs.isEmpty() )
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
  private static void addChildrenStreamInputStepMethod( @Nonnull final Step step )
  {
    final ParameterizedTypeName typeName =
      ParameterizedTypeName.get( ClassName.get( Stream.class ),
                                 WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );

    //TODO: Replace this with input enhancer
    step.addTerminalMethod( "children", "*children_stream*", typeName );
  }
}
