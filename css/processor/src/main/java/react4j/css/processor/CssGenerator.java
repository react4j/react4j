package react4j.css.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.GeneratorUtil;
import org.realityforge.proton.SuppressWarningsUtil;

final class CssGenerator {
    private CssGenerator() {}

    static TypeSpec buildType(final ProcessingEnvironment processingEnv, final CssDescriptor descriptor) {
        var owner = descriptor.element();
        String packageName = descriptor.packageName();
        String root = descriptor.rootClassName();
        var ownerType = ClassName.get(owner);
        var factory =
                MethodSpec.methodBuilder("get").addModifiers(Modifier.STATIC).returns(ownerType);
        if (descriptor.publicFactory()) {
            factory.addModifiers(Modifier.PUBLIC);
        }
        var generated = GeneratorUtil.getGeneratedClassName(owner, "Styled_", "");
        var builder =
                TypeSpec.classBuilder(generated).addModifiers(Modifier.FINAL).superclass(ownerType);
        builder.addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
        // A holder must not hide its owner's top-level type in the unnamed package.
        String holderName = "Internal".equals(ownerType.topLevelClassName().simpleName()) ? "Internal_" : "Internal";
        builder.addType(TypeSpec.classBuilder(holderName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addField(FieldSpec.builder(ownerType, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", generated)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .build());
        builder.addMethod(factory.addStatement("return $L.$L.INSTANCE", generated.simpleName(), holderName)
                .build());
        boolean qualified = false;
        // Proton omits java.lang imports, so peer and inherited types need explicit qualification.
        for (var name : List.of("String", "Override", "SuppressWarnings")) {
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
        for (var method : descriptor.accessors()) {
            String name = method.getSimpleName().toString();
            String token = root + ("style".equals(name) ? "" : " " + root + "--" + Names.encode(name));
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
