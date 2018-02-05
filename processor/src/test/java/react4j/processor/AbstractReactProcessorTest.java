package react4j.processor;

import arez.processor.ArezProcessor;
import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
    final StringBuilder daggerFactory = new StringBuilder();
    input.append( "input" );
    enhancedComponent.append( "expected" );
    builder.append( "expected" );
    daggerFactory.append( "expected" );
    for ( int i = 0; i < elements.length; i++ )
    {
      input.append( '/' );
      input.append( elements[ i ] );
      enhancedComponent.append( '/' );
      enhancedComponent.append( elements[ i ] );
      builder.append( '/' );
      builder.append( elements[ i ] );
      if ( i == elements.length - 1 )
      {
        enhancedComponent.append( "_" );
        builder.append( "Builder" );
      }
      daggerFactory.append( '/' );
      daggerFactory.append( elements[ i ] );
      if ( i == elements.length - 1 )
      {
        daggerFactory.append( "DaggerFactory" );
      }
    }
    input.append( ".java" );
    enhancedComponent.append( ".java" );
    builder.append( ".java" );
    daggerFactory.append( ".java" );
    final ArrayList<String> outputs = new ArrayList<>();
    outputs.add( enhancedComponent.toString() );
    outputs.add( builder.toString() );
    if ( dagger )
    {
      outputs.add( daggerFactory.toString() );
    }
    assertSuccessfulCompile( input.toString(), outputs.toArray( new String[ outputs.size() ] ) );
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
      final ImmutableList<JavaFileObject> fileObjects =
        Compiler.javac().withProcessors( new ReactProcessor(), new ArezProcessor() ).
          compile( inputs ).generatedSourceFiles();
      for ( final JavaFileObject fileObject : fileObjects )
      {
        final Path target = fixtureDir().resolve( "expected/" + fileObject.getName().replace( "/SOURCE_OUTPUT/", "" ) );
        if ( target.toFile().getName().startsWith( "Arez_" ) )
        {
          continue;
        }
        if ( Files.exists( target ) )
        {
          Files.delete( target );
        }

        final File dir = target.getParent().toFile();
        if ( !dir.exists() )
        {
          assertTrue( dir.mkdirs() );
        }
        Files.copy( fileObject.openInputStream(), target );
      }
    }
    final JavaFileObject firstExpected = fixture( outputs.get( 0 ) );
    final JavaFileObject[] restExpected =
      outputs.stream().skip( 1 ).map( this::fixture ).
        collect( Collectors.toList() ).
        toArray( new JavaFileObject[ 0 ] );
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      processedWith( new ReactProcessor(), new ArezProcessor() ).
      compilesWithoutError().
      and().
      generatesSources( firstExpected, restExpected );
  }

  void assertFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
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
    throws Exception
  {
    final JavaFileObject source = fixture( inputResource );
    assert_().about( JavaSourceSubjectFactory.javaSource() ).
      that( source ).
      processedWith( new ReactProcessor(), new ArezProcessor() ).
      failsToCompile().
      withErrorContaining( errorMessageFragment );
  }

  @Nonnull
  final JavaFileObject fixture( @Nonnull final String path )
  {
    try
    {
      return JavaFileObjects.forResource( fixtureDir().resolve( path ).toUri().toURL() );
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
