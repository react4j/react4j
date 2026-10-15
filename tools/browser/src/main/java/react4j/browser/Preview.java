package react4j.browser;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Preview {
    private Preview() {}

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("Usage: preview <directory> (http://localhost:3000)");
            return;
        }
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected a directory");
        }
        serve(Path.of(args[0]));
    }

    public static void serve(final Path directory) throws IOException {
        final var root = directory.toRealPath();
        final var server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 3000), 0);
        server.createContext("/", exchange -> {
            try (exchange) {
                final var method = exchange.getRequestMethod();
                if (!method.equals("GET") && !method.equals("HEAD")) {
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
                final var name = file.getFileName().toString();
                final var extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
                final var contentType =
                        switch (extension) {
                            case "html" -> "text/html; charset=utf-8";
                            case "css" -> "text/css; charset=utf-8";
                            case "js" -> "text/javascript; charset=utf-8";
                            case "svg" -> "image/svg+xml";
                            case "png" -> "image/png";
                            case "ico" -> "image/x-icon";
                            case "json" -> "application/json";
                            case "xml" -> "application/xml";
                            default -> "application/octet-stream";
                        };
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                if (method.equals("HEAD")) {
                    exchange.getResponseHeaders().set("Content-Length", Long.toString(Files.size(file)));
                    exchange.sendResponseHeaders(200, -1);
                } else {
                    exchange.sendResponseHeaders(200, Files.size(file));
                    Files.copy(file, exchange.getResponseBody());
                }
            }
        });
        server.start();
        System.out.println("Serving " + root
                + " at http://localhost:3000; rebuild with tools/examples.sh build or tools/site.sh build and"
                + " refresh the browser");
    }
}
