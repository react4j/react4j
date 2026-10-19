package react4j.css.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class CssGenerator {
    private CssGenerator() {}

    record Generated(TypeSpec type, String implementation) {}

    static Generated buildType(
            final ProcessingEnvironment processingEnv,
            final CssDescriptor descriptor,
            final Map<String, String> values) {
        var owner = descriptor.element();
        return buildType(
                processingEnv,
                owner,
                "Styled_",
                descriptor.publicFactory(),
                descriptor.accessesDeprecatedElements(),
                descriptor.accessors().stream().map(CssAccessor::method).toList(),
                values);
    }

    static Generated buildType(
            ProcessingEnvironment processingEnv,
            TypeElement owner,
            String prefix,
            boolean publicFactory,
            boolean accessesDeprecatedElements,
            List<ExecutableElement> methods,
            Map<String, String> values) {
        String packageName = GeneratorUtil.getQualifiedPackageName(owner);
        var ownerName = ClassName.get(owner);
        var members = processingEnv.getElementUtils().getAllMembers(owner);
        var reserved = new HashSet<String>();
        members.forEach(member -> reserved.add(member.getSimpleName().toString()));
        reserved.add(ownerName.topLevelClassName().simpleName());
        var generated = GeneratorUtil.getGeneratedClassName(owner, prefix, "");
        boolean facade = members.stream()
                .anyMatch(member ->
                        member instanceof TypeElement && member.getSimpleName().contentEquals(generated.simpleName()));
        String implementationName = facade ? freshName("Implementation", reserved) : generated.simpleName();
        var implementation = TypeSpec.classBuilder(implementationName)
                .addModifiers(Modifier.FINAL)
                .superclass(ownerName)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build());
        var builder = implementation;
        if (facade) {
            // A separate lexical scope can name the subclass even when an inherited type hides it.
            implementation.addModifiers(Modifier.STATIC);
            builder = TypeSpec.classBuilder(generated)
                    .addModifiers(Modifier.FINAL)
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PRIVATE)
                            .build());
        }
        boolean ownerShadowed = members.stream()
                .anyMatch(member -> member instanceof TypeElement
                        && member.getSimpleName()
                                .contentEquals(ownerName.topLevelClassName().simpleName()));
        var instanceType = !facade && ownerShadowed ? generated : ownerName;
        var factory =
                MethodSpec.methodBuilder("get").addModifiers(Modifier.STATIC).returns(instanceType);
        if (publicFactory) {
            factory.addModifiers(Modifier.PUBLIC);
        }
        String holderName = freshName("Internal", reserved);
        builder.addType(TypeSpec.classBuilder(holderName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addField(FieldSpec.builder(instanceType, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", facade ? generated.nestedClass(implementationName) : generated)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .build());
        builder.addMethod(factory.addStatement("return $L.INSTANCE", holderName).build());
        boolean qualified = false;
        // Inherited and peer types can hide the generated annotations and java.lang names.
        for (var name : List.of("String", "Override", "SuppressWarnings", "Generated", "NullMarked")) {
            var peer = processingEnv
                    .getElementUtils()
                    .getTypeElement(packageName.isEmpty() ? name : packageName + "." + name);
            boolean inherited = members.stream()
                    .anyMatch(member -> member instanceof TypeElement
                            && member.getSimpleName().contentEquals(name));
            if (null != peer || inherited) {
                builder.alwaysQualify(name);
                qualified = true;
            }
        }
        GeneratorUtil.addGeneratedAnnotation(processingEnv, builder, CssProcessor.class.getName());
        GeneratorUtil.addOriginatingTypes(owner, builder);
        var suppression = SuppressWarningsUtil.maybeSuppressWarningsAnnotation(
                packageName.isEmpty() ? "DefaultPackage" : null,
                qualified ? "UnnecessarilyFullyQualified" : null,
                accessesDeprecatedElements ? "deprecation" : null,
                accessesDeprecatedElements ? "removal" : null);
        if (null != suppression) {
            builder.addAnnotation(suppression);
        }
        for (var method : methods) {
            String name = method.getSimpleName().toString();
            String token = values.get(name);
            var accessor = MethodSpec.methodBuilder(name)
                    .addAnnotation(Override.class)
                    .returns(String.class)
                    .addStatement("return $S", token);
            GeneratorUtil.copyAccessModifiers(method, accessor);
            implementation.addMethod(accessor.build());
        }
        if (facade) {
            builder.addType(implementation.build());
        }
        return new Generated(builder.build(), generated.canonicalName() + (facade ? "$" + implementationName : ""));
    }

    private static String freshName(String base, Set<String> reserved) {
        String name = base;
        while (!reserved.add(name)) {
            name += "_";
        }
        return name;
    }
}
