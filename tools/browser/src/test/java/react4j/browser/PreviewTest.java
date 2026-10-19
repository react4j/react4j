package react4j.browser;

import static org.testng.Assert.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public void canonicalUrlsNegotiateRealBrotliWithCorrectMimeQualityAndHeadHeaders() throws Exception {
        var directory = Files.createTempDirectory("preview-http");
        try {
            var root = Files.createDirectory(directory.resolve("root"));
            var mounted = Files.createDirectories(root.resolve("mount/assets"));
            var source = mounted.resolve("icon.svg");
            Files.writeString(source, "<svg><path d='M0 0L10 10'/></svg>".repeat(1000));
            var br = mounted.resolve("icon.svg.br");
            var process = new ProcessBuilder(
                            Path.of(Objects.requireNonNull(System.getProperty("brotli")))
                                    .toAbsolutePath()
                                    .toString(),
                            "-q",
                            "11",
                            "-f",
                            "-o",
                            br.toString(),
                            source.toString())
                    .start();
            assertEquals(process.waitFor(), 0);
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
                for (var encoding : List.of("br", "gzip, BR;q=1", "*;q=1", "br;q=0.5,identity;q=0")) {
                    var response = request(client, base, "mount/assets/icon.svg", "GET", encoding);
                    assertEquals(response.statusCode(), 200);
                    assertEquals(response.headers().firstValue("Content-Type").orElseThrow(), "image/svg+xml");
                    assertEquals(
                            response.headers().firstValue("Content-Encoding").orElseThrow(), "br");
                    assertEquals(response.headers().firstValue("Vary").orElseThrow(), "Accept-Encoding");
                    assertEquals(response.body(), Files.readAllBytes(br));
                    var head = request(client, base, "mount/assets/icon.svg", "HEAD", encoding);
                    assertEquals(head.body().length, 0);
                    assertEquals(
                            head.headers().firstValue("Content-Length").orElseThrow(), Long.toString(Files.size(br)));
                }
                for (var encoding : List.of(
                        "",
                        "gzip",
                        "br;q=0",
                        "br;q=0.2",
                        "*;q=0,identity;q=1",
                        "br;q=bad",
                        "br;q=NaN",
                        "br;q=2",
                        "br;q=-1")) {
                    var response = request(client, base, "mount/assets/icon.svg", "GET", encoding);
                    assertEquals(response.statusCode(), 200, encoding);
                    assertTrue(response.headers().firstValue("Content-Encoding").isEmpty());
                    assertEquals(response.body(), Files.readAllBytes(source));
                }
                assertEquals(
                        request(client, base, "mount/assets/icon.svg", "GET", "br;q=0,identity;q=0")
                                .statusCode(),
                        406);
                assertEquals(
                        request(client, base, "index.html", "GET", "br,identity;q=0")
                                .statusCode(),
                        406);
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
