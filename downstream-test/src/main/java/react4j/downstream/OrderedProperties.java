package arez.downstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized Enumeration<Object> keys()
  {
    return Collections.enumeration( keySet() );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Object> keySet()
  {
    return new TreeSet<>( super.keySet() );
  }

  void mergeWithPrefix( @Nonnull final Properties properties, @Nonnull final String prefix )
  {
    properties.forEach( ( key, value ) -> put( prefix + key, value ) );
  }
}
