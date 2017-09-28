package react.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;
import org.realityforge.arez.processor.ArezProcessor;
import static com.google.common.truth.Truth.assert_;
import static org.testng.Assert.*;

@SuppressWarnings( "Duplicates" )
abstract class AbstractReactProcessorTest
{
  void assertSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "\\." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    final StringBuilder factory = new StringBuilder();
    final StringBuilder nativeComponent = new StringBuilder();
    input.append( "input" );
    factory.append( "expected" );
    nativeComponent.append( "expected" );
    for ( int i = 0; i < elements.length; i++ )
    {
      input.append( '/' );
      input.append( elements[ i ] );
      factory.append( '/' );
      factory.append( elements[ i ] );
      nativeComponent.append( '/' );
      final boolean isLastElement = i == elements.length - 1;
      if ( isLastElement )
      {
        nativeComponent.append( "React_" );
      }
      nativeComponent.append( elements[ i ] );
      if ( isLastElement )
      {
        factory.append( "_" );
      }
    }
    input.append( ".java" );
    factory.append( ".java" );
    nativeComponent.append( ".java" );
    assertSuccessfulCompile( input.toString(), factory.toString(), nativeComponent.toString() );
  }

  void assertSuccessfulCompile( @Nonnull final String inputResource, @Nonnull final String... expectedOutputResources )
    throws Exception
  {
    final JavaFileObject source = JavaFileObjects.forResource( inputResource );
    assertSuccessfulCompile( Collections.singletonList( source ), Arrays.asList( expectedOutputResources ) );
  }

  private void assertSuccessfulCompile( @Nonnull final List<JavaFileObject> inputs,
                                        @Nonnull final List<String> outputs )
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
    final JavaFileObject firstExpected = JavaFileObjects.forResource( outputs.get( 0 ) );
    final JavaFileObject[] restExpected =
      outputs.stream().skip( 1 ).map( JavaFileObjects::forResource ).
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
    final JavaFileObject source = JavaFileObjects.forResource( inputResource );
    assert_().about( JavaSourceSubjectFactory.javaSource() ).
      that( source ).
      processedWith( new ReactProcessor(), new ArezProcessor() ).
      failsToCompile().
      withErrorContaining( errorMessageFragment );
  }

  private Path fixtureDir()
  {
    final String fixtureDir = System.getProperty( "react.fixture_dir" );
    assertNotNull( fixtureDir,
                   "Expected System.getProperty( \"react.fixture_dir\" ) to return fixture directory if arez.output_fixture_data=true" );

    return new File( fixtureDir ).toPath();
  }

  private boolean outputFiles()
  {
    return System.getProperty( "react.output_fixture_data", "false" ).equals( "true" );
  }
}
