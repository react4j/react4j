package react4j.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import static com.google.common.truth.Truth.*;
import static org.testng.Assert.*;

public abstract class AbstractProcessorTest
{
  protected final void assertSuccessfulCompile( @Nonnull final String inputResource,
                                                @Nonnull final String... expectedOutputResources )
    throws Exception
  {
    final JavaFileObject source = fixture( inputResource );
    assertSuccessfulCompile( Collections.singletonList( source ), Arrays.asList( expectedOutputResources ) );
  }

  protected final void assertSuccessfulCompile( @Nonnull final List<JavaFileObject> inputs,
                                                @Nonnull final List<String> outputs )
    throws Exception
  {
    if ( outputFiles() )
    {
      final List<Processor> processors = new ArrayList<>();
      processors.add( processor() );
      processors.addAll( Arrays.asList( additionalProcessors() ) );
      final Compilation compilation =
        Compiler.javac()
          .withProcessors( processors )
          .withOptions( getOptions() )
          .compile( inputs );

      final Compilation.Status status = compilation.status();
      if ( Compilation.Status.SUCCESS != status )
      {
        /*
         * Ugly hackery that marks the compile as successful so we can emit output onto filesystem. This could
         * result in java code that is not compilable emitted to the filesystem. This makes determining problems
         * a little easier even if it does make re-running tests from the IDE a little harder.
         */
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, Compilation.Status.SUCCESS );
      }

      final ImmutableList<JavaFileObject> fileObjects = compilation.generatedSourceFiles();
      for ( final JavaFileObject fileObject : fileObjects )
      {
        if ( emitGeneratedFile( fileObject ) )
        {
          final Path target =
            fixtureDir().resolve( "expected/" + fileObject.getName().replace( "/SOURCE_OUTPUT/", "" ) );
          final File dir = target.getParent().toFile();
          if ( !dir.exists() )
          {
            assertTrue( dir.mkdirs() );
          }
          if ( Files.exists( target ) )
          {
            final byte[] existing = Files.readAllBytes( target );
            final InputStream generated = fileObject.openInputStream();
            final byte[] data = new byte[ generated.available() ];
            assertEquals( generated.read( data ), data.length );
            if ( Arrays.equals( existing, data ) )
            {
              /*
               * If the data on the filesystem is identical to data generated then do not write
               * to filesystem. The writing can be slow and it can also trigger the IDE or other
               * tools to recompile code which is problematic.
               */
              continue;
            }
            Files.delete( target );
          }
          Files.copy( fileObject.openInputStream(), target );
        }
      }

      if ( Compilation.Status.SUCCESS != status )
      {
        // Restore old status
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, status );

        // This next line will generate an error and fail the test
        //noinspection ResultOfMethodCallIgnored
        compilation.generatedSourceFiles();
      }
    }
    final JavaFileObject firstExpected = fixture( outputs.get( 0 ) );
    final JavaFileObject[] restExpected =
      outputs.stream().skip( 1 ).map( this::fixture ).toArray( JavaFileObject[]::new );
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      withCompilerOptions( getOptions() ).
      processedWith( processor(), additionalProcessors() ).
      compilesWithoutWarnings().
      and().
      generatesSources( firstExpected, restExpected );
  }

  protected boolean emitGeneratedFile( @Nonnull final JavaFileObject target )
  {
    return true;
  }

  @Nonnull
  protected abstract Processor processor();

  @Nonnull
  protected List<String> getOptions()
  {
    return Arrays.asList( "-Xlint:all,-processing",
                          "-implicit:none",
                          "-A" + getOptionPrefix() + ".defer.errors=false" );
  }

  protected final void assertFailedCompile( @Nonnull final String classname,
                                            @Nonnull final String errorMessageFragment )
  {
    assertFailedCompileResource( toFilename( "bad_input", classname ), errorMessageFragment );
  }

  @Nonnull
  protected final String toFilename( @Nonnull final String dir, @Nonnull final String classname )
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "\\." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    input.append( dir );
    for ( final String element : elements )
    {
      input.append( '/' );
      input.append( element );
    }
    input.append( ".java" );
    return input.toString();
  }

  protected final void assertFailedCompileResource( @Nonnull final String inputResource,
                                                    @Nonnull final String errorMessageFragment )
  {
    assertFailedCompileResource( Collections.singletonList( fixture( inputResource ) ), errorMessageFragment );
  }

  protected final void assertFailedCompileResource( @Nonnull final List<JavaFileObject> inputs,
                                                    @Nonnull final String errorMessageFragment )
  {
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      withCompilerOptions( getOptions() ).
      processedWith( processor(), additionalProcessors() ).
      failsToCompile().
      withErrorContaining( errorMessageFragment );
  }

  @Nonnull
  protected Processor[] additionalProcessors()
  {
    return new Processor[ 0 ];
  }

  @Nonnull
  protected final JavaFileObject fixture( @Nonnull final String filename )
  {
    final Path path = fixtureDir().resolve( filename );
    if ( !Files.exists( path ) )
    {
      fail( "Fixture " + path + " does not exist." );
    }
    try
    {
      return JavaFileObjects.forResource( path.toUri().toURL() );
    }
    catch ( final MalformedURLException e )
    {
      throw new IllegalStateException( e );
    }
  }

  @Nonnull
  protected final Path fixtureDir()
  {
    final String key = getOptionPrefix() + ".fixture_dir";
    final String fixtureDir = System.getProperty( key );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"" + key + "\" ) to return fixture directory" );
    return new File( fixtureDir ).toPath();
  }

  @Nonnull
  protected abstract String getOptionPrefix();

  protected final boolean outputFiles()
  {
    return System.getProperty( getOptionPrefix() + ".output_fixture_data", "false" ).equals( "true" );
  }
}
