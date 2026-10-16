package org.realityforge.css;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

final class CssBridge implements AutoCloseable {
    record Result(String css, String formatted) {}

    static final class InvalidCss extends IOException {
        private final int offset;

        InvalidCss(int offset, String message) {
            super(message);
            this.offset = offset;
        }

        int offset() {
            return offset;
        }
    }

    private final Process process;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    CssBridge(String tool) throws IOException {
        process = process(List.of(Path.of(tool).toAbsolutePath().toString(), "serve"))
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
    }

    static ProcessBuilder process(List<String> command) {
        var builder = new ProcessBuilder(command);
        // Java launchers export their own runfiles; a child tool must locate its own.
        for (var name : List.of("RUNFILES_DIR", "RUNFILES_MANIFEST_FILE", "JAVA_RUNFILES", "TEST_SRCDIR")) {
            builder.environment().remove(name);
        }
        return builder;
    }

    static Result compile(String tool, String css, String root, List<String> accessors) throws IOException {
        try (var bridge = new CssBridge(tool)) {
            return bridge.compile(css, root, accessors);
        }
    }

    Result compile(String css, String root, List<String> accessors) throws IOException {
        writer.write(root + "\t" + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8)) + "\t"
                + String.join(",", accessors) + "\n");
        writer.flush();
        String response = reader.readLine();
        if (response == null) {
            throw new IOException("CSS bridge closed without a response");
        }
        String[] fields = response.split("\t", -1);
        if (fields[0].equals("error") && fields.length == 3) {
            throw new InvalidCss(Integer.parseInt(fields[1]), decode(fields[2]));
        }
        if (!fields[0].equals("ok") || fields.length != 3) {
            throw new IOException("Invalid response from CSS bridge");
        }
        return new Result(decode(fields[1]), decode(fields[2]));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        writer.close();
        try {
            if (process.waitFor() != 0) {
                throw new IOException("CSS bridge failed");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("CSS tool interrupted", e);
        } finally {
            reader.close();
            process.destroy();
        }
    }
}
