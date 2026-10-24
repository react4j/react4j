package react4j.css.utility.language;

import com.helger.css.parser.CSSParseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import react4j.css.internal.CssLexical;

/** Balanced CSS components, with offsets into the original UTF-16 source. */
final class RuleSyntax {
    private RuleSyntax() {}

    record Part(String text, int offset) {}

    record Node(String head, List<Node> children, int offset, boolean block) {
        Node {
            children = List.copyOf(children);
        }
    }

    static List<Node> nodes(String text) {
        var cursor = new Cursor(text);
        var result = cursor.nodes(false);
        return List.copyOf(result);
    }

    static List<Part> split(String text, char delimiter) {
        var result = new ArrayList<Part>();
        int start = 0;
        var cursor = new Cursor(text);
        while (cursor.position < text.length()) {
            char c = text.charAt(cursor.position);
            if (delimiter == c) {
                result.add(new Part(text.substring(start, cursor.position).trim(), start));
                start = ++cursor.position;
            } else {
                cursor.component();
            }
        }
        result.add(new Part(text.substring(start).trim(), start));
        return List.copyOf(result);
    }

    static String clean(String text) {
        var result = new StringBuilder();
        var cursor = new Cursor(text);
        while (cursor.position < text.length()) {
            int start = cursor.position;
            char c = text.charAt(start);
            if (text.startsWith("/*", start)) {
                cursor.component();
                result.append(' ');
            } else if ('\\' == c || '\'' == c || '\"' == c) {
                cursor.component();
                result.append(text, start, cursor.position);
            } else {
                result.append(c);
                cursor.position++;
            }
        }
        return result.toString().trim();
    }

    static String decode(String text) {
        var result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ('\\' == c && i + 1 < text.length()) {
                char next = text.charAt(++i);
                if ('_' != next && '\\' != next) {
                    result.append('\\');
                }
                result.append(next);
            } else {
                result.append('_' == c ? ' ' : c);
            }
        }
        return result.toString();
    }

    static String guard(String text, String selector) {
        var result = new StringBuilder(text);
        var tokens = CssLexical.tokens(text, 0, text.length());
        for (int i = tokens.size() - 1; i >= 0; i--) {
            var token = tokens.get(i);
            if ("&".equals(token.value())) {
                result.replace(token.start(), token.end(), selector);
            }
        }
        return result.toString();
    }

    record Function(String name, String arguments, int start, int end, int argumentStart) {}

    static List<Function> functions(String text) {
        var result = new ArrayList<Function>();
        functions(text, 0, text.length(), result);
        return List.copyOf(result);
    }

    private static void functions(String text, int first, int last, List<Function> result) {
        var cursor = new Cursor(text);
        cursor.position = first;
        while (cursor.position < last) {
            int start = cursor.position;
            char c = text.charAt(start);
            if ('\"' == c || '\'' == c || text.startsWith("/*", start)) {
                cursor.component();
            } else if (Character.isLetterOrDigit(c) || '_' == c || '-' == c || c >= 128 || '\\' == c) {
                do {
                    if ('\\' == text.charAt(cursor.position)) {
                        cursor.position++;
                        int digits = 0;
                        while (cursor.position < last
                                && digits < 6
                                && Character.digit(text.charAt(cursor.position), 16) >= 0) {
                            cursor.position++;
                            digits++;
                        }
                        if (0 == digits && cursor.position < last) {
                            cursor.position++;
                        } else if (cursor.position < last && Character.isWhitespace(text.charAt(cursor.position))) {
                            cursor.position++;
                        }
                    } else {
                        cursor.position++;
                    }
                } while (cursor.position < last
                        && (Character.isLetterOrDigit(text.charAt(cursor.position))
                                || "_-\\".indexOf(text.charAt(cursor.position)) >= 0
                                || text.charAt(cursor.position) >= 128));
                if (cursor.position < last && '(' == text.charAt(cursor.position)) {
                    int open = cursor.position;
                    String name = CSSParseHelper.unescapeURL(text.substring(start, open))
                            .toLowerCase(Locale.ROOT);
                    cursor.component();
                    result.add(new Function(
                            name, text.substring(open + 1, cursor.position - 1), start, cursor.position, open + 1));
                    functions(text, open + 1, cursor.position - 1, result);
                }
            } else {
                cursor.position++;
            }
        }
    }

    static @Nullable Function function(String raw) {
        String text = raw.trim();
        return functions(text).stream()
                .filter(function -> 0 == function.start() && text.length() == function.end())
                .findFirst()
                .orElse(null);
    }

    static List<String> components(String raw) {
        String text = clean(raw);
        var cursor = new Cursor(text);
        var result = new ArrayList<String>();
        while (cursor.position < text.length()) {
            cursor.space();
            int start = cursor.position;
            while (cursor.position < text.length() && !Character.isWhitespace(text.charAt(cursor.position))) {
                char c = text.charAt(cursor.position);
                if ('[' == c && cursor.position > start) {
                    break;
                }
                cursor.component();
                if ('[' == c || '(' == c || '\'' == c || '\"' == c) {
                    break;
                }
            }
            if (start < cursor.position) {
                result.add(text.substring(start, cursor.position));
            }
        }
        return List.copyOf(result);
    }

    static boolean selectorHoles(String text) {
        var functions = functions(text);
        return functions.stream()
                .filter(function -> "param".equals(function.name()))
                .allMatch(hole -> functions.stream()
                        .anyMatch(parent -> "nth-child".equals(parent.name())
                                && clean(parent.arguments()).equals(clean(text.substring(hole.start(), hole.end())))));
    }

    static String substitute(String text, String parameter, String value) {
        var result = new StringBuilder();
        int offset = 0;
        for (var function : functions(text)) {
            if (function.start() >= offset && "param".equals(function.name())) {
                if (parameter.isEmpty() || !parameter.equals(clean(function.arguments()))) {
                    throw new Syntax("U_PARAMETER", "Undefined parameter in " + text, function.start());
                }
                result.append(text, offset, function.start()).append(value);
                offset = function.end();
            }
        }
        return result.append(text.substring(offset)).toString();
    }

    static final class Syntax extends IllegalArgumentException {
        final String code;
        final int offset;
        final List<Integer> related;

        Syntax(String code, String message, int offset) {
            this(code, message, offset, List.of());
        }

        Syntax(String code, String message, int offset, List<Integer> related) {
            super(message);
            this.code = code;
            this.offset = offset;
            this.related = List.copyOf(related);
        }
    }

    private static final class Cursor {
        private final String text;
        private int position;

        Cursor(String text) {
            this.text = text;
        }

        List<Node> nodes(boolean nested) {
            var result = new ArrayList<Node>();
            while (true) {
                space();
                if (position == text.length()) {
                    if (nested) {
                        throw error("Missing closing brace");
                    }
                    return result;
                }
                if ('}' == text.charAt(position)) {
                    if (!nested) {
                        throw error("Unexpected closing brace");
                    }
                    position++;
                    return result;
                }
                int start = position;
                while (position < text.length() && ";{}".indexOf(text.charAt(position)) < 0) {
                    component();
                }
                if (position == text.length()) {
                    throw error("Expected semicolon or block");
                }
                String head = text.substring(start, position).trim();
                char delimiter = text.charAt(position++);
                if (head.isEmpty() || '}' == delimiter) {
                    throw error("Empty or incomplete rule");
                }
                result.add(new Node(head, '{' == delimiter ? nodes(true) : List.of(), start, '{' == delimiter));
            }
        }

        void space() {
            while (position < text.length()) {
                if (Character.isWhitespace(text.charAt(position))) {
                    position++;
                } else if (text.startsWith("/*", position)) {
                    component();
                } else {
                    return;
                }
            }
        }

        void component() {
            char c = text.charAt(position++);
            if ('\\' == c) {
                if (position == text.length()) {
                    throw error("Incomplete escape");
                }
                position++;
            } else if ('/' == c && position < text.length() && '*' == text.charAt(position)) {
                int end = text.indexOf("*/", position + 1);
                if (end < 0) {
                    throw error("Unterminated comment");
                }
                position = end + 2;
            } else if ('"' == c || '\'' == c) {
                while (position < text.length()) {
                    char next = text.charAt(position++);
                    if (c == next) {
                        return;
                    }
                    if ('\\' == next && position < text.length()) {
                        position++;
                    } else if ('\n' == next || '\r' == next || '\f' == next) {
                        throw error("Newline in string");
                    }
                }
                throw error("Unterminated string");
            } else if ('(' == c || '[' == c) {
                char close = '(' == c ? ')' : ']';
                while (position < text.length() && close != text.charAt(position)) {
                    component();
                }
                if (position == text.length()) {
                    throw error("Unbalanced CSS component");
                }
                position++;
            } else if (')' == c || ']' == c || '}' == c) {
                throw error("Unexpected closing delimiter");
            }
        }

        Syntax error(String message) {
            return new Syntax("U_CANDIDATE_SYNTAX", message, position);
        }
    }
}
