package react4j.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
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
}
