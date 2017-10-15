package react4j.arez;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks event handler so that it is not automatically annotated with Action.
 * This is should only be present in subclasses of {@link ReactArezComponent} and should only be placed on
 * methods annotated with react4j.annotations.EventHandler. This method stops the react4j processor from
 * generating an {@link org.realityforge.arez.annotations.Action} annotation on synthesized method. The developer
 * is still free to add their own {@link org.realityforge.arez.annotations.Action} annotation and this may be useful
 * if you need fine-grain control of action parameters.
 */
@Documented
@Target( ElementType.METHOD )
public @interface NoAutoAction
{
}
