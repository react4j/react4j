package react4j.arez.test;

import java.io.File;
import java.util.Arrays;
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
    final JavaPackage annotations = constraint.addPackage( "react4j.annotations" );
    final JavaPackage react4jDom = constraint.addPackage( "react4j.dom" );
    final JavaPackage react4jArez = constraint.addPackage( "react4j.arez" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropAnnotations = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Core = constraint.addPackage( "elemental2.core" );
    final JavaPackage elemental2Promise = constraint.addPackage( "elemental2.promise" );
    final JavaPackage arez = constraint.addPackage( "arez" );
    final JavaPackage arezSpy = constraint.addPackage( "arez.spy" );

    annotations.dependsUpon( jsinteropAnnotations );

    react4jArez.dependsUpon( react4j );
    react4jArez.dependsUpon( jsinteropBase );
    react4jArez.dependsUpon( braincheck );
    react4jArez.dependsUpon( arez );
    react4jArez.dependsUpon( arezSpy );
    react4jArez.dependsUpon( elemental2Core );
    react4jArez.dependsUpon( elemental2Promise );
    react4jArez.dependsUpon( react4jDom );

    final DependencyConstraint.MatchResult result = jdepend.analyzeDependencies( constraint );

    assertEquals( result.getUndefinedPackages().size(), 0, "Undefined Packages: " + result.getUndefinedPackages() );

    assertTrue( result.matches(),
                "NonMatchingPackages: " +
                result.getNonMatchingPackages().stream().map( Arrays::asList ).collect( Collectors.toList() ) );
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "react4j.arez.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.arez.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
