package react4j.css.example;

import static react4j.webtesting.Checks.arrayEqual;
import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
@JsType
public final class CssBrowserTest {
    private final boolean _optimized;
    private final String _variant;
    private final Browser.Element _container = Browser.append("div");
    private final Browser.Element _style = Browser.appendHead("style");

    public CssBrowserTest(boolean optimized, String variant) {
        _optimized = optimized;
        _variant = variant;
    }

    public BrowserPromise setUp() {
        return stylesheet("styles").thenDo(() -> BrowserExample.render(_container, "#04c", "true"));
    }

    public void tearDown() {
        _container.remove();
        _style.remove();
    }

    private BrowserPromise stylesheet(String name) {
        return Browser.fetchText(
                        "/css/examples/save-button/src/test/js/react4j/css/example/" + _variant + name + ".css")
                .then(value -> {
                    _style.setTextContent(Browser.string(value));
                    return null;
                });
    }

    private Browser.Element node(String id) {
        return Browser.query(_container, "#" + id);
    }

    private Browser.Element symbol(String name) {
        return Browser.query(_container, "[data-symbol=" + name + "]");
    }

    private String css(String id, String property) {
        return Browser.computed(node(id), property);
    }

    public BrowserPromise testPackagedAssetsAtAnApplicationMountAndNestedRoute() {
        var root = _variant.isEmpty()
                ? "/css/examples/save-button/asset_example/"
                : "/css/examples/save-button/src/test/js/react4j/css/example/"
                        + _variant.substring(0, _variant.length() - 1) + "_assets/";
        return Browser.fetchJson(root.substring(0, root.length() - 1) + ".manifest.json")
                .then(inventory -> {
                    Object[] entries = Js.uncheckedCast(Browser.property(Objects.requireNonNull(inventory), "assets"));
                    var expected = new String[] {
                        "card/icon.svg",
                        "card/react4j.png",
                        "card/prepared.svg",
                        "card/background.svg",
                        "Righteous-Regular.ttf"
                    };
                    for (var suffix : expected) {
                        check(
                                Browser.matchesRegex(
                                        "^assets/[A-Za-z0-9_-]{43}\\.(svg|png|ttf)$", assetUrl(entries, suffix)),
                                "Hashed URL: " + suffix);
                    }
                    check(!_optimized == hasAsset(entries, "card/unused.svg"), "Unused asset pruning");
                    return Browser.fetchText(root + "css/assets.css")
                            .then(value -> {
                                String css = Browser.string(value);
                                check(!_optimized == css.contains("opacity:.13"), "Unused utility animation");
                                check(
                                        (_optimized ? 1 : 2) == css.split("@font-face", -1).length - 1,
                                        "Unused utility font face");
                                return null;
                            })
                            .then(ignored -> Browser.fetchText(root + assetUrl(entries, "card/prepared.svg"))
                                    .then(text -> {
                                        check(Browser.string(text).contains("fill=\"#04c\""), "Preprocessed SVG");
                                        return packagedRoute(root, "index.html", entries, expected);
                                    })
                                    .then(value -> packagedRoute(root, "nested/page.html", entries, expected)));
                });
    }

    private BrowserPromise packagedRoute(String root, String route, Object[] entries, String[] expected) {
        var frame = Browser.frame(root + route);
        return Browser.loaded(frame)
                .then(value -> Browser.until(
                        () -> Browser.exists(frame.getContentDocument(), "#card"),
                        10000,
                        20,
                        "Packaged asset application did not render: " + route))
                .then(value -> {
                    var page = frame.getContentDocument();
                    var base = Browser.absoluteUrl(root);
                    var chain = Browser.delay(0);
                    var images = new String[][] {
                        {"icon", "card/icon.svg"}, {"photo", "card/react4j.png"}, {"prepared", "card/prepared.svg"}
                    };
                    for (var pair : images) {
                        chain = chain.then(v -> {
                            var image = Browser.query(page, "#" + pair[0]);
                            return image.decode().thenDo(() -> {
                                check(0 < image.getNaturalWidth(), "Decoded image: " + pair[0]);
                                equal(base + assetUrl(entries, pair[1]), image.getSrc());
                            });
                        });
                    }
                    return chain.then(v -> {
                                var card = Browser.query(page, "#card");
                                check(
                                        Browser.computed(card, "background-image")
                                                .contains(base + assetUrl(entries, "card/background.svg")),
                                        "CSS background asset");
                                var family =
                                        Browser.computed(card, "font-family").split(",")[0];
                                check(!"heading".equals(family), "Owned utility font name");
                                check(
                                        !"none".equals(Browser.computed(card, "animation-name")),
                                        "Utility variable animation");
                                equal("100s", Browser.computed(card, "animation-duration"));
                                return Browser.loadFonts(page, "16px " + family);
                            })
                            .then(fonts -> {
                                Object[] loaded = Js.uncheckedCast(fonts);
                                check(1 == loaded.length, "One font face");
                                equal("loaded", Browser.string(Browser.property(loaded[0], "status")));
                                return Browser.fontsReady(page);
                            })
                            .thenDo(() -> {
                                var requested = Arrays.asList(Browser.resources(page));
                                for (var suffix : expected) {
                                    check(
                                            requested.contains(base + assetUrl(entries, suffix)),
                                            "Requested asset: " + suffix);
                                }
                                if (!_optimized) {
                                    check(
                                            !requested.contains(base + assetUrl(entries, "card/unused.svg")),
                                            "Unused retained asset was not requested");
                                }
                            });
                })
                .finallyDo(frame::remove);
    }

    private static boolean hasAsset(Object[] entries, String suffix) {
        for (var entry : entries) {
            if (Browser.string(Browser.property(entry, "logicalPath")).endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private static String assetUrl(Object[] entries, String suffix) {
        for (var entry : entries) {
            if (Browser.string(Browser.property(entry, "logicalPath")).endsWith(suffix)) {
                return Browser.string(Browser.property(entry, "url"));
            }
        }
        throw new AssertionError("Missing retained asset: " + suffix);
    }

    public BrowserPromise testUtilitiesKeepLibraryOrderAndExplicitGlobalPrecedence() {
        equal("rgb(30, 60, 90)", css("utility", "color"));
        equal("rgb(11, 22, 33)", css("utility-hybrid", "color"));
        equal("rgb(100, 110, 120)", css("utility-second", "color"));
        equal("rgb(180, 20, 30)", css("utility-important", "color"));
        equal("23px", css("utility-reset", "margin-left"));
        equal("13px", css("utility", "padding-left"));
        equal("21px", css("utility-second", "padding-right"));
        equal("15px", css("utility", "margin-right"));
        equal("2px", css("utility", "margin-top"));
        equal("17px", Browser.computed(node("utility"), BrowserExample.propertyOnlyName()));
        check(!_optimized == _style.getTextContent().contains("97px"), "Unused utility property");
        check(
                !_optimized == _style.getTextContent().contains("89px"),
                "Property accessor does not invent class presence");
        return stylesheet("component").thenDo(() -> {
            equal("rgb(11, 22, 33)", css("utility-important", "color"));
            equal("0px", css("utility-reset", "margin-left"));
            equal("6px", css("utility", "padding-left"));
            equal("6px", css("utility-reset", "padding-left"));
            equal("9px", Browser.computed(node("utility-reset"), BrowserExample.utilityBaseProperty()));
            equal("rgb(100, 110, 120)", css("utility-second", "color"));
        });
    }

    public void testClassNamingPolicy() {
        var root = node("success").getClassName().split(" ")[0];
        var control = symbol("control");
        var property = BrowserExample.successProperty();
        var animation = css("direct-animation", "animation-name");
        if (_optimized && !"uncompressed-".equals(_variant)) {
            check(
                    !root.startsWith("jcss-")
                            && !control.getId().startsWith("jcss-")
                            && !property.startsWith("--jcss-")
                            && !animation.startsWith("jcss-"),
                    "Compressed names");
            var prefix = "prefixed-".equals(_variant);
            check(
                    prefix == root.startsWith("app-")
                            && prefix == control.getId().startsWith("app-")
                            && prefix == property.startsWith("--app-")
                            && prefix == animation.startsWith("app-"),
                    "Configured prefix");
        } else {
            check(
                    root.startsWith("jcss-")
                            && control.getId().startsWith("jcss-")
                            && property.startsWith("--jcss-")
                            && animation.startsWith("jcss-"),
                    "Readable names");
        }
    }

    public void testGeneratedBindingsAreSingletons() {
        check(BrowserExample.bindingsAreSingletons(), "Singleton bindings");
    }

    public void testInliningKeepsModifierAndRootWhileDeadValuesLoseTheirCss() {
        equal("rgb(12, 34, 56)", css("modifier-only", "color"));
        equal("7px", css("modifier-only", "border-top-width"));
        for (var text : new String[] {"outline-width:17px", "98,76,54", "--keyframes-unused", "47px"}) {
            check(!_optimized == _style.getTextContent().contains(text), "Dead CSS value: " + text);
        }
        check(!_style.getTextContent().contains("$react4j$css$"), "No intermediate names");
    }

    public void testInterfaceImplementationsKeepIndependentClasses() {
        var button = node("success").getClassName().split(" ");
        var status = node("status").getClassName().split(" ");
        check(!button[0].equals(status[0]) && !button[1].equals(status[1]), "Independent interface implementations");
        equal("rgb(0, 136, 0)", css("success", "background-color"));
        equal("rgb(0, 136, 0)", css("status", "border-top-color"));
        equal("3px", css("status", "border-top-width"));
    }

    public BrowserPromise testSharedKeyframesAreReusedAndUnreferencedDefinitionsAreExcluded() {
        var button = Browser.animations(node("busy-button"));
        var status = Browser.animations(node("busy-status"));
        var name = css("busy-button", "animation-name");
        check(!"none".equals(name), "Shared animation name");
        equal(name, css("busy-status", "animation-name"));
        equal("0.8s", css("busy-button", "animation-duration"));
        equal("0.8s", css("busy-status", "animation-duration"));
        equal("infinite", css("busy-status", "animation-iteration-count"));
        check(1 == button.length && 1 == status.length, "One shared animation per element");
        var animation = button[0];
        animation.pause();
        return animation.getReady().thenDo(() -> {
            animation.setCurrentTime(400);
            var opacity = Double.parseDouble(css("busy-button", "opacity"));
            check(0.4 < opacity && 1 > opacity, "Interpolated animation opacity");
            var names = new ArrayList<String>();
            for (var child : Browser.childRules(componentLayer())) {
                if (child.getCssText().startsWith("@keyframes")) {
                    names.add(child.getName());
                }
            }
            var expected = new ArrayList<>(List.of(
                    name,
                    css("direct-animation", "animation-name"),
                    css("local-animation", "animation-name"),
                    css("accessor-animation", "animation-name")));
            check(4 == new HashSet<>(expected).size(), "Four independent live animations");
            if (!_optimized) {
                expected.add(symbol("equal-names").getId());
            }
            check(
                    (_optimized ? 0 : 1)
                            == names.stream()
                                    .filter(n -> n.endsWith("--keyframes-unused"))
                                    .count(),
                    "Unused keyframe pruning");
            names.removeIf(n -> n.endsWith("--keyframes-unused"));
            names.sort(String::compareTo);
            expected.sort(String::compareTo);
            arrayEqual(expected.toArray(new String[0]), names.toArray(new String[0]));
        });
    }

    public void testEqualReadablePropertyAliasesKeepOnlyTheirOwnProvider() {
        var node = symbol("property-aliases");
        equal(_optimized ? "13px" : "37px", Browser.computed(node, "padding-left"));
        equal(_optimized ? "0px" : "37px", Browser.computed(node, "border-right-width"));
        check(!_optimized == _style.getTextContent().contains("border-right:37px"), "Property alias provider pruning");
    }

    public void testCheckedIdsFragmentsAndJavaOnlyResourceBindings() {
        var control = symbol("control");
        var filter = Browser.query(_container, "filter");
        var global = symbol("global");
        check(!"control".equals(control.getId()), "Bound ID");
        equal("5px", Browser.computed(control, "border-bottom-width"));
        check(Browser.computed(control, "filter").contains("#" + filter.getId()), "Bound fragment");
        check(filter == Browser.byId(filter.getId()), "Fragment target");
        equal("9px", Browser.computed(global, "letter-spacing"));
        equal("direct-global", global.getId());
        equal("9px", Browser.inlineStyle(global, "--spacing"));
        for (var id : new String[] {"direct-animation", "local-animation", "accessor-animation"}) {
            check(1 == Browser.animations(node(id)).length, "Java-only animation: " + id);
            equal(Browser.inlineStyle(node(id), "animation-name"), css(id, "animation-name"));
        }
    }

    public void testEqualGetterTextPreservesBindingKindAndLiveness() {
        var node = symbol("equal-names");
        equal("jcss-react4j-css-example-BrowserExample_0024_EqualNamePulse--keyframes", node.getId());
        equal("11px", Browser.computed(node, "border-top-width"));
        check(!_optimized == _style.getTextContent().contains("opacity:0.12345"), "Equal getter binding liveness");
    }

    public void testEqualImportedIdsKeepOnlyTheLiveProviderChain() {
        var node = symbol("shared-import");
        equal("shared-import", node.getId());
        var properties = new String[][] {
            {"padding-left", "13px"},
            {"margin-left", "19px"},
            {"border-left-width", "23px"},
            {"margin-right", "29px"},
            {"font-weight", "700"},
            {"padding-right", _optimized ? "0px" : "37px"}
        };
        for (var pair : properties) {
            equal(pair[1], Browser.computed(node, pair[0]));
        }
        check(!_optimized == _style.getTextContent().contains("padding-right:37px"), "Imported ID provider pruning");
    }

    public BrowserPromise testExplicitGlobalSelectionAndDependencyFirstCascade() {
        equal("3px", css("base", "border-top-width"));
        equal("rgb(153, 0, 153)", Browser.computed(symbol("control"), "background-color"));
        excludedGlobal();
        return stylesheet("alternate").thenDo(() -> {
            equal("4px", css("base", "border-top-width"));
            equal("rgb(0, 153, 153)", Browser.computed(symbol("control"), "background-color"));
            excludedGlobal();
            equal("rgb(0, 136, 0)", css("success", "background-color"));
        });
    }

    public BrowserPromise testComponentImportsRetainGlobalsWithoutExplicitRoots() {
        return stylesheet("component").thenDo(() -> {
            equal("2px", css("base", "border-top-width"));
            equal("rgb(0, 136, 0)", Browser.computed(symbol("control"), "background-color"));
            excludedGlobal();
            equal("rgb(255, 255, 255)", css("base", "color"));
            equal("rgb(0, 136, 0)", css("success", "background-color"));
        });
    }

    private void excludedGlobal() {
        equal("", css("base", "--jcss-react4j-css-example-BrowserExample_0024_UnusedGlobals--excluded"));
    }

    public void testCheckedPeerSelectorFollowsFocus() {
        var button = node("peer-button");
        equal("rgb(255, 255, 255)", css("peer-label", "color"));
        button.focus();
        check(button.matches(":focus-visible"), "Peer focus");
        equal("rgb(0, 136, 0)", css("peer-label", "color"));
        equal("rgb(255, 255, 255)", css("busy-status", "color"));
        button.blur();
        equal("rgb(255, 255, 255)", css("peer-label", "color"));
    }

    public void testGeneratedBindingsAndCascade() {
        equal("rgb(255, 255, 255)", css("base", "color"));
        equal("rgb(0, 136, 0)", css("success", "background-color"));
        equal("rgb(170, 0, 0)", css("cancel", "background-color"));
        equal("dotted", css("cancel", "border-top-style"));
        equal("rgb(0, 68, 204)", css("custom", "background-color"));
        equal("0.5", css("disabled", "opacity"));
        equal("rgb(170, 0, 0)", css("conflict", "background-color"));
        var classes = node("conflict").getClassName().split(" ");
        Collections.reverse(Arrays.asList(classes));
        node("conflict").setClassName(String.join(" ", classes));
        equal("rgb(170, 0, 0)", css("conflict", "background-color"));
        equal("rgb(51, 51, 51)", css("nested", "background-color"));
        equal("rgb(0, 136, 0)", css("status", "border-top-color"));
    }

    public void testTypedPropertiesUpdateAndRemoveValues() {
        var custom = node("custom");
        var base = node("base");
        equal("#04c", Browser.inlineStyle(custom, BrowserExample.successProperty()));
        equal("true", base.getAttribute("data-shared"));
        BrowserExample.render(_container, "#a0c", "false");
        check(custom == node("custom") && base == node("base"), "DOM identity on update");
        equal("#a0c", Browser.inlineStyle(custom, BrowserExample.successProperty()));
        equal("rgb(170, 0, 204)", css("custom", "background-color"));
        equal("false", base.getAttribute("data-shared"));
        BrowserExample.render(_container, null, null);
        check(custom == node("custom") && base == node("base"), "DOM identity on removal");
        equal("", Browser.inlineStyle(custom, BrowserExample.successProperty()));
        equal("rgb(0, 136, 0)", css("custom", "background-color"));
        check(
                !base.hasAttribute("data-shared") && !node("nested").hasAttribute("data-shared"),
                "Null data attribute removal");
        BrowserExample.render(_container, "", "");
        equal("", Browser.inlineStyle(custom, BrowserExample.successProperty()));
        check(base.hasAttribute("data-shared"), "Empty data attribute retained");
        equal("", base.getAttribute("data-shared"));
    }

    public void testFocusAndIndependentFragmentReordering() {
        var button = node("base");
        button.focus();
        check(Browser.hasFocus() && button.matches(":focus-visible"), "Document and button focus");
        equal("2px", css("base", "outline-width"));
        equal("rgb(0, 102, 255)", css("base", "outline-color"));
        var before = snapshot();
        var layer = componentLayer();
        var groups = new LinkedHashMap<String, List<String>>();
        var animations = new ArrayList<String>();
        for (var rule : Browser.childRules(layer)) {
            var text = rule.getCssText();
            if (text.startsWith("@keyframes")) {
                animations.add(text);
                continue;
            }
            var match = Browser.match("\\.([A-Za-z0-9_-]+)", text);
            if (null == match) {
                throw new AssertionError("Expected a scoped component selector");
            }
            var owner = match[1].split("--")[0];
            groups.computeIfAbsent(owner, key -> new ArrayList<>()).add(text);
        }
        check((_optimized ? 5 : 6) == groups.size(), "Component group count");
        while (0 < Browser.childRules(layer).length) {
            layer.deleteRule(0);
        }
        for (var animation : animations) {
            layer.insertRule(animation, Browser.childRules(layer).length);
        }
        var ordered = new ArrayList<>(groups.values());
        for (int i = ordered.size() - 1; 0 <= i; i--) {
            for (var rule : ordered.get(i)) {
                layer.insertRule(rule, Browser.childRules(layer).length);
            }
        }
        arrayEqual(before, snapshot());
        equal("2px", css("base", "outline-width"));
    }

    private Browser.Rule componentLayer() {
        for (var rule : Browser.rules(_style)) {
            if (rule.getCssText().startsWith("@layer components {")) {
                return rule;
            }
        }
        throw new AssertionError("Missing component layer");
    }

    private String[] snapshot() {
        var ids = new String[] {
            "base",
            "success",
            "cancel",
            "custom",
            "disabled",
            "conflict",
            "nested",
            "status",
            "utility",
            "utility-hybrid",
            "utility-second",
            "utility-important"
        };
        var result = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            result[i] = String.join(
                    "|",
                    css(ids[i], "color"),
                    css(ids[i], "background-color"),
                    css(ids[i], "border"),
                    css(ids[i], "opacity"));
        }
        return result;
    }
}
