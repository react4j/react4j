package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumerated value to provide a hint to the browser about what the author thinks will lead to the best user experience.
 */
@Documented
@MagicConstant( valuesFromClass = ButtonType.class )
public @interface AudioPreload
{
  /**
   * Indicates that the audio should not be preloaded.
   */
  @Nonnull
  String none = "none";
  /**
   * Indicates that only audio metadata (e.g. length) is fetched.
   */
  @Nonnull
  String metadata = "metadata";
  /**
   * Indicates that the whole audio file can be downloaded, even if the user is not expected to use it.
   */
  @Nonnull
  String auto = "auto";
}
