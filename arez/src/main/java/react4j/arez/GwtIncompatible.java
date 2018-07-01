package react4j.arez;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation use to mark code as incompatible with GWT.
 * The Name of the annotation is all that matters.
 */
@Retention( RetentionPolicy.CLASS )
@Target( { ElementType.TYPE, ElementType.METHOD } )
@Documented
@interface GwtIncompatible
{
}
