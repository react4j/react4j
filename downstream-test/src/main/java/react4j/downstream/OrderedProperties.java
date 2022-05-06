package react4j.downstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;

/**
 * A simple customization of Properties that has a stable output order based on alphabetic ordering of keys.
 */
public final class OrderedProperties
  extends Properties
{
  @Nonnull
  static OrderedProperties load( @Nonnull final Path path )
    throws IOException
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.load( Files.newBufferedReader( path ) );
    return properties;
  }

  @Override
  public synchronized Enumeration<Object> keys()
  {
    return Collections.enumeration( keySet() );
  }

  @Nonnull
  @Override
  public Set<Object> keySet()
  {
    // Used in Java8 when writing properties
    return new TreeSet<>( super.keySet() );
  }

  @SuppressWarnings( "UseBulkOperation" )
  @Nonnull
  @Override
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    // Used in Java17+ when writing properties
    final TreeMap<Object, Object> map = new TreeMap<>();
    for ( final Map.Entry<Object, Object> entry : super.entrySet() )
    {
      map.put( entry.getKey(), entry.getValue() );
    }
    return map.entrySet();
  }

  void mergeWithPrefix( @Nonnull final Properties properties, @Nonnull final String prefix )
  {
    properties.forEach( ( key, value ) -> put( prefix + key, value ) );
  }
}
