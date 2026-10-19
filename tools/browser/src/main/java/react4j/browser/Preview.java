package react4j.browser;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
