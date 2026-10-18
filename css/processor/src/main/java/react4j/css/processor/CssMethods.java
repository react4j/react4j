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
import javax.lang.model.util.Types;

/** Effective Java methods, retaining their types as members of the CSS definition. */
final class CssMethods {
    private CssMethods() {}

    record Method(
            ExecutableElement element,
            ExecutableType type,
            List<ExecutableElement> declarations,
            List<ExecutableType> signatures) {
        boolean isAccessor(Types types) {
            var string = type.getReturnType();
            if (!type.getParameterTypes().isEmpty()
                    || !element.getTypeParameters().isEmpty()
                    || !(string instanceof DeclaredType declared)
                    || !((TypeElement) declared.asElement()).getQualifiedName().contentEquals("java.lang.String")) {
                return false;
            }
            // Covariant returns and the intersection of throws describe the common implementation.
            return signatures.stream().allMatch(signature -> types.isSubtype(string, signature.getReturnType()))
                    && signatures.stream()
                            .flatMap(signature -> signature.getThrownTypes().stream())
                            .map(types::erasure)
                            .noneMatch(thrown -> signatures.stream()
                                    .allMatch(signature -> signature.getThrownTypes().stream()
                                            .anyMatch(allowed -> types.isSubtype(thrown, types.erasure(allowed)))));
        }
    }

    static List<Method> collect(ProcessingEnvironment environment, TypeElement owner) {
        var elements = environment.getElementUtils();
        var types = environment.getTypeUtils();
        var methods = new ArrayList<Method>();
        var declarations = new ArrayList<ExecutableElement>();
        declarations.addAll(ElementFilter.methodsIn(owner.getEnclosedElements()));
        interfaces(environment, owner, new HashSet<>(), declarations);
        declarations.sort(Comparator.comparing(method -> method.getEnclosingElement() + "." + method));
        var candidates = new ArrayList<>(ElementFilter.methodsIn(elements.getAllMembers(owner)));
        // javac can hide a public Object implementation behind an interface redeclaration.
        var object = elements.getTypeElement("java.lang.Object");
        assert null != object;
        for (var method : ElementFilter.methodsIn(object.getEnclosedElements())) {
            if (method.getModifiers().contains(Modifier.PUBLIC)
                    && declarations.stream()
                            .noneMatch(declaration -> owner.equals(declaration.getEnclosingElement())
                                    && elements.overrides(declaration, method, owner))
                    && !candidates.contains(method)) {
                candidates.add(method);
            }
        }
        candidates.sort(Comparator.comparing(method -> method.getEnclosingElement() + "." + method));
        for (var candidate : candidates) {
            var type = (ExecutableType) types.asMemberOf((DeclaredType) owner.asType(), candidate);
            var contracts = declarations.stream()
                    .filter(declaration -> {
                        if (!candidate.getSimpleName().contentEquals(declaration.getSimpleName())) {
                            return false;
                        }
                        var inherited = (ExecutableType) types.asMemberOf((DeclaredType) owner.asType(), declaration);
                        return sameSignature(types, type, inherited);
                    })
                    .toList();
            var method = new Method(candidate, type, contracts, List.of(type));
            int index = -1;
            for (int i = 0; i < methods.size(); i++) {
                var previous = methods.get(i);
                if (candidate.getSimpleName().contentEquals(previous.element().getSimpleName())
                        && sameSignature(types, method.type(), previous.type())) {
                    index = i;
                    break;
                }
            }
            if (-1 == index) {
                methods.add(method);
            } else {
                var previous = methods.get(index);
                boolean candidateAbstract = candidate.getModifiers().contains(Modifier.ABSTRACT);
                boolean previousAbstract = previous.element().getModifiers().contains(Modifier.ABSTRACT);
                boolean candidateSubsignature = types.isSubsignature(type, previous.type());
                boolean previousSubsignature = types.isSubsignature(previous.type(), type);
                // A non-generic declaration can implement its generic peer, but not conversely.
                boolean preferCandidate = candidateAbstract != previousAbstract
                        ? !candidateAbstract
                        : candidateSubsignature != previousSubsignature
                                ? candidateSubsignature
                                : types.isSubtype(
                                        type.getReturnType(), previous.type().getReturnType());
                var selected = preferCandidate ? method : previous;
                var signatures = new ArrayList<>(previous.signatures());
                signatures.add(type);
                methods.set(index, new Method(selected.element(), selected.type(), contracts, List.copyOf(signatures)));
            }
        }
        methods.sort(
                Comparator.comparing(method -> method.element().getSimpleName().toString()));
        return List.copyOf(methods);
    }

    private static boolean sameSignature(Types types, ExecutableType first, ExecutableType second) {
        return types.isSubsignature((ExecutableType) types.erasure(first), (ExecutableType) types.erasure(second));
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
