package react4j.css.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.ElementFilter;

/** Effective Java methods, retaining their types as members of the CSS definition. */
final class CssMethods {
    private CssMethods() {}

    record Method(ExecutableElement element, ExecutableType type) {}

    static List<Method> collect(ProcessingEnvironment environment, TypeElement owner) {
        var elements = environment.getElementUtils();
        var types = environment.getTypeUtils();
        var methods = new ArrayList<Method>();
        var candidates = new ArrayList<>(ElementFilter.methodsIn(elements.getAllMembers(owner)));
        candidates.sort(Comparator.comparing(method -> method.getEnclosingElement() + "." + method));
        for (var candidate : candidates) {
            var method =
                    new Method(candidate, (ExecutableType) types.asMemberOf((DeclaredType) owner.asType(), candidate));
            int index = -1;
            for (int i = 0; i < methods.size(); i++) {
                var previous = methods.get(i);
                if (candidate.getSimpleName().contentEquals(previous.element().getSimpleName())
                        && (types.isSubsignature(method.type(), previous.type())
                                || types.isSubsignature(previous.type(), method.type()))) {
                    index = i;
                    break;
                }
            }
            if (-1 == index) {
                methods.add(method);
            } else {
                var previous = methods.get(index);
                if (elements.overrides(candidate, previous.element(), owner)
                        || (!elements.overrides(previous.element(), candidate, owner)
                                && ((!candidate.getModifiers().contains(Modifier.ABSTRACT)
                                                && previous.element()
                                                        .getModifiers()
                                                        .contains(Modifier.ABSTRACT))
                                        || types.isSubtype(
                                                method.type().getReturnType(),
                                                previous.type().getReturnType())))) {
                    methods.set(index, method);
                }
            }
        }
        methods.sort(
                Comparator.comparing(method -> method.element().getSimpleName().toString()));
        return List.copyOf(methods);
    }
}
