package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumeration describing the whether elements can have their values automatically completed by the browser.
 */
@Documented
@MagicConstant( valuesFromClass = OnOff.class )
public @interface OnOff
{
  /**
   * The browser may not automatically complete entries.
   */
  @Nonnull
  String off = "off";
  /**
   * The browser may automatically complete entries.
   */
  @Nonnull
  String on = "on";
}
