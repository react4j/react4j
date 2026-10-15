package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumeration describing the behavior of the button.
 */
@Documented
@MagicConstant(valuesFromClass = ButtonType.class)
public @interface ButtonType {
    /**
     * The button submits the form. This is the default value if the attribute is not specified, or if it is dynamically changed to an empty or invalid value.
     */
    String submit = "submit";
    /**
     * The button resets the form.
     */
    String reset = "reset";
    /**
     * The button has no default behaviour.
     */
    String button = "button";
    /**
     * The button displays a menu.
     * This is an experimental feature.
     */
    String menu = "menu";
}
