package react4j.release;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;

public final class ReleaseBundle {
    private static final List<String> MODULES = List.of("core", "dom", "processor");
    private static final List<String> SUFFIXES = List.of(".jar", "-sources.jar", "-javadoc.jar", ".pom");
    private static final Map<String, String> DIGESTS =
            Map.of("md5", "MD5", "sha1", "SHA-1", "sha256", "SHA-256", "sha512", "SHA-512");

    private ReleaseBundle() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println(
                    "Usage: release-bundle pack <outputs> <version> <arez-version> <zip> <unsigned|signed> | verify"
                        + " <zip> <version> <arez-version> <unsigned|signed> | upload <zip> <version> <arez-version> |"
                        + " notes <changelog> <version> <output> | prepare-notes <root> <version> <yyyy-mm-dd>");
        } else if (args.length == 6 && args[0].equals("pack")) {
            pack(Path.of(args[1]), args[2], args[3], Path.of(args[4]), signed(args[5]));
        } else if (args.length == 5 && args[0].equals("verify")) {
            verify(Path.of(args[1]), args[2], args[3], signed(args[4]));
        } else if (args.length == 4 && args[0].equals("upload")) {
            upload(Path.of(args[1]), args[2], args[3]);
        } else if (args.length == 4 && args[0].equals("prepare-notes")) {
            prepareNotes(Path.of(args[1]), args[2], LocalDate.parse(args[3]));
        } else if (args.length == 4 && args[0].equals("notes")) {
            final var output = Path.of(args[3]);
            final var content = notes(Files.readString(Path.of(args[1])), args[2]);
            Files.createDirectories(output.toAbsolutePath().getParent());
            Files.writeString(output, content);
        } else {
            throw new IllegalArgumentException("Invalid command; use --help");
        }
    }

    private static boolean signed(String mode) {
        if (!mode.equals("signed") && !mode.equals("unsigned")) {
            throw new IllegalArgumentException("Expected unsigned or signed");
        }
        return mode.equals("signed");
    }

    static void version(String version, boolean publication) {
        if (!version.matches("[0-9]+(?:\\.[0-9]+){1,2}" + (publication ? "" : "(?:-SNAPSHOT)?"))) {
            throw new IllegalArgumentException(
                    "Invalid " + (publication ? "publication " : "") + "version: " + version);
        }
    }

    private static String name(String module, String version, String suffix) {
        return "org/realityforge/react4j/react4j-" + module + "/" + version + "/react4j-" + module + "-" + version
                + suffix;
    }

    static void pack(Path outputs, String version, String arezVersion, Path bundle, boolean sign) throws Exception {
        version(version, sign);
        version(arezVersion, sign);
        final var files = new TreeMap<String, byte[]>();
        // Preflight all identities and files before signing or writing an output.
        for (final var module : MODULES) {
            for (final var suffix : SUFFIXES) {
                final var data = Files.readAllBytes(outputs.resolve("react4j-" + module + suffix));
                if (suffix.equals(".pom")) {
                    pom(data, module, version, arezVersion);
                } else {
                    jar(data, suffix);
                }
                files.put(name(module, version, suffix), data);
            }
        }
        for (final var entry : new TreeMap<>(files).entrySet()) {
            if (sign) {
                files.put(entry.getKey() + ".asc", signature(entry.getValue(), entry.getKey()));
            }
        }
        for (final var entry : new TreeMap<>(files).entrySet()) {
            for (final var digest : DIGESTS.entrySet()) {
                files.put(
                        entry.getKey() + "." + digest.getKey(),
                        bytes(checksum(entry.getValue(), digest.getValue()) + "\n"));
            }
        }
        Files.createDirectories(bundle.toAbsolutePath().getParent());
        write(bundle, files);
        verify(bundle, version, arezVersion, sign);
    }

    static void verify(Path bundle, String version, String arezVersion, boolean signed) throws Exception {
        version(version, signed);
        version(arezVersion, signed);
        final var files = read(bundle);
        final var expected = new HashSet<String>();
        for (final var module : MODULES) {
            for (final var suffix : SUFFIXES) {
                final var name = name(module, version, suffix);
                final var data = required(files, name);
                if (suffix.equals(".pom")) {
                    pom(data, module, version, arezVersion);
                } else {
                    jar(data, suffix);
                }
                final var entries = signed ? List.of(name, name + ".asc") : List.of(name);
                for (final var entry : entries) {
                    expected.add(entry);
                    final var content = required(files, entry);
                    for (final var digest : DIGESTS.entrySet()) {
                        final var digestName = entry + "." + digest.getKey();
                        expected.add(digestName);
                        if (!new String(required(files, digestName), StandardCharsets.US_ASCII)
                                .strip()
                                .equals(checksum(content, digest.getValue()))) {
                            throw new IOException("Checksum mismatch: " + entry);
                        }
                    }
                }
                if (signed) {
                    verifySignature(data, required(files, name + ".asc"), name);
                }
            }
        }
        if (!files.keySet().equals(expected)) {
            throw new IOException("Unexpected bundle entries: "
                    + files.keySet().stream()
                            .filter(key -> !expected.contains(key))
                            .toList());
        }
        System.out.println("Verified 12 artifacts and " + expected.size() + " " + (signed ? "signed" : "unsigned")
                + " bundle entries");
    }

    static Map<String, byte[]> read(Path bundle) throws IOException {
        final var files = new TreeMap<String, byte[]>();
        try (var zip = new ZipInputStream(Files.newInputStream(bundle))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory() || files.putIfAbsent(entry.getName(), zip.readAllBytes()) != null) {
                    throw new IOException("Duplicate or directory bundle entry: " + entry.getName());
                }
            }
        }
        return files;
    }

    static void write(Path bundle, Map<String, byte[]> files) throws IOException {
        try (var zip = new ZipOutputStream(Files.newOutputStream(bundle))) {
            for (final var entry : new TreeMap<>(files).entrySet()) {
                final var item = new ZipEntry(entry.getKey());
                item.setTime(0);
                zip.putNextEntry(item);
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        }
    }

    private static byte[] required(Map<String, byte[]> files, String name) throws IOException {
        final var data = files.get(name);
        if (data == null || data.length == 0) {
            throw new IOException("Missing or empty bundle entry: " + name);
        }
        return data;
    }

    private static void jar(byte[] data, String suffix) throws IOException {
        final var expected =
                suffix.equals("-sources.jar") ? ".java" : suffix.equals("-javadoc.jar") ? ".html" : ".class";
        boolean found = false;
        try (var zip = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(expected) && zip.readAllBytes().length > 0) {
                    found = true;
                }
            }
        }
        if (!found) {
            throw new IOException("Invalid or empty " + suffix + " archive");
        }
    }

    private static void pom(byte[] data, String module, String version, String arezVersion) throws Exception {
        final var factory = DocumentBuilderFactory.newDefaultInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        final var root = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(data))
                .getDocumentElement();
        if (!text(root, "groupId").equals("org.realityforge.react4j")
                || !text(root, "artifactId").equals("react4j-" + module)
                || !text(root, "version").equals(version)) {
            throw new IOException("Unexpected Maven identity for " + module);
        }
        boolean foundArez = false;
        final var dependencies = root.getElementsByTagName("dependency");
        for (int i = 0; i < dependencies.getLength(); i++) {
            final var dependency = (Element) dependencies.item(i);
            if (text(dependency, "groupId").equals("org.realityforge.arez")) {
                if (!text(dependency, "version").equals(arezVersion)) {
                    throw new IOException("Wrong migrated Arez dependency version");
                }
                foundArez = true;
            }
            if (text(dependency, "groupId").equals("org.realityforge.react4j")
                    && !text(dependency, "version").equals(version)) {
                throw new IOException("Wrong React4j dependency version");
            }
        }
        if (foundArez == module.equals("processor")) {
            throw new IOException("Wrong Arez dependency presence for " + module);
        }
    }

    private static String text(Element element, String name) throws IOException {
        final var nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element child && child.getTagName().equals(name)) {
                return child.getTextContent();
            }
        }
        throw new IOException("Missing POM field: " + name);
    }

    private static String checksum(byte[] data, String algorithm) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance(algorithm).digest(data));
    }

    private static byte[] bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] signature(byte[] data, String name) throws Exception {
        final var work = Files.createTempDirectory("react4j-release-sign");
        try {
            final var input = work.resolve(Path.of(name).getFileName());
            final var output = work.resolve("signature.asc");
            Files.write(input, data);
            final var command = new ArrayList<>(
                    List.of("gpg", "--batch", "--yes", "--armor", "--detach-sign", "--output", output.toString()));
            final var key = System.getenv("GPG_USER");
            if (key != null && !key.isBlank()) {
                command.addAll(List.of("--local-user", key));
            }
            command.add(input.toString());
            execute(command);
            return Files.readAllBytes(output);
        } finally {
            delete(work);
        }
    }

    private static void verifySignature(byte[] data, byte[] signature, String name) throws Exception {
        final var work = Files.createTempDirectory("react4j-release-verify");
        try {
            final var input = work.resolve(Path.of(name).getFileName());
            final var signatureFile = work.resolve("signature.asc");
            Files.write(input, data);
            Files.write(signatureFile, signature);
            execute(List.of("gpg", "--batch", "--verify", signatureFile.toString(), input.toString()));
        } finally {
            delete(work);
        }
    }

    private static void execute(List<String> command) throws Exception {
        if (new ProcessBuilder(command).inheritIO().start().waitFor() != 0) {
            throw new IOException("Command failed: " + command.get(0));
        }
    }

    static String notes(String changelog, String version) {
        version(version, true);
        final var matcher = Pattern.compile(
                        "(?ms)^### \\[v" + Pattern.quote(version) + "\\][^\\n]*\\n(.*?)(?=^### |\\z)")
                .matcher(changelog);
        if (!matcher.find() || matcher.group(1).isBlank()) {
            throw new IllegalArgumentException("Missing release notes for v" + version);
        }
        return matcher.group(1).strip() + "\n";
    }

    static void prepareNotes(Path root, String version, LocalDate date) throws IOException {
        version(version, true);
        final var changelog = root.resolve("CHANGELOG.md");
        final var setup = root.resolve("docs/project_setup.md");
        final var content = Files.readString(changelog);
        final var previous = Pattern.compile("(?m)^### \\[v([^]]+)\\]").matcher(content);
        if (!content.contains("### Unreleased\n\n") || !previous.find() || content.contains("### [v" + version + "]")) {
            throw new IllegalArgumentException("Expected Unreleased and a previous version; release must be new");
        }
        final var notes = content.split("### Unreleased\n\n", 2)[1].split("### \\[v", 2)[0].strip();
        if (notes.isBlank()) {
            throw new IllegalArgumentException("No Unreleased notes to publish");
        }
        final var header = "### [v" + version + "](https://github.com/react4j/react4j/tree/v" + version + ") (" + date
                + ") · [Full Changelog](https://github.com/react4j/react4j/compare/v" + previous.group(1) + "...v"
                + version + ")\n\nChanges in this release:\n\n";
        final var property =
                Pattern.compile("<react4j.version>[^<]+</react4j.version>").matcher(Files.readString(setup));
        if (property.results().count() != 1) {
            throw new IllegalArgumentException("Expected one react4j.version property in project setup");
        }
        final var updatedSetup = property.replaceAll("<react4j.version>" + version + "</react4j.version>");
        Files.writeString(changelog, content.replace("### Unreleased\n\n", "### Unreleased\n\n" + header));
        Files.writeString(setup, updatedSetup);
    }

    private static void upload(Path bundle, String version, String arezVersion) throws Exception {
        verify(bundle, version, arezVersion, true);
        final var token = System.getenv("MAVEN_CENTRAL_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Set MAVEN_CENTRAL_TOKEN to the Central Portal base64 user token");
        }
        final var boundary = "React4jPublicationBundle";
        final var body = new ByteArrayOutputStream();
        body.write(bytes("--" + boundary + "\r\nContent-Disposition: form-data; name=\"bundle\"; filename=\"react4j-"
                + version + ".zip\"\r\nContent-Type: application/octet-stream\r\n\r\n"));
        body.write(Files.readAllBytes(bundle));
        body.write(bytes("\r\n--" + boundary + "--\r\n"));
        final var request = HttpRequest.newBuilder(
                        URI.create("https://central.sonatype.com/api/v1/publisher/upload?name=react4j-" + version
                                + "&publishingType=AUTOMATIC"))
                .timeout(Duration.ofMinutes(10))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();
        final var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new IOException("Central upload failed (HTTP " + response.statusCode() + "): " + response.body());
        }
        System.out.println("Central deployment id: " + response.body().strip());
        System.out.println("Monitor validation/publication at https://central.sonatype.com/publishing");
    }

    private static void delete(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            for (final var path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
