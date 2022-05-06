package react4j.dom.proptypes.cssPropertyTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant( valuesFromClass = AnimationFillMode.class )
public @interface AnimationFillMode
{
  @Nonnull
  String none = "none";
  @Nonnull
  String forwards = "forwards";
  @Nonnull
  String backwards = "backwards";
  @Nonnull
  String both = "both";
  @Nonnull
  String initial = "initial";
  @Nonnull
  String inherit = "inherit";
}
