package react4j.css.internal;

import java.util.Locale;
import react4j.css.internal.CssResource.Kind;

public final class CssIdentity {
    private CssIdentity() {}

    public static String encode(String name) {
        var result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                result.append(c);
            } else if ('.' == c) {
                result.append('-');
            } else {
                result.append('_')
                        .append(String.format(Locale.ROOT, "%04x", (int) c))
                        .append('_');
            }
        }
        return result.toString();
    }

    public static String root(String binaryName) {
        return "jcss-" + encode(binaryName);
    }

    public static void checkAlias(String name, Kind kind) {
        if (!name.matches("(?:--|-?(?:[a-zA-Z_]|[^\\x00-\\x7f]))(?:[a-zA-Z0-9_-]|[^\\x00-\\x7f])*")
                || "--".equals(name)
                || (Kind.KEYFRAMES == kind
                        && ("none".equalsIgnoreCase(name)
                                || "default".equalsIgnoreCase(name)
                                || CssAnimationValues.CSS_WIDE.contains(name.toLowerCase(Locale.ROOT))))) {
            throw new IllegalArgumentException("Invalid imported CSS name: " + name);
        }
    }
}
