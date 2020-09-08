package react4j.dom.proptypes.html.attributeTypes;

import javax.annotation.Nonnull;

/**
 * An enumerated value to provide a hint to the browser about what the author thinks will lead to the best user experience.
 */
public final class AudioPreload
{
  /**
   * Indicates that the audio should not be preloaded.
   */
  @Nonnull
  public static final String none = "none";
  /**
   * Indicates that only audio metadata (e.g. length) is fetched.
   */
  @Nonnull
  public static final String metadata = "metadata";
  /**
   * Indicates that the whole audio file can be downloaded, even if the user is not expected to use it.
   */
  @Nonnull
  public static final String auto = "auto";

  private AudioPreload()
  {
  }
}
