package react4j.dom.test;

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

public class DomJDependTest
{
  @Test
  public void dependencyAnalysis()
    throws Exception
  {
    final JDepend jdepend = new JDepend( PackageFilter.all().excluding( "java.*", "javax.*" ) );
    jdepend.addDirectory( compileTargetDir() );
    jdepend.analyze();

    final DependencyConstraint constraint = new DependencyConstraint();

    final JavaPackage arez = constraint.addPackage( "arez" );
    final JavaPackage react4j = constraint.addPackage( "react4j" );
    final JavaPackage react4jDom = constraint.addPackage( "react4j.dom" );
    final JavaPackage react4jDomEvents = constraint.addPackage( "react4j.dom.events" );
    constraint.addPackage( "react4j.dom.proptypes.cssPropertyTypes" );
    final JavaPackage react4jDomHtml = constraint.addPackage( "react4j.dom.proptypes.html" );
    constraint.addPackage( "react4j.dom.proptypes.html.attributeTypes" );
    final JavaPackage braincheck = constraint.addPackage( "org.realityforge.braincheck" );
    final JavaPackage jsinteropAnnotations = constraint.addPackage( "jsinterop.annotations" );
    final JavaPackage jsinteropBase = constraint.addPackage( "jsinterop.base" );
    final JavaPackage akasha = constraint.addPackage( "akasha" );
    final JavaPackage akashaCore = constraint.addPackage( "akasha.core" );

    react4jDom.dependsUpon( arez );
    react4jDom.dependsUpon( react4j );
    react4jDom.dependsUpon( jsinteropAnnotations );
    react4jDom.dependsUpon( jsinteropBase );
    react4jDom.dependsUpon( akasha );
    react4jDom.dependsUpon( react4jDomHtml );

    react4jDomEvents.dependsUpon( jsinteropAnnotations );
    react4jDomEvents.dependsUpon( akasha );
    react4jDomEvents.dependsUpon( akashaCore );

    react4jDomHtml.dependsUpon( braincheck );
    react4jDomHtml.dependsUpon( react4j );
    react4jDomHtml.dependsUpon( jsinteropAnnotations );
    react4jDomHtml.dependsUpon( jsinteropBase );
    react4jDomHtml.dependsUpon( react4jDomEvents );

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
    final String fixtureDir = System.getProperty( "react4j.dom.compile_target" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"react4j.dom.compile_target\" ) to return directory" );
    return new File( fixtureDir ).getAbsolutePath();
  }
}
