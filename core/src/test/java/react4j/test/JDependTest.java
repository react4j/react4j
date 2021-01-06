package react4j.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jdepend.framework.DependencyConstraint;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class JDependTest
{
  @Test
  public void dependencyAnalysis()
    throws Exception
  {
    final JDepend jdepend = new JDepend( PackageFilter.all().excluding( "java.*", "javax.*" ) );
    jdepend.addDirectory( compileTargetDir() );
    jdepend.analyze();

    final DependencyConstraint constraint = new DependencyConstraint();

    final JavaPackage react4j = constraint.addPackage( "react4j" );
    final JavaPackage react4jInternal = constraint.addPackage( "react4j.internal" );
    constraint.addPackage( "react4j.annotations" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropAnnotations = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Core = constraint.addPackage( "elemental2.core" );
    final JavaPackage arez = constraint.addPackage( "arez" );
    final JavaPackage arezSpy = constraint.addPackage( "arez.spy" );
    final JavaPackage zemeckis = constraint.addPackage( "zemeckis" );

    react4j.dependsUpon( jsinteropAnnotations );
    react4j.dependsUpon( jsinteropBase );
    react4j.dependsUpon( braincheck );
    react4j.dependsUpon( elemental2Core );
    react4j.dependsUpon( react4jInternal );

    react4jInternal.dependsUpon( jsinteropAnnotations );
    react4jInternal.dependsUpon( jsinteropBase );
    react4jInternal.dependsUpon( elemental2Core );
    react4jInternal.dependsUpon( react4j );
    react4jInternal.dependsUpon( arez );
    react4jInternal.dependsUpon( arezSpy );
    react4jInternal.dependsUpon( zemeckis );

    final DependencyConstraint.MatchResult result = jdepend.analyzeDependencies( constraint );

    final List<JavaPackage> undefinedPackages = result.getUndefinedPackages();
    if ( !undefinedPackages.isEmpty() )
    {
      fail( "Undefined Packages: " +
            undefinedPackages.stream().map( Object::toString ).collect( Collectors.joining( ", " ) ) );
    }

    final List<JavaPackage[]> nonMatchingPackages = result.getNonMatchingPackages();
    if ( !nonMatchingPackages.isEmpty() )
    {
      final StringBuilder sb = new StringBuilder();
      sb.append( "Discovered packages where relationships do not align.\n" );
      for ( final JavaPackage[] packages : nonMatchingPackages )
      {
        final JavaPackage expected = packages[ 0 ];
        final JavaPackage actual = packages[ 1 ];

        final ArrayList<JavaPackage> oldAfferents = new ArrayList<>( expected.getAfferents() );
        oldAfferents.removeAll( actual.getAfferents() );

        oldAfferents.forEach( p -> sb
          .append( "Package " )
          .append( p.getName() )
          .append( " no longer depends upon " )
          .append( expected.getName() )
          .append( "\n" )
        );

        final ArrayList<JavaPackage> newAfferents = new ArrayList<>( actual.getAfferents() );
        newAfferents.removeAll( expected.getAfferents() );

        newAfferents.forEach( p -> sb
          .append( "Package " )
          .append( p.getName() )
          .append( " now depends upon " )
          .append( expected.getName() )
          .append( "\n" )
        );

        final ArrayList<JavaPackage> oldEfferents = new ArrayList<>( expected.getEfferents() );
        oldEfferents.removeAll( actual.getEfferents() );

        oldEfferents.forEach( p -> sb
          .append( "Package " )
          .append( expected.getName() )
          .append( " no longer depends upon " )
          .append( p.getName() )
          .append( "\n" )
        );

        final ArrayList<JavaPackage> newEfferents = new ArrayList<>( actual.getEfferents() );
        newEfferents.removeAll( expected.getEfferents() );

        newEfferents.forEach( p -> sb
          .append( "Package " )
          .append( expected.getName() )
          .append( " now depends upon " )
          .append( p.getName() )
          .append( "\n" )
        );
      }

      final String message =
        Arrays.stream( sb.toString().split( "\\n" ) ).sorted().distinct().collect( Collectors.joining( "\n" ) );
      fail( message );
    }
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "react4j.core.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.core.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
