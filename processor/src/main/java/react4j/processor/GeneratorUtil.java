package react4j.processor;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.processing.Filer;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

final class GeneratorUtil
{
  private GeneratorUtil()
  {
  }

  @Nonnull
  static ClassName getGeneratedClassName( @Nonnull final TypeElement element,
                                          @Nonnull final String prefix,
                                          @Nonnull final String postfix )
  {
    return ClassName.get( getQualifiedPackageName( element ), getGeneratedSimpleClassName( element, prefix, postfix ) );
  }

  @Nonnull
  static String getQualifiedPackageName( @Nonnull final TypeElement element )
  {
    return getPackageElement( element ).getQualifiedName().toString();
  }

  @Nonnull
  static String getGeneratedSimpleClassName( @Nonnull final TypeElement element,
                                             @Nonnull final String prefix,
                                             @Nonnull final String postfix )
  {
    return getNestedClassPrefix( element ) + prefix + element.getSimpleName() + postfix;
  }

  @Nonnull
  private static String getNestedClassPrefix( @Nonnull final TypeElement element )
  {
    final StringBuilder name = new StringBuilder();
    TypeElement t = element;
    while ( NestingKind.TOP_LEVEL != t.getNestingKind() )
    {
      t = (TypeElement) t.getEnclosingElement();
      name.insert( 0, t.getSimpleName() + "_" );
    }
    return name.toString();
  }

  @SuppressWarnings( "UnstableApiUsage" )
  @Nonnull
  static PackageElement getPackageElement( @Nonnull final TypeElement element )
  {
    return MoreElements.getPackage( element );
  }

  static void emitJavaType( @Nonnull final String packageName,
                            @Nonnull final TypeSpec typeSpec, final Filer filer )
    throws IOException
  {
    JavaFile.builder( packageName, typeSpec ).
      skipJavaLangImports( true ).
      build().
      writeTo( filer );
  }
}
