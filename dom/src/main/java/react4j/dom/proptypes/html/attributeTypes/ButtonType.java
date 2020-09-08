package react4j.dom.proptypes.html.attributeTypes;

import javax.annotation.Nonnull;

/**
 * An enumeration describing the behavior of the button.
 */
public final class ButtonType
{
  /**
   * The button submits the form. This is the default value if the attribute is not specified, or if it is dynamically changed to an empty or invalid value.
   */
  @Nonnull
  public static final String submit = "submit";
  /**
   * The button resets the form.
   */
  @Nonnull
  public static final String reset = "reset";
  /**
   * The button has no default behaviour.
   */
  @Nonnull
  public static final String button = "button";
  /**
   * The button displays a menu.
   * This is an experimental feature.
   */
  @Nonnull
  public static final String menu = "menu";

  private ButtonType()
  {
  }
}
