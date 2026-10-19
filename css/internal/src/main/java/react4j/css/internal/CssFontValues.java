package react4j.css.internal;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberFunction;
import com.helger.css.decl.CSSExpressionMemberMath;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSExpressionMemberTermURI;
import com.helger.css.decl.CSSFontFaceRule;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/** Font-family lists and the family portion of the font shorthand. */
public final class CssFontValues {
    private CssFontValues() {}

    public static final Set<String> RESERVED = Set.of(
            "inherit",
            "initial",
            "unset",
            "revert",
            "revert-layer",
            "serif",
            "sans-serif",
            "monospace",
            "cursive",
            "fantasy",
            "system-ui",
            "ui-serif",
            "ui-sans-serif",
            "ui-monospace",
            "ui-rounded",
            "emoji",
            "math",
            "fangsong",
            "caption",
            "icon",
            "menu",
            "message-box",
            "small-caption",
            "status-bar",
            "default");
    private static final Set<String> SIZES = Set.of(
            "xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large", "xxx-large", "larger", "smaller");
    private static final Set<String> PREFIXES = Set.of(
            "normal",
            "italic",
            "oblique",
            "small-caps",
            "bold",
            "bolder",
            "lighter",
            "ultra-condensed",
            "extra-condensed",
            "condensed",
            "semi-condensed",
            "semi-expanded",
            "expanded",
            "extra-expanded",
            "ultra-expanded");

    public record Family(CSSExpression expression, int first, int last, String name) {
        List<CSSExpressionMemberTermSimple> terms() {
            return expression.getAllMembers().subList(first, last).stream()
                    .map(CSSExpressionMemberTermSimple.class::cast)
                    .toList();
        }

        public void replace(String name) {
            var firstTerm = terms().get(0);
            firstTerm.setValue(name);
            for (int i = last - 1; i > first; i--) {
                expression.removeMember(i);
            }
        }
    }

    public record Value(List<Family> families, boolean dynamic) {}

    public static List<CssResource.FontUse> uses(String css, List<CssResource.SymbolId> fonts) {
        if (fonts.isEmpty()) {
            return List.of();
        }
        var uses = new ArrayList<CssResource.FontUse>();
        var syntax = new CssSyntax(css, 0);
        CSSVisitor.visitCSS(
                Objects.requireNonNull(CSSReader.readFromStringReader(css, new CSSReaderSettings().setTabSize(1))),
                new DefaultCSSVisitor() {
                    @Override
                    public void onDeclaration(CSSDeclaration declaration) {
                        if (read(declaration).dynamic()) {
                            var location = Objects.requireNonNull(declaration.getSourceLocation());
                            uses.add(new CssResource.FontUse(
                                    syntax.offset(
                                            location.getFirstTokenBeginLineNumber(),
                                            location.getFirstTokenBeginColumnNumber()),
                                    syntax.offset(
                                                    location.getLastTokenEndLineNumber(),
                                                    location.getLastTokenEndColumnNumber())
                                            + 1,
                                    fonts));
                        }
                    }
                });
        return List.copyOf(uses);
    }

    public static String canonical(String family) {
        if (family.isEmpty() || family.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Font family must be a nonempty static name");
        }
        return UnicodeCaseFold.fold(family);
    }

    public static String face(CSSFontFaceRule face) {
        var families = face.getAllDeclarationsOfPropertyName("font-family");
        if (1 != families.size() || null == face.getDeclarationOfPropertyName("src")) {
            throw new IllegalArgumentException("@font-face requires exactly one font-family and a src descriptor");
        }
        var value = read(families.get(0));
        if (value.dynamic() || 1 != value.families().size()) {
            throw new IllegalArgumentException("@font-face font-family requires one static family");
        }
        String name = value.families().get(0).name();
        if (RESERVED.contains(name)) {
            throw new IllegalArgumentException("Reserved owned font family: " + name);
        }
        var descriptors = Set.of(
                "font-family",
                "src",
                "font-style",
                "font-weight",
                "font-stretch",
                "font-display",
                "unicode-range",
                "font-feature-settings",
                "font-variation-settings",
                "font-named-instance",
                "ascent-override",
                "descent-override",
                "line-gap-override",
                "size-adjust");
        var seen = new HashSet<String>();
        for (var descriptor : face.getAllDeclarations()) {
            String property = descriptor.getProperty().toLowerCase(Locale.ROOT);
            if (!descriptors.contains(property) || !seen.add(property)) {
                throw new IllegalArgumentException("Unsupported or duplicate @font-face descriptor: " + property);
            }
        }
        boolean source = false;
        for (var member : Objects.requireNonNull(face.getDeclarationOfPropertyName("src"))
                .getExpression()
                .getAllMembers()) {
            if (ECSSExpressionOperator.COMMA == member) {
                if (!source) {
                    throw new IllegalArgumentException("Empty @font-face source");
                }
                source = false;
            } else if (!source) {
                if (member instanceof CSSExpressionMemberTermURI) {
                    source = true;
                } else if (member instanceof CSSExpressionMemberFunction function
                        && Set.of("local", "url")
                                .contains(CSSParseHelper.unescapeURL(function.getFunctionName())
                                        .toLowerCase(Locale.ROOT))
                        && null != function.getExpression()
                        && function.getExpression().getMemberCount() > 0) {
                    source = true;
                } else {
                    throw new IllegalArgumentException("@font-face src requires URL or local() sources");
                }
            } else if (!(member instanceof CSSExpressionMemberFunction function)
                    || !Set.of("format", "tech")
                            .contains(CSSParseHelper.unescapeURL(function.getFunctionName())
                                    .toLowerCase(Locale.ROOT))
                    || null == function.getExpression()
                    || 0 == function.getExpression().getMemberCount()) {
                throw new IllegalArgumentException("@font-face source accepts only format() or tech() hints");
            }
        }
        if (!source) {
            throw new IllegalArgumentException("Empty @font-face source");
        }
        return name;
    }

    public static Value read(CSSDeclaration declaration) {
        String property = CSSParseHelper.unescapeURL(declaration.getProperty()).toLowerCase(Locale.ROOT);
        if (!"font".equals(property) && !"font-family".equals(property)) {
            return new Value(List.of(), false);
        }
        var expression = declaration.getExpression();
        return read(expression, "font".equals(property), 0);
    }

    private static Value read(CSSExpression expression, boolean shorthand, int first) {
        int initial = first;
        if (shorthand) {
            var members = expression.getAllMembers();
            if (initial + 1 == members.size()
                    && members.get(initial) instanceof CSSExpressionMemberTermSimple simple
                    && RESERVED.contains(simple.getValue().toLowerCase(Locale.ROOT))) {
                return new Value(List.of(), false);
            }
            boolean size = false;
            while (first < members.size()) {
                var member = members.get(first++);
                if (member instanceof CSSExpressionMemberMath) {
                    size = true;
                    break;
                }
                if (member instanceof CSSExpressionMemberFunction function) {
                    String name = function.getFunctionName().toLowerCase(Locale.ROOT);
                    if (Set.of("calc", "min", "max", "clamp").contains(name)) {
                        size = true;
                        break;
                    }
                    if (Set.of("var", "env").contains(name)) {
                        var nested = function.getExpression();
                        if (initial + 1 == first && first == members.size() && null != nested) {
                            int comma = nested.getAllMembers().indexOf(ECSSExpressionOperator.COMMA);
                            if (comma >= 0 && comma + 1 < nested.getMemberCount()) {
                                return new Value(read(nested, true, comma + 1).families(), true);
                            }
                        }
                        return new Value(List.of(), true);
                    }
                    throw new IllegalArgumentException("Unsupported font shorthand before its size");
                }
                if (!(member instanceof CSSExpressionMemberTermSimple term)) {
                    throw new IllegalArgumentException("Invalid font shorthand size");
                }
                String value = term.getValue().toLowerCase(Locale.ROOT);
                if (SIZES.contains(value)
                        || value.matches(
                                "(?:[0-9]*\\.)?[0-9]+(?:px|em|rem|ex|ch|cap|ic|lh|rlh|vw|vh|vi|vb|vmin|vmax|svw|svh|lvw|lvh|dvw|dvh|cm|mm|q|in|pt|pc|%)")
                        || "0".equals(value)) {
                    size = true;
                    break;
                }
                if (!PREFIXES.contains(value) && !value.matches("[1-9][0-9]{0,2}|1000|(?:[0-9]*\\.)?[0-9]+deg")) {
                    throw new IllegalArgumentException("Invalid font shorthand prefix: " + value);
                }
            }
            if (!size || first >= members.size()) {
                throw new IllegalArgumentException("Font shorthand requires a size and family");
            }
            if (ECSSExpressionOperator.SLASH == members.get(first)) {
                first += 2;
                if (first >= members.size()) {
                    throw new IllegalArgumentException("Font shorthand requires a family after line-height");
                }
            }
        }
        return families(expression, first);
    }

    private static Value families(CSSExpression expression, int first) {
        var families = new ArrayList<Family>();
        boolean dynamic = false;
        var members = expression.getAllMembers();
        while (first < members.size()) {
            int last = first;
            while (last < members.size() && ECSSExpressionOperator.COMMA != members.get(last)) {
                last++;
            }
            if (last == first) {
                throw new IllegalArgumentException("Empty font family");
            }
            if (last == first + 1
                    && members.get(first) instanceof CSSExpressionMemberFunction function
                    && Set.of("var", "env").contains(function.getFunctionName().toLowerCase(Locale.ROOT))) {
                dynamic = true;
                var nested = function.getExpression();
                if (null != nested) {
                    int comma = nested.getAllMembers().indexOf(ECSSExpressionOperator.COMMA);
                    if (comma >= 0) {
                        var fallback = families(nested, comma + 1);
                        families.addAll(fallback.families());
                    }
                }
            } else {
                var words = new ArrayList<String>();
                for (int i = first; i < last; i++) {
                    if (!(members.get(i) instanceof CSSExpressionMemberTermSimple term)
                            || (term.isStringLiteral() && last != first + 1)) {
                        throw new IllegalArgumentException(
                                "Font families require identifiers, a quoted string or var()");
                    }
                    String value = term.getValue();
                    if (term.isStringLiteral()) {
                        value = Objects.requireNonNull(CSSParseHelper.extractStringValue(value));
                    } else if (!value.matches(
                            "(?:--|-?(?:[a-zA-Z_]|[^\\x00-\\x7f]))(?:[a-zA-Z0-9_-]|[^\\x00-\\x7f]|\\\\.)*")) {
                        throw new IllegalArgumentException("Invalid font family identifier: " + value);
                    }
                    words.add(value);
                }
                families.add(new Family(
                        expression, first, last, canonical(CSSParseHelper.unescapeURL(String.join(" ", words)))));
            }
            first = last + 1;
            if (first == members.size()) {
                throw new IllegalArgumentException("Empty font family after comma");
            }
        }
        return new Value(List.copyOf(families), dynamic);
    }
}
