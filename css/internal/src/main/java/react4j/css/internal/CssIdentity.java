package react4j.css.internal;

import java.util.Locale;

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
}
