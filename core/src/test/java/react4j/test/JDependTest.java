package react4j.test;

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
    constraint.addPackage( "react4j.annotations" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropAnnotations = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage elemental2Core = constraint.addPackage( "elemental2.core" );

    react4j.dependsUpon( jsinteropAnnotations );
    react4j.dependsUpon( jsinteropBase );
    react4j.dependsUpon( braincheck );
    react4j.dependsUpon( elemental2Core );

    final DependencyConstraint.MatchResult result = jdepend.analyzeDependencies( constraint );

    assertEquals( result.getUndefinedPackages().size(), 0, "Undefined Packages: " + result.getUndefinedPackages() );

    assertTrue( result.matches(),
                "NonMatchingPackages: " +
                result.getNonMatchingPackages().stream().map( Arrays::asList ).collect( Collectors.toList() ) );
  }

  @Nonnull
  private String compileTargetDir()
  {
    final String fixtureDir = System.getProperty( "react4j.core.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.core.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
