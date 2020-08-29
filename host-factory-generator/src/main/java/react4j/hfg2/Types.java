package react4j.hfg2;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

final class Types
{
  @Nonnull
  static final ClassName NONNULL = ClassName.get( "javax.annotation", "Nonnull" );
  @Nonnull
  static final ClassName NULLABLE = ClassName.get( "javax.annotation", "Nullable" );
  @Nonnull
  static final ClassName REACT_ELEMENT = ClassName.get( "react4j", "ReactElement" );
  @Nonnull
  static final ClassName REACT_NODE = ClassName.get( "react4j", "ReactNode" );
  @Nonnull
  static final ArrayTypeName REACT_NODE_ARRAY = ArrayTypeName.of( Types.REACT_NODE );
  @Nonnull
  static final ClassName JS = ClassName.get( "jsinterop.base", "Js" );
  @Nonnull
  static final ClassName JS_PROPERTY_MAP = ClassName.get( "jsinterop.base", "JsPropertyMap" );
  @Nonnull
  static final ParameterizedTypeName JS_PROPERTY_MAP_T_OBJECT =
    ParameterizedTypeName.get( JS_PROPERTY_MAP, TypeName.OBJECT );
  @Nonnull
  static final ClassName STREAM = ClassName.get( Stream.class );
  @Nonnull
  static final ParameterizedTypeName STREAM_T_REACT_NODE =
    ParameterizedTypeName.get( STREAM, WildcardTypeName.subtypeOf(REACT_NODE) );

  private Types()
  {
  }
}
