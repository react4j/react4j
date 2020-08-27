package react4j.hfg.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

public class Index
{
  @Nonnull
  public static final String FILENAME = "__index__.json";
  @Nonnull
  private final String _name;
  @Nonnull
  private final Path _directory;
  @Nonnull
  private final LinkedHashMap<String, ElementIndex> _elements;

  Index( @Nonnull final String name, @Nonnull final Path directory, @Nonnull final List<ElementIndex> elements )
  {
    _name = Objects.requireNonNull( name );
    _directory = Objects.requireNonNull( directory );
    _elements = new LinkedHashMap<>();
    for ( final ElementIndex element : elements )
    {
      element.setIndex( this );
      _elements.put( element.getName(), element );
    }
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  @Nonnull
  public Path getDirectory()
  {
    return _directory;
  }

  @Nonnull
  public Map<String, ElementIndex> getElements()
  {
    return _elements;
  }

  public void save()
    throws IndexSaveException
  {
    Index.save( this );
  }

  @Nonnull
  public static Index open( @Nonnull final Path directory )
    throws IndexException
  {
    return Files.exists( directory.resolve( FILENAME ) ) ? load( directory ) : create( directory );
  }

  @Nonnull
  private static Index create( @Nonnull final Path directory )
  {
    return new Index( directory.getFileName().toString(), directory, new ArrayList<>() );
  }

  @Nonnull
  private static Index load( @Nonnull final Path directory )
    throws IndexException
  {
    final Path path = directory.resolve( FILENAME );
    try ( final InputStream inputStream = new FileInputStream( path.toFile() ) )
    {
      final List<ElementIndex> elements =
        JsonbBuilder.create().fromJson( inputStream, new ArrayList<ElementIndex>()
        {
        }.getClass().getGenericSuperclass() );
      for ( final ElementIndex element : elements )
      {
        if ( null == element.getName() )
        {
          throw new IndexFormatException( "Index at " + path + " contains an entry missing the name value" );
        }
      }
      return new Index( directory.getFileName().toString(), directory, elements );
    }
    catch ( final IOException ioe )
    {
      throw new IndexIOException( "Error reading index at " + path, ioe );
    }
  }

  static void save( @Nonnull final Index index )
    throws IndexSaveException
  {
    try
    {
      final Path directory = index.getDirectory();
      Files.createDirectories( directory );
      final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting( true );
      final Jsonb jsonb = JsonbBuilder.create( jsonbConfig );
      final Path path = directory.resolve( FILENAME );
      try ( final FileOutputStream outputStream = new FileOutputStream( path.toFile() ) )
      {
        jsonb.toJson( index
                        .getElements()
                        .values()
                        .stream()
                        .sorted( Comparator.comparing( ElementIndex::getName ) )
                        .collect( Collectors.toList() ),
                      outputStream );
      }
      jsonb.close();
      // Add newline as json output omits trailing new line
      Files.write( path, new byte[]{ '\n' }, StandardOpenOption.APPEND );
    }
    catch ( final Exception e )
    {
      throw new IndexSaveException( index, e );
    }
  }
}
