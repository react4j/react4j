package react4j.downstream;

import gir.Gir;
import gir.delta.Patch;
import gir.git.Git;
import gir.ruby.Buildr;
import gir.ruby.Ruby;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class BuildDownstream
{
  public static void main( final String[] args )
  {
    try
    {
      run();
    }
    catch ( final Exception e )
    {
      System.err.println( "Failed command." );
      e.printStackTrace( System.err );
      System.exit( 42 );
    }
  }

  private static void run()
    throws Exception
  {
    Gir.go( () -> Stream.of( "react4j-widget", "react4j-windowportal" )
      .forEach( name -> WorkspaceUtil.forEachBranch( name,
                                                     "https://github.com/react4j/" + name + ".git",
                                                     Collections.singletonList( "master" ),
                                                     BuildDownstream::buildBranch ) ) );
  }

  private static void buildBranch( @Nonnull final WorkspaceUtil.BuildContext context )
  {
    final boolean initialBuildSuccess = WorkspaceUtil.runBeforeBuild( context, () -> {
      WorkspaceUtil.customizeBuildr( context.appDirectory );
      Ruby.buildr( "clean", "package", "PRODUCT_VERSION=", "PREVIOUS_PRODUCT_VERSION=" );
    } );

    WorkspaceUtil.runAfterBuild( context, initialBuildSuccess, () -> {
      final String version = WorkspaceUtil.getVersion();
      final String group = "org.realityforge.react4j";
      final boolean patched =
        Patch.patchAndAddFile( context.appDirectory,
                               context.appDirectory.resolve( "build.yaml" ),
                               c -> Buildr.patchMavenCoordinates( c, group, version ) );
      if ( patched )
      {
        final String message = "Update the '" + group + "' dependencies to version '" + version + "'";
        final Function<String, String> patchFunction = c -> {
          if ( c.contains( "### Unreleased\n\n#" ) )
          {
            return c.replace( "### Unreleased\n\n", "### Unreleased\n\n* " + message + "\n\n" );
          }
          else
          {
            return c.replace( "### Unreleased\n\n", "### Unreleased\n\n* " + message + "\n" );
          }
        };
        Patch.patchAndAddFile( context.appDirectory,
                               context.appDirectory.resolve( "CHANGELOG.md" ),
                               patchFunction );
        Git.commit( message );
      }

      WorkspaceUtil.customizeBuildr( context.appDirectory );
      /*
       * The process will run through the steps required for a release right up to tagging the source repository.
       * A subsequent call from within release.rake will complete the release process.
       */
      Ruby.buildr( "perform_release", "LAST_STAGE=TagProject", "PRODUCT_VERSION=", "PREVIOUS_PRODUCT_VERSION=" );
    }, null );
  }
}
