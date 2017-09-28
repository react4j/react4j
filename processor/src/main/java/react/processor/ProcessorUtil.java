package react.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

final class ProcessorUtil
{
  private static final String SENTINEL_NAME = "<default>";

  private ProcessorUtil()
  {
  }

  @Nonnull
  static List<ExecutableElement> getMethods( @Nonnull final TypeElement element )
  {
    final Map<String, ExecutableElement> methodMap = new LinkedHashMap<>();
    enumerateMethods( element, methodMap );
    return new ArrayList<>( methodMap.values() );
  }

  private static void enumerateMethods( @Nonnull final TypeElement element,
                                        @Nonnull final Map<String, ExecutableElement> methods )
  {
    final TypeMirror superclass = element.getSuperclass();
    if ( TypeKind.NONE != superclass.getKind() )
    {
      enumerateMethods( (TypeElement) ( (DeclaredType) superclass ).asElement(), methods );
    }
    for ( final TypeMirror interfaceType : element.getInterfaces() )
    {
      final TypeElement interfaceElement = (TypeElement) ( (DeclaredType) interfaceType ).asElement();
      enumerateMethods( interfaceElement, methods );
    }
    for ( final Element member : element.getEnclosedElements() )
    {
      if ( member.getKind() == ElementKind.METHOD )
      {
        methods.put( member.toString(), (ExecutableElement) member );
      }
    }
  }

  static void copyAccessModifiers( @Nonnull final TypeElement element, @Nonnull final TypeSpec.Builder builder )
  {
    if ( element.getModifiers().contains( Modifier.PUBLIC ) )
    {
      builder.addModifiers( Modifier.PUBLIC );
    }
  }

  static void copyTypeParameters( @Nonnull final ExecutableType action, @Nonnull final MethodSpec.Builder builder )
  {
    for ( final TypeVariable typeParameter : action.getTypeVariables() )
    {
      builder.addTypeVariable( TypeVariableName.get( typeParameter ) );
    }
  }

  static boolean isSentinelName( @Nonnull final String name )
  {
    return SENTINEL_NAME.equals( name );
  }

  static boolean isJavaIdentifier( @Nonnull final String value )
  {
    if ( !Character.isJavaIdentifierStart( value.charAt( 0 ) ) )
    {
      return false;
    }
    else
    {
      final int length = value.length();
      for ( int i = 1; i < length; i++ )
      {
        if ( !Character.isJavaIdentifierPart( value.charAt( i ) ) )
        {
          return false;
        }
      }

      return true;
    }
  }
}
