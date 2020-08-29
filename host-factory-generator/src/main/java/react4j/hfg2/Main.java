package react4j.hfg2;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

public final class Main
{
  public static void main( @Nonnull final String[] args )
  {
    if ( 1 != args.length )
    {
      System.err.println( "Must specify a single parameter that indicates directory in which to generate artifacts" );
      System.exit( 2 );
    }
    final Path path = Paths.get( args[ 0 ] );

    try
    {
      final ElementCollection elements = HTMLElementsGenerator.create();
      FactoryGenerator.generate( path, "react4j.dom2", "HTML", elements );
    }
    catch ( final Throwable e )
    {
      System.err.println( "Error generating factory:" );
      e.printStackTrace();
      System.exit( 1 );
    }
    System.exit( 0 );
  }
}
