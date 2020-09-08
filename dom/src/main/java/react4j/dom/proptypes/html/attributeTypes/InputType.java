package react4j.dom.proptypes.html.attributeTypes;

import javax.annotation.Nonnull;

/**
 * An enumeration specifying the type of input control to render.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#%3Cinput%3E_types"><code>&lt;input&gt;</code> type - MDN</a>
 */
public final class InputType
{
  /**
   * A push button with no default behavior displaying the value of the value attribute, empty by default.
   */
  @Nonnull
  public static final String button = "button";
  /**
   * A check box allowing single values to be selected/deselected.
   */
  @Nonnull
  public static final String checkbox = "checkbox";
  /**
   * A control for specifying a color; opening a color picker when active in supporting browsers.
   */
  @Nonnull
  public static final String color = "color";
  /**
   * A control for entering a date (year, month, and day, with no time). Opens a date picker or numeric wheels for year, month, day when active in supporting browsers.
   */
  @Nonnull
  public static final String date = "date";
  /**
   * A control for entering a date and time, with no time zone. Opens a date picker or numeric wheels for date- and time-components when active in supporting browsers.
   */
  @Nonnull
  public static final String datetime_local = "datetime-local";
  /**
   * A field for editing an email address. Looks like a text input, but has validation parameters and relevant keyboard in supporting browsers and devices with dynamic keyboards.
   */
  @Nonnull
  public static final String email = "email";
  /**
   * A control that lets the user select a file. Use the accept attribute to define the types of files that the control can select.
   */
  @Nonnull
  public static final String file = "file";
  /**
   * A control that is not displayed but whose value is submitted to the server.
   */
  @Nonnull
  public static final String hidden = "hidden";
  /**
   * A graphical submit button. Displays an image defined by the src attribute. The alt attribute displays if the image src is missing.
   */
  @Nonnull
  public static final String image = "image";
  /**
   * A control for entering a month and year, with no time zone.
   */
  @Nonnull
  public static final String month = "month";
  /**
   * A control for entering a number. Displays a spinner and adds default validation when supported.
   * Displays a numeric keypad in some devices with dynamic keypads.
   */
  @Nonnull
  public static final String number = "number";
  /**
   * A single-line text field whose value is obscured. Will alert user if site is not secure.
   */
  @Nonnull
  public static final String password = "password";
  /**
   * A radio button, allowing a single value to be selected out of multiple choices with the same name value.
   */
  @Nonnull
  public static final String radio = "radio";
  /**
   * A control for entering a number whose exact value is not important.
   * Displays as a range widget defaulting to the middle value. Used in conjunction min and max to define
   * the range of acceptable values.
   */
  @Nonnull
  public static final String range = "range";
  /**
   * A button that resets the contents of the form to default values. Not recommended.
   */
  @Nonnull
  public static final String reset = "reset";
  /**
   * A single-line text field for entering search strings. Line-breaks are automatically
   * removed from the input value. May include a delete icon in supporting browsers that
   * can be used to clear the field. Displays a search icon instead of enter key on some
   * devices with dynamic keypads.
   */
  @Nonnull
  public static final String search = "search";
  /**
   * A button that submits the form.
   */
  @Nonnull
  public static final String submit = "submit";
  /**
   * A control for entering a telephone number.
   * Displays a telephone keypad in some devices with dynamic keypads.
   */
  @Nonnull
  public static final String tel = "tel";
  /**
   * The default value. A single-line text field. Line-breaks are automatically removed from the input value.
   */
  @Nonnull
  public static final String text = "text";
  /**
   * A control for entering a time value with no time zone.
   */
  @Nonnull
  public static final String time = "time";
  /**
   * A field for entering a URL. Looks like a text input, but has validation parameters and relevant keyboard in supporting browsers and devices with dynamic keyboards.
   */
  @Nonnull
  public static final String url = "url";
  /**
   * A control for entering a date consisting of a week-year number and a week number with no time zone.
   */
  @Nonnull
  public static final String week = "week";

  private InputType()
  {
  }
}
