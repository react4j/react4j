package react4j.dom.proptypes.cssPropertyTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant( valuesFromClass = Visibility.class )
public @interface Visibility
{
  @Nonnull
  String hidden = "hidden";
  @Nonnull
  String visible = "visible";
  @Nonnull
  String collapse = "collapse";
  @Nonnull
  String initial = "initial";
  @Nonnull
  String inherit = "inherit";
}
