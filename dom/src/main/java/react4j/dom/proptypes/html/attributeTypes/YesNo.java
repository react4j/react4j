package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant( valuesFromClass = YesNo.class )
public @interface YesNo
{
  @Nonnull
  String yes = "yes";
  @Nonnull
  String no = "no";
}
