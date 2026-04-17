package react4j.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

record PreludeChecksDescriptor(@Nonnull Element element, @Nonnull TypeMirror type, @Nonnull ObserveMode observeMode)
{
}
