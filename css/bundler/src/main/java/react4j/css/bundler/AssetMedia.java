package react4j.css.bundler;

import java.util.Locale;

/** Canonical file extensions and media types shared by packaging and HTTP preview. */
public record AssetMedia(String extension, String type) {
    public static AssetMedia forPath(String logical) {
        String extension = logical.substring(logical.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (extension) {
            case "svg" -> new AssetMedia("svg", "image/svg+xml");
            case "png" -> new AssetMedia("png", "image/png");
            case "jpg", "jpeg" -> new AssetMedia("jpg", "image/jpeg");
            case "gif" -> new AssetMedia("gif", "image/gif");
            case "webp" -> new AssetMedia("webp", "image/webp");
            case "avif" -> new AssetMedia("avif", "image/avif");
            case "ico" -> new AssetMedia("ico", "image/x-icon");
            case "woff" -> new AssetMedia("woff", "font/woff");
            case "woff2" -> new AssetMedia("woff2", "font/woff2");
            case "ttf" -> new AssetMedia("ttf", "font/ttf");
            case "otf" -> new AssetMedia("otf", "font/otf");
            case "css" -> new AssetMedia("css", "text/css");
            case "js" -> new AssetMedia("js", "text/javascript");
            case "json", "map" -> new AssetMedia("json", "application/json");
            case "txt" -> new AssetMedia("txt", "text/plain");
            case "xml" -> new AssetMedia("xml", "application/xml");
            case "html" -> new AssetMedia("html", "text/html");
            case "wav" -> new AssetMedia("wav", "audio/wav");
            case "mp3" -> new AssetMedia("mp3", "audio/mpeg");
            case "ogg" -> new AssetMedia("ogg", "audio/ogg");
            case "wasm" -> new AssetMedia("wasm", "application/wasm");
            case "pdf" -> new AssetMedia("pdf", "application/pdf");
            default -> new AssetMedia("bin", "application/octet-stream");
        };
    }
}
