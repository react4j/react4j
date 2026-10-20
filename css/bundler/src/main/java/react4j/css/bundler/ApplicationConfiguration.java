package react4j.css.bundler;

import com.google.gson.Gson;
import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import java.util.ArrayList;
import java.util.List;

/** Uses the compiler's define grammar for uncompiled runtime configuration. */
final class ApplicationConfiguration {
    private ApplicationConfiguration() {}

    static String defines(List<String> definitions) {
        var options = new CompilerOptions();
        AbstractCommandLineRunner.createDefineReplacements(
                definitions.stream()
                        .filter(value -> value.startsWith("--define="))
                        .map(value -> value.substring(9))
                        .toList(),
                options);
        var json = new Gson();
        var compiler = new Compiler();
        var values = new ArrayList<String>();
        options.getDefineReplacements()
                .forEach((name, node) -> values.add(json.toJson(name) + ":"
                        + (node.isStringLit() ? json.toJson(node.getString()) : compiler.toSource(node))));
        return "{" + String.join(",", values) + "}";
    }

    static String withoutSourceMap(String source) {
        return source.replaceFirst(
                "(?m)^[\\t ]*(?://# sourceMappingURL=[^\\r\\n]*|/\\*# sourceMappingURL=[^*]*\\*/)[\\t \\r\\n]*\\z", "");
    }
}
