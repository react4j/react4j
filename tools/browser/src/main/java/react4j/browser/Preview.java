package react4j.browser;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import react4j.css.bundler.AssetMedia;

@NullMarked
public final class Preview {
    private Preview() {}

    public static void main(String[] args) throws IOException {
        if (1 == args.length && "--help".equals(args[0])) {
            System.out.println("Usage: preview <directory> (http://localhost:3000)");
            return;
        }
        if (1 != args.length) {
            throw new IllegalArgumentException("Expected a directory");
        }
        serve(Path.of(args[0]));
    }

    public static void serve(final Path directory) throws IOException {
        start(directory, 3000);
        System.out.println("Serving " + directory.toRealPath()
                + " at http://localhost:3000; rebuild with tools/examples.sh build or tools/site.sh build and"
                + " refresh the browser");
    }

    static HttpServer start(Path directory, int port) throws IOException {
        final var root = directory.toRealPath();
        final var server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/", exchange -> {
            try (exchange) {
                final var method = exchange.getRequestMethod();
                if (!"GET".equals(method) && !"HEAD".equals(method)) {
                    exchange.getResponseHeaders().set("Allow", "GET, HEAD");
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }
                var file = root.resolve(exchange.getRequestURI().getPath().substring(1))
                        .normalize();
                if (!file.startsWith(root)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                if (Files.isDirectory(file)) {
                    if (!exchange.getRequestURI().getPath().endsWith("/")) {
                        exchange.getResponseHeaders()
                                .set("Location", exchange.getRequestURI().getRawPath() + "/");
                        exchange.sendResponseHeaders(301, -1);
                        return;
                    }
                    file = file.resolve("index.html");
                }
                if (!Files.isRegularFile(file) || !file.toRealPath().startsWith(root)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                String contentType =
                        AssetMedia.forPath(file.getFileName().toString()).type();
                if (contentType.startsWith("text/") || "application/json".equals(contentType)) {
                    contentType += "; charset=utf-8";
                }
                String accepted =
                        String.join(",", exchange.getRequestHeaders().getOrDefault("Accept-Encoding", List.of()));
                var qualities = encodings(accepted);
                double identity = qualities.getOrDefault("identity", 0 == qualities.getOrDefault("*", 1.0) ? 0.0 : 1.0);
                double brotli = qualities.getOrDefault("br", qualities.getOrDefault("*", 0.0));
                var compressed = file.resolveSibling(file.getFileName() + ".br");
                if (brotli > 0
                        && brotli >= identity
                        && Files.isRegularFile(compressed)
                        && compressed.toRealPath().startsWith(root)) {
                    file = compressed;
                    exchange.getResponseHeaders().set("Content-Encoding", "br");
                } else if (identity <= 0) {
                    exchange.sendResponseHeaders(406, -1);
                    return;
                }
                exchange.getResponseHeaders().set("Vary", "Accept-Encoding");
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                if ("HEAD".equals(method)) {
                    exchange.getResponseHeaders().set("Content-Length", Long.toString(Files.size(file)));
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(200, Files.size(file));
                    Files.copy(file, exchange.getResponseBody());
                }
            }
        });
        server.start();
        return server;
    }

    private static Map<String, Double> encodings(String header) {
        var encodings = new HashMap<String, Double>();
        for (var item : header.split(",")) {
            var parts = item.trim().toLowerCase(Locale.ROOT).split(";");
            if (parts[0].isEmpty()) {
                continue;
            }
            double quality = 1;
            for (int i = 1; i < parts.length; i++) {
                var parameter = parts[i].trim();
                if (parameter.startsWith("q=")) {
                    try {
                        quality = Double.parseDouble(parameter.substring(2));
                        if (!Double.isFinite(quality) || quality < 0 || quality > 1) {
                            quality = 0;
                        }
                    } catch (NumberFormatException ignored) {
                        quality = 0;
                    }
                }
            }
            encodings.put(parts[0], quality);
        }
        return encodings;
    }
}
