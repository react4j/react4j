package react4j.release;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/** Builds normalized release archives and relocates processor dependencies. */
public final class JarBuilder {
    private static final long STABLE_TIME = 0L;

    private JarBuilder() {}

    /** Merges the declared jars and resources into one release archive. */
    public static void main(final String[] args) throws Exception {
        if (args.length == 0 || !"merge".equals(args[0])) {
            throw new IllegalArgumentException("Usage: merge --output <jar> --input <jar>...");
        }

        final var inputs = new ArrayList<Path>();
        final var resources = new ArrayList<Resource>();
        final var relocations = new LinkedHashMap<String, String>();
        final var exclusions = new LinkedHashSet<String>();
        Path output = null;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--output":
                    output = Path.of(args[++i]);
                    break;
                case "--input":
                    inputs.add(Path.of(args[++i]));
                    break;
                case "--resource":
                    resources.add(parseResource(args[++i]));
                    break;
                case "--relocate":
                    final String rule = args[++i];
                    final int equals = rule.indexOf('=');
                    if (equals <= 0 || equals == rule.length() - 1) {
                        throw new IllegalArgumentException("Expected --relocate <old-package>=<new-package>");
                    }
                    relocations.put(rule.substring(0, equals), rule.substring(equals + 1));
                    break;
                case "--exclude-prefix":
                    exclusions.add(args[++i]);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
        if (output == null) {
            throw new IllegalArgumentException("Missing --output");
        }
        merge(output, inputs, resources, relocations, exclusions);
    }

    private static Resource parseResource(final String arg) {
        final int index = arg.indexOf('=');
        if (index <= 0 || index == arg.length() - 1) {
            throw new IllegalArgumentException("Expected resource argument in the form <path>=<entry>");
        }
        return new Resource(Path.of(arg.substring(0, index)), arg.substring(index + 1));
    }

    private static void merge(
            final Path output,
            final List<Path> inputs,
            final List<Resource> resources,
            final Map<String, String> relocations,
            final LinkedHashSet<String> exclusions)
            throws IOException {
        final var entries = new LinkedHashMap<String, byte[]>();
        final var services = new TreeMap<String, LinkedHashSet<String>>();
        for (final Path input : inputs) {
            mergeJar(input, entries, services, relocations, exclusions);
        }
        for (final Resource resource : resources) {
            entries.put(resource.entryName(), Files.readAllBytes(resource.path()));
        }

        Files.createDirectories(output.toAbsolutePath().getParent());
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(output))) {
            final var manifestContent = new ByteArrayOutputStream();
            manifest().write(manifestContent);
            writeEntry(out, JarFile.MANIFEST_NAME, manifestContent.toByteArray());
            writeEntries(out, entries, services);
        }
    }

    private static Manifest manifest() {
        final var manifest = new Manifest();
        final var attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
    }

    private static void mergeJar(
            final Path input,
            final Map<String, byte[]> entries,
            final Map<String, LinkedHashSet<String>> services,
            final Map<String, String> relocations,
            final LinkedHashSet<String> exclusions)
            throws IOException {
        try (JarFile jar = new JarFile(input.toFile())) {
            final var jarEntries = Collections.list(jar.entries());
            jarEntries.sort(Comparator.comparing(ZipEntry::getName));
            for (final JarEntry entry : jarEntries) {
                final String name = relocate(relocate(entry.getName(), relocations, '/'), relocations, '.');
                if (entry.isDirectory()
                        || shouldSkip(name)
                        || exclusions.stream().anyMatch(name::startsWith)) {
                    continue;
                }
                byte[] content = read(jar, entry);
                if (name.endsWith(".class") && !relocations.isEmpty()) {
                    content = relocateClass(content, relocations);
                } else if (name.startsWith("META-INF/services/") && !relocations.isEmpty()) {
                    content = relocateText(content, relocations);
                }
                if (name.startsWith("META-INF/services/")) {
                    mergeService(services, name, content);
                } else {
                    addEntry(entries, name, content, isLegalMetadata(name));
                }
            }
        }
    }

    private static String relocate(final String value, final Map<String, String> relocations, final char separator) {
        String result = value;
        for (final Map.Entry<String, String> rule : relocations.entrySet()) {
            result = result.replace(
                    rule.getKey().replace('.', separator), rule.getValue().replace('.', separator));
        }
        return result;
    }

    private static byte[] relocateText(final byte[] content, final Map<String, String> relocations) {
        String text = new String(content, StandardCharsets.UTF_8);
        text = relocate(text, relocations, '.');
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] relocateClass(final byte[] content, final Map<String, String> relocations)
            throws IOException {
        // Rewrite names in constant-pool strings while leaving class structure and bytecode intact.
        if (content.length < 10 || readU4(content, 0) != 0xCAFEBABEL) {
            throw new IOException("Invalid class file in release jar");
        }
        final int count = readU2(content, 8);
        final var output = new ByteArrayOutputStream(content.length);
        output.write(content, 0, 10);
        int offset = 10;
        for (int index = 1; index < count; index++) {
            final int tag = content[offset++];
            output.write(tag);
            if (tag == 1) {
                final int length = readU2(content, offset);
                offset += 2;
                byte[] value = Arrays.copyOfRange(content, offset, offset + length);
                offset += length;
                for (final Map.Entry<String, String> rule : relocations.entrySet()) {
                    value = replace(
                            value,
                            rule.getKey().replace('.', '/'),
                            rule.getValue().replace('.', '/'));
                    value = replace(value, rule.getKey(), rule.getValue());
                }
                if (value.length > 65535) {
                    throw new IOException("Relocated class constant exceeds the class file limit");
                }
                output.write(value.length >> 8);
                output.write(value.length & 0xFF);
                output.write(value);
            } else {
                final int size =
                        switch (tag) {
                            case 3, 4, 9, 10, 11, 12, 17, 18 -> 4;
                            case 5, 6 -> 8;
                            case 7, 8, 16, 19, 20 -> 2;
                            case 15 -> 3;
                            default -> throw new IOException("Unknown class constant tag " + tag);
                        };
                output.write(content, offset, size);
                offset += size;
                if (tag == 5 || tag == 6) {
                    index++;
                }
            }
        }
        output.write(content, offset, content.length - offset);
        return output.toByteArray();
    }

    private static byte[] replace(final byte[] source, final String oldValue, final String newValue) {
        final byte[] oldBytes = oldValue.getBytes(StandardCharsets.UTF_8);
        final byte[] newBytes = newValue.getBytes(StandardCharsets.UTF_8);
        final var output = new ByteArrayOutputStream(source.length);
        for (int index = 0; index < source.length; ) {
            if (index + oldBytes.length <= source.length
                    && Arrays.equals(source, index, index + oldBytes.length, oldBytes, 0, oldBytes.length)) {
                output.write(newBytes, 0, newBytes.length);
                index += oldBytes.length;
            } else {
                output.write(source[index++]);
            }
        }
        return output.toByteArray();
    }

    private static int readU2(final byte[] content, final int offset) {
        return (content[offset] & 0xFF) << 8 | (content[offset + 1] & 0xFF);
    }

    private static long readU4(final byte[] content, final int offset) {
        return ((long) readU2(content, offset) << 16) | readU2(content, offset + 2);
    }

    private static boolean shouldSkip(final String name) {
        if ("META-INF/MANIFEST.MF".equalsIgnoreCase(name)) {
            return true;
        }
        if (isModuleDescriptor(name)) {
            return true;
        }
        final var upper = name.toUpperCase(Locale.ROOT);
        return upper.startsWith("META-INF/")
                && (upper.endsWith(".SF") || upper.endsWith(".RSA") || upper.endsWith(".DSA") || upper.endsWith(".EC"));
    }

    private static boolean isModuleDescriptor(final String name) {
        return "module-info.class".equals(name)
                || (name.startsWith("META-INF/versions/") && name.endsWith("/module-info.class"));
    }

    private static boolean isLegalMetadata(final String name) {
        final var upper = name.toUpperCase(Locale.ROOT);
        return upper.startsWith("META-INF/LICENSE")
                || upper.startsWith("META-INF/NOTICE")
                || upper.startsWith("META-INF/DEPENDENCIES");
    }

    private static byte[] read(final JarFile jar, final ZipEntry entry) throws IOException {
        try (InputStream input = jar.getInputStream(entry)) {
            return input.readAllBytes();
        }
    }

    private static void mergeService(
            final Map<String, LinkedHashSet<String>> services, final String name, final byte[] content) {
        final var lines = services.computeIfAbsent(name, ignored -> new LinkedHashSet<>());
        final var text = new String(content, StandardCharsets.UTF_8);
        for (final String line : text.split("\\R")) {
            final String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                lines.add(trimmed);
            }
        }
    }

    private static void addEntry(
            final Map<String, byte[]> entries, final String name, final byte[] content, final boolean keepFirst)
            throws IOException {
        final byte[] existing = entries.get(name);
        if (existing == null) {
            entries.put(name, content);
        } else if (keepFirst || Arrays.equals(existing, content)) {
            return;
        } else {
            throw new IOException("Duplicate non-identical jar entry: " + name);
        }
    }

    private static void writeEntries(
            final JarOutputStream out,
            final Map<String, byte[]> entries,
            final Map<String, LinkedHashSet<String>> services)
            throws IOException {
        final var sortedEntries = new TreeMap<>(entries);
        for (final Map.Entry<String, LinkedHashSet<String>> service : services.entrySet()) {
            final var content = new ByteArrayOutputStream();
            for (final String line : service.getValue()) {
                content.write(line.getBytes(StandardCharsets.UTF_8));
                content.write('\n');
            }
            sortedEntries.put(service.getKey(), content.toByteArray());
        }
        for (final Map.Entry<String, byte[]> entry : sortedEntries.entrySet()) {
            writeEntry(out, entry.getKey(), entry.getValue());
        }
    }

    private static void writeEntry(final JarOutputStream out, final String name, final byte[] content)
            throws IOException {
        final var entry = new JarEntry(name);
        entry.setTime(STABLE_TIME);
        out.putNextEntry(entry);
        out.write(content);
        out.closeEntry();
    }

    private record Resource(Path path, String entryName) {}
}
