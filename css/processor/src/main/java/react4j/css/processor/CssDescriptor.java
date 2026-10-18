package react4j.css.processor;

import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;

final class CssDescriptor {
    private final TypeElement _element;
    private final String _binaryName;
    private final List<ExecutableElement> _accessors;
    private final boolean _publicFactory;
    private final boolean _deprecated;

    CssDescriptor(
            final TypeElement element,
            final String binaryName,
            final List<ExecutableElement> accessors,
            final boolean publicFactory,
            final boolean deprecated) {
        _element = element;
        _binaryName = binaryName;
        _accessors = List.copyOf(accessors);
        _publicFactory = publicFactory;
        _deprecated = deprecated;
    }

    TypeElement getElement() {
        return _element;
    }

    String getPackageName() {
        return GeneratorUtil.getQualifiedPackageName(_element);
    }

    String getRootClassName() {
        return Names.root(_binaryName);
    }

    List<ExecutableElement> getAccessors() {
        return _accessors;
    }

    boolean isFactoryPublic() {
        return _publicFactory;
    }

    boolean accessesDeprecatedElements() {
        return _deprecated;
    }
}
