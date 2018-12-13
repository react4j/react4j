package react4j.arez.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import jdepend.framework.DependencyConstraint;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ArezJDependTest
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
    final JavaPackage react4jDom = constraint.addPackage( "react4j.dom" );
    final JavaPackage react4jArez = constraint.addPackage( "react4j.arez" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Promise = constraint.addPackage( "elemental2.promise" );
    final JavaPackage arez = constraint.addPackage( "arez" );
    final JavaPackage arezSpy = constraint.addPackage( "arez.spy" );

    react4jArez.dependsUpon( react4j );
    react4jArez.dependsUpon( jsinteropBase );
    react4jArez.dependsUpon( arez );
    react4jArez.dependsUpon( arezSpy );
    react4jArez.dependsUpon( elemental2Promise );

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
          .append( " no longer depends depends upon " )
          .append( p.getName() )
          .append( "\n" )
        );

        final ArrayList<JavaPackage> newEfferents = new ArrayList<>( actual.getEfferents() );
        newEfferents.removeAll( expected.getEfferents() );

        newEfferents.forEach( p -> sb
          .append( "Package " )
          .append( expected.getName() )
          .append( " now upon " )
          .append( p.getName() )
          .append( "\n" )
        );
      }
      fail( sb.toString() );
    }
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "react4j.arez.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.arez.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
