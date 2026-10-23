package react4j.css.processor;

import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;

record CssDescriptor(
        TypeElement element,
        String binaryName,
        List<CssAccessor> accessors,
        boolean publicFactory,
        boolean accessesDeprecatedElements) {
    CssDescriptor {
        accessors = List.copyOf(accessors);
    }

    CssCompiler.Exports exports() {
        return new CssCompiler.Exports(
                accessors.stream()
                        .filter(a -> CssAccessor.Kind.CLASS == a.kind())
                        .map(CssAccessor::name)
                        .toList(),
                accessors.stream()
                        .filter(a -> CssAccessor.Kind.ID == a.kind())
                        .map(CssAccessor::name)
                        .collect(Collectors.toSet()),
                accessors.stream()
                        .filter(a -> CssAccessor.Kind.ANIMATION == a.kind())
                        .map(CssAccessor::name)
                        .collect(Collectors.toSet()),
                accessors.stream()
                        .filter(a -> !a.utilities().isEmpty())
                        .map(CssAccessor::name)
                        .collect(Collectors.toSet()));
    }

    String packageName() {
        return GeneratorUtil.getQualifiedPackageName(element);
    }

    String rootClassName() {
        return Names.root(binaryName);
    }
}
