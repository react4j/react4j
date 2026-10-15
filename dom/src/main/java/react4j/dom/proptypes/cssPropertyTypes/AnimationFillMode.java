package react4j.dom.proptypes.cssPropertyTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant(valuesFromClass = AnimationFillMode.class)
public @interface AnimationFillMode {

    String none = "none";

    String forwards = "forwards";

    String backwards = "backwards";

    String both = "both";

    String initial = "initial";

    String inherit = "inherit";
}
