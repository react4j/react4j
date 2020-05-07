package react4j;

import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ContextsTest
  extends AbstractTest
{
  @Test
  public void basicOperation_withQualifier()
  {
    final Runnable defaultValue = mock( Runnable.class );
    final Context<Runnable> context = mockContext();

    final Contexts.ContextProvider provider = mock( Contexts.ContextProvider.class );
    Contexts.setContextProvider( provider );

    when( provider.createContext( eq( defaultValue ) ) ).thenReturn( context );

    Contexts.register( Runnable.class, "someQual", defaultValue );

    verify( provider, only() ).createContext( eq( defaultValue ) );

    assertSame( Contexts.get( Runnable.class, "someQual" ), context );
  }

  @Test
  public void basicOperation_withoutDefaultValue()
  {
    final Context<Runnable> context = mockContext();

    final Contexts.ContextProvider provider = mock( Contexts.ContextProvider.class );
    Contexts.setContextProvider( provider );

    when( provider.createContext( isNull( Runnable.class ) ) ).thenReturn( context );

    Contexts.register( Runnable.class, "someQual" );

    verify( provider, only() ).createContext( isNull( Runnable.class ) );

    assertSame( Contexts.get( Runnable.class, "someQual" ), context );
  }

  @Test
  public void basicOperation_withoutQualifier()
  {
    final Runnable defaultValue = mock( Runnable.class );
    final Context<Runnable> context = mockContext();

    final Contexts.ContextProvider provider = mock( Contexts.ContextProvider.class );
    Contexts.setContextProvider( provider );

    when( provider.createContext( eq( defaultValue ) ) ).thenReturn( context );

    Contexts.register( Runnable.class, defaultValue );

    verify( provider, only() ).createContext( eq( defaultValue ) );

    assertSame( Contexts.get( Runnable.class ), context );
    assertSame( Contexts.get( Runnable.class, "" ), context );
  }

  @Test
  public void basicOperation_withoutDefaultValueOrQualifier()
  {
    final Context<Runnable> context = mockContext();

    final Contexts.ContextProvider provider = mock( Contexts.ContextProvider.class );
    Contexts.setContextProvider( provider );

    when( provider.createContext( isNull( Runnable.class ) ) ).thenReturn( context );

    Contexts.register( Runnable.class );

    verify( provider, only() ).createContext( isNull( Runnable.class ) );

    assertSame( Contexts.get( Runnable.class ), context );
    assertSame( Contexts.get( Runnable.class, "" ), context );
  }

  @Test
  public void register_duplicate()
  {
    final Runnable defaultValue = mock( Runnable.class );
    final Context<Runnable> context = mockContext();

    final Contexts.ContextProvider provider = mock( Contexts.ContextProvider.class );
    Contexts.setContextProvider( provider );

    when( provider.createContext( eq( defaultValue ) ) ).thenReturn( context );

    Contexts.register( Runnable.class, "someQual", defaultValue );

    verify( provider, only() ).createContext( eq( defaultValue ) );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> Contexts.register( Runnable.class, "someQual", defaultValue ) );

    verify( provider, only() ).createContext( eq( defaultValue ) );

    assertEquals( exception.getMessage(),
                  "Attempting to register React context with type interface java.lang.Runnable and qualifier 'someQual' but a context already exists with that type and name" );
  }

  @Test
  public void get_missing()
  {
    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> Contexts.get( Runnable.class, "someQual" ) );

    assertEquals( exception.getMessage(),
                  "Attempting to retrieve React context with type interface java.lang.Runnable and qualifier 'someQual' but no such context exists with that type and name" );
  }

  @Test
  public void get_withoutQualifier_missing()
  {
    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> Contexts.get( Runnable.class ) );
    assertEquals( exception.getMessage(),
                  "Attempting to retrieve React context with type interface java.lang.Runnable and qualifier '' but no such context exists with that type and name" );
  }

  @SuppressWarnings( "unchecked" )
  private <T> Context<T> mockContext()
  {
    return mock( Context.class );
  }
}
