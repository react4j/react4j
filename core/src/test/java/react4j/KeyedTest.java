package react4j;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class KeyedTest
{
  @Test
  public void getKey()
  {
    final String key = "abcdef";
    final Keyed keyed = new TestKeyed( key );
    assertEquals( keyed.getKey(), key );
    assertEquals( Keyed.getKey( keyed ), key );
  }

  @Test
  public void getKey_onNonKeyed()
  {
    assertNull( Keyed.getKey( "XXXX" ) );
  }

  private static class TestKeyed
    implements Keyed
  {
    private final String _key;

    TestKeyed( @Nonnull final String key )
    {
      _key = Objects.requireNonNull( key );
    }

    @Nonnull
    @Override
    public String getKey()
    {
      return _key;
    }
  }
}
