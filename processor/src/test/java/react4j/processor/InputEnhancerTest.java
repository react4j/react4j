package react4j.processor;

import static org.testng.Assert.*;

import arez.processor.ArezProcessor;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.testing.StrictGeneratedCode;

public final class InputEnhancerTest extends AbstractProcessorTest {
    @Override
    protected String getOptionPrefix() {
        return "react4j";
    }

    @Override
    protected Processor processor() {
        return new React4jProcessor();
    }

    @Override
    protected Processor[] additionalProcessors() {
        return new Processor[] {new ArezProcessor()};
    }

    private static JavaFileObject source(final String declarations, final String inputs) {
        return source(declarations, inputs, "");
    }

    private static JavaFileObject source(final String declarations, final String inputs, final String typeParameters) {
        final var text = """
            package test;
            import java.util.*;
            import org.jspecify.annotations.*;
            import react4j.ReactNode;
            import react4j.annotations.*;
            @NullMarked
            public final class Case {
              %s
              @View(type=View.Type.STATEFUL)
                  static abstract class Component%s {
                %s
                @Render @Nullable ReactNode render() { return null; }
              }
            }
            """.formatted(declarations, typeParameters, inputs);
        return new SimpleJavaFileObject(URI.create("string:///test/Case.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return text;
            }
        };
    }

    @DataProvider(name = "invalid")
    public Object[][] invalid() {
        return new Object[][] {
            {"public static class Enhancer<R> {}", "must be a non-sealed interface"},
            {
                "public sealed interface Enhancer<R> permits Impl { R value(String v); } public static final class"
                        + " Impl implements Enhancer<String> { public String value(String v) { return v; } }",
                "must be a non-sealed interface"
            },
            {"private interface Enhancer<R> { R value(String v); }", "must be accessible"},
            {"public interface Enhancer { String value(String v); }", "exactly one unbounded"},
            {"public interface Enhancer<R, V> { R value(V v); }", "exactly one unbounded"},
            {"public interface Enhancer<R extends CharSequence> { R value(String v); }", "exactly one unbounded"},
            {"public interface Enhancer<R> {}", "exactly one abstract"},
            {"public interface Enhancer<R> { R value(String v); R other(int v); }", "exactly one abstract"},
            {"public interface Enhancer<R> { R wrong(String v); }", "abstract setter must match"},
            {"public interface Enhancer<R> { R value(Object v); }", "abstract setter must match"},
            {"public interface Enhancer<R> { R value(); }", "abstract setter must match"},
            {"public interface Enhancer<R> { R value(String v, int n); }", "abstract setter must match"},
            {"public interface Enhancer<R> { <T> R value(String v); }", "abstract setter must match"},
            {"public interface Enhancer<R> { R value(String v) throws Exception; }", "abstract setter must match"},
            {"public interface Enhancer<R> { String value(String v); }", "result type parameter directly"},
            {"public interface Enhancer<R> { @Nullable R value(String v); }", "result type parameter directly"},
            {"@NullUnmarked public interface Enhancer<R> { R value(String v); }", "result type parameter directly"},
            {"public interface Enhancer<R> { R value(@Nullable String v); }", "parameter nullness is incompatible"},
            {
                "public interface Enhancer<R> { R value(String v); default String bad() { return \"x\"; } }",
                "result type parameter directly"
            },
            {
                "public interface Enhancer<R> { R value(String v); default R build() { return value(\"x\"); } }",
                "conflicts with generated builder method"
            },
            {
                "public interface Enhancer<R> { R value(String v); default R newBuilder() { return value(\"x\"); } }",
                "conflicts with generated builder method"
            },
            {
                "public interface Enhancer<R> { R value(String v); default R other(int v) { return value(\"x\"); } }",
                "conflicts with generated builder method"
            },
        };
    }

    @Test(dataProvider = "invalid")
    public void invalid(final String declaration, final String diagnostic) throws Exception {
        assertInvalid(
                declaration,
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract int other();",
                diagnostic);
    }

    @Test
    public void contextAndNestedNullness() throws Exception {
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); }",
                "@Input(enhancer=Enhancer.class, fromTreeContext=true) abstract String value();",
                "tree-context");
        assertInvalid(
                "public interface Enhancer<R> { R value(List<@Nullable String> v); }",
                "@Input(enhancer=Enhancer.class) abstract List<String> value();",
                "parameter nullness is incompatible");
        assertInvalid(
                "public interface Base<R,V> { R value(V v); } public interface Enhancer<R> extends Base<R,@Nullable"
                        + " String> {}",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "parameter nullness is incompatible");
    }

    @Test
    public void sharedBuilderConflicts() throws Exception {
        final var inputs =
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();";
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R preset() { return value(\"x\"); } }"
                        + " public interface Other<R> { R other(int v); default R preset() { return other(1); } }",
                inputs,
                "conflicts with another enhancer");
        assertInvalid(
                "public interface Base<R> {} public interface Enhancer<R> extends Base<R> { R value(String v); }"
                        + " public interface Other<R> extends Base<R> { R other(int v); }",
                inputs,
                "incompatible generic parent");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R convert(List<String> v) { return"
                    + " value(\"x\"); } } public interface Other<R> { R other(int v); default R convert(List<Integer>"
                    + " v) { return other(1); } }",
                inputs,
                "conflicts with another enhancer");
    }

    @Test
    public void continuationsAndInheritedContracts() throws Exception {
        final var declarations = """
            public interface Base<R,V> {
              R value(V value);
              default R standard(V value) { return value(value); }
            }
            public interface Enhancer<R> extends Base<R,String> {
              default <E extends Enum<E>> R enumValue(Class<E> type) throws Exception { return value(type.getName()); }
              default R joined(String... values) { return value(String.join(",", values)); }
              private String local() { return "x"; }
              static String utility() { return "x"; }
            }
            """;
        assertValid(declarations, "@Input(enhancer=Enhancer.class) abstract String value();", "Enhancer<ReactNode>");
        assertValid(
                declarations,
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract String z();",
                "Enhancer<Step2>");
        assertValid(
                declarations,
                "@Input(enhancer=Enhancer.class) abstract @Nullable String value(); @Input abstract @Nullable String"
                        + " z();",
                "Enhancer<Step1>");
        assertValid(
                "public interface Enhancer<R> { R value(int v); default R one() { return value(1); } }",
                "Component(@Input(name=\"value\", enhancer=Enhancer.class) int renamed) {}",
                "Enhancer<ReactNode>");
        assertValid(
                "public interface Enhancer<R> { R value(@Nullable String v); default R empty() { return value(null); }"
                        + " }",
                "@Input(enhancer=Enhancer.class) abstract @Nullable String value();",
                "Enhancer<ReactNode>");
    }

    private void assertValid(final String declarations, final String inputs, final String expected) throws Exception {
        assertValid(declarations, inputs, expected, "");
    }

    private void assertValid(
            final String declarations, final String inputs, final String expected, final String typeParameters)
            throws Exception {
        final var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                List.of(source(declarations, inputs, typeParameters)), getOptions(), processors(), List.of());
        try {
            StrictGeneratedCode.check(compilation, getOptions());
            final var builder = Files.readString(compilation.sourceOutput().resolve("test/Case_ComponentBuilder.java"));
            assertTrue(builder.contains(expected), builder);
        } finally {
            cleanup(compilation);
        }
    }

    @Test
    public void compatibleSharedDefaults() throws Exception {
        final var base = "public interface Common<R> { default R preset() { throw new IllegalStateException(); } } ";
        final var other = "public interface Other<R> extends Common<R> { R other(@Nullable String v); } ";
        final var inputs =
                "@Input(enhancer=Enhancer.class) abstract @Nullable String value(); @Input(enhancer=Other.class)"
                        + " abstract @Nullable String other();";
        assertValid(
                base + other + "public interface Enhancer<R> extends Common<R> { R value(@Nullable String v); }",
                inputs,
                "Enhancer<Step1>");
        assertValid(
                base + other
                        + "public interface Enhancer<R> extends Common<R> { R value(@Nullable String v); @Override"
                        + " default R preset() { return value(\"override\"); } }",
                inputs,
                "Enhancer<Step1>");
    }

    @Test
    public void requiredCallCannotBeSkipped() throws Exception {
        final var declaration =
                "public interface Enhancer<R> { R value(String v); default R preset() { return value(\"x\"); } }"
                        + " static ReactNode invalid() { return Case_ComponentBuilder.newBuilder().preset(); }";
        final var compilation = CompileTestUtil.compile(
                List.of(source(
                        declaration,
                        "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract String z();")),
                getOptions(),
                processors(),
                List.of());
        try {
            assertFalse(compilation.success());
            final var message = CompileTestUtil.asMessage(compilation.diagnostics());
            assertTrue(message.contains("Step2 cannot be converted to react4j.ReactNode"), message);
        } finally {
            cleanup(compilation);
        }
    }

    @Test
    public void genericStaticShortcuts() throws Exception {
        final var declaration = """
            public interface Base<Q> {
              Q value(String value);
              default <T extends Number, E extends Exception> Q generic(T value, E error) throws E {
                if (value.intValue() < 0) { throw error; }
                return value(value.toString());
              }
              default <R> Q stringify(R value) { return value(String.valueOf(value)); }
              default Q joined(String... values) { return value(String.join(",", values)); }
              @SuppressWarnings("unchecked")
              default <X> Q several(X... values) { return value(Arrays.toString(values)); }
            }
            public interface Enhancer<R> extends Base<R> {}
            static ReactNode calls() throws java.io.IOException {
              var error = new java.io.IOException("conversion");
              Case_ComponentBuilder.<Integer, Double, java.io.IOException>generic(1.0, error).z("done");
              Case_ComponentBuilder.<Integer, String>stringify("typed").z("done");
              Case_ComponentBuilder.<Integer>joined("a", "b").z("done");
              return Case_ComponentBuilder.<Integer, String>several("a", "b").z("done");
            }
            """;
        final var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                List.of(source(
                        declaration,
                        "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract String z();",
                        "<T extends Number>")),
                getOptions(),
                processors(),
                List.of());
        try {
            StrictGeneratedCode.check(compilation, getOptions());
            final var builder = Files.readString(compilation.sourceOutput().resolve("test/Case_ComponentBuilder.java"));
            assertTrue(builder.contains("T2 extends Number"), builder);
            assertTrue(builder.contains("throws E"), builder);
            final var shortcut = builder.substring(
                    builder.indexOf("static <T extends Number, T2"), builder.indexOf("interface Step1"));
            assertFalse(shortcut.contains("@Contract"), shortcut);
        } finally {
            cleanup(compilation);
        }
    }

    @Test
    public void staticNestedNullness() throws Exception {
        assertValid("""
            public interface Base<R,V extends @Nullable Object> {
              R value(String value);
              default R nested(List<V> value) { return value(value.toString()); }
              default R upper(List<? extends V> value) { return value(value.toString()); }
              default R lower(List<? super V> value) { return value(value.toString()); }
            }
            public interface Enhancer<R> extends Base<R,@Nullable String> {}
            static ReactNode call() { return Case_ComponentBuilder.nested(Arrays.asList("a", null)); }
            """, "@Input(enhancer=Enhancer.class) abstract String value();", "List<@Nullable String>");
    }

    @Test
    public void inaccessibleHelperTypesAndContextConflicts() throws Exception {
        final var declaration =
                "private static class Secret {} public interface Enhancer<R> { R value(String v); default R"
                        + " hidden(List<? super Secret[]> v) { return value(\"x\"); } }";
        assertInvalid(declaration, "@Input(enhancer=Enhancer.class) abstract String value();", "helper signature type");
        assertInvalid(
                "private interface Secret {} public interface Enhancer<R> { R value(String v); default <T extends"
                        + " Number & Secret> R hidden(T v) { return value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "helper signature type");
        assertInvalid(
                "private static class Secret extends Exception { private static final long serialVersionUID=1L; }"
                        + " public interface Enhancer<R> { R value(String v); default <T extends Secret> R hidden(T v)"
                        + " throws T { return value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "helper signature type");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R $context_context(@Nullable String v) {"
                        + " return value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(fromTreeContext=true) abstract"
                        + " @Nullable String context();",
                "conflicts with generated builder method");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R build(react4j.ReactElement v) { return"
                        + " value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(fromTreeContext=true) abstract"
                        + " @Nullable String context();",
                "conflicts with generated builder method");
        assertInvalid(
                "public interface Enhancer<R> { R children(ReactNode[] v); default R"
                        + " children(java.util.stream.Stream<? extends ReactNode> v) { return"
                        + " children(v.toArray(ReactNode[]::new)); } }",
                "@Input(enhancer=Enhancer.class) abstract ReactNode @Nullable [] children(); @Input abstract @Nullable"
                        + " String value();",
                "conflicts with generated builder method");
    }

    @Test
    public void nestedGenericParentConflicts() throws Exception {
        final var inputs =
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();";
        assertInvalid(
                "public interface Base<X> {} public interface Enhancer<R> extends Base<R[]> { R value(String v); }"
                        + " public interface Other<R> extends Base<R[]> { R other(int v); }",
                inputs,
                "incompatible generic parent");
        assertInvalid(
                "public interface Base<X> {} public interface Enhancer<R> extends Base<List<? extends R>> { R"
                    + " value(String v); } public interface Other<R> extends Base<List<? extends R>> { R other(int v);"
                    + " }",
                inputs,
                "incompatible generic parent");
    }

    @Test
    public void nestedOwnerAndResultParameters() throws Exception {
        assertValid("""
                public static class Outer<T extends @Nullable Object> { public class Inner<U> {} }
            public static class Plain { public class Inner<U> {} }
            public interface Enhancer<R> {
              R value(String value);
              default R owners(Outer<@Nullable String>.Inner<Integer> a, Plain.Inner<String> b) { return value("owners"); }
              default R previous(@Nullable R value) { return value("previous"); }
              default <T extends Number & Comparable<T>> R intersection(T value) { return value(value.toString()); }
              default <T extends Comparable<T> & Runnable> R interfaces(T value) { return value(value.toString()); }
              default <T extends R> R resultBound(T value) { return value("result-bound"); }
            }
            """, "@Input(enhancer=Enhancer.class) abstract String value();", "Plain.Inner<String>");
        assertValid(
                "public interface Common<R> { default R previous(R value) { return value; } } public interface"
                        + " Enhancer<R> extends Common<R> { R value(@Nullable String v); } public interface Other<S>"
                        + " extends Common<S> { S other(@Nullable String v); }",
                "@Input(enhancer=Enhancer.class) abstract @Nullable String value(); @Input(enhancer=Other.class)"
                        + " abstract @Nullable String other();",
                "Enhancer<Step1>");
    }

    @Test
    public void unmarkedHelperScope() throws Exception {
        assertValid(
                "public interface Enhancer<R> { R value(String v); @NullUnmarked default @NonNull R converted(Object"
                        + " value) { return value(String.valueOf(value)); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "@NullUnmarked");
    }

    @Test
    public void deprecatedEnhancer() throws Exception {
        assertValid(
                "@Deprecated public interface Enhancer<R> { R value(String v); default R preset() { return"
                        + " value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "Enhancer<ReactNode>");
        assertValid(
                "@Deprecated public static class Old {} public interface Enhancer<R> { R value(String v); default R"
                        + " converted(Old value) { return value(\"old\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "Enhancer<ReactNode>");
    }

    @Test
    public void interfaceIntersectionErasureConflict() throws Exception {
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default <T extends Comparable<T> & Runnable> R"
                        + " converted(T value) { return value(\"x\"); } } public interface Other<R> { R other(int v);"
                        + " default R converted(Comparable<?> value) { return other(1); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();",
                "conflicts with another enhancer");
    }

    @Test
    public void helperVariablesDoNotShadowBuilderTypes() throws Exception {
        final var declaration = """
            public interface Enhancer<R> {
              R value(String v);
              default <Step1, Step2, Builder, ContextHolder, Case_ComponentBuilder, ReactNode, String, List> R shadow(
                  Step1 a, Step2 b, Builder c, ContextHolder d, Case_ComponentBuilder e, ReactNode f, String g, List h) {
                return value("x");
              }
              default R referenced(List<? extends String[]> value) { return value("x"); }
            }
            """;
        assertValid(declaration, "@Input(enhancer=Enhancer.class) abstract String value();", "Enhancer<ReactNode>");
        assertValid(
                declaration,
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract String z();",
                "Enhancer<Step2>");
        assertValid(
                declaration,
                "@Input(enhancer=Enhancer.class) abstract @Nullable String value(); @Input abstract @Nullable String"
                        + " z();",
                "Enhancer<Step1>");
    }

    @Test
    public void declarationErasureConflicts() throws Exception {
        final var first = "public interface Enhancer<R> { R value(String v); default R converted(R v) { return v; } } ";
        assertInvalid(
                first
                        + "public interface Other<R> { R other(int v); default R converted(Object v) { return"
                        + " other(1); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();",
                "conflicts with another enhancer");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R other(R v) { return v; } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input abstract Object other();",
                "conflicts with generated builder method");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R converted(R[] v) { return v[0]; } }"
                    + " public interface Other<R> { R other(int v); default R converted(Object[] v) { return other(1);"
                    + " } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();",
                "conflicts with another enhancer");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default <T extends R> R converted(T v) { return v;"
                        + " } } public interface Other<R> { R other(int v); default R converted(Object v) { return"
                        + " other(1); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();",
                "conflicts with another enhancer");
        assertInvalid(
                "public interface Enhancer<R> { R value(String v); default R other(Object v) { return value(\"x\"); }"
                        + " } public interface Base<R,V> { R other(V v); } public interface Other<R> extends"
                        + " Base<R,String> {}",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract String"
                        + " other();",
                "conflicts with generated builder method");
        assertValid(
                first
                        + "public interface Other<R> { R other(int v); default R converted(String v) { return"
                        + " other(1); } }",
                "@Input(enhancer=Enhancer.class) abstract String value(); @Input(enhancer=Other.class) abstract int"
                        + " other();",
                "Enhancer<Step2>");
    }

    @Test
    public void concreteCollectionHelperParameters() throws Exception {
        assertValid("""
            public interface Enhancer<R> {
              R value(String v);
              @SuppressWarnings("NonApiType")
              default R converted(ArrayList<String> list, HashSet<String> set) { return value(list + ":" + set); }
            }
            """, "@Input(enhancer=Enhancer.class) abstract String value();", "ArrayList<String>");
    }

    @Test
    public void helperVariablesDoNotShadowUnusedViewBounds() throws Exception {
        assertValid(
                "public interface Enhancer<R> { R value(String v); default <AtomicInteger> R shadow(AtomicInteger v) {"
                        + " return value(\"x\"); } }",
                "@Input(enhancer=Enhancer.class) abstract String value();",
                "AtomicInteger2",
                "<T extends java.util.concurrent.atomic.AtomicInteger>");
    }

    @Test
    public void overriddenDeclarationErasuresFromSourceAndCompiledContracts() throws Exception {
        assertInvalidSourceAndCompiledContracts("""
            public interface Base<R,V> { R other(V v); }
            public interface Other<R> extends Base<R,String> { @Override R other(String v); }
            public interface Enhancer<R> {
              R value(String v);
              default R other(Object v) { return value("x"); }
            }
            """, "conflicts with generated builder method");
        assertInvalidSourceAndCompiledContracts("""
            public interface Base<R,V> { default R converted(V v) { throw new IllegalStateException(); } }
            public interface Enhancer<R> extends Base<R,String> {
              R value(String v);
              @Override default R converted(String v) { return value(v); }
            }
            public interface Other<R> {
              R other(String v);
              default R converted(Object v) { return other(v.toString()); }
            }
            """, "conflicts with another enhancer");
    }

    private void assertInvalidSourceAndCompiledContracts(final String declarations, final String expected)
            throws Exception {
        final var contractText = """
            package test;
            @org.jspecify.annotations.NullMarked
            public final class Contracts {
              %s
            }
            """.formatted(declarations);
        final var contracts =
                new SimpleJavaFileObject(URI.create("string:///test/Contracts.java"), JavaFileObject.Kind.SOURCE) {
                    @Override
                    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                        return contractText;
                    }
                };
        final var view = source(
                "",
                "@Input(enhancer=Contracts.Enhancer.class) abstract String value();"
                        + " @Input(enhancer=Contracts.Other.class) abstract String other();");
        final var fromSource = CompileTestUtil.compile(List.of(contracts, view), getOptions(), processors(), List.of());
        try {
            assertEnhancerDiagnostic(fromSource, expected);
        } finally {
            cleanup(fromSource);
        }
        final var compiledContracts =
                CompileTestUtil.assertCompilesWithoutWarnings(List.of(contracts), getOptions(), List.of(), List.of());
        try {
            final var fromCompiled = CompileTestUtil.compile(
                    List.of(view),
                    getOptions(),
                    processors(),
                    buildClasspath(compiledContracts.classOutput().toFile()));
            try {
                assertEnhancerDiagnostic(fromCompiled, expected);
            } finally {
                cleanup(fromCompiled);
            }
        } finally {
            cleanup(compiledContracts);
        }
    }

    private void assertInvalid(final String declarations, final String inputs, final String expected) throws Exception {
        final var compilation =
                CompileTestUtil.compile(List.of(source(declarations, inputs)), getOptions(), processors(), List.of());
        try {
            assertEnhancerDiagnostic(compilation, expected);
        } finally {
            cleanup(compilation);
        }
    }

    private static void assertEnhancerDiagnostic(final Compilation compilation, final String expected) {
        assertFalse(compilation.success());
        final var messages = CompileTestUtil.asMessage(compilation.diagnostics());
        assertTrue(messages.contains("@Input enhancer for") && messages.contains(expected), messages);
    }

    private static void cleanup(final Compilation compilation) throws IOException {
        for (final var directory : List.of(compilation.sourceOutput(), compilation.classOutput())) {
            try (var files = Files.walk(directory)) {
                for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
