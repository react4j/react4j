package react4j.downstream;

import gir.Gir;
import gir.io.FileUtil;
import gir.ruby.Buildr;
import gir.ruby.Ruby;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public final class CollectFluxChallengeBuildStats
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
    Gir.go( () -> {
      final List<String> branches = Collections.singletonList( "master" );
      WorkspaceUtil.forEachBranch( "react4j-flux-challenge",
                                   "https://github.com/react4j/react4j-flux-challenge.git",
                                   branches,
                                   context -> buildBranch( context, WorkspaceUtil.getVersion() ) );
      WorkspaceUtil.collectStatistics( branches, branch -> true, false );
    } );
  }

  private static void buildBranch( @Nonnull final WorkspaceUtil.BuildContext context,
                                   @Nonnull final String version )
  {

    final boolean initialBuildSuccess = WorkspaceUtil.runBeforeBuild( context, () -> {
      final String prefix = context.branch + ".before";
      final Path archiveDir = WorkspaceUtil.getArchiveDir( context.workingDirectory, prefix );
      buildAndRecordStatistics( context.appDirectory, archiveDir );
    } );

    WorkspaceUtil.runAfterBuild( context, initialBuildSuccess, () -> {
      Buildr.patchBuildYmlDependency( context.appDirectory, "org.realityforge.react4j", version );

      final String prefix = context.branch + ".after";
      final Path archiveDir = WorkspaceUtil.getArchiveDir( context.workingDirectory, prefix );
      buildAndRecordStatistics( context.appDirectory, archiveDir );
    }, () -> {
      final Path dir = WorkspaceUtil.getArchiveDir( context.workingDirectory, context.branch + ".after" );
      FileUtil.deleteDirIfExists( dir );
    } );
  }

  private static void buildAndRecordStatistics( @Nonnull final Path appDirectory, @Nonnull final Path archiveDir )
  {
    WorkspaceUtil.customizeBuildr( appDirectory );

    if ( !archiveDir.toFile().mkdirs() )
    {
      final String message = "Error creating archive directory: " + archiveDir;
      Gir.messenger().error( message );
    }

    // Perform the build
    Ruby.buildr( "clean", "package", "EXCLUDE_GWT_DEV_MODULE=true", "GWT=react4j-sithtracker" );

    archiveBuildrOutput( archiveDir );
    archiveStatistics( archiveDir );
  }

  private static void archiveStatistics( @Nonnull final Path archiveDir )
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.setProperty( "sithtracker.size", String.valueOf( getJsSize( archiveDir ) ) );
    properties.setProperty( "sithtracker.gz.size", String.valueOf( getJsGzSize( archiveDir ) ) );

    final Path statisticsFile = archiveDir.resolve( "statistics.properties" );
    Gir.messenger().info( "Archiving statistics to " + statisticsFile + "." );
    WorkspaceUtil.writeProperties( statisticsFile, properties );
  }

  private static void archiveBuildrOutput( @Nonnull final Path archiveDir )
  {
    final Path currentDirectory = FileUtil.getCurrentDirectory();
    WorkspaceUtil.archiveDirectory( currentDirectory.resolve( "target/assets" ), archiveDir.resolve( "assets" ) );
    WorkspaceUtil.archiveDirectory( currentDirectory
                                      .resolve( "target/gwt_compile_reports/react4j.sithtracker.SithTrackerProd" ),
                                    archiveDir.resolve( "compileReports" ) );
  }

  private static long getJsSize( @Nonnull final Path archiveDir )
  {
    return WorkspaceUtil.getFileSize( archiveDir.resolve( "assets" )
                                        .resolve( "sithtracker" )
                                        .resolve( "sithtracker.nocache.js" ) );
  }

  private static long getJsGzSize( @Nonnull final Path archiveDir )
  {
    return WorkspaceUtil.getFileSize( archiveDir.resolve( "assets" )
                                        .resolve( "sithtracker" )
                                        .resolve( "sithtracker.nocache.js.gz" ) );
  }
}
