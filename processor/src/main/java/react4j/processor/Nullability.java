package react4j.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ProcessorException;

/** Resolves nullness at the original declaration, before generated marked scopes are applied. */
final class Nullability {
    private static final String NULLABLE = "org.jspecify.annotations.Nullable";
    private static final String NONNULL = "org.jspecify.annotations.NonNull";
    private static final String MARKED = "org.jspecify.annotations.NullMarked";
    private static final String UNMARKED = "org.jspecify.annotations.NullUnmarked";

    enum Nullness {
        NONNULL,
        NULLABLE,
        PARAMETRIC,
        UNSPECIFIED
    }

    private Nullability() {}

    static boolean isNonNull(final Element declaration) {
        return isNonNull(declaration, declaredType(declaration));
    }

    static boolean isNonNull(final Element declaration, final TypeMirror resolvedType) {
        return requireSpecified(declaration, resolve(declaration, resolvedType)) == Nullness.NONNULL;
    }

    static boolean isNonNull(final Element declaration, final TypeMirror resolvedType, final TypeElement context) {
        return requireSpecified(declaration, resolve(declaration, resolvedType, context)) == Nullness.NONNULL;
    }

    static boolean isNullable(final Element declaration) {
        return requireSpecified(declaration, resolve(declaration, declaredType(declaration))) == Nullness.NULLABLE;
    }

    static Nullness resolve(final Element declaration, final TypeMirror resolvedType) {
        return resolve(declaration, resolvedType, null);
    }

    private static Nullness resolve(
            final Element declaration, final TypeMirror resolvedType, final @Nullable TypeElement context) {
        final TypeMirror original = declaredType(declaration);
        if (original.getKind().isPrimitive() || original.getKind() == TypeKind.VOID) {
            return Nullness.NONNULL;
        }
        if (AnnotationsUtil.hasAnnotationOfType(original, NULLABLE)
                && AnnotationsUtil.hasAnnotationOfType(original, NONNULL)) {
            throw new ProcessorException("Conflicting JSpecify @Nullable and @NonNull annotations", declaration);
        }
        if (AnnotationsUtil.hasAnnotationOfType(original, NULLABLE)) {
            return Nullness.NULLABLE;
        }
        if (AnnotationsUtil.hasAnnotationOfType(original, NONNULL)) {
            return Nullness.NONNULL;
        }
        if (!isMarked(declaration)) {
            return Nullness.UNSPECIFIED;
        }
        if (context != null && original instanceof TypeVariable variable) {
            final Nullness binding = inheritedBinding(context, variable.asElement(), new HashMap<>());
            if (binding != null) {
                return binding;
            }
        }
        if (AnnotationsUtil.hasAnnotationOfType(resolvedType, NULLABLE)) {
            return Nullness.NULLABLE;
        }
        if (AnnotationsUtil.hasAnnotationOfType(resolvedType, NONNULL)) {
            return Nullness.NONNULL;
        }
        if (resolvedType instanceof TypeVariable variable) {
            return nonNullBound(variable, new HashSet<>()) ? Nullness.NONNULL : Nullness.PARAMETRIC;
        }
        return Nullness.NONNULL;
    }

    static Nullness requireDefinite(final Element declaration, final TypeMirror resolvedType) {
        return requireDefinite(declaration, resolvedType, null);
    }

    static Nullness requireDefinite(
            final Element declaration, final TypeMirror resolvedType, final @Nullable TypeElement context) {
        final Nullness nullness = requireSpecified(declaration, resolve(declaration, resolvedType, context));
        if (nullness == Nullness.PARAMETRIC) {
            throw new ProcessorException(
                    "Parametric JSpecify nullness requires an explicit @Nullable or @NonNull contract for generated"
                            + " initialization",
                    declaration);
        }
        return nullness;
    }

    static boolean isMarked(final Element declaration) {
        for (Element scope = declaration; scope != null; scope = scope.getEnclosingElement()) {
            if (AnnotationsUtil.hasAnnotationOfType(scope, UNMARKED)) {
                return false;
            }
            if (AnnotationsUtil.hasAnnotationOfType(scope, MARKED)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static Nullness inheritedBinding(
            final TypeElement type, final Element parameter, final Map<Element, Nullness> bindings) {
        if (type.getTypeParameters().contains(parameter)) {
            final Nullness binding = bindings.get(parameter);
            return binding != null ? binding : argumentNullness(parameter.asType(), type, bindings);
        }
        final var parents = new ArrayList<TypeMirror>(type.getInterfaces());
        parents.add(type.getSuperclass());
        for (final TypeMirror parent : parents) {
            if (parent instanceof DeclaredType declared) {
                final var element = (TypeElement) declared.asElement();
                final Map<Element, Nullness> parentBindings = new HashMap<>();
                for (int i = 0; i < declared.getTypeArguments().size(); i++) {
                    parentBindings.put(
                            element.getTypeParameters().get(i),
                            argumentNullness(declared.getTypeArguments().get(i), type, bindings));
                }
                final Nullness result = inheritedBinding(element, parameter, parentBindings);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static Nullness argumentNullness(
            final TypeMirror argument, final TypeElement scope, final Map<Element, Nullness> bindings) {
        if (AnnotationsUtil.hasAnnotationOfType(argument, NULLABLE)) {
            return Nullness.NULLABLE;
        }
        if (AnnotationsUtil.hasAnnotationOfType(argument, NONNULL)) {
            return Nullness.NONNULL;
        }
        if (!isMarked(scope)) {
            return Nullness.UNSPECIFIED;
        }
        if (argument instanceof TypeVariable variable) {
            final Nullness binding = bindings.get(variable.asElement());
            return binding != null
                    ? binding
                    : nonNullBound(variable, new HashSet<>()) ? Nullness.NONNULL : Nullness.PARAMETRIC;
        }
        return Nullness.NONNULL;
    }

    static boolean hasNonNullBound(final TypeVariable variable) {
        return nonNullBound(variable, new HashSet<>());
    }

    private static boolean nonNullBound(final TypeVariable variable, final Set<Element> visited) {
        if (!visited.add(variable.asElement())) {
            return false;
        }
        final TypeMirror bound = variable.getUpperBound();
        if (AnnotationsUtil.hasAnnotationOfType(bound, NULLABLE)) {
            return false;
        }
        if (AnnotationsUtil.hasAnnotationOfType(bound, NONNULL)) {
            return true;
        }
        if (bound instanceof IntersectionType intersection) {
            return intersection.getBounds().stream()
                    .anyMatch(part -> AnnotationsUtil.hasAnnotationOfType(part, NONNULL)
                            || (!AnnotationsUtil.hasAnnotationOfType(part, NULLABLE)
                                    && isMarked(variable.asElement())));
        }
        return bound instanceof TypeVariable parent ? nonNullBound(parent, visited) : isMarked(variable.asElement());
    }

    private static TypeMirror declaredType(final Element declaration) {
        return declaration instanceof ExecutableElement method ? method.getReturnType() : declaration.asType();
    }

    private static Nullness requireSpecified(final Element declaration, final Nullness nullness) {
        if (nullness == Nullness.UNSPECIFIED) {
            throw new ProcessorException(
                    "Unspecified JSpecify nullness: declare @Nullable or @NonNull, or use a @NullMarked scope",
                    declaration);
        }
        return nullness;
    }
}
