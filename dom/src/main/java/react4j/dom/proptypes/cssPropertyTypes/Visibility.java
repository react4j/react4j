package react4j.dom.proptypes.cssPropertyTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant(valuesFromClass = Visibility.class)
public @interface Visibility {

    String hidden = "hidden";

    String visible = "visible";

    String collapse = "collapse";

    String initial = "initial";

    String inherit = "inherit";
}
