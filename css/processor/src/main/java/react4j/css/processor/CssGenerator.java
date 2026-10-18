package react4j.css.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class CssGenerator {
    private CssGenerator() {}

    static TypeSpec buildType(
            final ProcessingEnvironment processingEnv,
            final CssDescriptor descriptor,
            final Map<String, String> values) {
        var owner = descriptor.element();
        String packageName = descriptor.packageName();
        var ownerName = ClassName.get(owner);
        boolean ownerShadowed = processingEnv.getElementUtils().getAllMembers(owner).stream()
                .anyMatch(member -> member instanceof TypeElement
                        && member.getSimpleName()
                                .contentEquals(ownerName.topLevelClassName().simpleName()));
        var generated = GeneratorUtil.getGeneratedClassName(owner, "Styled_", "");
        // A hidden owner name cannot be referenced inside the subclass, even in the unnamed package.
        var instanceType = ownerShadowed ? generated : ownerName;
        var factory =
                MethodSpec.methodBuilder("get").addModifiers(Modifier.STATIC).returns(instanceType);
        if (descriptor.publicFactory()) {
            factory.addModifiers(Modifier.PUBLIC);
        }
        var builder =
                TypeSpec.classBuilder(generated).addModifiers(Modifier.FINAL).superclass(ownerName);
        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
        // A holder must not hide its owner's top-level type in the unnamed package.
        String holderName = "Internal".equals(ownerName.topLevelClassName().simpleName()) ? "Internal_" : "Internal";
        builder.addType(TypeSpec.classBuilder(holderName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addField(FieldSpec.builder(instanceType, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", generated)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .build());
        builder.addMethod(factory.addStatement("return $L.$L.INSTANCE", generated.simpleName(), holderName)
                .build());
        boolean qualified = false;
        // Inherited and peer types can hide the generated annotations and java.lang names.
        for (var name : List.of("String", "Override", "SuppressWarnings", "Generated", "NullMarked")) {
            var peer = processingEnv
                    .getElementUtils()
                    .getTypeElement(packageName.isEmpty() ? name : packageName + "." + name);
            boolean inherited = processingEnv.getElementUtils().getAllMembers(owner).stream()
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
                descriptor.accessesDeprecatedElements() ? "deprecation" : null,
                descriptor.accessesDeprecatedElements() ? "removal" : null);
        if (null != suppression) {
            builder.addAnnotation(suppression);
        }
        for (var binding : descriptor.accessors()) {
            var method = binding.method();
            String name = method.getSimpleName().toString();
            String token = values.get(name);
            var accessor = MethodSpec.methodBuilder(name)
                    .addAnnotation(Override.class)
                    .returns(String.class)
                    .addStatement("return $S", token);
            GeneratorUtil.copyAccessModifiers(method, accessor);
            builder.addMethod(accessor.build());
        }
        return builder.build();
    }
}
