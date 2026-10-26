package react4j.processor;

import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeVariableName;
import com.palantir.javapoet.WildcardTypeName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

/** Builds static signatures without confusing enhancer, view and helper type variables with equal names. */
final class EnhancerMethod {
    private final TypeElement _enhancer;
    private final Map<Element, TypeName> _variables = new HashMap<>();

    private EnhancerMethod(final TypeElement enhancer, final TypeName result) {
        _enhancer = enhancer;
        _variables.put(enhancer.getTypeParameters().get(0), result);
    }

    static MethodSpec shortcut(
            final ProcessingEnvironment env,
            final ViewDescriptor view,
            final InputEnhancer enhancer,
            final ExecutableElement helper,
            final TypeName result,
            final Set<String> reservedNames) {
        final var signature = new EnhancerMethod(enhancer.getElement(), result);
        final var type = enhancer.methodType(env.getTypeUtils(), helper);
        final var method = MethodSpec.methodBuilder(helper.getSimpleName().toString())
                .addModifiers(Modifier.STATIC)
                .returns(Nullability.qualify(
                        helper, helper.getReturnType(), type.getReturnType(), enhancer.getElement(), result))
                .varargs(helper.isVarArgs());
        if (view.exportBuilder()) {
            method.addModifiers(Modifier.PUBLIC);
        }
        GeneratorUtil.copyTypeParameters(view.getElement(), method);
        GeneratorUtil.copyWhitelistedAnnotations(helper, method);
        Nullability.preserveScope(helper, method);
        final var parameters = type.getParameterTypes();
        final var nonReifiableVarargs = helper.isVarArgs()
                && !env.getTypeUtils()
                        .isSameType(
                                parameters.get(parameters.size() - 1),
                                env.getTypeUtils().erasure(parameters.get(parameters.size() - 1)));
        // The shortcut forwards the array unchanged; preserve the author's varargs contract without its declaration
        // warning.
        if (nonReifiableVarargs) {
            method.addAnnotation(SuppressWarningsUtil.suppressWarningsAnnotation("unchecked"));
        }
        final var usedNames = view.getElement().getTypeParameters().stream()
                .map(p -> p.getSimpleName().toString())
                .collect(Collectors.toCollection(HashSet::new));
        usedNames.addAll(reservedNames);
        final var packageName =
                env.getElementUtils().getPackageOf(view.getElement()).getQualifiedName();
        for (final var variable : type.getTypeVariables()) {
            final var name = variable.asElement().getSimpleName().toString();
            var candidate = name;
            for (var suffix = 2;
                    usedNames.contains(candidate)
                            || null != env.getElementUtils().getTypeElement("java.lang." + candidate)
                            || null != env.getElementUtils().getTypeElement(packageName + "." + candidate);
                    suffix++) {
                candidate = name + suffix;
            }
            usedNames.add(candidate);
            signature._variables.put(variable.asElement(), TypeVariableName.get(candidate));
        }
        for (final var variable : type.getTypeVariables()) {
            final var parameter = (TypeParameterElement) variable.asElement();
            final var upper = variable.getUpperBound();
            var bounds = upper instanceof IntersectionType intersection ? intersection.getBounds() : List.of(upper);
            if (bounds.size() > parameter.getBounds().size()
                    && "java.lang.Object"
                            .equals(env.getTypeUtils().erasure(bounds.get(0)).toString())) {
                bounds = bounds.subList(1, bounds.size());
            }
            final var names = new TypeName[bounds.size()];
            for (var i = 0; i < bounds.size(); i++) {
                names[i] = signature.typeName(parameter, parameter.getBounds().get(i), bounds.get(i));
            }
            method.addTypeVariable(TypeVariableName.get(
                    Objects.requireNonNull(signature._variables.get(parameter)).toString(), names));
        }
        for (var i = 0; i < type.getParameterTypes().size(); i++) {
            final var parameter = helper.getParameters().get(i);
            method.addParameter(
                    signature.typeName(
                            parameter,
                            parameter.asType(),
                            type.getParameterTypes().get(i)),
                    "arg" + i,
                    Modifier.FINAL);
        }
        for (var i = 0; i < type.getThrownTypes().size(); i++) {
            method.addException(signature
                    .typeName(
                            helper,
                            helper.getThrownTypes().get(i),
                            type.getThrownTypes().get(i))
                    .withoutAnnotations());
        }
        final var viewArguments = BuilderGenerator.asTypeArgumentsInfix(view.getDeclaredType());
        final var helperArguments = type.getTypeVariables().isEmpty()
                ? ""
                : type.getTypeVariables().stream()
                        .map(v -> Objects.requireNonNull(signature._variables.get(v.asElement()))
                                .toString())
                        .collect(Collectors.joining(", ", "<", ">"));
        final var arguments = IntStream.range(0, helper.getParameters().size())
                .mapToObj(i -> "arg" + i)
                .collect(Collectors.joining(", "));
        method.addStatement(
                "return $T." + viewArguments + "newBuilder()." + helperArguments + "$N(" + arguments + ")",
                view.getBuilderClassName(),
                helper.getSimpleName().toString());
        return method.build();
    }

    static void reserveTypeNames(final TypeMirror type, final Set<String> names) {
        reserveTypeNames(type, names, new HashSet<>());
    }

    private static void reserveTypeNames(final TypeMirror type, final Set<String> names, final Set<Element> visited) {
        if (type instanceof IntersectionType intersection) {
            for (final var bound : intersection.getBounds()) {
                reserveTypeNames(bound, names, visited);
            }
        } else if (type instanceof DeclaredType declared) {
            names.addAll(ClassName.get((TypeElement) declared.asElement()).simpleNames());
            reserveTypeNames(declared.getEnclosingType(), names, visited);
            for (final var argument : declared.getTypeArguments()) {
                reserveTypeNames(argument, names, visited);
            }
        } else if (type instanceof ArrayType array) {
            reserveTypeNames(array.getComponentType(), names, visited);
        } else if (type instanceof TypeVariable variable && visited.add(variable.asElement())) {
            reserveTypeNames(variable.getUpperBound(), names, visited);
        } else if (type instanceof WildcardType wildcard) {
            if (null != wildcard.getExtendsBound()) {
                reserveTypeNames(wildcard.getExtendsBound(), names, visited);
            }
            if (null != wildcard.getSuperBound()) {
                reserveTypeNames(wildcard.getSuperBound(), names, visited);
            }
        }
    }

    private TypeName typeName(final Element declaration, final TypeMirror original, final TypeMirror type) {
        final TypeName name;
        if (type instanceof TypeVariable variable) {
            name = _variables.getOrDefault(variable.asElement(), GeneratorUtil.getTypeName(type));
        } else if (type instanceof ArrayType array) {
            name = ArrayTypeName.of(typeName(
                    declaration,
                    original instanceof ArrayType value ? value.getComponentType() : array.getComponentType(),
                    array.getComponentType()));
        } else if (type instanceof DeclaredType declared) {
            final var element = (TypeElement) declared.asElement();
            final var originalArguments =
                    original instanceof DeclaredType value ? value.getTypeArguments() : List.<TypeMirror>of();
            final var arguments = new TypeName[declared.getTypeArguments().size()];
            for (var i = 0; i < arguments.length; i++) {
                final var argument = declared.getTypeArguments().get(i);
                arguments[i] = typeName(
                        declaration, i < originalArguments.size() ? originalArguments.get(i) : argument, argument);
            }
            final var enclosing = declared.getEnclosingType();
            if (enclosing instanceof DeclaredType && !element.getModifiers().contains(Modifier.STATIC)) {
                final var owner = typeName(
                        declaration,
                        original instanceof DeclaredType value ? value.getEnclosingType() : enclosing,
                        enclosing);
                name = owner instanceof ParameterizedTypeName parameterized
                        ? parameterized.nestedClass(element.getSimpleName().toString(), List.of(arguments))
                        : 0 == arguments.length
                                ? ((ClassName) owner)
                                        .nestedClass(element.getSimpleName().toString())
                                : ParameterizedTypeName.get(
                                        ((ClassName) owner)
                                                .nestedClass(
                                                        element.getSimpleName().toString()),
                                        arguments);
            } else {
                name = 0 == arguments.length
                        ? ClassName.get(element)
                        : ParameterizedTypeName.get(ClassName.get(element), arguments);
            }
        } else if (type instanceof WildcardType wildcard) {
            final var lower = wildcard.getSuperBound();
            final var upper = wildcard.getExtendsBound();
            if (null != lower) {
                return WildcardTypeName.supertypeOf(typeName(
                        declaration,
                        original instanceof WildcardType value && null != value.getSuperBound()
                                ? value.getSuperBound()
                                : lower,
                        lower));
            }
            return WildcardTypeName.subtypeOf(
                    null == upper
                            ? ClassName.get(Object.class)
                            : typeName(
                                    declaration,
                                    original instanceof WildcardType value && null != value.getExtendsBound()
                                            ? value.getExtendsBound()
                                            : upper,
                                    upper));
        } else {
            name = GeneratorUtil.getTypeName(type);
        }
        return Nullability.qualify(declaration, original, type, _enhancer, name);
    }
}
