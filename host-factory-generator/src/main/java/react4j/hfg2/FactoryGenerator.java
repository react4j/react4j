package react4j.hfg2;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

final class FactoryGenerator
{
  @SuppressWarnings( "SameParameterValue" )
  static void generate( @Nonnull final Path baseDirectory,
                        @Nonnull final String packageName,
                        @Nonnull final String className,
                        @Nonnull final ElementCollection elements )
    throws IOException
  {
    final TypeSpec.Builder type =
      TypeSpec
        .classBuilder( className )
        .addModifiers( Modifier.PUBLIC, Modifier.FINAL );

    type.addAnnotation( AnnotationSpec
                          .builder( SourceVersion.RELEASE_8 == SourceVersion.latest() ?
                                    ClassName.get( Generated.class ) :
                                    ClassName.bestGuess( "javax.annotation.processing.Generated" ) )
                          .addMember( "value", "$S", Main.class.getName() )
                          .build() );
    type.addJavadoc( "Element factory that provides convenience wrappers for creating react4j host elements" );

    type.addMethod( MethodSpec.constructorBuilder().addModifiers( Modifier.PRIVATE ).build() );

    for ( final Element element : elements.elements() )
    {
      type.addMethod( MethodSpec
                        .methodBuilder( element.getName() )
                        .returns( Types.REACT_NODE )
                        .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                        .addAnnotation( Types.NONNULL )
                        .addStatement( "return createElement( $S, null, ($T) null )",
                                       element.getName(),
                                       Types.REACT_NODE_ARRAY )
                        .build() );
      if ( element.supportsChildren() )
      {
        type.addMethod( MethodSpec
                          .methodBuilder( element.getName() )
                          .returns( Types.REACT_NODE )
                          .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                          .addAnnotation( Types.NONNULL )
                          .addParameter( ParameterSpec
                                           .builder( Types.REACT_NODE_ARRAY, "children", Modifier.FINAL )
                                           .addAnnotation( Types.NULLABLE )
                                           .build() )
                          .varargs()
                          .addStatement( "return createElement( $S, null, children )", element.getName() )
                          .build() );
        type.addMethod( MethodSpec
                          .methodBuilder( element.getName() )
                          .returns( Types.REACT_NODE )
                          .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                          .addAnnotation( Types.NONNULL )
                          .addParameter( ParameterSpec
                                           .builder( Types.STREAM_T_REACT_NODE, "children", Modifier.FINAL )
                                           .addAnnotation( Types.NULLABLE )
                                           .build() )
                          .addStatement( "return $N( toArray( children ) )", element.getName() )
                          .build() );
        final Set<String> permittedContent = element.getPermittedContent();
        if ( permittedContent.contains( "phrasing content" ) || permittedContent.contains( "flow content" ) )
        {
          type.addMethod( emitElementFactoryWithSimpleContent( element, Types.STRING ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.BYTE ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.SHORT ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.INT ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.LONG ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.FLOAT ) );
          type.addMethod( emitElementFactoryWithSimpleContent( element, TypeName.DOUBLE ) );
        }
      }
    }

    type.addMethod( emitToArray() );

    type.addMethod( emitCreateElement() );

    JavaFile.builder( packageName, type.build() ).
      skipJavaLangImports( true ).
      build().
      writeTo( baseDirectory );
  }

  @Nonnull
  private static MethodSpec emitElementFactoryWithSimpleContent( @Nonnull final Element element,
                                                                 @Nonnull final TypeName paramType )
  {
    final ParameterSpec.Builder parameter = ParameterSpec.builder( paramType, "content", Modifier.FINAL );
    if ( !paramType.isPrimitive() )
    {
      parameter.addAnnotation( Types.NULLABLE );
    }

    return MethodSpec
      .methodBuilder( element.getName() )
      .returns( Types.REACT_NODE )
      .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
      .addAnnotation( Types.NONNULL )
      .addParameter( parameter.build() )
      .addStatement( "return $N( $T.of( content ) )", element.getName(), Types.REACT_NODE )
      .build();
  }

  /*
    @Nonnull
    public static ReactNode ul( @Nonnull final HtmlProps props )
    {
      return createElement( "ul", Js.asPropertyMap( props ), (ReactNode[]) null );
    }

    @Nonnull
    public static ReactNode ul( @Nonnull final HtmlProps props, @Nullable final ReactNode... children )
    {
      return createElement( "ul", Js.asPropertyMap( props ), children );
    }

    @Nonnull
    public static ReactNode ul( @Nonnull final HtmlProps props, @Nonnull final String content )
    {
      return createElement( "ul", Js.asPropertyMap( props ), text( content ) );
    }

    @Nonnull
    public static ReactNode ul( @Nonnull final HtmlProps props, @Nonnull final Stream<? extends ReactNode> children )
    {
      return ul( props, toArray( children ) );
    }
   */
  @Nonnull
  private static MethodSpec emitToArray()
  {
    return MethodSpec
      .methodBuilder( "toArray" )
      .returns( Types.REACT_NODE_ARRAY )
      .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
      .addAnnotation( Types.NONNULL )
      .addParameter( ParameterSpec.builder( Types.STREAM_T_REACT_NODE, "children", Modifier.FINAL )
                       .addAnnotation( Types.NULLABLE )
                       .build() )
      .addStatement( "return children.toArray( $T::new )", Types.REACT_NODE_ARRAY )
      .build();
  }

  @Nonnull
  private static MethodSpec emitCreateElement()
  {
    // This factory approach is reasonably inefficient. It emulates existing react architecture and
    // in the future we should probably change it so that we do not need so much ceremony. We can
    // set key and ref directly without creating a new map for the props.

    // We could also add runtime validation here to check that elements are only contained by elements
    // as allowed by the spec or view components

    // If we ever decided to rewrite the reconciler we could also do things like add hints to indicate a
    // particular element always contain an array of children, a single child or no children or add hints
    // like all children must be keyed etc. This is looking off into the future...

    return MethodSpec
      .methodBuilder( "createElement" )
      .returns( Types.REACT_ELEMENT )
      .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
      .addAnnotation( Types.NONNULL )
      .addParameter( ParameterSpec.builder( String.class, "type", Modifier.FINAL )
                       .addAnnotation( Types.NONNULL )
                       .build() )
      .addParameter( ParameterSpec.builder( Types.JS_PROPERTY_MAP_T_OBJECT, "props", Modifier.FINAL )
                       .addAnnotation( Types.NULLABLE )
                       .build() )
      .addParameter( ParameterSpec.builder( Types.REACT_NODE_ARRAY, "children", Modifier.FINAL )
                       .addAnnotation( Types.NULLABLE )
                       .build() )
      .varargs()
      .addStatement( "final $T actual = $T.of()", Types.JS_PROPERTY_MAP_T_OBJECT, Types.JS_PROPERTY_MAP )
      .addStatement( "$T key = null", String.class )
      .addStatement( "$T ref = null", TypeName.OBJECT )
      .addCode( CodeBlock
                  .builder()
                  .beginControlFlow( "if ( null != props )" )
                  .addStatement( "key = props.has( \"key\" ) ? $T.asString( props.get( \"key\" ) ) : null",
                                 Types.JS )
                  .addStatement( "ref = props.has( \"ref\" ) ? props.get( \"ref\" ) : null" )
                  .addStatement(
                    "props.forEach( p -> { if ( !p.equals( \"key\" ) && !p.equals( \"ref\" ) ) { actual.set( p, props.get( p ) ); } } )" )
                  .endControlFlow()
                  .build() )
      .addCode( CodeBlock
                  .builder()
                  .beginControlFlow( "if ( null != children && children.length > 0 )" )
                  .addStatement( "actual.set( \"children\", 1 == children.length ? children[ 0 ] : children )" )
                  .endControlFlow()
                  .build() )
      .addStatement( "return $T.createHostElement( type, key, ref, actual )", Types.REACT_ELEMENT )
      .build();
  }
}
