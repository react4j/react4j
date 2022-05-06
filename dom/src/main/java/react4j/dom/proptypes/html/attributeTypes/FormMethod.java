package react4j.dom.proptypes.html.attributeTypes;

import java.lang.annotation.Documented;
import javax.annotation.Nonnull;
import org.intellij.lang.annotations.MagicConstant;

/**
 * An enumeration specifying the HTTP method to submit the form with.
 */
@Documented
@MagicConstant( valuesFromClass = FormMethod.class )
public @interface FormMethod
{
  /**
   * The POST method; form data sent as the request body.
   */
  @Nonnull
  String post = "post";
  /**
   * The GET method; form data appended to the action URL with a ? separator. Use this method when the form has no side-effects.
   */
  @Nonnull
  String get = "get";
  /**
   * When the form is inside a &lt;dialog&gt;, closes the dialog on submission.
   */
  @Nonnull
  String dialog = "dialog";
}
