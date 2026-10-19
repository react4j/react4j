package react4j.css.internal;

import com.google.gson.JsonParser;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberHost;
import com.helger.css.decl.CSSSelectorMemberHostContext;
import com.helger.css.decl.CSSSelectorMemberNot;
import com.helger.css.decl.CSSSelectorMemberPseudoHas;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorMemberSlotted;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.CSSReaderSettings;
import com.helger.css.reader.errorhandler.ThrowingCSSParseErrorHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssResource.SymbolKind;

/** Edits only parsed identifier occurrences; never matches declaration strings or arbitrary text. */
public final class CssRenamer {
    private final Map<String, String> replacements;

    public CssRenamer(CssNames names) {
        replacements = names.entries().stream()
                .filter(entry -> SymbolKind.CLASS == entry.symbol().kind()
                        && !entry.readable().equals(entry.compiled()))
                .collect(Collectors.toMap(CssNames.Entry::readable, CssNames.Entry::compiled));
    }

    private static void selectors(String css, Consumer<CSSSelectorSimpleMember> visitor) {
        var sheet = Objects.requireNonNull(CSSReader.readFromStringReader(
                css, new CSSReaderSettings().setTabSize(1).setCustomErrorHandler(new ThrowingCSSParseErrorHandler())));
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            private void selector(CSSSelector selector) {
                for (var member : selector.getAllMembers()) {
                    if (member instanceof CSSSelector nested) {
                        selector(nested);
                    } else if (member instanceof CSSSelectorSimpleMember simple) {
                        visitor.accept(simple);
                    } else if (member instanceof CSSSelectorMemberNot function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoIs function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoWhere function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberPseudoHas function) {
                        function.getAllSelectors().forEach(this::selector);
                    } else if (member instanceof CSSSelectorMemberHost function) {
                        selector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberHostContext function) {
                        selector(function.getSelector());
                    } else if (member instanceof CSSSelectorMemberSlotted function) {
                        selector(function.getSelector());
                    }
                }
            }

            @Override
            public void onStyleRuleSelector(CSSSelector selector) {
                selector(selector);
            }
        });
    }

    public static Set<String> names(String css) {
        var names = new HashSet<String>();
        selectors(css, simple -> {
            if (simple.isClass() || simple.isHash()) {
                names.add(simple.getValue().substring(1));
            }
        });
        return Set.copyOf(names);
    }

    public Resource rename(Resource resource) {
        if (replacements.isEmpty() || resource.css().isEmpty()) {
            return resource;
        }
        var edits = new ArrayList<SourceMap.Edit>();
        var syntax = new CssSyntax(resource.css(), 0);
        selectors(resource.css(), simple -> {
            if (simple.isClass()) {
                String value = simple.getValue().substring(1);
                String compiled = replacements.get(value);
                if (null != compiled) {
                    var location = Objects.requireNonNull(simple.getSourceLocation());
                    int start = syntax.offset(
                            location.getFirstTokenBeginLineNumber(), location.getFirstTokenBeginColumnNumber());
                    if (!resource.css().startsWith("." + value, start)) {
                        throw new IllegalArgumentException("CSS selector location differs from its parsed token");
                    }
                    edits.add(new SourceMap.Edit(start + 1, start + 1 + value.length(), compiled));
                }
            }
        });
        edits.sort(Comparator.comparingInt(SourceMap.Edit::start));
        var writer = SourceMap.replace(resource.css(), resource.map(), edits);
        return new Resource(
                resource.metadata(),
                writer.css(),
                JsonParser.parseString(writer.map(resource.map().get("file").getAsString()))
                        .getAsJsonObject());
    }
}
