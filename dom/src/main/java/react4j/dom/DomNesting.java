package react4j.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.reconciler.ReconcilerHost.HostFiber;

final class DomNesting {
    private static final Set<String> PARAGRAPH_CLOSERS = Set.of(
            "address",
            "article",
            "aside",
            "blockquote",
            "center",
            "details",
            "dialog",
            "dir",
            "div",
            "dl",
            "fieldset",
            "figcaption",
            "figure",
            "footer",
            "header",
            "hgroup",
            "main",
            "menu",
            "nav",
            "ol",
            "p",
            "section",
            "summary",
            "ul",
            "pre",
            "listing",
            "table",
            "hr",
            "xmp",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6");
    private static final Set<String> SPECIAL = Set.of(
            "applet",
            "area",
            "article",
            "aside",
            "base",
            "basefont",
            "bgsound",
            "blockquote",
            "body",
            "br",
            "button",
            "caption",
            "center",
            "col",
            "colgroup",
            "dd",
            "details",
            "dir",
            "dl",
            "dt",
            "embed",
            "fieldset",
            "figcaption",
            "figure",
            "footer",
            "form",
            "frame",
            "frameset",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6",
            "head",
            "header",
            "hgroup",
            "hr",
            "html",
            "iframe",
            "img",
            "input",
            "isindex",
            "li",
            "link",
            "listing",
            "main",
            "marquee",
            "menu",
            "menuitem",
            "meta",
            "nav",
            "noembed",
            "noframes",
            "noscript",
            "object",
            "ol",
            "param",
            "plaintext",
            "pre",
            "script",
            "section",
            "select",
            "source",
            "style",
            "summary",
            "table",
            "tbody",
            "td",
            "template",
            "textarea",
            "tfoot",
            "th",
            "thead",
            "title",
            "tr",
            "track",
            "ul",
            "wbr",
            "xmp");
    private static final Set<String> SCOPE_BOUNDARIES = Set.of(
            "applet",
            "caption",
            "html",
            "table",
            "td",
            "th",
            "marquee",
            "object",
            "template",
            "foreignObject",
            "desc",
            "title");
    private static final Set<String> HEADINGS = Set.of("h1", "h2", "h3", "h4", "h5", "h6");
    private static final Set<String> IMPLIED_END = Set.of("dd", "dt", "li", "option", "optgroup", "p", "rp", "rt");
    private static final Set<String> RESTRICTED = Set.of(
            "body", "caption", "col", "colgroup", "frame", "head", "html", "tbody", "td", "tfoot", "th", "thead", "tr");
    private static final Set<Pattern> WARNED = new HashSet<>();

    private DomNesting() {}

    static void checkBodyContainer(final DomHost.Node container) {
        if (1 == container.nodeType && "BODY".equals(Js.<DomHost.Element>uncheckedCast(container).tagName)) {
            warning("Warning: render(): Rendering components directly into document.body is discouraged, since its "
                    + "children are often manipulated by third-party scripts and browser extensions. This may lead to "
                    + "subtle reconciliation issues. Try rendering into a container element created for your app.");
        }
    }

    static void checkHost(
            final HostFiber fiber,
            final String tag,
            final boolean mounting,
            final @Nullable String text,
            final Supplier<String> stack) {
        final var ancestors = ancestors(fiber);
        if (mounting) {
            check(tag, null, ancestors, stack);
        }
        if (null != text) {
            ancestors.add(0, tag);
            check("#text", text, ancestors, stack);
        }
    }

    static void checkText(final HostFiber fiber, final String text, final Supplier<String> stack) {
        check("#text", text, ancestors(fiber), stack);
    }

    private static List<String> ancestors(final HostFiber fiber) {
        final List<String> tags = new ArrayList<>();
        for (var parent = fiber.parentHost(); null != parent; parent = parent.parentHost()) {
            if (parent.isHostElement()) {
                tags.add(Js.asString(parent.type()));
            } else {
                final var container = Js.<DomHost.Node>uncheckedCast(parent.type());
                final var tag = rootTag(container);
                if (null != tag) {
                    tags.add(tag);
                }
                break;
            }
        }
        return tags;
    }

    private static @Nullable String rootTag(final DomHost.Node node) {
        final var container = 8 == node.nodeType ? node.parentNode : node;
        if (null == container) {
            return null;
        }
        return switch (container.nodeType) {
            case 1 -> Js.<DomHost.Element>uncheckedCast(container).tagName.toLowerCase(Locale.ROOT);
            case 9 -> "#document";
            case 11 -> "#fragment";
            default -> null;
        };
    }

    private static void check(
            final String child,
            final @Nullable String text,
            final List<String> ancestors,
            final Supplier<String> stack) {
        if (ancestors.isEmpty()) {
            return;
        }
        final var parent = ancestors.get(0);
        final boolean invalidParent = !validParent(child, parent);
        final var ancestor = invalidParent ? parent : invalidAncestor(child, ancestors);
        if (null == ancestor) {
            return;
        }
        final var context = stack.get();
        if (!WARNED.add(new Pattern(invalidParent, child, ancestor, context))) {
            return;
        }
        final var whitespace = null != text
                && text.matches("[\\s\\u00a0\\u1680\\u2000-\\u200a\\u2028\\u2029" + "\\u202f\\u205f\\u3000\\ufeff]*");
        final var subject = null == text ? "<" + child + ">" : whitespace ? "Whitespace text nodes" : "Text nodes";
        final var explanation = whitespace
                ? " Make sure you don't have any extra whitespace between tags on each line of your source code."
                : "table".equals(ancestor) && "tr".equals(child)
                        ? " Add a <tbody> to your code to match the DOM tree generated by the browser."
                        : "";
        warning("Warning: validateDOMNesting(...): " + subject + " cannot appear as a "
                + (invalidParent ? "child" : "descendant") + " of <" + ancestor + ">." + explanation + context);
    }

    private static boolean validParent(final String child, final String parent) {
        return switch (parent) {
            case "select" -> "option".equals(child) || "optgroup".equals(child) || "#text".equals(child);
            case "optgroup" -> "option".equals(child) || "#text".equals(child);
            case "option" -> "#text".equals(child);
            case "tr" -> "td".equals(child) || "th".equals(child) || tableSupporting(child);
            case "tbody", "thead", "tfoot" -> "tr".equals(child) || tableSupporting(child);
            case "colgroup" -> "col".equals(child) || "template".equals(child);
            case "table" ->
                switch (child) {
                    case "caption", "colgroup", "tbody", "tfoot", "thead" -> true;
                    default -> tableSupporting(child);
                };
            case "head" ->
                switch (child) {
                    case "base", "basefont", "bgsound", "link", "meta", "title", "noscript", "noframes" -> true;
                    default -> tableSupporting(child);
                };
            case "html" -> "head".equals(child) || "body".equals(child);
            case "#document" -> "html".equals(child);
            default ->
                !RESTRICTED.contains(child)
                        && !(HEADINGS.contains(child) && HEADINGS.contains(parent))
                        && !(("rp".equals(child) || "rt".equals(child)) && IMPLIED_END.contains(parent));
        };
    }

    private static boolean tableSupporting(final String tag) {
        return "style".equals(tag) || "script".equals(tag) || "template".equals(tag);
    }

    private static @Nullable String invalidAncestor(final String child, final List<String> ancestors) {
        if ("form".equals(child) && ancestors.contains("form")) {
            return "form";
        }
        final var match = "form".equals(child) || PARAGRAPH_CLOSERS.contains(child) ? "p" : child;
        for (final var tag : ancestors) {
            switch (match) {
                case "p", "button", "a", "nobr" -> {
                    if (match.equals(tag)) {
                        return tag;
                    }
                    if (SCOPE_BOUNDARIES.contains(tag) || ("p".equals(match) && "button".equals(tag))) {
                        return null;
                    }
                }
                case "li", "dd", "dt" -> {
                    if ("li".equals(match) ? "li".equals(tag) : "dd".equals(tag) || "dt".equals(tag)) {
                        return tag;
                    }
                    if (SPECIAL.contains(tag)) {
                        return null;
                    }
                }
                default -> {
                    return null;
                }
            }
        }
        return null;
    }

    private record Pattern(boolean parent, String child, String ancestor, String context) {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warning(String message);
}
