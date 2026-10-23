package react4j.css.bundler;

import com.google.gson.JsonParser;
import com.helger.css.ICSSSourceLocationAware;
import com.helger.css.decl.AbstractHasTopLevelRules;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSFontFaceRule;
import com.helger.css.decl.CSSKeyframesRule;
import com.helger.css.decl.CSSLayerRule;
import com.helger.css.decl.CSSPropertyRule;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorMemberPseudoIs;
import com.helger.css.decl.CSSSelectorMemberPseudoWhere;
import com.helger.css.decl.CSSSelectorSimpleMember;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSUnknownRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.DefaultCSSVisitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import react4j.css.internal.CssAnimationValues;
import react4j.css.internal.CssCatalogue.Resource;
import react4j.css.internal.CssFontValues;
import react4j.css.internal.CssResource;
import react4j.css.internal.CssSelectors;
import react4j.css.internal.CssSyntax;
import react4j.css.internal.SourceMap;

/** Proves impossible selectors and copies surviving bytes with their original mappings. */
final class CssPruner {
    private final Resource resource;
    private final Set<String> known;
    private final Set<String> present;
    private final Set<String> managedAnimations = new HashSet<>();
    private final Set<String> managedFonts = new HashSet<>();
    private final Set<String> managedProperties = new HashSet<>();
    private final Set<String> managedClasses = new HashSet<>();
    private final CascadingStyleSheet sheet;
    private final List<Integer> lines = new ArrayList<>();
    private final List<SourceMap.Span> removed = new ArrayList<>();

    CssPruner(Resource resource, Set<String> known, Set<String> present) {
        this.resource = resource;
        this.known = known;
        this.present = present;
        known.stream()
                .filter(name -> name.startsWith("."))
                .map(name -> name.substring(1))
                .forEach(managedClasses::add);
        for (var symbol : resource.metadata().symbols()) {
            if (CssResource.SymbolKind.ANIMATION == symbol.kind()) {
                managedAnimations.add(symbol.value());
            }
            if (CssResource.SymbolKind.FONT == symbol.kind()) {
                managedFonts.add(CssFontValues.canonical(symbol.value()));
            }
            if (CssResource.SymbolKind.PROPERTY == symbol.kind()) {
                managedProperties.add(symbol.value());
            }
        }
        for (var occurrence : resource.metadata().occurrences()) {
            if (CssResource.SymbolKind.PROPERTY == occurrence.symbol().kind()) {
                managedProperties.add(resource.css().substring(occurrence.start(), occurrence.end()));
            }
        }
        sheet = new CssSyntax(resource.css(), 0).parse(CssSyntax.Mode.PROCESSED);
        lines.add(0);
        for (int i = 0; i < resource.css().length(); i++) {
            if ('\n' == resource.css().charAt(i)) {
                lines.add(i + 1);
            }
        }
    }

    Set<String> animations() {
        var names = new HashSet<String>();
        CSSVisitor.visitCSS(sheet, new DefaultCSSVisitor() {
            @Override
            public void onBeginKeyframesRule(CSSKeyframesRule frames) {
                if (CssResource.Kind.UTILITY != resource.metadata().kind()
                        || managedAnimations.contains(CssAnimationValues.name(frames.getAnimationName()))) {
                    names.add(CssAnimationValues.name(frames.getAnimationName()));
                }
            }
        });
        return Set.copyOf(names);
    }

    Resource prune(Set<String> animations, Set<String> fonts, Set<String> properties) {
        removed.clear();
        rules(sheet.getAllRules(), animations, fonts, properties);
        var spans = new ArrayList<SourceMap.Span>();
        int offset = 0;
        for (var span : removed.stream()
                .sorted(Comparator.comparingInt(SourceMap.Span::start))
                .toList()) {
            if (span.start() > offset) {
                spans.add(new SourceMap.Span(offset, span.start()));
            }
            offset = Math.max(offset, span.end());
        }
        spans.add(new SourceMap.Span(offset, resource.css().length()));
        var output = SourceMap.project(resource.css(), resource.map(), spans);
        return new Resource(
                resource.metadata().project(spans),
                output.css(),
                JsonParser.parseString(output.map(resource.map().get("file").getAsString()))
                        .getAsJsonObject());
    }

    private boolean rules(List<?> rules, Set<String> animations, Set<String> fonts, Set<String> properties) {
        boolean retained = false;
        for (var rule : rules) {
            if (rule instanceof CSSStyleRule style) {
                // Native nesting inherits maximum parent-list specificity and can negate &.
                if (!style.hasRules()) {
                    if (style.getAllSelectors().stream().noneMatch(this::possible)) {
                        remove(style);
                        continue;
                    }
                    selectors(style.getAllSelectors());
                }
                if (CssResource.Kind.UTILITY == resource.metadata().kind()
                        && style.getAllSelectors().stream()
                                .noneMatch(selector ->
                                        possible(selector) && CssSelectors.requiresClass(selector, managedClasses))
                        && !declarations(style.getAllDeclarations(), properties)
                        && !style.hasRules()) {
                    remove(style);
                    continue;
                }
                rules(style.getAllRules(), animations, fonts, properties);
            } else if (rule instanceof CSSKeyframesRule frames) {
                if ((CssResource.Kind.UTILITY != resource.metadata().kind()
                                || managedAnimations.contains(CssAnimationValues.name(frames.getAnimationName())))
                        && !animations.contains(CssAnimationValues.name(frames.getAnimationName()))) {
                    remove(frames);
                    continue;
                }
            } else if (rule instanceof CSSFontFaceRule face) {
                if ((CssResource.Kind.UTILITY != resource.metadata().kind()
                                || managedFonts.contains(CssFontValues.face(face)))
                        && !fonts.contains(CssFontValues.face(face))) {
                    remove(face);
                    continue;
                }
            } else if (rule instanceof CSSUnknownRule conditional) {
                var parsed = Objects.requireNonNull(conditional.getParsedRules());
                if (!rules(parsed.getAllRules(), animations, fonts, properties)) {
                    remove(conditional);
                    continue;
                }
            } else if (rule instanceof CSSPropertyRule registration) {
                if (CssResource.Kind.UTILITY == resource.metadata().kind()
                        && managedProperties.contains(registration.getIdentifier())
                        && !properties.contains(registration.getIdentifier())) {
                    remove(registration);
                    continue;
                }
            } else if (rule instanceof AbstractHasTopLevelRules nested) {
                if (!rules(nested.getAllRules(), animations, fonts, properties) && !(rule instanceof CSSLayerRule)) {
                    remove((ICSSSourceLocationAware) rule);
                    continue;
                }
            }
            retained = true;
        }
        return retained;
    }

    private boolean declarations(List<CSSDeclaration> declarations, Set<String> properties) {
        boolean retained = false;
        for (var declaration : declarations) {
            if (managedProperties.contains(declaration.getProperty())
                    && !properties.contains(declaration.getProperty())) {
                remove(declaration);
            } else {
                retained = true;
            }
        }
        return retained;
    }

    private void selectors(List<CSSSelector> selectors) {
        int i = 0;
        while (i < selectors.size()) {
            if (possible(selectors.get(i))) {
                i++;
                continue;
            }
            int first = i;
            while (i < selectors.size() && !possible(selectors.get(i))) {
                i++;
            }
            // Only top-level list branches are deleted. Function arguments retain their specificity.
            removed.add(
                    i < selectors.size()
                            ? new SourceMap.Span(start(selectors.get(first)), start(selectors.get(i)))
                            : new SourceMap.Span(end(selectors.get(first - 1)), end(selectors.get(i - 1))));
        }
    }

    private boolean possible(CSSSelector selector) {
        for (var member : selector.getAllMembers()) {
            if (member instanceof CSSSelectorSimpleMember simple && (simple.isClass() || simple.isHash())) {
                String name = simple.getValue();
                if (known.contains(name) && !present.contains(name)) {
                    return false;
                }
            } else if (member instanceof CSSSelector nested && !possible(nested)) {
                return false;
            } else if (member instanceof CSSSelectorMemberPseudoIs function
                    && function.getAllSelectors().stream().noneMatch(this::possible)) {
                return false;
            } else if (member instanceof CSSSelectorMemberPseudoWhere function
                    && function.getAllSelectors().stream().noneMatch(this::possible)) {
                return false;
            }
            // Negation and unrecognized functions are uncertain; preserve their spelling and specificity.
        }
        return true;
    }

    private void remove(ICSSSourceLocationAware node) {
        removed.add(new SourceMap.Span(start(node), end(node)));
    }

    private int start(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return lines.get(location.getFirstTokenBeginLineNumber() - 1) + location.getFirstTokenBeginColumnNumber() - 1;
    }

    private int end(ICSSSourceLocationAware node) {
        var location = Objects.requireNonNull(node.getSourceLocation());
        return lines.get(location.getLastTokenEndLineNumber() - 1) + location.getLastTokenEndColumnNumber();
    }
}
