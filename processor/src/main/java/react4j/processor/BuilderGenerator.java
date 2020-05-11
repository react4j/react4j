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
                             @Nonnull final ComponentDescriptor descriptor )
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

    // first step which may be required prop, optional props, or build terminal step.
    buildStaticStepMethodMethods( processingEnv, descriptor, builder, steps.get( 0 ) );

    if ( descriptor.getProps().stream().anyMatch( PropDescriptor::isContextProp ) )
    {
      builder.addType( buildContextHolder( descriptor ) );
    }

    builder.addType( buildBuilder( processingEnv, descriptor, builderDescriptor ) );

    return builder.build();
  }

  private static void buildStaticStepMethodMethods( @Nonnull final ProcessingEnvironment processingEnv,
                                                    @Nonnull final ComponentDescriptor descriptor,
                                                    @Nonnull final TypeSpec.Builder builder,
                                                    @Nonnull final Step step )
  {
    for ( final StepMethod method : step.getMethods() )
    {
      builder.addMethod( buildStaticStepMethodMethod( processingEnv, descriptor, step, method ) );
    }
  }

  @Nonnull
  private static MethodSpec buildStaticNewBuilderMethod( @Nonnull final ComponentDescriptor descriptor )
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
                                                         @Nonnull final ComponentDescriptor descriptor,
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
      final ExecutableElement propMethod = stepMethod.getPropMethod();
      if ( null != propMethod )
      {
        GeneratorUtil.copyWhitelistedAnnotations( propMethod, parameter );
        final ExecutableType methodType = stepMethod.getPropMethodType();
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
  private static MethodSpec.Builder buildStepInterfaceMethod( @Nonnull final ComponentDescriptor descriptor,
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
      method.returns( parameterizeIfRequired( descriptor, ClassName.bestGuess( "Step" + returnIndex ) ) );
    }
  }

  @Nonnull
  private static TypeName parameterizeIfRequired( @Nonnull final ComponentDescriptor descriptor,
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
                                                     @Nonnull final ComponentDescriptor descriptor,
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
          final ExecutableType propMethodType = stepMethod.getPropMethodType();
          if ( null != propMethodType )
          {
            GeneratorUtil.copyTypeParameters( propMethodType, m );
          }
          if ( stepMethod.isChildrenIntrinsic() )
          {
            m.varargs();
          }
          final ParameterSpec.Builder parameter = ParameterSpec.builder( stepMethod.getType(), stepMethod.getName() );
          final ExecutableElement propMethod = stepMethod.getPropMethod();
          if ( null != propMethod )
          {
            GeneratorUtil.copyWhitelistedAnnotations( propMethod, parameter );
            final ExecutableType methodType = stepMethod.getPropMethodType();
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
                                                  @Nonnull final ComponentDescriptor descriptor,
                                                  @Nonnull final Step step,
                                                  @Nonnull final StepMethod stepMethod )
  {
    final MethodSpec.Builder method = MethodSpec.methodBuilder( stepMethod.getName() );
    method.addModifiers( Modifier.PUBLIC, Modifier.FINAL );
    method.addAnnotation( Override.class );
    method.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );

    final PropDescriptor prop = stepMethod.getProp();
    final ExecutableType propMethodType = stepMethod.getPropMethodType();
    if ( null != propMethodType )
    {
      GeneratorUtil.copyTypeParameters( propMethodType, method );
    }
    final ParameterSpec.Builder parameter =
      ParameterSpec.builder( stepMethod.getType(), stepMethod.getName(), Modifier.FINAL );
    final ExecutableElement propMethod = stepMethod.getPropMethod();
    if ( null != propMethod )
    {
      GeneratorUtil.copyWhitelistedAnnotations( propMethod, parameter );
      final ExecutableType methodType = stepMethod.getPropMethodType();
      assert null != methodType;
      SuppressWarningsUtil.addSuppressWarningsIfRequired( processingEnv, parameter, methodType.getReturnType() );
    }
    else if ( stepMethod.isChildrenStreamIntrinsic() )
    {
      parameter.addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
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
      method.addStatement( "children( $N.toArray( $T[]::new ) )",
                           stepMethod.getName(),
                           REACT_NODE_CLASSNAME );
    }
    else if ( stepMethod.isChildIntrinsic() )
    {
      assert null != propMethod;
      assert null != prop;
      if ( AnnotationsUtil.hasNonnullAnnotation( propMethod ) )
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
      if ( ( null != propMethod && AnnotationsUtil.hasNonnullAnnotation( propMethod ) ) &&
           !stepMethod.getType().isPrimitive() )
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
      .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
    return buildBuildMethodContent( descriptor, method, "_element" );
  }

  @Nonnull
  private static MethodSpec buildContextBuildStepImpl( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> contextProps =
      descriptor.getProps().stream().filter( PropDescriptor::isContextProp ).collect( Collectors.toList() );

    final int count = contextProps.size();

    final StringBuilder sb = new StringBuilder();
    final List<Object> args = new ArrayList<>();

    sb.append( "return " );

    for ( int i = 0; i < count; i++ )
    {
      final PropDescriptor prop = contextProps.get( i );
      sb.append( "$T.$N.consumer().render( $N -> " );
      args.add( ClassName.bestGuess( "ContextHolder" ) );
      args.add( "CONTEXT_" + prop.getConstantName() );
      args.add( "v" + i );
    }

    sb.append( "build( _element" );

    for ( int i = 0; i < count; i++ )
    {
      final PropDescriptor prop = contextProps.get( i );
      sb.append( ".prop( $T.Props.$N, $N )" );
      args.add( descriptor.getEnhancedClassName() );
      args.add( prop.getConstantName() );
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
  private static MethodSpec buildInternalBuildStepImpl( @Nonnull final ComponentDescriptor descriptor )
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
  private static MethodSpec buildBuildMethodContent( @Nonnull final ComponentDescriptor descriptor,
                                                     @Nonnull final MethodSpec.Builder method,
                                                     @Nonnull final String elementName )
  {
    final List<PropDescriptor> syntheticProps =
      descriptor.getProps().stream().filter( PropDescriptor::isImmutable ).collect( Collectors.toList() );
    if ( syntheticProps.size() > 1 )
    {
      method.addStatement( "final $T props = $N.props()", JS_PROPERTY_MAP_T_OBJECT_CLASSNAME, elementName );

      final StringBuilder sb = new StringBuilder();
      sb.append( "$N.setKey( $T.class.getName()" );
      final List<Object> params = new ArrayList<>();
      params.add( elementName );
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
      .addStatement( "$N.complete()", elementName )
      .addStatement( "return $N", elementName )
      .returns( REACT_NODE_CLASSNAME );
    return method.build();
  }

  @Nonnull
  private static TypeSpec buildContextHolder( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeSpec.Builder builder = TypeSpec.classBuilder( "ContextHolder" );
    GeneratorUtil.copyTypeParameters( descriptor.getElement(), builder );

    builder.addModifiers( Modifier.PRIVATE, Modifier.STATIC );

    builder.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    final List<PropDescriptor> contextProps =
      descriptor.getProps().stream().filter( PropDescriptor::isContextProp ).collect( Collectors.toList() );

    for ( final PropDescriptor prop : contextProps )
    {
      final TypeName type = TypeName.get( prop.getMethodType().getReturnType() ).box();
      final FieldSpec.Builder field = FieldSpec
        .builder( ParameterizedTypeName.get( CONTEXT_CLASSNAME, type ),
                  "CONTEXT_" + prop.getConstantName(),
                  Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
        .addAnnotation( GeneratorUtil.NONNULL_CLASSNAME );
      final String qualifier = prop.getQualifier();
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
                                        @Nonnull final ComponentDescriptor descriptor,
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

    final List<PropDescriptor> propsWithDefaults = descriptor.getProps()
      .stream()
      .filter( p -> p.hasDefaultField() || p.hasDefaultMethod() )
      .collect( Collectors.toList() );
    if ( !propsWithDefaults.isEmpty() )
    {
      final MethodSpec.Builder method = MethodSpec.constructorBuilder();
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
    if ( propsWithDefaults.isEmpty() )
    {
      field.initializer( "$T.createComponentElement( $T.Factory.TYPE )",
                         REACT_ELEMENT_CLASSNAME,
                         descriptor.getEnhancedClassName() );
    }
    builder.addField( field.build() );

    if ( descriptor.getProps().stream().anyMatch( PropDescriptor::isContextProp ) )
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

  static String asTypeArgumentsInfix( final DeclaredType declaredType )
  {
    final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    return typeArguments.isEmpty() ?
           "" :
           "<" + typeArguments.stream().map( TypeMirror::toString ).collect( Collectors.joining( ", " ) ) + ">";
  }

  @Nonnull
  private static BuilderDescriptor buildBuilderDescriptor( @Nonnull final ComponentDescriptor descriptor )
  {
    final BuilderDescriptor builder = new BuilderDescriptor();

    Step optionalPropStep = null;
    final List<PropDescriptor> props =
      descriptor.getProps().stream().filter( p -> !p.isContextProp() ).collect( Collectors.toList() );

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
        optionalPropStep.addMethod( prop, hasSingleOptional ? StepMethodType.TERMINATE : StepMethodType.STAY );
      }
      else
      {
        if ( null != optionalPropStep )
        {
          // Need this when we have children magic prop that is required that follows the optional props.
          optionalPropStep.addMethod( prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
          // This is when children are built up using child steps
          if ( prop.getName().equals( "children" ) )
          {
            addChildrenStreamPropStepMethod( optionalPropStep );
          }
          hasRequiredAfterOptional = true;
        }
        // Single method step
        final Step step = builder.addStep();
        step.addMethod( prop, isLast ? StepMethodType.TERMINATE : StepMethodType.ADVANCE );
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
      ParameterizedTypeName.get( ClassName.get( Stream.class ),
                                 WildcardTypeName.subtypeOf( REACT_NODE_CLASSNAME ) );

    //TODO: Replace this with prop enhancer
    step.addTerminalMethod( "children", "*children_stream*", typeName );
  }
}
