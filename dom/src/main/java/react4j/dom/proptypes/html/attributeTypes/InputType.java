package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumeration specifying the type of input control to render.
 */
@Documented
@MagicConstant(valuesFromClass = InputType.class)
public @interface InputType {
    /**
     * A push button with no default behavior displaying the value of the value attribute, empty by default.
     */
    String button = "button";
    /**
     * A check box allowing single values to be selected/deselected.
     */
    String checkbox = "checkbox";
    /**
     * A control for specifying a color; opening a color picker when active in supporting browsers.
     */
    String color = "color";
    /**
     * A control for entering a date (year, month, and day, with no time). Opens a date picker or numeric wheels for year, month, day when active in supporting browsers.
     */
    String date = "date";
    /**
     * A control for entering a date and time, with no time zone. Opens a date picker or numeric wheels for date- and time-components when active in supporting browsers.
     */
    String datetime_local = "datetime-local";
    /**
     * A field for editing an email address. Looks like a text input, but has validation parameters and relevant keyboard in supporting browsers and devices with dynamic keyboards.
     */
    String email = "email";
    /**
     * A control that lets the user select a file. Use the accept attribute to define the types of files that the control can select.
     */
    String file = "file";
    /**
     * A control that is not displayed but whose value is submitted to the server.
     */
    String hidden = "hidden";
    /**
     * A graphical submit button. Displays an image defined by the src attribute. The alt attribute displays if the image src is missing.
     */
    String image = "image";
    /**
     * A control for entering a month and year, with no time zone.
     */
    String month = "month";
    /**
     * A control for entering a number. Displays a spinner and adds default validation when supported.
     * Displays a numeric keypad in some devices with dynamic keypads.
     */
    String number = "number";
    /**
     * A single-line text field whose value is obscured. Will alert user if site is not secure.
     */
    String password = "password";
    /**
     * A radio button, allowing a single value to be selected out of multiple choices with the same name value.
     */
    String radio = "radio";
    /**
     * A control for entering a number whose exact value is not important.
     * Displays as a range widget defaulting to the middle value. Used in conjunction min and max to define
     * the range of acceptable values.
     */
    String range = "range";
    /**
     * A button that resets the contents of the form to default values. Not recommended.
     */
    String reset = "reset";
    /**
     * A single-line text field for entering search strings. Line-breaks are automatically
     * removed from the input value. May include a delete icon in supporting browsers that
     * can be used to clear the field. Displays a search icon instead of enter key on some
     * devices with dynamic keypads.
     */
    String search = "search";
    /**
     * A button that submits the form.
     */
    String submit = "submit";
    /**
     * A control for entering a telephone number.
     * Displays a telephone keypad in some devices with dynamic keypads.
     */
    String tel = "tel";
    /**
     * The default value. A single-line text field. Line-breaks are automatically removed from the input value.
     */
    String text = "text";
    /**
     * A control for entering a time value with no time zone.
     */
    String time = "time";
    /**
     * A field for entering a URL. Looks like a text input, but has validation parameters and relevant keyboard in supporting browsers and devices with dynamic keyboards.
     */
    String url = "url";
    /**
     * A control for entering a date consisting of a week-year number and a week number with no time zone.
     */
    String week = "week";
}
