package react4j.css.utility.language;

import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import com.helger.css.writer.CSSWriterSettings;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import react4j.css.internal.CssAnchors;
import react4j.css.internal.CssLexical;
import react4j.css.internal.CssSyntax;

/** Anchor values require both a static selector and a declaration whose context is known. */
final class RuleAnchors {
    private RuleAnchors() {}

    private static final Set<String> INSETS = Set.of(
            "top",
            "right",
            "bottom",
            "left",
            "inset",
            "inset-block",
            "inset-inline",
            "inset-block-start",
            "inset-block-end",
            "inset-inline-start",
            "inset-inline-end");
    private static final Set<String> SIZES = Set.of(
            "width",
            "height",
            "min-width",
            "min-height",
            "max-width",
            "max-height",
            "inline-size",
            "block-size",
            "min-inline-size",
            "min-block-size",
            "max-inline-size",
            "max-block-size");
    private static final Set<String> MARGINS = Set.of(
            "margin",
            "margin-top",
            "margin-right",
            "margin-bottom",
            "margin-left",
            "margin-block",
            "margin-inline",
            "margin-block-start",
            "margin-block-end",
            "margin-inline-start",
            "margin-inline-end");

    static void template(String property, String value, int offset) {
        if (Set.of("anchor-name", "position-anchor", "anchor-scope", "container", "container-name")
                        .contains(property)
                && RuleSyntax.functions(value).stream().anyMatch(f -> "param".equals(f.name()))) {
            throw RuleLibrary.error("U_DYNAMIC_NAME", "Owned names cannot be parameterized", offset);
        }
        // A template hole may supply a fallback, but never the selector portion before its comma.
        for (var function : RuleSyntax.functions(value)) {
            if (anchor(function.name())
                    && RuleSyntax.functions(RuleSyntax.split(function.arguments(), ',')
                                    .get(0)
                                    .text())
                            .stream()
                            .anyMatch(f -> "param".equals(f.name()))) {
                throw RuleLibrary.error("U_DYNAMIC_NAME", "Anchor name and side/size selectors must be static", offset);
            }
        }
    }

    static void function(RuleSyntax.Function function) {
        try {
            arguments(function);
        } catch (RuleSyntax.Syntax e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw error(String.valueOf(e.getMessage()));
        }
    }

    private static void arguments(RuleSyntax.Function function) {
        boolean size = "anchor-size".equals(function.name());
        var arguments = RuleSyntax.split(function.arguments(), ',');
        if (arguments.size() > 2) {
            throw error("Expected one optional anchor fallback");
        }
        var selectors = RuleSyntax.components(arguments.get(0).text());
        if (size && 1 == selectors.size() && 1 == arguments.size()) {
            String value = selectors.get(0);
            var nested = RuleSyntax.function(value);
            boolean numeric = null == nested
                    && !CSSParseHelper.unescapeURL(value).startsWith("--")
                    && !Set.of("width", "height", "block", "inline", "self-block", "self-inline")
                            .contains(value.toLowerCase(Locale.ROOT));
            if (numeric
                    || null != nested && Set.of("calc", "min", "max", "clamp").contains(nested.name())) {
                RuleValues.check("length-percentage", value);
                return;
            }
        }
        if (2 == arguments.size() && selectors.isEmpty()) {
            throw error("Omit the comma when anchor-size() has only a fallback");
        }
        boolean named = false;
        boolean selected = false;
        for (String selector : selectors) {
            String decoded = CSSParseHelper.unescapeURL(selector);
            if (decoded.startsWith("--")) {
                if (named) {
                    throw error("Expected at most one anchor name");
                }
                CssAnchors.checkName(selector);
                named = true;
            } else {
                var keywords = size
                        ? Set.of("width", "height", "block", "inline", "self-block", "self-inline")
                        : Set.of(
                                "inside",
                                "outside",
                                "top",
                                "right",
                                "bottom",
                                "left",
                                "start",
                                "end",
                                "self-start",
                                "self-end",
                                "center");
                boolean percentage = false;
                if (!size) {
                    try {
                        percentage =
                                "percentage".equals(RuleValues.scalar(selector).type());
                    } catch (IllegalArgumentException ignored) {
                        // Other selectors must be one of the fixed CSS keywords below.
                    }
                }
                if (selected || !keywords.contains(decoded.toLowerCase(Locale.ROOT)) && !percentage) {
                    throw error("Expected a static anchor "
                            + (size ? "size keyword" : "side keyword or literal percentage"));
                }
                selected = true;
            }
        }
        if (!size && !selected) {
            throw error("anchor() requires a side");
        }
        if (2 == arguments.size()) {
            RuleValues.check("length-percentage", arguments.get(1).text());
        }
    }

    static void check(String css, Set<String> external) {
        var syntax = new CssSyntax(css, 0);
        var sheet = syntax.parse(CssSyntax.Mode.UTILITY);
        var names = new HashSet<>(external);
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                CssAnchors.read(declaration)
                        .names()
                        .forEach(term -> names.add(CSSParseHelper.unescapeURL(term.getValue())));
                CssAnchors.references(declaration.getExpression())
                        .forEach(term -> names.add(CSSParseHelper.unescapeURL(term.getValue())));
            }
        });
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onUnknownRule(CSSUnknownRule rule) {
                if ("@container".equalsIgnoreCase(rule.getDeclaration())) {
                    RuleConditions.check(syntax.condition(rule).parameters().trim(), "", 0);
                }
            }

            @Override
            public void onDeclaration(CSSDeclaration declaration) {
                String property =
                        CSSParseHelper.unescapeURL(declaration.getProperty()).toLowerCase(Locale.ROOT);
                String value = declaration.getExpression().getAsCSSString(new CSSWriterSettings(false), 0);
                for (var function : RuleSyntax.functions(value)) {
                    if (anchor(function.name())) {
                        boolean inset = INSETS.contains(property);
                        if (!inset
                                && (!"anchor-size".equals(function.name())
                                        || !SIZES.contains(property) && !MARGINS.contains(property))) {
                            throw syntax.invalid(
                                    "U_VALUE_CONTEXT: " + function.name() + "() cannot be used in " + property,
                                    declaration);
                        }
                        try {
                            function(function);
                        } catch (RuleSyntax.Syntax e) {
                            throw syntax.invalid(e.code + ": " + e.getMessage(), declaration);
                        }
                    }
                }
                if (property.startsWith("--") || "initial-value".equals(property)) {
                    var functions = RuleSyntax.functions(value);
                    for (var token : CssLexical.tokens(value, 0, value.length())) {
                        if ((ParserCSS30Constants.DASHED_IDENT == token.kind()
                                        || ParserCSS30Constants.IDENT == token.kind())
                                && names.contains(token.name())
                                && functions.stream()
                                        .noneMatch(f -> "var".equals(f.name())
                                                && f.argumentStart() <= token.start()
                                                && RuleSyntax.clean(value.substring(f.argumentStart(), token.start()))
                                                        .isEmpty())) {
                            throw syntax.invalid(
                                    "U_DYNAMIC_NAME: A custom property cannot carry an anchor name: " + token.name(),
                                    declaration);
                        }
                    }
                }
            }
        });
    }

    static boolean anchor(String name) {
        return "anchor".equals(name) || "anchor-size".equals(name);
    }

    private static RuleSyntax.Syntax error(String message) {
        return RuleLibrary.error("U_ANCHOR_FUNCTION", message, 0);
    }
}
