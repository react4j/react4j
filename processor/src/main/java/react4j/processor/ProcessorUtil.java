package react4j.processor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ProcessorException;

@SuppressWarnings( "SameParameterValue" )
final class ProcessorUtil
{
  private static final Pattern GETTER_PATTERN = Pattern.compile( "^get([A-Z].*)$" );
  private static final Pattern ISSER_PATTERN = Pattern.compile( "^is([A-Z].*)$" );

  private ProcessorUtil()
  {
  }

  @Nonnull
  static String getPropertyAccessorName( @Nonnull final ExecutableElement method,
                                         @Nonnull final String specifiedName,
                                         @Nonnull final String sentinelName )
    throws ProcessorException
  {
    String name = deriveName( method, GETTER_PATTERN, specifiedName, sentinelName );
    if ( null != name )
    {
      return name;
    }
    else if ( method.getReturnType().getKind() == TypeKind.BOOLEAN )
    {
      name = deriveName( method, ISSER_PATTERN, specifiedName, sentinelName );
      if ( null != name )
      {
        return name;
      }
    }
    return method.getSimpleName().toString();
  }

  @Nullable
  static String deriveName( @Nonnull final Element method,
                            @Nonnull final Pattern pattern,
                            @Nonnull final String name,
                            @Nonnull final String sentinelName )
    throws ProcessorException
  {
    if ( sentinelName.equals( name ) )
    {
      final String methodName = method.getSimpleName().toString();
      final Matcher matcher = pattern.matcher( methodName );
      if ( matcher.find() )
      {
        final String candidate = matcher.group( 1 );
        return Character.toLowerCase( candidate.charAt( 0 ) ) + candidate.substring( 1 );
      }
      else
      {
        return null;
      }
    }
    else
    {
      return name;
    }
  }

  @Nullable
  static ExecutableElement getOverriddenMethod( @Nonnull final ProcessingEnvironment processingEnv,
                                                @Nonnull final TypeElement typeElement,
                                                @Nonnull final ExecutableElement method )
  {
    final TypeMirror superclass = typeElement.getSuperclass();
    if ( TypeKind.NONE == superclass.getKind() )
    {
      return null;
    }
    else
    {
      final TypeElement parent = (TypeElement) processingEnv.getTypeUtils().asElement( superclass );
      final List<? extends Element> enclosedElements = parent.getEnclosedElements();
      for ( final Element enclosedElement : enclosedElements )
      {
        if ( ElementKind.METHOD == enclosedElement.getKind() &&
             processingEnv.getElementUtils().overrides( method, (ExecutableElement) enclosedElement, typeElement ) )
        {
          return (ExecutableElement) enclosedElement;
        }
      }
      return getOverriddenMethod( processingEnv, typeElement, method );
    }
  }
}
