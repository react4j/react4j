package react4j.css.processor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import react4j.css.internal.CssInput;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;
import react4j.css.utility.language.UtilityLanguage;

/** Packages explicit CSS sources and derived catalogues for javac and J2CL classpaths. */
public final class CssInputJar {
    private CssInputJar() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 3 || 0 == args.length % 2) {
            throw new IllegalArgumentException("Usage: CssInputJar output.jar logical/source.css input.css [...]");
        }
        var entries = new TreeMap<String, String>();
        for (int i = 1; i < args.length; i += 2) {
            String css = Files.readString(Path.of(args[i + 1]));
            String path = CssInput.path(args[i]);
            if (null != entries.putIfAbsent(path, css)) {
                throw new IllegalArgumentException("Duplicate CSS source: " + args[i]);
            }
            try {
                if (UtilityLanguage.nativeSource(css)) {
                    String normalized = CssInput.normalize(css);
                    var writer = new SourceMap.Writer();
                    int line = 0;
                    int column = 0;
                    for (int at = 0; at < normalized.length(); at++) {
                        writer.append(
                                normalized.substring(at, at + 1), new SourceMap.Location(args[i], css, line, column));
                        if ('\n' == normalized.charAt(at)) {
                            line++;
                            column = 0;
                        } else {
                            column++;
                        }
                    }
                    var prepared = UtilityLanguage.prepare(
                            new UtilityLanguage.Source(args[i], normalized, writer.map("rules.css")));
                    entries.put(path + ".rules.json", prepared.json());
                } else {
                    entries.put(path + ".classes.json", CssInput.catalogue(args[i], css));
                }
            } catch (CssSyntax.InvalidCss e) {
                int offset = Math.min(css.length(), e.offset());
                int line = (int) css.substring(0, offset)
                                .chars()
                                .filter(c -> '\n' == c)
                                .count()
                        + 1;
                int column = offset - css.lastIndexOf('\n', offset - 1);
                throw new IllegalArgumentException(args[i] + ":" + line + ":" + column + ": " + e.getMessage(), e);
            }
        }
        try (var output = new ZipOutputStream(Files.newOutputStream(Path.of(args[0])))) {
            for (var entry : entries.entrySet()) {
                var file = new ZipEntry(entry.getKey());
                file.setTime(0);
                output.putNextEntry(file);
                output.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
    }
}
