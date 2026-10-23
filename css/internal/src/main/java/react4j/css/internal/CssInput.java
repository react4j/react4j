package react4j.css.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;

/** Header-visible catalogue derived from one explicitly supplied stylesheet. */
public final class CssInput {
    private CssInput() {}

    public static String path(String source) {
        AssetResource.validatePath(source);
        return "react4j_css_inputs/" + HexFormat.of().formatHex(source.getBytes(StandardCharsets.UTF_8)) + ".css";
    }

    public static String normalize(String css) {
        return css.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String digest(String css) {
        try {
            return HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256").digest(css.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String catalogue(String source, String css) {
        path(source);
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("source", source);
        json.addProperty("digest", digest(css));
        var classes = new JsonArray();
        CssTokens.read(normalize(css)).stream()
                .filter(token -> CssResource.SymbolKind.CLASS == token.kind())
                .map(CssTokens.Token::name)
                .distinct()
                .sorted()
                .forEach(classes::add);
        json.add("classes", classes);
        return json + "\n";
    }

    public static List<String> classes(String source, String css, String catalogue) {
        path(source);
        try {
            var json = JsonParser.parseString(catalogue).getAsJsonObject();
            if (!"1".equals(json.get("version").toString())
                    || !source.equals(json.get("source").getAsString())
                    || !digest(css).equals(json.get("digest").getAsString())) {
                throw new IllegalArgumentException("CSS input catalogue differs from its source: " + source);
            }
            var classes = new LinkedHashSet<String>();
            for (var item : json.getAsJsonArray("classes")) {
                if (!item.isJsonPrimitive()
                        || !item.getAsJsonPrimitive().isString()
                        || !classes.add(item.getAsString())) {
                    throw new IllegalArgumentException("Invalid CSS input class catalogue: " + source);
                }
            }
            return List.copyOf(classes);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid CSS input catalogue: " + source + ": " + e.getMessage(), e);
        }
    }
}
