package react.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.NativeAdapterComponent;
import react.core.ReactConfig;

final class Generator
{
  private Generator()
  {
  }

  @Nonnull
  static TypeSpec buildConstructorFactory( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getConstructorFactoryName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    ProcessorUtil.copyAccessModifiers( element, builder );

    //Ensure it can not be subclassd
    builder.addModifiers( Modifier.FINAL );

    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    final FieldSpec.Builder field =
      FieldSpec.builder( getJsConstructorFnType( descriptor ),
                         "TYPE",
                         Modifier.STATIC,
                         Modifier.FINAL,
                         Modifier.PUBLIC ).
        initializer( "getConstrutorFunction()" );
    builder.addField( field.build() );

    builder.addMethod( buildConstructorFnMethod( descriptor ).build() );

    return builder.build();
  }

  @Nonnull
  private static ParameterizedTypeName getJsConstructorFnType( final @Nonnull ComponentDescriptor descriptor )
  {
    return ParameterizedTypeName.get( ClassName.get( JsConstructorFn.class ),
                                      descriptor.getNativeComponentClassName() );
  }

  {

  @Nonnull
  private static MethodSpec.Builder buildConstructorFnMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    final ParameterizedTypeName constructorType = getJsConstructorFnType( descriptor );

    final MethodSpec.Builder method =
      MethodSpec.methodBuilder( "getConstrutorFunction" ).
        addAnnotation( Nonnull.class ).
        addModifiers( Modifier.STATIC, Modifier.PRIVATE ).
        returns( constructorType );

    method.addStatement( "final $T constructorFn = $T.of( $T.class ) ",
                         constructorType,
                         JsConstructorFn.class,
                         descriptor.getNativeComponentClassName() );
    method.addStatement( "$T.invariant( () -> null != constructorFn,\n" +
                         "              () -> \"Unable to locate constructor function for " +
                         descriptor.getName() + " defined by class " + element.getQualifiedName().toString() + "\" )",
                         Guards.class );
    method.addStatement( "assert null != constructorFn" );

    final CodeBlock.Builder codeBlock = CodeBlock.builder();
    codeBlock.beginControlFlow( "if ( $T.enableComponentNames() )", ReactConfig.class );
    codeBlock.addStatement( "$T.of( constructorFn ).set( \"displayName\", $S )",
                            JsPropertyMap.class,
                            descriptor.getName() );
    codeBlock.endControlFlow();

    method.addCode( codeBlock.build() );
    method.addStatement( "return constructorFn" );
    return method;
  }

  @Nonnull
  static TypeSpec buildNativeComponent( @Nonnull final ComponentDescriptor descriptor )
  {
    final TypeElement element = descriptor.getElement();

    final String name = descriptor.getNestedClassPrefix() + descriptor.getNativeComponentName();
    final TypeSpec.Builder builder = TypeSpec.classBuilder( name );

    //Ensure it can not be subclassed
    builder.addModifiers( Modifier.FINAL );

    builder.addAnnotation( AnnotationSpec.builder( JsType.class ).build() );
    // Mark it as generated
    builder.addAnnotation( AnnotationSpec.builder( Generated.class ).
      addMember( "value", "$S", ReactProcessor.class.getName() ).
      build() );

    final TypeName superType =
      ParameterizedTypeName.get( ClassName.get( NativeAdapterComponent.class ),
                                 ClassName.get( descriptor.getPropsType().asType() ),
                                 ClassName.get( descriptor.getStateType().asType() ),
                                 ClassName.get( descriptor.getElement() ) );

    builder.superclass( superType );

    // build the constructor
    {
      final ParameterSpec.Builder props =
        ParameterSpec.builder( ClassName.get( descriptor.getPropsType() ), "props", Modifier.FINAL ).
          addAnnotation( Nonnull.class );
      final MethodSpec.Builder method =
        MethodSpec.constructorBuilder().
          addAnnotation( JsConstructor.class ).
          addModifiers( Modifier.PRIVATE ).
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
      final ClassName className = ClassName.get( descriptor.getElement() );
      final ArrayList<String> names = new ArrayList<>( className.simpleNames() );
      final String cname = ( descriptor.isArezComponent() ? "Arez_" : "" ) + element.getSimpleName();
      final ClassName target;
      if ( names.size() > 1 )
      {
        names.remove( names.size() - 1 );
        names.add( cname );
        target =
          ClassName.get( className.packageName(),
                         names.get( 0 ),
                         names.subList( 1, names.size() ).toArray( new String[ 0 ] ) );
      }
      else
      {
        target = ClassName.get( className.packageName(), cname );
      }
      method.addStatement( "return new $T()", target );
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
}
