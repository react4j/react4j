package react4j.processor;

import java.util.Objects;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.ProcessorException;

final class InputPolicies {
    private InputPolicies() {}

    static void parseEquality(final ProcessingEnvironment env, final ViewDescriptor view, final InputDescriptor input) {
        var type = (TypeMirror)
                AnnotationsUtil.getAnnotationValue(input.getElement(), Constants.INPUT_CLASSNAME, "equalityComparator")
                        .getValue();
        final boolean explicit = !Constants.EQUALITY_COMPARATOR_CLASSNAME.equals(type.toString());
        if (input.isImmutable()) {
            if (explicit) {
                throw error(input, "equalityComparator is only valid on mutable method inputs");
            }
            return;
        }
        if (!explicit) {
            if (!(input.getType() instanceof DeclaredType declared)) {
                return;
            }
            final var annotation = AnnotationsUtil.findAnnotationByType(
                    declared.asElement(), Constants.DEFAULT_EQUALITY_COMPARATOR_CLASSNAME);
            if (null == annotation) {
                return;
            }
            type = AnnotationsUtil.getAnnotationValueValue(annotation, "value");
        }
        input.setEqualityComparator(validateProvider(env, view, input, type, Constants.EQUALITY_COMPARATOR_CLASSNAME));
    }

    static void parseKey(final ProcessingEnvironment env, final ViewDescriptor view, final InputDescriptor input) {
        final var type = (TypeMirror)
                AnnotationsUtil.getAnnotationValue(input.getElement(), Constants.INPUT_CLASSNAME, "keyGenerator")
                        .getValue();
        if (TypeKind.VOID == type.getKind()) {
            return;
        }
        if (!input.isImmutable()) {
            throw error(input, "keyGenerator is only valid on immutable constructor inputs");
        }
        final var provider = validateProvider(env, view, input, type, Constants.INPUT_KEY_GENERATOR_CLASSNAME);
        final var contract = findContract(env, provider.asType(), Constants.INPUT_KEY_GENERATOR_CLASSNAME);
        if (null == contract || 1 != contract.getTypeArguments().size()) {
            throw error(input, "keyGenerator must implement InputKeyGenerator<T> with a resolved type argument");
        }
        final var api =
                Objects.requireNonNull(env.getElementUtils().getTypeElement(Constants.INPUT_KEY_GENERATOR_CLASSNAME));
        final var apiMethod = ElementsUtil.getMethods(api, env.getElementUtils(), env.getTypeUtils()).stream()
                .filter(method -> method.getSimpleName().contentEquals("getKey"))
                .findFirst()
                .orElseThrow();
        final var signature = (ExecutableType) env.getTypeUtils().asMemberOf(contract, apiMethod);
        ExecutableElement target = apiMethod;
        ExecutableType targetType = signature;
        for (final var method : ElementsUtil.getMethods(provider, env.getElementUtils(), env.getTypeUtils())) {
            if (method.getSimpleName().contentEquals("getKey")
                    && !method.getModifiers().contains(Modifier.STATIC)) {
                final var resolved =
                        (ExecutableType) env.getTypeUtils().asMemberOf((DeclaredType) provider.asType(), method);
                if (env.getTypeUtils().isSubsignature(resolved, signature)) {
                    target = method;
                    targetType = resolved;
                    break;
                }
            }
        }
        final var sourceType = input.getType() instanceof PrimitiveType primitive
                ? env.getTypeUtils().boxedClass(primitive).asType()
                : input.getType();
        if (!Nullability.isAssignable(
                        env.getTypeUtils(),
                        input.getElement(),
                        sourceType,
                        view.getElement(),
                        apiMethod.getParameters().get(0),
                        signature.getParameterTypes().get(0),
                        provider)
                || !Nullability.isAssignable(
                        env.getTypeUtils(),
                        input.getElement(),
                        sourceType,
                        view.getElement(),
                        target.getParameters().get(0),
                        targetType.getParameterTypes().get(0),
                        provider)) {
            throw error(input, "keyGenerator parameter type or nullness is incompatible with the input");
        }
        if (Nullability.Nullness.NONNULL != Nullability.resolve(target, targetType.getReturnType(), provider)) {
            throw error(input, "keyGenerator must return a non-null String");
        }
        input.setKeyGenerator(provider, GeneratorUtil.getTypeName(contract));
    }

    @Nullable
    private static DeclaredType findContract(
            final ProcessingEnvironment env, final TypeMirror type, final String name) {
        if (type instanceof DeclaredType declared
                && declared.asElement().toString().equals(name)) {
            return declared;
        }
        for (final var parent : env.getTypeUtils().directSupertypes(type)) {
            final var found = findContract(env, parent, name);
            if (null != found) {
                return found;
            }
        }
        return null;
    }

    static TypeElement validateProvider(
            final ProcessingEnvironment env,
            final ViewDescriptor view,
            final InputDescriptor input,
            final TypeMirror type,
            final String contractName) {
        if (!(type instanceof DeclaredType declared)
                || !(declared.asElement() instanceof TypeElement provider)
                || ElementKind.CLASS != provider.getKind()
                || provider.getModifiers().contains(Modifier.ABSTRACT)
                || ElementsUtil.isNonStaticNestedType(provider)
                || !provider.getTypeParameters().isEmpty()
                || !ElementsUtil.isTypeAccessibleFrom(view.getElement(), provider)
                || !ElementsUtil.hasAccessibleNoArgConstructor(view.getElement(), provider)) {
            throw error(
                    input,
                    "provider must be a concrete accessible class, static if nested, without type parameters,"
                            + " and with an accessible no-argument constructor");
        }
        final var contract = Objects.requireNonNull(env.getElementUtils().getTypeElement(contractName));
        if (!env.getTypeUtils()
                .isAssignable(
                        env.getTypeUtils().erasure(type), env.getTypeUtils().erasure(contract.asType()))) {
            throw error(input, "provider must implement " + contractName);
        }
        for (final var member : provider.getEnclosedElements()) {
            if (ElementKind.CONSTRUCTOR == member.getKind()
                    && ((ExecutableElement) member).getParameters().isEmpty()) {
                for (final var thrown : ((ExecutableElement) member).getThrownTypes()) {
                    if (!env.getTypeUtils()
                                    .isSubtype(
                                            thrown,
                                            Objects.requireNonNull(env.getElementUtils()
                                                            .getTypeElement("java.lang.RuntimeException"))
                                                    .asType())
                            && !env.getTypeUtils()
                                    .isSubtype(
                                            thrown,
                                            Objects.requireNonNull(env.getElementUtils()
                                                            .getTypeElement("java.lang.Error"))
                                                    .asType())) {
                        throw error(input, "provider no-argument constructor must not throw checked exceptions");
                    }
                }
            }
        }
        return provider;
    }

    private static ProcessorException error(final InputDescriptor input, final String message) {
        return new ProcessorException("@Input policy for '" + input.getName() + "': " + message, input.getElement());
    }
}
