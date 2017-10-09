package react4j.arez.spy;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.realityforge.arez.browser.extras.spy.ConsoleSpyEventProcessor;
import org.realityforge.arez.browser.extras.spy.StringifyReplacer;

/**
 * A customized console event processor that avoids accessing "key" and "ref" attributes.
 * This causes warnings when accessing props.key and props.ref in react components.
 */
public class ReactArezConsoleSpyEventProcessor
  extends ConsoleSpyEventProcessor
{
  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  protected StringifyReplacer getStringifyReplacer()
  {
    return new StringifyReplacer()
    {
      @Nonnull
      @Override
      protected String[] getPropertyNames( @Nonnull final Object object )
      {
        return Arrays.stream( super.getPropertyNames( object ) ).
          filter( n -> !n.equals( "key" ) && !n.equals( "ref" ) ).
          collect( Collectors.toList() ).
          toArray( new String[ 0 ] );
      }
    };
  }
}
