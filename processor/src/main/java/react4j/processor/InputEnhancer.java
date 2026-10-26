package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;
import org.realityforge.proton.AnnotationsUtil;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.ProcessorException;

final class InputEnhancer {
    private final TypeElement _element;
    private final ExecutableElement _setter;
    private final List<ExecutableElement> _helpers = new ArrayList<>();
    private boolean _usesDeprecatedTypes;
    private boolean _usesConcreteCollections;

    private InputEnhancer(final TypeElement element, final ExecutableElement setter) {
        _element = element;
        _setter = setter;
    }

    static void parse(final ProcessingEnvironment env, final ViewDescriptor view, final InputDescriptor input) {
        final var type = (TypeMirror)
                AnnotationsUtil.getAnnotationValue(input.getElement(), Constants.INPUT_CLASSNAME, "enhancer")
                        .getValue();
        if (TypeKind.VOID == type.getKind()) {
            return;
        }
        if (input.isFromTreeContext()) {
            throw error(input, "cannot be specified on a tree-context input");
        }
        if (!(type instanceof DeclaredType declared)
                || !(declared.asElement() instanceof TypeElement element)
                || ElementKind.INTERFACE != element.getKind()
                || element.getModifiers().contains(Modifier.SEALED)) {
            throw error(input, "must be a non-sealed interface");
        }
        for (Element owner = element; owner instanceof TypeElement; owner = owner.getEnclosingElement()) {
            if (owner.getModifiers().contains(Modifier.PRIVATE)
                    || (!owner.getModifiers().contains(Modifier.PUBLIC)
                            && !env.getElementUtils()
                                    .getPackageOf(owner)
                                    .equals(env.getElementUtils().getPackageOf(view.getElement())))) {
                throw error(input, "interface must be accessible from the generated builder");
            }
        }
        final var parameters = element.getTypeParameters();
        if (1 != parameters.size()
                || 1 != parameters.get(0).getBounds().size()
                || !"java.lang.Object"
                        .equals(env.getTypeUtils()
                                .erasure(parameters.get(0).getBounds().get(0))
                                .toString())) {
            throw error(input, "must have exactly one unbounded result type parameter");
        }
        final var methods = ElementsUtil.getMethods(element, env.getElementUtils(), env.getTypeUtils()).stream()
                .filter(m -> !m.getModifiers().contains(Modifier.STATIC)
                        && !m.getModifiers().contains(Modifier.PRIVATE))
                .toList();
        final var abstractMethods = methods.stream()
                .filter(m -> m.getModifiers().contains(Modifier.ABSTRACT))
                .toList();
        if (1 != abstractMethods.size()) {
            throw error(input, "must have exactly one abstract input setter");
        }
        final var setter = abstractMethods.get(0);
        final var enhancer = new InputEnhancer(element, setter);
        for (Element owner = element; owner instanceof TypeElement; owner = owner.getEnclosingElement()) {
            enhancer._usesDeprecatedTypes |= env.getElementUtils().isDeprecated(owner);
        }
        final var setterType = enhancer.methodType(env.getTypeUtils(), setter);
        if (!setter.getSimpleName().contentEquals(input.getName())
                || !setterType.getTypeVariables().isEmpty()
                || 1 != setterType.getParameterTypes().size()
                || !env.getTypeUtils().isSameType(setterType.getParameterTypes().get(0), input.getType())
                || !setterType.getThrownTypes().isEmpty()) {
            throw error(
                    input,
                    "abstract setter must match '" + input.getName() + "(" + input.getType()
                            + ")' without method type parameters or exceptions");
        }
        enhancer.validateResult(env, input, setter, setterType);
        if (!Nullability.isAssignable(
                env.getTypeUtils(),
                setter.getParameters().get(0),
                setterType.getParameterTypes().get(0),
                element,
                input.getElement(),
                input.getType(),
                view.getElement())) {
            throw error(input, "setter parameter nullness is incompatible with the input");
        }
        for (final var method : methods) {
            if (method.getModifiers().contains(Modifier.DEFAULT)) {
                final var methodType = enhancer.methodType(env.getTypeUtils(), method);
                enhancer.validateResult(env, input, method, methodType);
                for (final var parameter : methodType.getParameterTypes()) {
                    enhancer.validateAccessibleType(env, view, input, parameter, new HashSet<>());
                }
                for (final var thrown : methodType.getThrownTypes()) {
                    enhancer.validateAccessibleType(env, view, input, thrown, new HashSet<>());
                }
                for (final var variable : methodType.getTypeVariables()) {
                    enhancer.validateAccessibleType(env, view, input, variable, new HashSet<>());
                }
                enhancer._helpers.add(method);
            }
        }
        input.setEnhancer(enhancer);
    }

    private void validateAccessibleType(
            final ProcessingEnvironment env,
            final ViewDescriptor view,
            final InputDescriptor input,
            final TypeMirror type,
            final Set<Element> visited) {
        if (type instanceof IntersectionType intersection) {
            for (final var bound : intersection.getBounds()) {
                validateAccessibleType(env, view, input, bound, visited);
            }
        } else if (type instanceof DeclaredType declared) {
            _usesConcreteCollections |=
                    "java.util.ArrayList".equals(declared.asElement().toString())
                            || "java.util.HashSet".equals(declared.asElement().toString());
            for (Element owner = declared.asElement();
                    owner instanceof TypeElement;
                    owner = owner.getEnclosingElement()) {
                _usesDeprecatedTypes |= env.getElementUtils().isDeprecated(owner);
                if (owner.getModifiers().contains(Modifier.PRIVATE)
                        || (!owner.getModifiers().contains(Modifier.PUBLIC)
                                && !env.getElementUtils()
                                        .getPackageOf(owner)
                                        .equals(env.getElementUtils().getPackageOf(view.getElement())))) {
                    throw error(
                            input,
                            "helper signature type '" + declared.asElement()
                                    + "' must be accessible from the generated builder");
                }
            }
            validateAccessibleType(env, view, input, declared.getEnclosingType(), visited);
            for (final var argument : declared.getTypeArguments()) {
                validateAccessibleType(env, view, input, argument, visited);
            }
        } else if (type instanceof TypeVariable variable && visited.add(variable.asElement())) {
            validateAccessibleType(env, view, input, variable.getUpperBound(), visited);
        } else if (type instanceof ArrayType array) {
            validateAccessibleType(env, view, input, array.getComponentType(), visited);
        } else if (type instanceof WildcardType wildcard) {
            final var upper = wildcard.getExtendsBound();
            final var lower = wildcard.getSuperBound();
            if (null != upper) {
                validateAccessibleType(env, view, input, upper, visited);
            }
            if (null != lower) {
                validateAccessibleType(env, view, input, lower, visited);
            }
        }
    }

    private void validateResult(
            final ProcessingEnvironment env,
            final InputDescriptor input,
            final ExecutableElement method,
            final ExecutableType methodType) {
        if (!env.getTypeUtils().isSameType(methodType.getReturnType(), resultVariable())
                || Nullability.Nullness.NULLABLE == Nullability.resolve(method, methodType.getReturnType(), _element)
                || Nullability.Nullness.UNSPECIFIED
                        == Nullability.resolve(method, methodType.getReturnType(), _element)) {
            throw error(
                    input, "method '" + method.getSimpleName() + "' must return its result type parameter directly");
        }
    }

    private TypeMirror resultVariable() {
        return _element.getTypeParameters().get(0).asType();
    }

    TypeElement getElement() {
        return _element;
    }

    boolean usesDeprecatedTypes() {
        return _usesDeprecatedTypes;
    }

    boolean usesConcreteCollections() {
        return _usesConcreteCollections;
    }

    List<ExecutableElement> getHelpers() {
        return _helpers;
    }

    ExecutableType methodType(final Types types, final ExecutableElement method) {
        return (ExecutableType) types.asMemberOf((DeclaredType) _element.asType(), method);
    }

    TypeName superinterface(final TypeName result) {
        return ParameterizedTypeName.get(ClassName.get(_element), result);
    }

    private record Helper(InputEnhancer enhancer, ExecutableElement method, String result) {}

    static void validateBuilder(
            final ProcessingEnvironment env, final ViewDescriptor view, final BuilderDescriptor builder) {
        final var generated = new HashMap<String, String>();
        generated.put("newBuilder()", "newBuilder");
        generated.put("build()", "build");
        for (final var input : view.getInputs()) {
            final var enhancer = input.getEnhancer();
            if (null != enhancer) {
                generated.put(declarationSignature(env.getTypeUtils(), enhancer._setter), input.getName());
            }
            if (input.isFromTreeContext()) {
                generated.put(
                        "$context_" + input.getName() + "(" + env.getTypeUtils().erasure(input.getType()) + ")",
                        "$context_" + input.getName());
                generated.put("build(react4j.ReactElement)", "build");
            }
        }
        for (final var step : builder.getSteps()) {
            for (final var method : step.getMethods()) {
                final var methodType = method.getType();
                final var signature = method.getName() + "("
                        + (method.isBuildIntrinsic()
                                ? ""
                                : methodType instanceof ParameterizedTypeName parameterized
                                        ? parameterized.rawType().toString()
                                        : methodType.withoutAnnotations().toString())
                        + ")";
                // TypeMirror erasure is required for ordinary generic inputs.
                final var type = method.getTypeMirror();
                generated.put(
                        null == type
                                ? signature
                                : method.getName() + "(" + env.getTypeUtils().erasure(type) + ")",
                        method.getName());
            }
        }
        final var helpers = new LinkedHashMap<String, Helper>();
        final var erasedHelpers = new HashMap<String, Helper>();
        final var parents = new HashMap<Element, String>();
        for (final var step : builder.getSteps()) {
            for (final var stepMethod : step.getMethods()) {
                final var input = stepMethod.getInput();
                if (null == input) {
                    continue;
                }
                final var enhancer = input.getEnhancer();
                if (null == enhancer) {
                    continue;
                }
                final var result = BuilderGenerator.stepReturnType(view, step, stepMethod.getStepMethodType())
                        .toString();
                enhancer.validateParents(
                        env.getTypeUtils(), (DeclaredType) enhancer._element.asType(), result, parents, input);
                for (final var method : enhancer._helpers) {
                    final var signature = enhancer.signature(env.getTypeUtils(), method, result);
                    final var erasedSignature = declarationSignature(env.getTypeUtils(), method);
                    final var generatedSignature = generated.containsKey(signature) ? signature : erasedSignature;
                    if (generated.containsKey(generatedSignature)) {
                        throw error(
                                input,
                                "helper '" + generatedSignature + "' conflicts with generated builder method '"
                                        + generated.get(generatedSignature) + "'");
                    }
                    final var current = new Helper(enhancer, method, result);
                    final var resolvedPrevious = helpers.putIfAbsent(signature, current);
                    final var erasedPrevious = erasedHelpers.putIfAbsent(erasedSignature, current);
                    for (final var previous : Stream.of(resolvedPrevious, erasedPrevious)
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList()) {
                        final var overrides =
                                env.getElementUtils().overrides(method, previous.method(), enhancer._element);
                        final var overridden = env.getElementUtils()
                                .overrides(previous.method(), method, previous.enhancer()._element);
                        if (!previous.result().equals(result)
                                || !(previous.method().equals(method) || overrides || overridden)) {
                            throw error(
                                    input,
                                    "helper '" + signature + "' conflicts with another enhancer on the shared builder");
                        }
                        if (overrides) {
                            helpers.put(signature, current);
                            erasedHelpers.put(erasedSignature, current);
                        }
                    }
                }
            }
        }
    }

    private void validateParents(
            final Types types,
            final DeclaredType type,
            final String result,
            final Map<Element, String> parents,
            final InputDescriptor input) {
        final var identity = type.asElement()
                + type.getTypeArguments().stream()
                        .map(t -> identity(types, t, result))
                        .collect(Collectors.joining(",", "<", ">"));
        final var previous = parents.putIfAbsent(type.asElement(), identity);
        if (null != previous && !previous.equals(identity)) {
            throw error(input, "incompatible generic parent '" + type.asElement() + "' on the shared builder");
        }
        for (final var parent : types.directSupertypes(type)) {
            validateParents(types, (DeclaredType) parent, result, parents, input);
        }
    }

    private String identity(final Types types, final TypeMirror type, final String result) {
        if (type instanceof ArrayType array) {
            return identity(types, array.getComponentType(), result) + "[]";
        }
        if (type instanceof WildcardType wildcard) {
            final var lower = wildcard.getSuperBound();
            final var upper = wildcard.getExtendsBound();
            return null != lower
                    ? "? super " + identity(types, lower, result)
                    : null != upper ? "? extends " + identity(types, upper, result) : "?";
        }
        if (type instanceof DeclaredType declared) {
            return declared.asElement()
                    + declared.getTypeArguments().stream()
                            .map(t -> identity(types, t, result))
                            .collect(Collectors.joining(",", "<", ">"));
        }
        return types.isSameType(type, resultVariable()) ? result : type.toString();
    }

    String signature(final Types types, final ExecutableElement method, final String result) {
        return method.getSimpleName()
                + methodType(types, method).getParameterTypes().stream()
                        .map(t -> erasure(types, t, result))
                        .collect(Collectors.joining(",", "(", ")"));
    }

    private static String declarationSignature(final Types types, final ExecutableElement method) {
        return method.getSimpleName()
                + method.getParameters().stream()
                        .map(p -> types.erasure(p.asType()).toString())
                        .collect(Collectors.joining(",", "(", ")"));
    }

    private String erasure(final Types types, final TypeMirror type, final String result) {
        if (type instanceof TypeVariable variable) {
            return variable.asElement().equals(((TypeVariable) resultVariable()).asElement())
                    ? result.replaceAll("<.*>", "")
                    : variable.getUpperBound() instanceof TypeVariable
                            ? erasure(types, variable.getUpperBound(), result)
                            : types.erasure(variable).toString();
        }
        return type instanceof ArrayType array
                ? erasure(types, array.getComponentType(), result) + "[]"
                : types.erasure(type).toString();
    }

    private static ProcessorException error(final InputDescriptor input, final String detail) {
        return new ProcessorException("@Input enhancer for '" + input.getName() + "' " + detail, input.getElement());
    }
}
