package react4j.browser;

import static org.testng.Assert.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;

@NullMarked
public final class PreviewTest {
    private static HttpResponse<byte[]> request(
            HttpClient client, URI base, String path, String method, String encoding) throws Exception {
        var request = HttpRequest.newBuilder(base.resolve(path)).method(method, HttpRequest.BodyPublishers.noBody());
        if (!encoding.isEmpty()) {
            request.header("Accept-Encoding", encoding);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
    }

    @Test(timeOut = 60000)
    public void canonicalFilesPreserveMimeHeadHeadersAndPathBoundaries() throws Exception {
        var directory = Files.createTempDirectory("preview-http");
        try {
            var root = Files.createDirectory(directory.resolve("root"));
            var mounted = Files.createDirectories(root.resolve("mount/assets"));
            var source = mounted.resolve("icon.svg");
            Files.writeString(source, "<svg><path d='M0 0L10 10'/></svg>");
            Files.writeString(root.resolve("index.html"), "<title>preview</title>");
            Files.writeString(root.resolve("mount/index.html"), "<title>mount</title>");
            var outside = directory.resolve("outside.svg");
            Files.writeString(outside, "outside");
            Files.createSymbolicLink(root.resolve("escape.svg"), outside);
            var server = Preview.start(root, 0);
            var client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();
            try {
                var base = URI.create("http://localhost:" + server.getAddress().getPort() + "/");
                var response = request(client, base, "mount/assets/icon.svg", "GET", "gzip, br");
                assertEquals(response.statusCode(), 200);
                assertEquals(response.headers().firstValue("Content-Type").orElseThrow(), "image/svg+xml");
                assertTrue(response.headers().firstValue("Content-Encoding").isEmpty());
                assertTrue(response.headers().firstValue("Vary").isEmpty());
                assertEquals(response.body(), Files.readAllBytes(source));
                var head = request(client, base, "mount/assets/icon.svg", "HEAD", "");
                assertEquals(head.statusCode(), 200);
                assertEquals(head.body().length, 0);
                assertEquals(
                        head.headers().firstValue("Content-Length").orElseThrow(), Long.toString(Files.size(source)));
                assertEquals(request(client, base, "missing.svg", "GET", "br").statusCode(), 404);
                assertEquals(
                        request(client, base, "%2e%2e/outside.svg", "GET", "").statusCode(), 404);
                assertEquals(request(client, base, "escape.svg", "GET", "").statusCode(), 404);
                assertEquals(request(client, base, "index.html", "POST", "").statusCode(), 405);
                var redirect = request(client, base, "mount", "GET", "");
                assertEquals(redirect.statusCode(), 301);
                assertEquals(redirect.headers().firstValue("Location").orElseThrow(), "/mount/");
                assertEquals(request(client, base, "mount/", "GET", "").statusCode(), 200);
                assertEquals(request(client, base, "", "HEAD", "").body().length, 0);
                for (var entry : Map.of(
                                "woff2",
                                "font/woff2",
                                "png",
                                "image/png",
                                "js",
                                "text/javascript; charset=utf-8",
                                "css",
                                "text/css; charset=utf-8",
                                "map",
                                "application/json; charset=utf-8")
                        .entrySet()) {
                    Files.writeString(root.resolve("file." + entry.getKey()), "mime");
                    assertEquals(
                            request(client, base, "file." + entry.getKey(), "GET", "")
                                    .headers()
                                    .firstValue("Content-Type")
                                    .orElseThrow(),
                            entry.getValue());
                }
            } finally {
                server.stop(0);
            }
            Preview.main(new String[] {"--help"});
            expectThrows(IllegalArgumentException.class, () -> Preview.main(new String[0]));
        } finally {
            try (var paths = Files.walk(directory)) {
                for (var file : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }
}
