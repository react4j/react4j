package react4j.verification;

import com.google.devtools.build.runfiles.AutoBazelRepository;
import com.google.devtools.build.runfiles.Runfiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NullMarked;

@NullMarked
@AutoBazelRepository
final class BrowserCoverage {
    private BrowserCoverage() {}

    static JsonObject run(Path metadata, Map<String, String> assets, String completed, String result) throws Exception {
        var bytes = new TreeMap<String, byte[]>();
        assets.forEach((path, contents) -> bytes.put(path, contents.getBytes(StandardCharsets.UTF_8)));
        return runBytes(metadata, bytes, completed, result);
    }

    static JsonObject runBytes(Path metadata, Map<String, byte[]> assets, String completed, String result)
            throws Exception {
        var server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            try (exchange) {
                String path = exchange.getRequestURI().getPath();
                byte[] bytes = assets.get(path);
                if (!"GET".equals(exchange.getRequestMethod()) || null == bytes) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                exchange.getResponseHeaders()
                        .set(
                                "Content-Type",
                                switch (path.substring(path.lastIndexOf('.') + 1)) {
                                    case "js" -> "text/javascript; charset=utf-8";
                                    case "css" -> "text/css; charset=utf-8";
                                    case "map" -> "application/json";
                                    case "svg" -> "image/svg+xml";
                                    case "png" -> "image/png";
                                    case "ttf" -> "font/ttf";
                                    default -> "text/html; charset=utf-8";
                                });
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });
        server.start();
        try (var driver = new Driver(metadata)) {
            var url = new JsonObject();
            url.addProperty("url", "http://127.0.0.1:" + server.getAddress().getPort() + "/index.html");
            driver.command("POST", "/url", url);
            long deadline = System.nanoTime() + Duration.ofSeconds(60).toNanos();
            while (System.nanoTime() < deadline) {
                if (driver.execute("return Boolean(" + completed + ");").getAsBoolean()) {
                    return driver.execute("return " + result + ";").getAsJsonObject();
                }
                Thread.sleep(50);
            }
            throw new IOException("Browser coverage did not complete within 60 seconds");
        } finally {
            server.stop(0);
        }
    }

    private static final class Driver implements AutoCloseable {
        private final HttpClient client =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        private final Path log;
        private final Process process;
        private final URI endpoint;
        private final String session;

        Driver(Path metadata) throws Exception {
            var config = JsonParser.parseString(Files.readString(metadata)).getAsJsonObject();
            var named = new JsonObject();
            for (var entry : config.getAsJsonArray("webTestFiles")) {
                entry.getAsJsonObject()
                        .getAsJsonObject("namedFiles")
                        .entrySet()
                        .forEach(e -> named.add(e.getKey(), e.getValue()));
            }
            var runfiles = Runfiles.preload().withSourceRepository(AutoBazelRepository_BrowserCoverage.NAME);
            String executable = Objects.requireNonNull(
                    runfiles.rlocation(named.get("CHROMEDRIVER").getAsString()));
            String browser = Objects.requireNonNull(
                    runfiles.rlocation(named.get("CHROMIUM").getAsString()));
            int port;
            try (var socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
                port = socket.getLocalPort();
            }
            endpoint = URI.create("http://127.0.0.1:" + port);
            log = Files.createTempFile("react4j-coverage-driver-", ".log");
            try {
                process = new ProcessBuilder(executable, "--port=" + port)
                        .redirectErrorStream(true)
                        .redirectOutput(log.toFile())
                        .start();
            } catch (IOException e) {
                Files.deleteIfExists(log);
                throw e;
            }
            try {
                long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
                boolean ready = false;
                while (process.isAlive() && System.nanoTime() < deadline) {
                    if (Files.readString(log).contains("ChromeDriver was started successfully")) {
                        ready = true;
                        break;
                    }
                    Thread.sleep(50);
                }
                if (!ready) {
                    throw new IOException("ChromeDriver did not start: " + Files.readString(log));
                }
                var options = config.getAsJsonObject("capabilities")
                        .getAsJsonObject("goog:chromeOptions")
                        .deepCopy();
                options.addProperty("binary", browser);
                var matching = new JsonObject();
                matching.addProperty("browserName", "chrome");
                matching.add("goog:chromeOptions", options);
                var capabilities = new JsonObject();
                capabilities.add("alwaysMatch", matching);
                var body = new JsonObject();
                body.add("capabilities", capabilities);
                session = request("POST", "/session", body)
                        .getAsJsonObject()
                        .get("sessionId")
                        .getAsString();
            } catch (Exception e) {
                process.destroyForcibly();
                Files.deleteIfExists(log);
                throw e;
            }
        }

        JsonElement execute(String script) throws IOException, InterruptedException {
            var body = new JsonObject();
            body.addProperty("script", script);
            body.add("args", new JsonArray());
            return command("POST", "/execute/sync", body);
        }

        JsonElement command(String method, String path, JsonObject body) throws IOException, InterruptedException {
            return request(method, "/session/" + session + path, body);
        }

        private JsonElement request(String method, String path, JsonObject body)
                throws IOException, InterruptedException {
            var request = HttpRequest.newBuilder(endpoint.resolve(path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .method(method, HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (200 != response.statusCode()) {
                throw new IOException("ChromeDriver " + path + ": " + response.body());
            }
            return JsonParser.parseString(response.body()).getAsJsonObject().get("value");
        }

        @Override
        public void close() throws IOException {
            try {
                command("DELETE", "", new JsonObject());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while closing coverage browser", e);
            } finally {
                process.destroy();
                try {
                    if (!process.waitFor(5, TimeUnit.SECONDS)) {
                        process.destroyForcibly();
                    }
                } catch (InterruptedException e) {
                    process.destroyForcibly();
                    Thread.currentThread().interrupt();
                }
                Files.deleteIfExists(log);
            }
        }
    }
}
