package react4j.dom.proptypes.html.attributeTypes;

import javax.annotation.Nonnull;

/**
 * An enumeration describing the whether elements can have their values automatically completed by the browser.
 */
public final class OnOff
{
  /**
   * The browser may not automatically complete entries.
   */
  @Nonnull
  public static final String off = "off";
  /**
   * The browser may automatically complete entries.
   */
  @Nonnull
  public static final String on = "on";

  private OnOff()
  {
  }
}
