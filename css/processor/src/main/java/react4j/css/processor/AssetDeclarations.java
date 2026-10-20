package react4j.css.processor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import org.realityforge.proton.GeneratorUtil;
import react4j.css.internal.AssetResource;

final class AssetDeclarations {
    private AssetDeclarations() {}

    record Definition(
            String packageName,
            List<ExecutableElement> methods,
            List<AssetResource.Binding> bindings,
            boolean publicFactory,
            boolean deprecated) {
        Definition {
            methods = List.copyOf(methods);
            bindings = List.copyOf(bindings);
        }

        Map<String, String> values() {
            var values = new LinkedHashMap<String, String>();
            bindings.forEach(binding -> values.put(binding.method(), binding.value()));
            return values;
        }
    }

    static Definition parse(ProcessingEnvironment environment, TypeElement owner) {
        if (!owner.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new IllegalArgumentException("@Assets requires an abstract class");
        }
        String name = environment.getElementUtils().getBinaryName(owner).toString();
        boolean deprecated = environment.getElementUtils().isDeprecated(owner);
        for (var enclosing = owner.getEnclosingElement();
                enclosing instanceof TypeElement;
                enclosing = enclosing.getEnclosingElement()) {
            deprecated |= environment.getElementUtils().isDeprecated(enclosing);
        }
        var methods = new ArrayList<ExecutableElement>();
        var bindings = new ArrayList<AssetResource.Binding>();
        var inherited = CssMethods.collect(environment, owner);
        for (var member : inherited) {
            var method = member.element();
            var annotation = CssProcessor.annotation(member.declaration(), "react4j.css.Asset");
            for (var mirror : member.declaration().getAnnotationMirrors()) {
                String type = mirror.getAnnotationType().toString();
                if (List.of(
                                "react4j.css.CssId",
                                "react4j.css.CssAnimation",
                                "react4j.css.CssCustomProperty",
                                "react4j.css.CssStable")
                        .contains(type)) {
                    throw new IllegalArgumentException(
                            "@Assets accessors must use only @Asset: " + method.getSimpleName());
                }
            }
            if (null == annotation) {
                if (method.getModifiers().contains(Modifier.ABSTRACT)) {
                    throw new IllegalArgumentException(
                            "@Assets abstract methods require @Asset: " + method.getSimpleName());
                }
                continue;
            }
            if (!method.getModifiers().contains(Modifier.ABSTRACT) || !member.isAccessor(environment.getTypeUtils())) {
                throw new IllegalArgumentException(
                        "@Asset accessors must be abstract, take no arguments and return String"
                                + " without type parameters or throws: " + method.getSimpleName());
            }
            var relative = (String)
                    annotation.getElementValues().values().iterator().next().getValue();
            methods.add(method);
            bindings.add(new AssetResource.Binding(
                    method.getSimpleName().toString(), AssetResource.resolve(name, relative)));
            deprecated |= environment.getElementUtils().isDeprecated(method)
                    || environment.getElementUtils().isDeprecated(member.declaration());
        }
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("@Assets requires at least one @Asset accessor");
        }
        return new Definition(
                GeneratorUtil.getQualifiedPackageName(owner),
                methods,
                bindings,
                CssMethods.publicFactory(environment, owner, inherited, "@Assets"),
                deprecated);
    }

    static void emit(ProcessingEnvironment environment, TypeElement owner, String implementation, Definition definition)
            throws IOException {
        String name = environment.getElementUtils().getBinaryName(owner).toString();
        var resource = new AssetResource(name, implementation, definition.bindings());
        try (var stream = environment
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", AssetResource.path(name), owner)
                .openOutputStream()) {
            stream.write(resource.json().getBytes(StandardCharsets.UTF_8));
        }
    }
}
