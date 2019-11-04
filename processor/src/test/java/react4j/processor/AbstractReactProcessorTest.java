package react4j.processor;

import arez.processor.ArezProcessor;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.BufferedReader;
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
import javax.tools.JavaFileObject;
import static com.google.common.truth.Truth.*;
import static org.testng.Assert.*;

@SuppressWarnings( "Duplicates" )
abstract class AbstractReactProcessorTest
{
  void assertSuccessfulCompile( @Nonnull final String classname, final boolean dagger )
    throws Exception
  {
    // It should be noted that we do not test the output of any Arez artifact
    // emitted. We assume the Arez project adequately tests this scenario
    final String[] elements = classname.contains( "." ) ? classname.split( "\\." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    final StringBuilder enhancedComponent = new StringBuilder();
    final StringBuilder builder = new StringBuilder();
    final StringBuilder componentExtension = dagger ? new StringBuilder() : null;
    input.append( "input" );
    enhancedComponent.append( "expected" );
    builder.append( "expected" );
    if ( dagger )
    {
      componentExtension.append( "expected" );
    }
    for ( int i = 0; i < elements.length; i++ )
    {
      input.append( '/' );
      input.append( elements[ i ] );
      enhancedComponent.append( '/' );
      if ( i == elements.length - 1 )
      {
        enhancedComponent.append( "React4j_" );
      }
      enhancedComponent.append( elements[ i ] );
      builder.append( '/' );
      builder.append( elements[ i ] );
      if ( i == elements.length - 1 )
      {
        builder.append( "Builder" );
      }
      if ( dagger )
      {
        componentExtension.append( '/' );
        componentExtension.append( elements[ i ] );
        if ( i == elements.length - 1 )
        {
          componentExtension.append( "DaggerComponentExtension" );
        }
      }
    }
    input.append( ".java" );
    enhancedComponent.append( ".java" );
    builder.append( ".java" );
    if ( dagger )
    {
      componentExtension.append( ".java" );
    }
    final ArrayList<String> outputs = new ArrayList<>();
    outputs.add( enhancedComponent.toString() );
    outputs.add( builder.toString() );
    if ( dagger )
    {
      outputs.add( componentExtension.toString() );
    }
    assertSuccessfulCompile( input.toString(), outputs.toArray( new String[ 0 ] ) );
  }

  void assertSuccessfulCompile( @Nonnull final String inputResource, @Nonnull final String... expectedOutputResources )
    throws Exception
  {
    final JavaFileObject source = fixture( inputResource );
    assertSuccessfulCompile( Collections.singletonList( source ), Arrays.asList( expectedOutputResources ) );
  }

  void assertSuccessfulCompile( @Nonnull final List<JavaFileObject> inputs, @Nonnull final List<String> outputs )
    throws Exception
  {
    // Arez processor required so that our tests emit all the right outputs
    // when outputFiles() is true
    if ( outputFiles() )
    {
      final Compilation compilation =
        Compiler.javac()
          .withOptions( Arrays.asList( "-Xlint:all,-processing", "-implicit:none", "-parameters" ) )
          .withProcessors( new ReactProcessor(), new ArezProcessor() )
          .compile( inputs );

      final Compilation.Status status = compilation.status();
      if ( Compilation.Status.SUCCESS != status )
      {
        /*
         * Ugly hackery that marks the compile as successful so we can emit output onto filesystem. This could
         * result in java code that is not compilable emitted to filesystem. This re-running determining problems
         * a little easier even if it does make re-running tests from IDE a little harder
         */
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, Compilation.Status.SUCCESS );
      }

      final ImmutableList<JavaFileObject> fileObjects = compilation.generatedSourceFiles();
      for ( final JavaFileObject fileObject : fileObjects )
      {
        final Path target = fixtureDir().resolve( "expected/" + fileObject.getName().replace( "/SOURCE_OUTPUT/", "" ) );
        boolean isGeneratedByArez = false;
        try ( final BufferedReader reader = new BufferedReader( fileObject.openReader( true ) ) )
        {
          String line = reader.readLine();
          while ( null != line )
          {
            if ( line.contains( "arez.processor.ArezProcessor" ) )
            {
              isGeneratedByArez = true;
              break;
            }
            else if ( line.contains( "@Generated" ) )
            {
              // If we get here then generated by React4j annotation processor
              break;
            }
            line = reader.readLine();
          }
        }

        if ( isGeneratedByArez )
        {
          continue;
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

        final File dir = target.getParent().toFile();
        if ( !dir.exists() )
        {
          assertTrue( dir.mkdirs() );
        }
        Files.copy( fileObject.openInputStream(), target );
      }

      if ( Compilation.Status.SUCCESS != status )
      {
        // Restore old status
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, status );

        // This next line will generate an error
        //noinspection ResultOfMethodCallIgnored
        compilation.generatedSourceFiles();
      }
    }
    final JavaFileObject firstExpected = fixture( outputs.get( 0 ) );
    final JavaFileObject[] restExpected =
      outputs.stream().skip( 1 ).map( this::fixture ).toArray( JavaFileObject[]::new );
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      withCompilerOptions( "-Xlint:all,-processing", "-implicit:none", "-parameters" ).
      processedWith( new ReactProcessor(), new ArezProcessor() ).
      compilesWithoutWarnings().
      and().
      generatesSources( firstExpected, restExpected );
  }

  void assertFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "\\." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    input.append( "bad_input" );
    for ( final String element : elements )
    {
      input.append( '/' );
      input.append( element );
    }
    input.append( ".java" );
    assertFailedCompileResource( input.toString(), errorMessageFragment );
  }

  private void assertFailedCompileResource( @Nonnull final String inputResource,
                                            @Nonnull final String errorMessageFragment )
  {
    assertFailedCompileResource( Collections.singletonList( fixture( inputResource ) ), errorMessageFragment );
  }

  void assertFailedCompileResource( @Nonnull final List<JavaFileObject> inputs,
                                    @Nonnull final String errorMessageFragment )
  {
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      processedWith( new ArezProcessor(), new ReactProcessor() ).
      failsToCompile().
      withWarningContaining( errorMessageFragment );
  }

  @Nonnull
  final JavaFileObject fixture( @Nonnull final String path )
  {
    final Path outputFile = fixtureDir().resolve( path );
    if ( !Files.exists( outputFile ) )
    {
      fail( "Output file fixture " + outputFile + " does not exist. Thus can not compare against it." );
    }
    try
    {
      return JavaFileObjects.forResource( outputFile.toUri().toURL() );
    }
    catch ( final MalformedURLException e )
    {
      throw new IllegalStateException( e );
    }
  }

  @Nonnull
  private Path fixtureDir()
  {
    final String fixtureDir = System.getProperty( "react4j.fixture_dir" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.fixture_dir\" ) to return fixture directory" );
    return new File( fixtureDir ).toPath();
  }

  private boolean outputFiles()
  {
    return System.getProperty( "react4j.output_fixture_data", "false" ).equals( "true" );
  }
}
