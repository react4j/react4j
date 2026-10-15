package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant(valuesFromClass = Target.class)
public @interface Target {

    String blank = "_blank";

    String self = "_self";

    String parent = "_parent";

    String top = "_top";
}
