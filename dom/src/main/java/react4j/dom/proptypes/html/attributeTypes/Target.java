package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant( valuesFromClass = Target.class )
public @interface Target
{
  @Nonnull
  String blank = "_blank";
  @Nonnull
  String self = "_self";
  @Nonnull
  String parent = "_parent";
  @Nonnull
  String top = "_top";
}
