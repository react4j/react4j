package react4j.css.internal;

import com.helger.css.parser.CSSCharStream;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import com.helger.css.parser.ParserCSS30TokenManager;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/** Lexes validated source ranges without losing comments, escapes or UTF-16 positions. */
public final class CssLexical {
    private CssLexical() {}

    public record Token(int kind, int start, int end, String value) {
        public String name() {
            return CSSParseHelper.unescapeURL(value);
        }
    }

    public static List<Token> tokens(String source, int first, int last) {
        String range = source.substring(first, last);
        var lines = new ArrayList<Integer>();
        lines.add(0);
        for (int i = 0; i < range.length(); i++) {
            if ('\n' == range.charAt(i)) {
                lines.add(i + 1);
            }
        }
        var stream = new CSSCharStream(new StringReader(range));
        stream.setTabSize(1);
        var lexer = new ParserCSS30TokenManager(stream);
        var result = new ArrayList<Token>();
        while (true) {
            var token = lexer.getNextToken();
            if (ParserCSS30Constants.EOF == token.kind) {
                return List.copyOf(result);
            }
            int start = first + lines.get(token.beginLine - 1) + token.beginColumn - 1;
            result.add(new Token(token.kind, start, start + token.image.length(), token.image));
        }
    }
}
