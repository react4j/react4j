package react4j.hfg.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder( { "name", "url", "contentCategories", "domInterface", "omitEndTag" } )
public final class ElementModel
{
  private String name;
  /**
   * The url from which documentation was extracted.
   */
  private String url;
  /**
   * The set of content categories that classify the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Content_categories">MDN - Content categories</a>
   */
  @Nonnull
  private final List<String> contentCategories = new ArrayList<>();
  private String domInterface;
  private boolean omitEndTag;

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl( final String url )
  {
    this.url = url;
  }

  @Nonnull
  public List<String> getContentCategories()
  {
    return contentCategories;
  }

  public void setContentCategories( @Nonnull final List<String> contentCategories )
  {
    this.contentCategories.clear();
    contentCategories.stream().sorted().distinct().forEach( this.contentCategories::add );
  }

  public String getDomInterface()
  {
    return domInterface;
  }

  public void setDomInterface( final String domInterface )
  {
    this.domInterface = domInterface;
  }

  public boolean isOmitEndTag()
  {
    return omitEndTag;
  }

  public void setOmitEndTag( final boolean omitEndTag )
  {
    this.omitEndTag = omitEndTag;
  }

  @Nonnull
  public static ElementModel open( @Nonnull final Path directory, @Nonnull final String name )
    throws IndexException
  {
    return Files.exists( directory.resolve( name + ".json" ) ) ? load( directory, name ) : create( name );
  }

  @Nonnull
  private static ElementModel create( @Nonnull final String name )
  {
    final ElementModel model = new ElementModel();
    model.setName( name );
    return model;
  }

  @Nonnull
  private static ElementModel load( @Nonnull final Path dataDirectory, @Nonnull final String name )
    throws IndexException
  {
    final Path path = dataDirectory.resolve( name + ".json" );
    try ( final InputStream inputStream = new FileInputStream( path.toFile() ) )
    {
      final ElementModel model = JsonbBuilder.create().fromJson( inputStream, ElementModel.class );
      if ( !Objects.equals( name, model.getName() ) )
      {
        throw new IndexFormatException( "Element model at " + path + " contains name '" + model.getName() +
                                        "' that does not match expected name '" + name + "'" );
      }
      return model;
    }
    catch ( final IOException ioe )
    {
      throw new IndexIOException( "Error reading element model at " + path, ioe );
    }
  }

  public void save( @Nonnull final Path dataDirectory )
  {
    try
    {
      Files.createDirectories( dataDirectory );
      final JsonbConfig jsonbConfig = new JsonbConfig().withFormatting( true );
      final Jsonb jsonb = JsonbBuilder.create( jsonbConfig );
      final Path path = dataDirectory.resolve( name + ".json" );
      try ( final FileOutputStream outputStream = new FileOutputStream( path.toFile() ) )
      {
        jsonb.toJson( this, outputStream );
      }
      jsonb.close();
      // Add newline as json output omits trailing new line
      Files.write( path, new byte[]{ '\n' }, StandardOpenOption.APPEND );
    }
    catch ( final Exception e )
    {
      throw new RuntimeException( "Error saving element model named " + name, e );
    }
  }
}
