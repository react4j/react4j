package react4j.downstream;

import gir.Gir;
import gir.GirException;
import gir.delta.Patch;
import gir.git.Git;
import gir.io.Exec;
import gir.io.FileUtil;
import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class WorkspaceUtil
{
  private WorkspaceUtil()
  {
  }

  static boolean storeStatistics()
  {
    return System.getProperty( "react4j.deploy_test.store_statistics", "false" ).equals( "true" );
  }

  private static boolean buildBeforeChanges()
  {
    return System.getProperty( "react4j.deploy_test.build_before", "true" ).equals( "true" );
  }

  @Nonnull
  static String getVersion()
  {
    return SystemProperty.get( "react4j.next.version" );
  }

  @Nonnull
  static Path getFixtureDirectory()
  {
    return Paths.get( SystemProperty.get( "react4j.deploy_test.fixture_dir" ) ).toAbsolutePath().normalize();
  }

  @Nonnull
  private static String getLocalRepositoryUrl()
  {
    return SystemProperty.get( "react4j.deploy_test.local_repository_url" );
  }

  @Nonnull
  static Path setupWorkingDirectory()
  {
    final Path workingDirectory =
      Paths.get( SystemProperty.get( "react4j.deploy_test.work_dir" ) ).toAbsolutePath().normalize();
    if ( !workingDirectory.toFile().exists() )
    {
      Gir.messenger().info( "Working directory does not exist." );
      Gir.messenger().info( "Creating directory: " + workingDirectory );
      if ( !workingDirectory.toFile().mkdirs() )
      {
        Gir.messenger().error( "Failed to create working directory: " + workingDirectory );
      }
    }
    return workingDirectory;
  }

  static void archiveFile( @Nonnull final Path sourceFilename, @Nonnull final Path targetFilename )
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

  static void archiveDirectory( @Nonnull final Path assetsDir, @Nonnull final Path targetDir )
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

  static long getFileSize( @Nonnull final Path path )
  {
    final File file = path.toFile();
    assert file.exists();
    return file.length();
  }

  static void writeProperties( @Nonnull final Path outputFile, @Nonnull final OrderedProperties properties )
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

  static void loadStatistics( @Nonnull final OrderedProperties statistics,
                              @Nonnull final Path archiveDir,
                              @Nonnull final String keyPrefix )
    throws IOException
  {
    final Properties properties = new Properties();
    properties.load( Files.newBufferedReader( archiveDir.resolve( "statistics.properties" ) ) );
    properties.forEach( ( key, value ) -> statistics.put( keyPrefix + "." + key, value ) );
  }

  @Nonnull
  static Path getArchiveDir( @Nonnull final Path workingDirectory, @Nonnull final String build )
  {
    return workingDirectory.resolve( "archive" ).resolve( build );
  }

  static void customizeMaven( @Nonnull final Path appDirectory )
  {
    customizeMaven( appDirectory, getLocalRepositoryUrl() );
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

  static void customizeBuildr( @Nonnull final Path appDirectory )
  {
    customizeBuildr( appDirectory, getLocalRepositoryUrl() );
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

  static void forEachBranch( @Nonnull final String name,
                             @Nonnull final String repositoryUrl,
                             @Nonnull final List<String> branches,
                             @Nonnull final Consumer<BuildContext> action )
  {
    final Path workingDirectory = setupWorkingDirectory();
    FileUtil.inDirectory( workingDirectory, () -> {
      Gir.messenger().info( "Cloning " + name + " into " + workingDirectory );
      Git.clone( repositoryUrl, name );
      final Path appDirectory = workingDirectory.resolve( name );
      FileUtil.inDirectory( appDirectory, () -> {
        Git.fetch();
        Git.resetBranch();
        Git.checkout();
        Git.pull();
        Git.deleteLocalBranches();
        branches.forEach( branch -> {
          Gir.messenger().info( "Processing branch " + branch + "." );

          Git.checkout( branch );
          Git.clean();

          action.accept( new BuildContext( workingDirectory, appDirectory, branch ) );

        } );
      } );
    } );
  }

  @Nonnull
  private static String switchToUpgradeBranch( @Nonnull final BuildContext context )
  {
    final String version = WorkspaceUtil.getVersion();
    final String newBranch = context.branch + "-React4jUpgrade-" + version;
    if ( Git.remoteTrackingBranches().contains( "origin/" + newBranch ) )
    {
      Git.checkout( newBranch );
      Git.resetBranch( "origin/" + newBranch );
    }
    else
    {
      Git.checkout( context.branch );
      Git.clean();
      Git.checkout( newBranch, true );
    }
    return newBranch;
  }

  static void runAfterBuild( @Nonnull final BuildContext context,
                             final boolean initialBuildSuccess,
                             @Nonnull final Action buildAction,
                             @Nullable final Runnable cleanupAction )
  {
    Gir.messenger().info( "Building branch " + context.branch + " after modifications." );
    try
    {
      final String newBranch = switchToUpgradeBranch( context );

      buildAction.call();
      Git.checkout( context.branch );
      Exec.system( "git", "merge", newBranch );
      Git.deleteBranch( newBranch );
    }
    catch ( final Throwable e )
    {
      if ( buildBeforeChanges() && !initialBuildSuccess )
      {
        Gir.messenger().error( "Failed to build branch '" + context.branch + "' before modifications " +
                               "but branch also failed prior to modifications.", e );
      }
      else
      {
        Gir.messenger().error( "Failed to build branch '" + context.branch + "' after modifications.", e );
      }
      if ( null != cleanupAction )
      {
        cleanupAction.run();
      }
      /*
       * If the build has failed for one of the downstream projects then make sure the command fails.
       */
      if ( e instanceof GirException )
      {
        throw (GirException) e;
      }
      else
      {
        throw new GirException( e );
      }
    }
  }

  @FunctionalInterface
  interface Action
  {
    void call()
      throws Exception;
  }

  static boolean runBeforeBuild( @Nonnull final BuildContext context,
                                 @Nonnull final Action action )
  {
    boolean initialBuildSuccess = false;
    if ( buildBeforeChanges() )
    {
      Gir.messenger().info( "Building branch " + context.branch + " prior to modifications." );
      try
      {
        action.call();
        initialBuildSuccess = true;
      }
      catch ( final Throwable e )
      {
        Gir.messenger().info( "Failed to build branch '" + context.branch + "' before modifications.", e );
      }

      Git.resetBranch();
      Git.clean();
    }
    return initialBuildSuccess;
  }

  static class BuildContext
  {
    @Nonnull
    final Path workingDirectory;
    @Nonnull
    final Path appDirectory;
    @Nonnull
    final String branch;

    BuildContext( @Nonnull final Path workingDirectory,
                  @Nonnull final Path appDirectory,
                  @Nonnull final String branch )
    {
      this.workingDirectory = Objects.requireNonNull( workingDirectory );
      this.appDirectory = Objects.requireNonNull( appDirectory );
      this.branch = Objects.requireNonNull( branch );
    }
  }
}
