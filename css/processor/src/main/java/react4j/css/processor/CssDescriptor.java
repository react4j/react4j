package react4j.css.processor;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;

record CssDescriptor(
        TypeElement element,
        String binaryName,
        List<ExecutableElement> accessors,
        boolean publicFactory,
        boolean accessesDeprecatedElements) {
    CssDescriptor {
        accessors = List.copyOf(accessors);
    }

    String packageName() {
        return GeneratorUtil.getQualifiedPackageName(element);
    }

    String rootClassName() {
        return Names.root(binaryName);
    }
}
