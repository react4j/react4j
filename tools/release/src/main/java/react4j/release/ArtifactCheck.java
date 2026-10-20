package react4j.release;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;

/** Verifies publication contents and a Java 21 consumer with isolated processor paths. */
public final class ArtifactCheck {
    private static final String SERVICE = "META-INF/services/javax.annotation.processing.Processor";
    private static final String VENDOR = "react4j/processor/vendor/";
    private static final List<String> MODULES = List.of("core", "dom", "processor");

    private ArtifactCheck() {}

    public static void main(String[] args) throws Exception {
        var options = new HashMap<String, List<String>>();
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Expected option/value pairs");
        }
        for (var i = 0; i < args.length; i += 2) {
            options.computeIfAbsent(args[i], ignored -> new ArrayList<>()).add(args[i + 1]);
        }
        var version = Files.readString(Path.of(one(options, "--version"))).trim();
        var arezVersion =
                Files.readString(Path.of(one(options, "--arez-version"))).trim();
        for (var module : MODULES) {
            inspect(module, options, version, arezVersion);
        }
        var aggregate = archive(Path.of(one(options, "--site-javadoc")));
        for (var page :
                List.of("react4j/React.html", "react4j/dom/DOM.html", "react4j/processor/React4jProcessor.html")) {
            require(aggregate.containsKey(page), "Missing aggregate Javadoc: " + page);
        }
        try (var loader = new URLClassLoader(
                new URL[] {Path.of(one(options, "--processor-jar")).toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            var providers = ServiceLoader.load(Processor.class, loader).stream().toList();
            require(providers.size() == 1, "Expected one isolated processor service");
            var processor = providers.get(0).get();
            require(
                    processor.getClass().getName().equals("react4j.processor.React4jProcessor")
                            && processor.getSupportedAnnotationTypes().contains("react4j.annotations.View"),
                    "Wrong isolated processor service");
        }
        isolatedConsumer(options);
        System.out.println(
                "Verified 12 Maven artifacts, aggregate Javadocs, and isolated Java 21 processor/JVM consumption");
    }

    private static void inspect(String module, Map<String, List<String>> options, String version, String arezVersion)
            throws Exception {
        var binary = archive(Path.of(one(options, "--" + module + "-jar")));
        var sources = archive(Path.of(one(options, "--" + module + "-sources")));
        var raw = archiveSources(Path.of(one(options, "--" + module + "-raw-sources")));
        if (!module.equals("processor")) {
            var projection = archive(Path.of(one(options, "--" + module + "-j2cl-sources")));
            var assetPrefix = module.equals("core") ? "react4j/public/" : "react4j/dom/public/";
            var expected = sources.keySet().stream()
                    .filter(name -> !name.startsWith(assetPrefix))
                    .collect(Collectors.toSet());
            require(projection.keySet().equals(expected), "J2CL projection must exclude only UMD assets: " + module);
            for (var name : expected) {
                require(
                        Arrays.equals(sources.get(name), projection.get(name)),
                        "J2CL changed publication bytes: " + name);
            }
        }

        require(
                binary.keySet().stream().noneMatch(name -> name.endsWith(".java") || name.endsWith(".js")),
                "Source payload in binary: " + module);
        require(sources.keySet().stream().noneMatch(name -> name.endsWith(".class")), "Bytecode in sources: " + module);
        for (var entry : raw.entrySet()) {
            if (entry.getKey().endsWith(".java")) {
                require(
                        Arrays.equals(sources.get(entry.getKey()), entry.getValue()),
                        "Missing/changed source: " + entry.getKey());
            }
        }
        var owned = 0;
        var shaded = 0;
        var java21 = 0;
        for (var entry : binary.entrySet()) {
            var name = entry.getKey();
            if (name.endsWith(".class")) {
                var bytes = entry.getValue();
                require(
                        bytes.length >= 8
                                && bytes[0] == (byte) 0xCA
                                && bytes[1] == (byte) 0xFE
                                && bytes[2] == (byte) 0xBA
                                && bytes[3] == (byte) 0xBE,
                        "Invalid class: " + name);
                var major = (bytes[6] & 255) * 256 + (bytes[7] & 255);
                if (name.startsWith(VENDOR)) {
                    require(major <= 65, "Shaded class requires newer than Java 21: " + name);
                    shaded++;
                    if (major == 65) {
                        java21++;
                    }
                } else {
                    require(major == 61 && name.startsWith("react4j/"), "Owned class must target Java 17: " + name);
                    var source =
                            name.substring(0, name.length() - ".class".length()).split("\\$", 2)[0] + ".java";
                    require(sources.containsKey(source), "Missing class source: " + name);
                    owned++;
                }
            }
            if (name.startsWith("META-INF/services/")) {
                for (var provider : new String(entry.getValue(), StandardCharsets.UTF_8)
                        .lines()
                        .toList()) {
                    require(
                            provider.isBlank() || binary.containsKey(provider.replace('.', '/') + ".class"),
                            "Missing service provider: " + provider);
                }
            }
        }
        require(owned > 0, "Empty binary: " + module);
        if (module.equals("processor")) {
            require(
                    shaded > 0 && java21 > 0 && binary.containsKey(VENDOR + "javapoet/JavaFile.class"),
                    "Missing shaded compiler dependencies");
            require(
                    new String(entry(binary, SERVICE), StandardCharsets.UTF_8)
                            .trim()
                            .equals("react4j.processor.React4jProcessor"),
                    "Wrong processor service");
            require(Arrays.equals(binary.get(SERVICE), sources.get(SERVICE)), "Missing processor source service");
            require(
                    binary.keySet().stream()
                            .noneMatch(name -> name.startsWith("org/")
                                    || name.startsWith("javax/")
                                    || name.startsWith("com/palantir/")
                                    || name.startsWith(VENDOR + "proton/vendor/jackson/")),
                    "Unrelocated/unused processor payload");
        }
        if (module.equals("dom")) {
            require(
                    sources.containsKey("react4j/dom/DOM.java") && binary.containsKey("react4j/dom/DOM.class"),
                    "Missing generated DOM");
        }
        var docs = archive(Path.of(one(options, "--" + module + "-javadoc")));
        var sentinel =
                switch (module) {
                    case "core" -> "react4j/React.html";
                    case "dom" -> "react4j/dom/DOM.html";
                    default -> "react4j/processor/React4jProcessor.html";
                };
        require(docs.containsKey("index.html") && docs.containsKey(sentinel), "Incomplete Javadocs: " + module);
        var timestamp = Pattern.compile("<!-- Generated by javadoc [^\\r\\n]* on ");
        for (var entry : docs.entrySet()) {
            if (entry.getKey().endsWith(".html")) {
                require(
                        !timestamp
                                .matcher(new String(entry.getValue(), StandardCharsets.UTF_8))
                                .find(),
                        "Stamped Javadoc");
            }
        }
        checkPom(Path.of(one(options, "--" + module + "-pom")), module, version, arezVersion);
        for (var resource : options.getOrDefault("--" + module + "-resource", List.of())) {
            var parts = resource.split("=", 2);
            require(
                    Arrays.equals(sources.get(parts[0]), Files.readAllBytes(Path.of(parts[1]))),
                    "Missing/changed resource: " + parts[0]);
        }
        System.out.println(module + ": " + owned + " Java 17 classes, " + shaded + " shaded classes, " + sources.size()
                + " source entries");
    }

    private static Map<String, byte[]> archive(Path path) throws IOException {
        var entries = archiveSources(path);
        var names = new ArrayList<>(entries.keySet());
        require(!names.isEmpty() && names.get(0).equals(JarFile.MANIFEST_NAME), "Manifest must be first: " + path);
        require(
                names.subList(1, names.size())
                        .equals(names.subList(1, names.size()).stream().sorted().toList()),
                "Unsorted archive: " + path);
        require(
                Arrays.equals(
                        entries.get(JarFile.MANIFEST_NAME),
                        "Manifest-Version: 1.0\r\n\r\n".getBytes(StandardCharsets.UTF_8)),
                "Stamped manifest: " + path);
        require(entries.containsKey("META-INF/LICENSE"), "Missing license: " + path);
        require(
                names.stream().noneMatch(name -> name.endsWith(".gwt.xml") || name.startsWith("META-INF/versions/")),
                "Obsolete GWT/multi-release payload: " + path);
        return entries;
    }

    private static Map<String, byte[]> archiveSources(Path path) throws IOException {
        var result = new LinkedHashMap<String, byte[]>();
        try (var jar = new JarFile(path.toFile())) {
            var entries = jar.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                require(!result.containsKey(entry.getName()), "Duplicate archive entry: " + entry.getName());
                // Raw compiler source jars retain their own metadata; release archives are checked below.
                if (path.getFileName().toString().startsWith("react4j-")) {
                    require(entry.getTime() == 0, "Unstable archive timestamp: " + entry.getName());
                }
                try (var input = jar.getInputStream(entry)) {
                    result.put(entry.getName(), input.readAllBytes());
                }
            }
        }
        return result;
    }

    private static void checkPom(Path path, String module, String version, String arezVersion) throws Exception {
        var document =
                DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(path.toFile());
        var root = document.getDocumentElement();
        require(
                text(root, "groupId").equals("org.realityforge.react4j")
                        && text(root, "artifactId").equals("react4j-" + module)
                        && text(root, "version").equals(version)
                        && text(root, "packaging").equals("jar"),
                "Wrong Maven identity: " + path);
        require(text(root, "url").equals("https://github.com/react4j/react4j"), "Wrong project URL");
        require(
                text((Element) root.getElementsByTagName("license").item(0), "name")
                        .equals("The Apache Software License, Version 2.0"),
                "Missing Maven license");
        var dependencies = new HashSet<String>();
        var nodes = root.getElementsByTagName("dependency");
        for (var i = 0; i < nodes.getLength(); i++) {
            var dependency = (Element) nodes.item(i);
            dependencies.add(text(dependency, "groupId") + ":" + text(dependency, "artifactId") + ":"
                    + text(dependency, "version"));
        }
        require(
                dependencies.stream()
                        .noneMatch(value -> value.contains("javax.annotation")
                                || value.toLowerCase(Locale.ROOT).contains("gwt")),
                "Obsolete Maven dependency");
        if (module.equals("processor")) {
            require(dependencies.isEmpty(), "Processor must be self-contained");
        } else {
            require(
                    dependencies.contains("org.jspecify:jspecify:1.0.1")
                            && dependencies.contains("org.realityforge.arez:arez-core:" + arezVersion),
                    "Missing JSpecify/migrated Arez dependency");
            if (module.equals("dom")) {
                require(
                        dependencies.contains("org.realityforge.react4j:react4j-core:" + version),
                        "Wrong core version");
            }
        }
    }

    private static String text(Element element, String name) {
        for (var i = 0; i < element.getChildNodes().getLength(); i++) {
            var node = element.getChildNodes().item(i);
            if (node instanceof Element child && child.getTagName().equals(name)) {
                return child.getTextContent();
            }
        }
        throw new AssertionError("Missing POM field: " + name);
    }

    private static void isolatedConsumer(Map<String, List<String>> options) throws Exception {
        require(Runtime.version().feature() == 21, "Isolated consumer must use Java 21");
        var work = Files.createTempDirectory("react4j-packaged-consumer");
        try {
            var home = System.getProperty("java.home");
            if (home == null) {
                throw new IOException("java.home is not set");
            }
            var classes = Files.createDirectories(work.resolve("classes"));
            var generated = Files.createDirectories(work.resolve("generated"));
            var classpath = new ArrayList<String>();
            for (var module : List.of("core", "dom")) {
                classpath.add(one(options, "--" + module + "-jar"));
            }
            classpath.addAll(options.getOrDefault("--dependency", List.of()));
            classpath.add(classes.toString());
            var cp = String.join(File.pathSeparator, classpath);
            var javac = Path.of(home, "bin", "javac").toString();
            var flags = List.of(
                    "--release",
                    "17",
                    "-Werror",
                    "-Xlint:all,-processing,-serial",
                    "-sourcepath",
                    "",
                    "-implicit:none",
                    "-XDaddTypeAnnotationsToSymbol=true",
                    "-classpath",
                    cp,
                    "-d",
                    classes.toString());
            var sources = options.getOrDefault("--consumer-source", List.of());
            require(sources.size() == 3, "Expected three consumer sources");
            var parent = sources.stream()
                    .filter(value -> value.endsWith("/Parent.java"))
                    .findFirst()
                    .orElseThrow();
            var first = new ArrayList<>(List.of(javac));
            first.addAll(flags);
            first.addAll(List.of("-proc:none", parent));
            run(first);
            var command = new ArrayList<>(List.of(javac));
            command.addAll(flags);
            for (var pkg : List.of("api", "code", "file", "parser", "tree", "util")) {
                command.add("-J--add-exports=jdk.compiler/com.sun.tools.javac." + pkg + "=ALL-UNNAMED");
            }
            var processors = List.of(
                    one(options, "--processor-jar"),
                    one(options, "--arez-processor"),
                    one(options, "--sting-processor"));
            command.addAll(List.of(
                    "-proc:full",
                    "-processorpath",
                    String.join(File.pathSeparator, processors),
                    "-Aarez.defer.errors=false",
                    "-s",
                    generated.toString()));
            command.addAll(
                    sources.stream().filter(value -> !value.equals(parent)).toList());
            run(command);
            Set<String> names;
            try (var files = Files.walk(generated)) {
                names = files.filter(path -> path.toString().endsWith(".java"))
                        .map(path -> generated.relativize(path).toString())
                        .collect(Collectors.toSet());
            }
            require(
                    names.equals(Set.of(
                            "isolated/React4j_SampleView.java",
                            "isolated/SampleBuilder.java",
                            "isolated/React4j_SampleViewFactory.java",
                            "isolated/Arez_React4j_SampleView.java",
                            "isolated/Sting_React4j_SampleViewFactory.java")),
                    "Unexpected generated consumer files: " + names);
            var view = Files.readString(generated.resolve("isolated/React4j_SampleView.java"))
                    .replaceAll("\\s+", "");
            require(
                    view.contains("@NullableStringvalue()")
                            && view.contains("List<@NullableString>nested()")
                            && view.contains("@NullableString@Nullable[]array()"),
                    "Lost compiled inherited/nested/array contracts");
            var java = Path.of(home, "bin", "java").toString();
            run(List.of(java, "-ea", "-Dbraincheck.environment=development", "-classpath", cp, "isolated.Consumer"));
            try (var files = Files.walk(classes)) {
                for (var file :
                        files.filter(path -> path.toString().endsWith(".class")).toList()) {
                    var bytes = Files.readAllBytes(file);
                    require(
                            (bytes[6] & 255) * 256 + (bytes[7] & 255) == 61,
                            "Consumer/generated bytecode is not Java 17");
                }
            }
        } finally {
            try (var files = Files.walk(work)) {
                for (var path : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    private static void run(List<String> command) throws Exception {
        var process = new ProcessBuilder(command).redirectErrorStream(true).start();
        var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        require(process.waitFor() == 0, String.join(" ", command) + "\n" + output);
    }

    private static byte[] entry(Map<String, byte[]> entries, String name) {
        var content = entries.get(name);
        if (content == null) {
            throw new AssertionError("Missing archive entry: " + name);
        }
        return content;
    }

    private static String one(Map<String, List<String>> options, String name) {
        var values = options.get(name);
        if (values == null || values.size() != 1) {
            throw new IllegalArgumentException("Expected one " + name);
        }
        return values.get(0);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
