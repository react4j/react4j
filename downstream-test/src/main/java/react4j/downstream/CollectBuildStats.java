package react4j.downstream;

import gir.Gir;
import gir.GirException;
import gir.delta.Patch;
import gir.git.Git;
import gir.io.Exec;
import gir.io.FileUtil;
import gir.ruby.Buildr;
import gir.ruby.Ruby;
import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class CollectBuildStats
{
  public static void main( final String[] args )
    throws Exception
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
      final boolean storeStatistics =
        System.getProperty( "react4j.deploy_test.store_statistics", "false" ).equals( "true" );
      final String version = SystemProperty.get( "react4j.version" );
      final OrderedProperties overallStatistics = new OrderedProperties();
      final Path workingDirectory =
        Paths.get( SystemProperty.get( "react4j.deploy_test.work_dir" ) ).toAbsolutePath().normalize();
      final Path fixtureDirectory =
        Paths.get( SystemProperty.get( "react4j.deploy_test.fixture_dir" ) ).toAbsolutePath().normalize();

      final Path path = fixtureDirectory.resolve( "statistics.properties" );
      final OrderedProperties fixtureStatistics = new OrderedProperties();
      fixtureStatistics.load( Files.newBufferedReader( path ) );

      final String localRepositoryUrl = SystemProperty.get( "react4j.deploy_test.local_repository_url" );

      if ( !workingDirectory.toFile().exists() )
      {
        Gir.messenger().info( "Working directory does not exist." );
        Gir.messenger().info( "Creating directory: " + workingDirectory );
        if ( !workingDirectory.toFile().mkdirs() )
        {
          Gir.messenger().error( "Failed to create working directory: " + workingDirectory );
        }
      }

      FileUtil.inDirectory( workingDirectory, () -> {
        Gir.messenger().info( "Cloning react4j-todomvc into " + workingDirectory );
        Git.clone( "https://github.com/react4j/react4j-todomvc.git", "react4j-todomvc" );
        final Path appDirectory = workingDirectory.resolve( "react4j-todomvc" );
        FileUtil.inDirectory( appDirectory, () -> {
          Git.fetch();
          Git.resetBranch();
          Git.checkout();
          Git.pull();
          Git.deleteLocalBranches();
          Stream.of( "raw", "arez", "dagger" ).forEach( branch -> {
            Gir.messenger().info( "Processing branch " + branch + "." );

            Git.checkout( branch );
            Git.clean();
            final String newBranch = branch + "-React4jUpgrade-" + version;
            System.out.println( "Checking out " + newBranch );
            Git.checkout( newBranch, true );
            if ( Git.remoteTrackingBranches().contains( "origin/" + newBranch ) )
            {
              Git.pull();
            }
            Git.clean();

            Gir.messenger().info( "Building branch " + branch + " prior to modifications." );
            boolean initialBuildSuccess = false;
            try
            {
              customizeBuildr( appDirectory, localRepositoryUrl );

              final String prefix = branch + ".before";
              final Path archiveDir = getArchiveDir( workingDirectory, prefix );
              buildAndRecordStatistics( archiveDir );
              loadStatistics( overallStatistics, archiveDir, prefix );
              loadStatistics( fixtureStatistics, archiveDir, version + "." + branch );
              initialBuildSuccess = true;
            }
            catch ( final GirException | IOException e )
            {
              Gir.messenger().info( "Failed to build branch '" + branch + "' before modifications.", e );
            }

            Git.clean();

            if ( Buildr.patchBuildYmlDependency( appDirectory, "org.realityforge.react4j", version ) )
            {
              Gir.messenger().info( "Building branch " + branch + " after modifications." );
              customizeBuildr( appDirectory, localRepositoryUrl );

              final String prefix = branch + ".after";
              final Path archiveDir = getArchiveDir( workingDirectory, prefix );
              try
              {
                buildAndRecordStatistics( archiveDir );
                loadStatistics( overallStatistics, archiveDir, prefix );
                loadStatistics( fixtureStatistics, archiveDir, version + "." + branch );
                Git.checkout( branch );
                Exec.system( "git", "merge", newBranch );
                Git.deleteBranch( newBranch );
              }
              catch ( final GirException | IOException e )
              {
                if ( !initialBuildSuccess )
                {
                  Gir.messenger().error( "Failed to build branch '" + branch + "' before modifications " +
                                         "but branch also failed prior to modifications.", e );
                }
                else
                {
                  Gir.messenger().error( "Failed to build branch '" + branch + "' after modifications.", e );
                }
                FileUtil.deleteDir( archiveDir );
                /*
                 * If the build has failed for one of the downstream projects then make sure the command fails.
                 */
                if ( e instanceof GirException )
                {
                  //noinspection RedundantCast
                  throw (GirException) e;
                }
                else
                {
                  throw new GirException( e );
                }
              }
            }
            else
            {
              Gir.messenger().info( "Branch " + branch + " not rebuilt as no modifications made." );
              Git.checkout( branch );
              Git.deleteBranch( newBranch );
              /*
               * We copy the before build to after directory as later steps will perform further analysis
               * so we need to have the build on the filesystem.
               */
              final String build = branch + ".after";
              final Path archiveDir = getArchiveDir( workingDirectory, build );
              FileUtil.copyDirectory( getArchiveDir( workingDirectory, branch + ".before" ), archiveDir );
              try
              {
                loadStatistics( overallStatistics, archiveDir, build );
                loadStatistics( fixtureStatistics, archiveDir, version + "." + branch );
              }
              catch ( final IOException e )
              {
                Gir.messenger().error( "Failed to load statistics for branch '" + branch + "'.", e );
                throw new GirException( e );
              }
            }
          } );
        } );
      } );

      overallStatistics.keySet().forEach( k -> System.out.println( k + ": " + overallStatistics.get( k ) ) );
      final Path statisticsFile = workingDirectory.resolve( "statistics.properties" );
      Gir.messenger().info( "Writing overall build statistics to " + statisticsFile + "." );
      writeProperties( statisticsFile, overallStatistics );

      if ( storeStatistics )
      {
        Gir.messenger().info( "Updating fixture build statistics at" + path + "." );
        writeProperties( path, fixtureStatistics );
      }
    } );
  }

  private static void customizeBuildr( @Nonnull final Path appDirectory, @Nonnull final String localRepositoryUrl )
  {
    try
    {
      final String content = "repositories.remote.unshift('" + localRepositoryUrl + "')\n";
      Files.write( appDirectory.resolve( "_buildr.rb" ), content.getBytes() );
    }
    catch ( final IOException ioe )
    {
      Gir.messenger().error( "Failed to emit _buildr.rb configuration file.", ioe );
    }
  }

  @Nonnull
  private static Path getArchiveDir( @Nonnull final Path workingDirectory, @Nonnull final String build )
  {
    return workingDirectory.resolve( "archive" ).resolve( build );
  }

  private static void loadStatistics( @Nonnull final OrderedProperties statistics,
                                      @Nonnull final Path archiveDir,
                                      @Nonnull final String keyPrefix )
    throws IOException
  {
    final Properties properties = new Properties();
    properties.load( Files.newBufferedReader( archiveDir.resolve( "statistics.properties" ) ) );
    properties.forEach( ( key, value ) -> statistics.put( keyPrefix + "." + key, value ) );
  }

  private static void writeProperties( @Nonnull final Path outputFile, @Nonnull final OrderedProperties properties )
  {
    try
    {
      properties.store( new FileWriter( outputFile.toFile() ), "" );
      Patch.file( outputFile, c -> c.replaceAll( "\\#.*\n", "" ) );
    }
    catch ( final IOException ioe )
    {
      final String message = "Failed to write properties file: " + outputFile;
      Gir.messenger().error( message, ioe );
      throw new GirException( message, ioe );
    }
  }

  private static void buildAndRecordStatistics( @Nonnull final Path archiveDir )
  {
    if ( !archiveDir.toFile().mkdirs() )
    {
      final String message = "Error creating archive directory: " + archiveDir;
      Gir.messenger().error( message );
    }

    // Perform the build
    Ruby.buildr( "clean", "package", "EXCLUDE_GWT_DEV_MODULE=true", "GWT=react4j-todomvc" );

    archiveOutput( archiveDir );
    archiveStatistics( archiveDir );
  }

  private static void archiveStatistics( @Nonnull final Path archiveDir )
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.setProperty( "todomvc.size", String.valueOf( getTodoMvcSize() ) );
    properties.setProperty( "todomvc.gz.size", String.valueOf( getTodoMvcGzSize() ) );

    final Path statisticsFile = archiveDir.resolve( "statistics.properties" );
    Gir.messenger().info( "Archiving statistics to " + statisticsFile + "." );
    writeProperties( statisticsFile, properties );
  }

  private static void archiveOutput( @Nonnull final Path archiveDir )
  {
    archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/assets" ), archiveDir.resolve( "assets" ) );
    archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/gwt_compile_reports" ), archiveDir.resolve( "compileReports" ) );
  }

  private static void archiveDirectory( @Nonnull final Path assetsDir, @Nonnull final Path targetDir )
  {
    Gir.messenger().info( "Archiving output " + assetsDir + " to " + targetDir );
    try
    {
      FileUtil.copyDirectory( assetsDir, targetDir );
    }
    catch ( final GirException e )
    {
      final String message = "Failed to archive directory: " + assetsDir;
      Gir.messenger().error( message, e );
    }
  }

  private static long getTodoMvcSize()
  {
    return getFileSize( "target/generated/gwt/react4j.todomvc.TodomvcProd/todomvc/todomvc.nocache.js" );
  }

  private static long getTodoMvcGzSize()
  {
    return getFileSize( "target/generated/gwt/react4j.todomvc.TodomvcProd/todomvc/todomvc.nocache.js.gz" );
  }

  private static long getFileSize( @Nonnull final String filename )
  {
    final File file = FileUtil.getCurrentDirectory().resolve( filename ).toFile();
    assert file.exists();
    return file.length();
  }
}
