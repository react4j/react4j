package org.realityforge.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Decodes a source token while retaining the source offset of each cooked character. */
record TextBlock(String value, int[] offsets) {
    private record Character(char value, int offset) {}

    static TextBlock decode(String token, int start, String expected) {
        var unicode = new ArrayList<Character>();
        int slashes = 0;
        boolean fromUnicode = false;
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            int offset = start + i;
            boolean translated = false;
            if (c == '\\'
                    && (slashes % 2 == 0 || fromUnicode)
                    && i + 1 < token.length()
                    && token.charAt(i + 1) == 'u') {
                int j = i + 1;
                while (token.charAt(j) == 'u') {
                    j++;
                }
                c = (char) Integer.parseInt(token.substring(j, j + 4), 16);
                i = j + 3;
                translated = true;
            }
            unicode.add(new Character(c, offset));
            slashes = c == '\\' ? slashes + 1 : 0;
            fromUnicode = translated;
        }
        String translated = string(unicode);
        if (!translated.startsWith("\"\"\"") || !translated.endsWith("\"\"\"")) {
            throw new IllegalArgumentException("@Css requires a literal Java text block");
        }
        int body = 3;
        while (translated.charAt(body) != '\r' && translated.charAt(body) != '\n') {
            body++;
        }
        if (translated.charAt(body++) == '\r' && translated.charAt(body) == '\n') {
            body++;
        }
        var normalized = new ArrayList<Character>();
        for (int i = body; i < unicode.size() - 3; i++) {
            var c = unicode.get(i);
            if (c.value() == '\r') {
                normalized.add(new Character('\n', c.offset()));
                if (i + 1 < unicode.size() - 3 && unicode.get(i + 1).value() == '\n') {
                    i++;
                }
            } else {
                normalized.add(c);
            }
        }
        var lines = new ArrayList<List<Character>>();
        int lineStart = 0;
        for (int i = 0; i < normalized.size(); i++) {
            if (normalized.get(i).value() == '\n') {
                lines.add(normalized.subList(lineStart, i));
                lineStart = i + 1;
            }
        }
        lines.add(normalized.subList(lineStart, normalized.size()));
        int indent = Integer.MAX_VALUE;
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            int leading = 0;
            while (leading < line.size()
                    && java.lang.Character.isWhitespace(line.get(leading).value())) {
                leading++;
            }
            if (leading != line.size() || i == lines.size() - 1) {
                indent = Math.min(indent, leading);
            }
        }
        var stripped = new ArrayList<Character>();
        int newlineIndex = 0;
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            int leading = 0;
            while (leading < Math.min(indent, line.size())
                    && java.lang.Character.isWhitespace(line.get(leading).value())) {
                leading++;
            }
            int last = line.size();
            while (last > leading
                    && java.lang.Character.isWhitespace(line.get(last - 1).value())) {
                last--;
            }
            stripped.addAll(line.subList(leading, last));
            newlineIndex += line.size();
            if (i != lines.size() - 1) {
                stripped.add(normalized.get(newlineIndex++));
            }
        }
        var cooked = new ArrayList<Character>();
        for (int i = 0; i < stripped.size(); i++) {
            var c = stripped.get(i);
            if (c.value() != '\\') {
                cooked.add(c);
                continue;
            }
            char escaped = stripped.get(++i).value();
            if (escaped == '\n') {
                continue;
            }
            char value;
            if (escaped >= '0' && escaped <= '7') {
                int number = escaped - '0';
                int max = escaped <= '3' ? 3 : 2;
                for (int n = 1; n < max && i + 1 < stripped.size(); n++) {
                    char next = stripped.get(i + 1).value();
                    if (next < '0' || next > '7') {
                        break;
                    }
                    number = number * 8 + next - '0';
                    i++;
                }
                value = (char) number;
            } else {
                value = switch (escaped) {
                    case 'b' -> '\b';
                    case 't' -> '\t';
                    case 'n' -> '\n';
                    case 'f' -> '\f';
                    case 'r' -> '\r';
                    case 's' -> ' ';
                    case '\\', '\'', '"' -> escaped;
                    default -> throw new IllegalArgumentException("Unsupported Java escape in @Css");
                };
            }
            cooked.add(new Character(value, c.offset()));
        }
        String value = string(cooked);
        if (!value.equals(expected)) {
            throw new IllegalArgumentException("CSS source mapping disagrees with javac's text-block value");
        }
        int[] offsets =
                Arrays.copyOf(cooked.stream().mapToInt(Character::offset).toArray(), cooked.size() + 1);
        offsets[cooked.size()] = unicode.get(unicode.size() - 3).offset();
        return new TextBlock(value, offsets);
    }

    private static String string(List<Character> chars) {
        var text = new StringBuilder();
        chars.forEach(c -> text.append(c.value()));
        return text.toString();
    }
}
