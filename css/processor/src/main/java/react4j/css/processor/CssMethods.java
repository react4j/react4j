package react4j.css.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    record Method(ExecutableElement element, ExecutableType type, List<ExecutableElement> declarations) {}

    static List<Method> collect(ProcessingEnvironment environment, TypeElement owner) {
        var elements = environment.getElementUtils();
        var types = environment.getTypeUtils();
        var methods = new ArrayList<Method>();
        var declarations = new ArrayList<ExecutableElement>();
        declarations.addAll(ElementFilter.methodsIn(owner.getEnclosedElements()));
        interfaces(environment, owner, new HashSet<>(), declarations);
        declarations.sort(Comparator.comparing(method -> method.getEnclosingElement() + "." + method));
        var candidates = new ArrayList<>(ElementFilter.methodsIn(elements.getAllMembers(owner)));
        candidates.sort(Comparator.comparing(method -> method.getEnclosingElement() + "." + method));
        for (var candidate : candidates) {
            var type = (ExecutableType) types.asMemberOf((DeclaredType) owner.asType(), candidate);
            var contracts = declarations.stream()
                    .filter(declaration -> {
                        if (!candidate.getSimpleName().contentEquals(declaration.getSimpleName())) {
                            return false;
                        }
                        var inherited = (ExecutableType) types.asMemberOf((DeclaredType) owner.asType(), declaration);
                        return types.isSubsignature(type, inherited) || types.isSubsignature(inherited, type);
                    })
                    .toList();
            var method = new Method(candidate, type, contracts);
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
                                        || (candidate.getModifiers().contains(Modifier.ABSTRACT)
                                                        == previous.element()
                                                                .getModifiers()
                                                                .contains(Modifier.ABSTRACT)
                                                && types.isSubtype(
                                                        method.type().getReturnType(),
                                                        previous.type().getReturnType()))))) {
                    methods.set(index, method);
                }
            }
        }
        methods.sort(
                Comparator.comparing(method -> method.element().getSimpleName().toString()));
        return List.copyOf(methods);
    }

    private static void interfaces(
            ProcessingEnvironment environment,
            TypeElement type,
            Set<TypeElement> visited,
            List<ExecutableElement> declarations) {
        for (var parent : type.getInterfaces()) {
            var contract = (TypeElement) environment.getTypeUtils().asElement(parent);
            if (visited.add(contract)) {
                for (var method : ElementFilter.methodsIn(contract.getEnclosedElements())) {
                    if (!method.getModifiers().contains(Modifier.STATIC)
                            && !method.getModifiers().contains(Modifier.PRIVATE)) {
                        declarations.add(method);
                    }
                }
                interfaces(environment, contract, visited, declarations);
            }
        }
    }
}
