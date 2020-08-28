package react4j.hfg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.realityforge.getopt4j.CLArgsParser;
import org.realityforge.getopt4j.CLOption;
import org.realityforge.getopt4j.CLOptionDescriptor;
import org.realityforge.getopt4j.CLUtil;
import react4j.hfg.model.ElementModel;
import react4j.hfg.model.Index;
import react4j.hfg.util.fetch.fetch.FetchException;
import react4j.hfg.util.fetch.fetch.FetchResult;
import react4j.hfg.util.fetch.fetch.FetchUtil;

public final class Fetch
{
  @Nonnull
  public static final String HTML_DOCS_BASE_URL = "https://developer.mozilla.org/en-US/docs/Web/HTML/Element";
  private static final int HELP_OPT = 'h';
  private static final int QUIET_OPT = 'q';
  private static final int VERBOSE_OPT = 'v';
  private static final int DATA_DIRECTORY_OPT = 'd';
  private static final int NO_REMOVE_SOURCE_OPT = 3;
  private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]
    {
      new CLOptionDescriptor( "help",
                              CLOptionDescriptor.ARGUMENT_DISALLOWED,
                              HELP_OPT,
                              "print this message and exit" ),
      new CLOptionDescriptor( "quiet",
                              CLOptionDescriptor.ARGUMENT_DISALLOWED,
                              QUIET_OPT,
                              "Do not output unless an error occurs.",
                              new int[]{ VERBOSE_OPT } ),
      new CLOptionDescriptor( "verbose",
                              CLOptionDescriptor.ARGUMENT_DISALLOWED,
                              VERBOSE_OPT,
                              "Verbose output of differences.",
                              new int[]{ QUIET_OPT } ),
      new CLOptionDescriptor( "data-directory",
                              CLOptionDescriptor.ARGUMENT_REQUIRED,
                              DATA_DIRECTORY_OPT,
                              "Directory where data is stored." ),
      new CLOptionDescriptor( "no-remove-source",
                              CLOptionDescriptor.ARGUMENT_DISALLOWED,
                              NO_REMOVE_SOURCE_OPT,
                              "Do not remove html after fetching and extracting data." )
    };
  @Nonnull
  private static final Logger c_logger = Logger.getGlobal();
  private static Path c_dataDirectory;
  private static boolean c_noRemoveSource;

  public static void main( @Nonnull final String[] args )
  {
    setupLogger();
    if ( !processOptions( args ) )
    {
      System.exit( ExitCodes.ERROR_PARSING_ARGS_EXIT_CODE );
    }
    final Index index = fetchIndex();

    for ( final Index.ElementIndex entry : index.getElements().values() )
    {
      final String name = entry.getName();
      try
      {
        final String url = HTML_DOCS_BASE_URL + "/" + name;
        final Path indexFile = fetchData( url, entry.getLastUpdatedAt(), name + ".html" );
        final Document document = Jsoup.parse( indexFile.toFile(), StandardCharsets.UTF_8.name() );

        final ElementModel model = ElementModel.open( c_dataDirectory, name );
        model.setUrl( url );
        final Elements headers = document.select( "table.properties > tbody > tr > th" );
        for ( final Element th : headers )
        {
          final Element td = th.nextElementSibling();
          assert "td".equals( td.tagName() );

          final String headerText = th.text();
          if ( "Content categories".equals( headerText ) )
          {
            final String text = td.text().trim();
            model.getContentCategories().clear();
            final String contentCategoriesText = text.toLowerCase().replaceAll( "\\.$", "" );
            if ( !"none".equals( contentCategoriesText ) )
            {
              // Example value:  Flow content, heading content, palpable content.
              model.setContentCategories( Arrays.asList( contentCategoriesText.split( ", " ) ) );
            }
          }
          else if ( "Permitted content".equals( headerText ) )
          {
          }
          else if ( "Tag omission".equals( headerText ) )
          {
            final String text = td.text().toLowerCase();
            final boolean omitEndTag =
              ( text.contains( "start tag must be present" ) && text.contains( "end tag must not be present" ) ) ||
              text.contains( "there must be no closing tag" ) ||
              ( text.contains( "must have a start tag" ) && text.contains( "must not have an end tag" ) );
            model.setOmitEndTag( omitEndTag );
          }
          else if ( "Permitted parents".equals( headerText ) )
          {
          }
          else if ( "Implicit ARIA role".equals( headerText ) )
          {
          }
          else if ( "Permitted ARIA roles".equals( headerText ) )
          {
          }
          else if ( "DOM interface".equals( headerText ) || "DOM Interface".equals( headerText ) )
          {
            model.setDomInterface( td.text() );
          }
          else
          {
            System.out.println( name + " has unexpected property " + headerText );
          }
        }
        model.save( c_dataDirectory );
        entry.setLastUpdatedAt( System.currentTimeMillis() );
      }
      catch ( final Exception e )
      {
        c_logger.log( Level.SEVERE, "Error extracting data for element named '" + name + "'", e );
        System.exit( ExitCodes.ERROR_EXIT_CODE );
        throw new UnsupportedOperationException();
      }
    }
    System.exit( ExitCodes.SUCCESS_EXIT_CODE );
  }

  @Nonnull
  private static Index fetchIndex()
  {
    try
    {
      final Path indexFile = fetchData( HTML_DOCS_BASE_URL, 0, "element_index.html" );
      final Document document = Jsoup.parse( indexFile.toFile(), StandardCharsets.UTF_8.name() );
      final List<String> elements = document
        .select( "h2 + p + table.standard-table > tbody > tr > td:first-child > a > code," +
                 "h2 + table.standard-table > tbody > tr > td:first-child > a > code" )
        .stream()
        .map( Element::text )
        // Strip the brackets at end of constructors
        .map( text -> text.replaceAll( "^<", "" ).replaceAll( ">$", "" ) )
        .collect( Collectors.toList() );

      c_logger.log( Level.INFO, () -> "Detected " + elements.size() + " elements referenced from the the html index" );

      if ( c_logger.isLoggable( Level.FINE ) )
      {
        c_logger.log( Level.FINE, () -> "Elements include: " );
        elements.forEach( elementName -> c_logger.log( Level.FINE, () -> "  " + elementName ) );
      }
      final Index index = Index.open( c_dataDirectory );
      final Map<String, Index.ElementIndex> existing = index.getElements();
      for ( final String element : elements )
      {
        if ( !existing.containsKey( element ) )
        {
          c_logger.log( Level.INFO, () -> "Added " + element + " to the local index" );
          final Index.ElementIndex elementEntry = new Index.ElementIndex();
          elementEntry.setName( element );
          existing.put( element, elementEntry );
        }
      }
      index.save();

      if ( !c_noRemoveSource )
      {
        Files.delete( indexFile );
      }
      return index;
    }
    catch ( final Exception e )
    {
      c_logger.log( Level.SEVERE, "Error building element index", e );
      System.exit( ExitCodes.ERROR_EXIT_CODE );
      throw new UnsupportedOperationException();
    }
  }

  @SuppressWarnings( "SameParameterValue" )
  @Nonnull
  private static Path fetchData( @Nonnull final String url,
                                 final long lastModifiedAt,
                                 @Nonnull final String targetFileName )
    throws FetchException
  {
    final Path target = c_dataDirectory.resolve( targetFileName );
    final FetchResult result = FetchUtil.downloadURL( url, lastModifiedAt );
    assert null != result;
    try
    {
      Files.deleteIfExists( target );
      Files.move( result.getPath(), target );
    }
    catch ( final IOException ioe )
    {
      c_logger.log( Level.SEVERE, "Error: Failed to copy fetched content to target file.", ioe );
      System.exit( ExitCodes.ERROR_EXIT_CODE );
    }
    return target;
  }

  static boolean processOptions( @Nonnull final String... args )
  {
    // Parse the arguments
    final CLArgsParser parser = new CLArgsParser( args, OPTIONS );

    //Make sure that there was no errors parsing arguments
    if ( null != parser.getErrorString() )
    {
      c_logger.log( Level.SEVERE, "Error: " + parser.getErrorString() );
      return false;
    }
    c_logger.setLevel( Level.INFO );

    for ( final CLOption option : parser.getArguments() )
    {
      switch ( option.getId() )
      {
        case CLOption.TEXT_ARGUMENT:
        {
          final String argument = option.getArgument();
          c_logger.log( Level.SEVERE, "Error: Unexpected argument specified: " + argument );
          return false;
        }
        case DATA_DIRECTORY_OPT:
        {
          final String argument = option.getArgument();
          final Path directory = Paths.get( argument ).toAbsolutePath().normalize();
          if ( !Files.exists( directory ) )
          {
            c_logger.log( Level.SEVERE,
                          "Error: Specified data directory does not exist. Specified value: " + argument );
            return false;
          }
          if ( !Files.isDirectory( directory ) )
          {
            c_logger.log( Level.SEVERE,
                          "Error: Specified data directory is not a directory. Specified value: " + argument );
            return false;
          }
          c_dataDirectory = directory;
          break;
        }

        case VERBOSE_OPT:
        {
          c_logger.setLevel( Level.ALL );
          break;
        }
        case QUIET_OPT:
        {
          c_logger.setLevel( Level.WARNING );
          break;
        }
        case NO_REMOVE_SOURCE_OPT:
        {
          c_noRemoveSource = true;
          return false;
        }
        case HELP_OPT:
        {
          printUsage();
          return false;
        }
      }
    }

    if ( null == c_dataDirectory )
    {
      c_dataDirectory = Paths.get( "." ).toAbsolutePath().normalize();
    }
    return true;
  }

  /**
   * Print out a usage statement
   */
  static void printUsage()
  {
    c_logger.severe( "java " + Fetch.class.getName() + " [options] [command]" );
    c_logger.severe( "\tOptions:" );
    final String[] options =
      CLUtil.describeOptions( OPTIONS ).toString().split( System.getProperty( "line.separator" ) );
    for ( final String line : options )
    {
      c_logger.severe( line );
    }
  }

  static void setupLogger()
  {
    final ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter( new RawFormatter() );
    handler.setLevel( Level.ALL );
    c_logger.setUseParentHandlers( false );
    c_logger.addHandler( handler );
    c_logger.setLevel( Level.INFO );
  }
}
