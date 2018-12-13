package react4j.downstream;

import gir.Gir;
import gir.GirException;
import gir.delta.Patch;
import gir.git.Git;
import gir.io.Exec;
import gir.io.FileUtil;
import gir.maven.Maven;
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
      final String version = SystemProperty.get( "react4j.next.version" );
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
          Stream.of( "raw",
                     "arez",
                     "dagger",
                     "raw_maven",
                     "arez_maven",
                     "dagger_maven",
                     "raw_maven_j2cl",
                     "arez_maven_j2cl",
                     "dagger_maven_j2cl" ).forEach( branch -> {
            Gir.messenger().info( "Processing branch " + branch + "." );

            final boolean isMaven = branch.contains( "_maven" );
            final boolean isj2cl = branch.contains( "_j2cl" );

            Git.checkout( branch );
            Git.clean();

            Gir.messenger().info( "Building branch " + branch + " prior to modifications." );
            boolean initialBuildSuccess = false;
            try
            {
              if ( isMaven )
              {
                customizeMaven( appDirectory, localRepositoryUrl );
              }
              else
              {
                customizeBuildr( appDirectory, localRepositoryUrl );
              }

              final String prefix = branch + ".before";
              final Path archiveDir = getArchiveDir( workingDirectory, prefix );
              buildAndRecordStatistics( archiveDir, !isMaven, isj2cl );
              loadStatistics( overallStatistics, archiveDir, prefix );
              initialBuildSuccess = true;
            }
            catch ( final GirException | IOException e )
            {
              Gir.messenger().info( "Failed to build branch '" + branch + "' before modifications.", e );
            }

            Git.resetBranch();
            Git.clean();

            final String newBranch = branch + "-React4jUpgrade-" + version;
            if ( Git.remoteTrackingBranches().contains( "origin/" + newBranch ) )
            {
              Git.checkout( newBranch );
              Git.resetBranch( "origin/" + newBranch );
            }
            else
            {
              Git.checkout( branch );
              Git.clean();
              Git.checkout( newBranch, true );
            }

            if ( isMaven )
            {
              Maven.patchPomProperty( appDirectory,
                                      () -> "Update the 'react4j' dependencies to version '" + version + "'",
                                      "react4j.version",
                                      version );
            }
            else
            {
              Buildr.patchBuildYmlDependency( appDirectory, "org.realityforge.react4j", version );
            }

            Gir.messenger().info( "Building branch " + branch + " after modifications." );
            if ( isMaven )
            {
              customizeMaven( appDirectory, localRepositoryUrl );
            }
            else
            {
              customizeBuildr( appDirectory, localRepositoryUrl );
            }

            final String prefix = branch + ".after";
            final Path archiveDir = getArchiveDir( workingDirectory, prefix );
            try
            {
              buildAndRecordStatistics( archiveDir, !isMaven, isj2cl );
              loadStatistics( overallStatistics, archiveDir, prefix );
              if ( !isMaven || isj2cl )
              {
                loadStatistics( fixtureStatistics, archiveDir, version + "." + branch );
              }
              if ( isMaven )
              {
                // Reset is required to remove changes that were made to the pom to add local repository
                Git.resetBranch();
              }
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

  private static void customizeMaven( @Nonnull final Path appDirectory, @Nonnull final String localRepositoryUrl )
  {
    final String replacement =
      "<repositories>\n" +
      "    <repository>\n" +
      "      <id>local-repository</id>\n" +
      "      <url>" + localRepositoryUrl + "</url>\n" +
      "    </repository>";
    if ( !Patch.file( appDirectory.resolve( "pom.xml" ), c -> c.replace( "<repositories>", replacement ) ) )
    {
      Gir.messenger().error( "Failed to patch pom.xml to add local repository." );
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
      Patch.file( outputFile, c -> c.replaceAll( "#.*\n", "" ) );
    }
    catch ( final IOException ioe )
    {
      final String message = "Failed to write properties file: " + outputFile;
      Gir.messenger().error( message, ioe );
      throw new GirException( message, ioe );
    }
  }

  private static void buildAndRecordStatistics( @Nonnull final Path archiveDir,
                                                final boolean useBuildr,
                                                final boolean isj2cl )
  {
    if ( !archiveDir.toFile().mkdirs() )
    {
      final String message = "Error creating archive directory: " + archiveDir;
      Gir.messenger().error( message );
    }

    if ( useBuildr )
    {
      // Perform the build
      Ruby.buildr( "clean", "package", "EXCLUDE_GWT_DEV_MODULE=true", "GWT=react4j-todomvc" );

      archiveBuildrOutput( archiveDir );
    }
    else if ( isj2cl )
    {
      // Assume maven
      Exec.system( "mvn", "clean", "package", "-Pdevmode" );

      archivej2clOutput( archiveDir );
    }
    else
    {
      // Assume maven
      Exec.system( "mvn", "clean", "package" );

      archiveMavenOutput( archiveDir );
    }
    archiveStatistics( archiveDir );
  }

  private static void archiveStatistics( @Nonnull final Path archiveDir )
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.setProperty( "todomvc.size", String.valueOf( getTodoMvcSize( archiveDir ) ) );
    final long todoMvcGzSize = getTodoMvcGzSize( archiveDir );
    if ( 0 != todoMvcGzSize )
    {
      //j2cl does not produce a gzipped version so no need to include 0 all the time
      properties.setProperty( "todomvc.gz.size", String.valueOf( todoMvcGzSize ) );
    }

    final Path statisticsFile = archiveDir.resolve( "statistics.properties" );
    Gir.messenger().info( "Archiving statistics to " + statisticsFile + "." );
    writeProperties( statisticsFile, properties );
  }

  private static void archiveBuildrOutput( @Nonnull final Path archiveDir )
  {
    final Path currentDirectory = FileUtil.getCurrentDirectory();
    archiveDirectory( currentDirectory.resolve( "target/assets" ), archiveDir.resolve( "assets" ) );
    archiveDirectory( currentDirectory.resolve( "target/gwt_compile_reports/react4j.todomvc.TodomvcProd" ),
                      archiveDir.resolve( "compileReports" ) );
  }

  private static void archivej2clOutput( @Nonnull final Path archiveDir )
  {
    archiveDirectory( FileUtil.getCurrentDirectory().resolve( "out/sources" ), archiveDir.resolve( "sources" ) );
    archiveFile( FileUtil.getCurrentDirectory().resolve( "out/app.js" ),
                 archiveDir.resolve( "assets/todomvc/todomvc.nocache.js" ) );
    archiveFile( FileUtil.getCurrentDirectory().resolve( "out/app.map" ),
                 archiveDir.resolve( "assets/todomvc/todomvc.nocache.map" ) );
  }

  private static void archiveMavenOutput( @Nonnull final Path archiveDir )
  {
    archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/react4j-todomvc-1.0.0-SNAPSHOT" ),
                      archiveDir.resolve( "assets" ) );
    archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/extras" ),
                      archiveDir.resolve( "compileReports" ) );
  }

  private static void archiveFile( @Nonnull final Path sourceFilename, @Nonnull final Path targetFilename )
  {
    Gir.messenger().info( "Archiving output file " + sourceFilename + " to " + targetFilename );
    try
    {
      final Path dir = targetFilename.getParent();
      if ( !dir.toFile().exists() )
      {
        if ( !dir.toFile().mkdirs() )
        {
          Gir.messenger().error( "Failed to create archive directory: " + dir );
        }
      }
      Files.copy( sourceFilename, targetFilename );
    }
    catch ( final IOException e )
    {
      final String message = "Failed to archive file: " + sourceFilename;
      Gir.messenger().error( message, e );
    }
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

  private static long getTodoMvcSize( @Nonnull final Path archiveDir )
  {
    return getFileSize( archiveDir.resolve( "assets" ).resolve( "todomvc" ).resolve( "todomvc.nocache.js" ) );
  }

  private static long getTodoMvcGzSize( @Nonnull final Path archiveDir )
  {
    return getFileSize( archiveDir.resolve( "assets" ).resolve( "todomvc" ).resolve( "todomvc.nocache.js.gz" ) );
  }

  private static long getFileSize( @Nonnull final Path path )
  {
    final File file = path.toFile();
    assert file.exists();
    return file.length();
  }
}
