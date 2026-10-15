package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import org.intellij.lang.annotations.MagicConstant;

@Documented
@MagicConstant(valuesFromClass = YesNo.class)
public @interface YesNo {

    String yes = "yes";

    String no = "no";
}
