package react4j.css.utility.language;

import java.util.ArrayList;
import java.util.List;
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

    static String substitute(String text, String parameter, String value) {
        var result = new StringBuilder();
        var cursor = new Cursor(text);
        while (cursor.position < text.length()) {
            int start = cursor.position;
            char c = text.charAt(start);
            if ('\'' == c || '"' == c || text.startsWith("/*", start) || '\\' == c) {
                cursor.component();
                result.append(text, start, cursor.position);
            } else if (text.startsWith("param(", start)
                    && (0 == start || !Character.isJavaIdentifierPart(text.charAt(start - 1)))) {
                cursor.position += 5;
                int open = cursor.position;
                cursor.component();
                if (!parameter.equals(
                                text.substring(open + 1, cursor.position - 1).trim())
                        || parameter.isEmpty()) {
                    throw new Syntax("U_PARAMETER", "Undefined parameter in " + text, start);
                }
                result.append(value);
            } else {
                result.append(c);
                cursor.position++;
            }
        }
        return result.toString();
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
