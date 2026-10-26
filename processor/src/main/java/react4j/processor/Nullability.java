package react4j.processor;

import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.GeneratorUtil;
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

    static Nullness resolve(
            final Element declaration, final TypeMirror resolvedType, final @Nullable TypeElement context) {
        return resolve(declaration, declaredType(declaration), resolvedType, context);
    }

    private static Nullness resolve(
            final Element declaration,
            final TypeMirror original,
            final TypeMirror resolvedType,
            final @Nullable TypeElement context) {
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

    static Nullness requireSpecified(Element declaration, TypeMirror resolvedType, TypeElement context) {
        return requireSpecified(declaration, resolve(declaration, resolvedType, context));
    }

    static void requireSpecifiedTree(Element declaration, TypeMirror type) {
        if (type instanceof WildcardType wildcard) {
            var lower = wildcard.getSuperBound();
            var upper = wildcard.getExtendsBound();
            if (lower != null) {
                requireSpecifiedTree(declaration, lower);
            }
            if (upper != null) {
                requireSpecifiedTree(declaration, upper);
            }
            return;
        }
        if (!type.getKind().isPrimitive()
                && !isMarked(declaration)
                && !AnnotationsUtil.hasAnnotationOfType(type, NULLABLE)
                && !AnnotationsUtil.hasAnnotationOfType(type, NONNULL)) {
            throw new ProcessorException(
                    "Unspecified JSpecify nullness in injected constructor parameter: qualify all stored reference"
                            + " types, including type arguments and array elements",
                    declaration);
        }
        if (type instanceof ArrayType array) {
            requireSpecifiedTree(declaration, array.getComponentType());
        } else if (type instanceof DeclaredType declared) {
            for (var argument : declared.getTypeArguments()) {
                requireSpecifiedTree(declaration, argument);
            }
            for (var owner = declared.getEnclosingType();
                    owner instanceof DeclaredType enclosing;
                    owner = enclosing.getEnclosingType()) {
                for (var argument : enclosing.getTypeArguments()) {
                    requireSpecifiedTree(declaration, argument);
                }
            }
        }
    }

    private record TypeUse(
            Element declaration,
            TypeMirror original,
            TypeMirror type,
            Map<Element, TypeUse> bindings,
            @Nullable Nullness overrideNullness) {
        TypeUse(Element declaration, TypeMirror original, TypeMirror type) {
            this(declaration, original, type, Map.of(), null);
        }

        TypeUse at(TypeMirror originalChild, TypeMirror child) {
            return new TypeUse(declaration, originalChild, child, bindings, null);
        }

        TypeUse substitute(TypeElement context) {
            if (type instanceof TypeVariable variable) {
                var binding = bindings.get(variable.asElement());
                if (binding != null) {
                    var replacement = binding.substitute(context);
                    var qualifier = AnnotationsUtil.hasAnnotationOfType(original, NULLABLE)
                                    || AnnotationsUtil.hasAnnotationOfType(original, NONNULL)
                            ? resolve(declaration, original, type, context)
                            : overrideNullness;
                    return qualifier == null
                            ? replacement
                            : new TypeUse(
                                    replacement.declaration(),
                                    replacement.original(),
                                    replacement.type(),
                                    replacement.bindings(),
                                    qualifier);
                }
            }
            return this;
        }

        @Nullable
        TypeUse owner() {
            if (type instanceof DeclaredType declared && declared.getEnclosingType() instanceof DeclaredType owner) {
                return at(
                        original instanceof DeclaredType value && value.getEnclosingType() instanceof DeclaredType
                                ? value.getEnclosingType()
                                : owner,
                        owner);
            }
            return null;
        }

        void bindArguments(Map<Element, TypeUse> target) {
            var owner = owner();
            if (owner != null) {
                owner.bindArguments(target);
            }
            var declared = (DeclaredType) type;
            var element = (TypeElement) declared.asElement();
            for (int i = 0; i < declared.getTypeArguments().size(); i++) {
                target.put(element.getTypeParameters().get(i), argument(i));
            }
        }

        TypeUse argument(int index) {
            var arguments = ((DeclaredType) type).getTypeArguments();
            var originalArguments = original instanceof DeclaredType declared ? declared.getTypeArguments() : arguments;
            return at(
                    originalArguments.size() == arguments.size() ? originalArguments.get(index) : arguments.get(index),
                    arguments.get(index));
        }
    }

    private enum Relation {
        ASSIGNABLE,
        CONTAINED,
        IDENTICAL
    }

    static boolean isAssignable(
            Types types,
            Element source,
            TypeMirror sourceType,
            Element target,
            TypeMirror targetType,
            TypeElement context) {
        return types.isAssignable(sourceType, targetType)
                && compatible(
                        types,
                        new TypeUse(source, declaredType(source), sourceType),
                        new TypeUse(target, declaredType(target), targetType),
                        context,
                        Relation.ASSIGNABLE);
    }

    static boolean isAssignable(
            Types types,
            Element source,
            TypeMirror sourceType,
            TypeElement sourceContext,
            Element target,
            TypeMirror targetType,
            TypeElement targetContext) {
        return types.isAssignable(sourceType, targetType)
                && compatible(
                        types,
                        new TypeUse(source, declaredType(source), sourceType),
                        new TypeUse(target, declaredType(target), targetType),
                        sourceContext,
                        targetContext,
                        Relation.ASSIGNABLE);
    }

    private static boolean compatible(
            Types types, TypeUse source, TypeUse target, TypeElement context, Relation relation) {
        return compatible(types, source, target, context, context, relation);
    }

    private static boolean compatible(
            Types types,
            TypeUse source,
            TypeUse target,
            TypeElement sourceContext,
            TypeElement targetContext,
            Relation relation) {
        source = source.substitute(sourceContext);
        target = target.substitute(targetContext);
        if (target.type() instanceof WildcardType wildcard) {
            var upper = wildcard.getExtendsBound();
            var lower = wildcard.getSuperBound();
            if (relation == Relation.IDENTICAL) {
                if (!(source.type() instanceof WildcardType sourceWildcard)) {
                    return false;
                }
                var sourceUpper = sourceWildcard.getExtendsBound();
                var sourceLower = sourceWildcard.getSuperBound();
                if ((upper == null) != (sourceUpper == null) || (lower == null) != (sourceLower == null)) {
                    return false;
                }
                if (upper != null && sourceUpper != null) {
                    var originalUpper =
                            target.original() instanceof WildcardType value ? value.getExtendsBound() : upper;
                    var originalSourceUpper =
                            source.original() instanceof WildcardType value ? value.getExtendsBound() : sourceUpper;
                    return originalUpper != null
                            && originalSourceUpper != null
                            && compatible(
                                    types,
                                    source.at(originalSourceUpper, sourceUpper),
                                    target.at(originalUpper, upper),
                                    sourceContext,
                                    targetContext,
                                    relation);
                }
                if (lower != null && sourceLower != null) {
                    var originalLower = target.original() instanceof WildcardType value ? value.getSuperBound() : lower;
                    var originalSourceLower =
                            source.original() instanceof WildcardType value ? value.getSuperBound() : sourceLower;
                    return originalLower != null
                            && originalSourceLower != null
                            && compatible(
                                    types,
                                    source.at(originalSourceLower, sourceLower),
                                    target.at(originalLower, lower),
                                    sourceContext,
                                    targetContext,
                                    relation);
                }
                return true;
            }
            if (upper != null) {
                var sourceBound = source.type() instanceof WildcardType value ? value.getExtendsBound() : source.type();
                var originalBound =
                        source.original() instanceof WildcardType value ? value.getExtendsBound() : source.original();
                var originalUpper = target.original() instanceof WildcardType value ? value.getExtendsBound() : upper;
                if (sourceBound == null || originalBound == null) {
                    return originalUpper != null
                            && resolve(target.declaration(), originalUpper, upper, targetContext) == Nullness.NULLABLE;
                }
                return originalUpper != null
                        && compatible(
                                types,
                                source.at(originalBound, sourceBound),
                                target.at(originalUpper, upper),
                                sourceContext,
                                targetContext,
                                Relation.ASSIGNABLE);
            }
            if (lower != null) {
                var sourceBound = source.type() instanceof WildcardType value ? value.getSuperBound() : source.type();
                var originalBound =
                        source.original() instanceof WildcardType value ? value.getSuperBound() : source.original();
                var originalLower = target.original() instanceof WildcardType value ? value.getSuperBound() : lower;
                return sourceBound != null
                        && originalBound != null
                        && originalLower != null
                        && compatible(
                                types,
                                target.at(originalLower, lower),
                                source.at(originalBound, sourceBound),
                                targetContext,
                                sourceContext,
                                Relation.ASSIGNABLE);
            }
            return true;
        }
        if (source.type() instanceof WildcardType) {
            return false;
        }
        var from = source.overrideNullness() != null
                ? source.overrideNullness()
                : resolve(source.declaration(), source.original(), source.type(), sourceContext);
        var to = target.overrideNullness() != null
                ? target.overrideNullness()
                : resolve(target.declaration(), target.original(), target.type(), targetContext);
        if (from == Nullness.UNSPECIFIED
                || to == Nullness.UNSPECIFIED
                || (relation != Relation.ASSIGNABLE
                        ? from != to
                        : from != to && from != Nullness.NONNULL && to != Nullness.NULLABLE)) {
            return false;
        }
        if (target.type() instanceof DeclaredType declared
                && ((TypeElement) declared.asElement()).getQualifiedName().contentEquals("java.lang.Object")) {
            return true;
        }
        if (source.type() instanceof TypeVariable variable && !(target.type() instanceof TypeVariable)) {
            var upper = variable.getUpperBound();
            var bounds = upper instanceof IntersectionType intersection ? intersection.getBounds() : List.of(upper);
            for (var bound : bounds) {
                if (types.isAssignable(types.erasure(bound), types.erasure(target.type()))
                        && compatible(
                                types,
                                new TypeUse(variable.asElement(), bound, bound, source.bindings(), from),
                                target,
                                sourceContext,
                                targetContext,
                                relation)) {
                    return true;
                }
            }
            return false;
        }
        if (source.type() instanceof ArrayType sourceArray && target.type() instanceof ArrayType targetArray) {
            return compatible(
                    types,
                    source.at(
                            source.original() instanceof ArrayType array
                                    ? array.getComponentType()
                                    : sourceArray.getComponentType(),
                            sourceArray.getComponentType()),
                    target.at(
                            target.original() instanceof ArrayType array
                                    ? array.getComponentType()
                                    : targetArray.getComponentType(),
                            targetArray.getComponentType()),
                    sourceContext,
                    targetContext,
                    relation);
        }
        if (source.type() instanceof DeclaredType sourceDeclared
                && target.type() instanceof DeclaredType targetDeclared) {
            if (!sourceDeclared.asElement().equals(targetDeclared.asElement())) {
                var element = (TypeElement) sourceDeclared.asElement();
                var bindings = new HashMap<>(source.bindings());
                source.bindArguments(bindings);
                // Types.directSupertypes drops substituted type-use annotations; bind declared parents instead.
                var parents = new ArrayList<TypeMirror>(element.getInterfaces());
                parents.add(element.getSuperclass());
                for (var parent : parents) {
                    if (parent instanceof DeclaredType
                            && types.isAssignable(types.erasure(parent), types.erasure(target.type()))
                            && compatible(
                                    types,
                                    new TypeUse(element, parent, parent, bindings, from),
                                    target,
                                    sourceContext,
                                    targetContext,
                                    relation)) {
                        return true;
                    }
                }
                return false;
            }
            var sourceArguments = sourceDeclared.getTypeArguments();
            var targetArguments = targetDeclared.getTypeArguments();
            if (sourceArguments.size() < targetArguments.size()) {
                return false;
            }
            for (int i = 0; i < targetArguments.size(); i++) {
                if (!compatible(
                        types,
                        source.argument(i),
                        target.argument(i),
                        sourceContext,
                        targetContext,
                        relation == Relation.ASSIGNABLE ? Relation.CONTAINED : Relation.IDENTICAL)) {
                    return false;
                }
            }
            var sourceOwner = source.owner();
            var targetOwner = target.owner();
            if (sourceOwner != null && targetOwner != null) {
                return compatible(types, sourceOwner, targetOwner, sourceContext, targetContext, Relation.IDENTICAL);
            }
        }
        return true;
    }

    static TypeName typeName(Element declaration, TypeMirror resolvedType, TypeElement context) {
        requireSpecified(declaration, resolvedType, context);
        return qualify(
                declaration, declaredType(declaration), resolvedType, context, GeneratorUtil.getTypeName(resolvedType));
    }

    static TypeName qualify(
            Element declaration, TypeMirror original, TypeMirror resolvedType, TypeElement context, TypeName type) {
        var contract = resolve(declaration, original, resolvedType, context);
        if (contract == Nullness.NULLABLE) {
            return nullable(type);
        }
        if (contract == Nullness.NONNULL && !type.isPrimitive()) {
            type = type.withoutAnnotations();
            if (type instanceof TypeVariableName || !isMarked(declaration)) {
                type = type.annotated(AnnotationSpec.builder(ClassName.get("org.jspecify.annotations", "NonNull"))
                        .build());
            }
        }
        return type;
    }

    static TypeName nullable(TypeName type) {
        return type.box()
                .withoutAnnotations()
                .annotated(
                        AnnotationSpec.builder(GeneratorUtil.NULLABLE_CLASSNAME).build());
    }

    static MethodSpec.Builder preserveScope(Element declaration, MethodSpec.Builder method) {
        if (!isMarked(declaration)) {
            method.addAnnotation(ClassName.get("org.jspecify.annotations", "NullUnmarked"));
        }
        return method;
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
