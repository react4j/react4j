package react4j.dom;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

final class DomHost {
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final String MATHML = "http://www.w3.org/1998/Math/MathML";
    private static final String HTML = "http://www.w3.org/1999/xhtml";
    private static final String XLINK = "http://www.w3.org/1999/xlink";
    private static final String XML = "http://www.w3.org/XML/1998/namespace";
    private static final Set<String> BOOLEAN_ATTRIBUTES = Set.of(
            "allowFullScreen",
            "async",
            "autoPlay",
            "controls",
            "default",
            "defer",
            "disabled",
            "formNoValidate",
            "hidden",
            "loop",
            "noModule",
            "noValidate",
            "open",
            "playsInline",
            "readOnly",
            "required",
            "reversed",
            "scoped",
            "seamless",
            "itemScope");
    private static final Set<String> BOOLEAN_STRINGS = Set.of(
            "contentEditable",
            "draggable",
            "spellCheck",
            "focusable",
            "autoReverse",
            "externalResourcesRequired",
            "preserveAlpha",
            "value");
    private static final Set<String> SVG_HYPHENATED = Set.of(
            "accentHeight",
            "alignmentBaseline",
            "arabicForm",
            "baselineShift",
            "capHeight",
            "clipPath",
            "clipRule",
            "colorInterpolation",
            "colorInterpolationFilters",
            "colorProfile",
            "colorRendering",
            "dominantBaseline",
            "enableBackground",
            "fillOpacity",
            "fillRule",
            "floodColor",
            "floodOpacity",
            "fontFamily",
            "fontSize",
            "fontSizeAdjust",
            "fontStretch",
            "fontStyle",
            "fontVariant",
            "fontWeight",
            "glyphName",
            "glyphOrientationHorizontal",
            "glyphOrientationVertical",
            "horizAdvX",
            "horizOriginX",
            "imageRendering",
            "letterSpacing",
            "lightingColor",
            "markerEnd",
            "markerMid",
            "markerStart",
            "overlinePosition",
            "overlineThickness",
            "paintOrder",
            "panose1",
            "pointerEvents",
            "renderingIntent",
            "shapeRendering",
            "stopColor",
            "stopOpacity",
            "strikethroughPosition",
            "strikethroughThickness",
            "strokeDasharray",
            "strokeDashoffset",
            "strokeLinecap",
            "strokeLinejoin",
            "strokeMiterlimit",
            "strokeOpacity",
            "strokeWidth",
            "textAnchor",
            "textDecoration",
            "textRendering",
            "transformOrigin",
            "underlinePosition",
            "underlineThickness",
            "unicodeBidi",
            "unicodeRange",
            "unitsPerEm",
            "vAlphabetic",
            "vHanging",
            "vIdeographic",
            "vMathematical",
            "vectorEffect",
            "vertAdvY",
            "vertOriginX",
            "vertOriginY",
            "wordSpacing",
            "writingMode",
            "xHeight");
    private static final Set<String> UNITLESS_STYLES = Set.of(
            "animationIterationCount",
            "borderImageOutset",
            "borderImageSlice",
            "borderImageWidth",
            "boxFlex",
            "boxFlexGroup",
            "boxOrdinalGroup",
            "columnCount",
            "columns",
            "fillOpacity",
            "flex",
            "flexGrow",
            "flexNegative",
            "flexOrder",
            "flexPositive",
            "flexShrink",
            "floodOpacity",
            "fontWeight",
            "gridArea",
            "gridColumn",
            "gridColumnEnd",
            "gridColumnSpan",
            "gridColumnStart",
            "gridRow",
            "gridRowEnd",
            "gridRowSpan",
            "gridRowStart",
            "lineClamp",
            "lineHeight",
            "opacity",
            "order",
            "orphans",
            "stopOpacity",
            "strokeDasharray",
            "strokeDashoffset",
            "strokeMiterlimit",
            "strokeOpacity",
            "strokeWidth",
            "tabSize",
            "widows",
            "zIndex",
            "zoom");
    private static final Set<String> POSITIVE_ATTRIBUTES = Set.of("cols", "rows", "size", "span");
    private static final Set<String> NUMERIC_ATTRIBUTES = Set.of("rowSpan", "start");
    private static final Set<String> CONTROLLED_TAGS = Set.of("input", "select", "textarea");
    private static final Set<String> RESERVED_HYPHENATED_TAGS = Set.of(
            "annotation-xml",
            "color-profile",
            "font-face",
            "font-face-src",
            "font-face-uri",
            "font-face-format",
            "font-face-name",
            "missing-glyph");
    private static final String ATTRIBUTE_NAME_START =
            ":A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF"
                    + "\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD";
    private static final AttributeName ATTRIBUTE_NAME = new AttributeName("^[" + ATTRIBUTE_NAME_START + "]["
            + ATTRIBUTE_NAME_START + "\\-.0-9\\u00B7\\u0300-\\u036F\\u203F-\\u2040]*$");

    private DomHost() {}

    static String rootNamespace(final Node container) {
        if (9 == container.nodeType) {
            final var root = container.document().documentElement;
            final var namespace = null == root ? null : root.namespaceURI;
            return null == namespace ? HTML : namespace;
        }
        final var node = 8 == container.nodeType ? container.parentNode : container;
        if (null != node && 1 == node.nodeType) {
            final var element = Js.<Element>uncheckedCast(node);
            final var namespace = element.namespaceURI;
            if (null != namespace) {
                final var tag = element.tagName;
                return childNamespace(elementNamespace(namespace, tag), tag);
            }
        }
        return HTML;
    }

    static String elementNamespace(final String parentNamespace, final String tag) {
        if (HTML.equals(parentNamespace)) {
            return switch (tag) {
                case "svg" -> SVG;
                case "math" -> MATHML;
                default -> HTML;
            };
        }
        return parentNamespace;
    }

    static String childNamespace(final String namespace, final String tag) {
        return SVG.equals(namespace) && "foreignObject".equals(tag) ? HTML : namespace;
    }

    static Element createElement(
            final Node container,
            final String namespace,
            final String tag,
            final JsPropertyMap<@Nullable Object> inputs) {
        final var document = container.document();
        if (!HTML.equals(namespace)) {
            return document.createElementNS(namespace, tag);
        }
        if ("script".equals(tag)) {
            final var wrapper = document.createElement("div");
            wrapper.innerHTML = "<script></script>";
            return Js.uncheckedCast(wrapper.removeChild(Objects.requireNonNull(wrapper.firstChild)));
        }
        final var customName = inputs.get("is");
        if ("string".equals(Js.typeof(customName))) {
            return document.createElement(tag, JsPropertyMap.of("is", customName));
        }
        final var element = document.createElement(tag);
        if ("select".equals(tag) && Js.isTruthy(inputs.get("multiple"))) {
            properties(element).set("multiple", true);
        }
        return element;
    }

    static Node createText(final Node container, final String value) {
        return container.document().createTextNode(value);
    }

    static void setTextContent(final Node node, final String text) {
        if (!text.isEmpty()) {
            final var first = node.firstChild;
            if (null != first && 3 == first.nodeType && null == first.nextSibling) {
                first.nodeValue = text;
                return;
            }
        }
        node.textContent = text;
    }

    static DomEvents.State prepareInitial(
            final Element element,
            final String tag,
            final JsPropertyMap<@Nullable Object> inputs,
            @Nullable final String text) {
        final var state = new DomEvents.State(inputs, tag, HTML.equals(element.namespaceURI));
        applyProperties(element, state, null, inputs, text);
        if (isControlTag(tag)) {
            restoreControlled(element, state);
        }
        return state;
    }

    static void attachInitial(
            final Element element,
            final DomEvents.State state,
            final Node boundary,
            final Node rootContainer,
            @Nullable final Element logicalParent) {
        DomEvents.attach(element, state, boundary, rootContainer, logicalParent);
    }

    static void update(
            final Element element,
            final DomEvents.State state,
            final JsPropertyMap<@Nullable Object> next,
            @Nullable final String text) {
        final var previous = state.inputs;
        state.inputs = next;
        applyProperties(element, state, previous, next, text);
    }

    private static void applyProperties(
            final Element element,
            final DomEvents.State state,
            @Nullable final JsPropertyMap<@Nullable Object> previous,
            final JsPropertyMap<@Nullable Object> next,
            @Nullable final String text) {
        final var tag = state.tag;
        final boolean custom = tag.contains("-")
                ? !RESERVED_HYPHENATED_TAGS.contains(tag)
                : "string".equals(Js.typeof(next.get("is")));
        if ("input".equals(tag) && null != previous && null == next.get("checked")) {
            final var previousChecked = previous.get("checked");
            if (null != previousChecked && !Js.isTripleEqual(previousChecked, state.initialChecked)) {
                properties(element).set("checked", Js.isTruthy(state.initialChecked));
            }
        }
        if (null != previous) {
            for (final var name : ownKeys(previous)) {
                if (!hasOwn(next, name)) {
                    setProperty(element, state, name, previous.get(name), null, custom);
                }
            }
        }
        for (final var name : ownKeys(next)) {
            if (null != text && "children".equals(name)) {
                setTextContent(element, text);
                continue;
            }
            final var oldValue = null == previous ? null : previous.get(name);
            final var newValue = next.get(name);
            if (!Js.isTripleEqual(oldValue, newValue)) {
                setProperty(element, state, name, oldValue, newValue, custom);
            }
        }
    }

    static void clear(final Element element) {
        DomEvents.clear(element);
    }

    static boolean hasHtml(final JsPropertyMap<@Nullable Object> inputs) {
        return null != rawHtml(inputs.get("dangerouslySetInnerHTML"));
    }

    @Nullable
    private static Object rawHtml(@Nullable final Object value) {
        return null == value ? null : properties(value).get("__html");
    }

    private static void setProperty(
            final Element element,
            final DomEvents.State state,
            final String name,
            @Nullable final Object previous,
            @Nullable final Object value,
            final boolean custom) {
        switch (name) {
            case "children",
                    "key",
                    "ref",
                    "innerHTML",
                    "autoFocus",
                    "suppressContentEditableWarning",
                    "suppressHydrationWarning" -> {}
            case "style" -> setStyles(element, previous, value);
            case "dangerouslySetInnerHTML" -> {
                final var html = rawHtml(value);
                if (null != html && !Js.isTripleEqual(rawHtml(previous), html)) {
                    element.innerHTML = stringify(html);
                }
            }
            case "checked", "multiple", "muted", "selected" -> {
                if (!"checked".equals(name) || !"input".equals(state.tag)) {
                    if (custom) {
                        setCustomAttribute(element, name, value);
                    } else {
                        properties(element).set(name, Js.isTruthy(value));
                    }
                }
            }
            case "defaultValue", "defaultChecked" -> {}
            case "value" -> {
                if (!CONTROLLED_TAGS.contains(state.tag)) {
                    setAttribute(element, name, value, custom, state.html);
                }
            }
            default -> {
                if (isEventProperty(name)) {
                    if (DomEvents.observeHandler(name, value)) {
                        state.hasHandlers = true;
                    }
                    if (!custom || DomEvents.isRegistered(name)) {
                        break;
                    }
                }
                setAttribute(element, name, value, custom, state.html);
            }
        }
    }

    static boolean isEventProperty(final String name) {
        return name.length() > 2
                && ('o' == name.charAt(0) || 'O' == name.charAt(0))
                && ('n' == name.charAt(1) || 'N' == name.charAt(1));
    }

    private static void setCustomAttribute(final Element element, final String name, @Nullable final Object value) {
        if (!ATTRIBUTE_NAME.test(name)) {
            return;
        }
        final var type = Js.typeof(value);
        if (null == value || "function".equals(type) || "symbol".equals(type)) {
            element.removeAttribute(name);
        } else {
            element.setAttribute(name, stringify(value));
        }
    }

    private static void setAttribute(
            final Element element,
            final String name,
            @Nullable final Object value,
            final boolean custom,
            final boolean html) {
        if (custom) {
            setCustomAttribute(element, name, value);
            return;
        }
        final var attribute =
                switch (name) {
                    case "className" -> "class";
                    case "htmlFor" -> "for";
                    case "acceptCharset" -> "accept-charset";
                    case "httpEquiv" -> "http-equiv";
                    case "tabIndex" -> "tabindex";
                    case "crossOrigin" -> "crossorigin";
                    case "xmlnsXlink" -> "xmlns:xlink";
                    case "xlinkActuate" -> "xlink:actuate";
                    case "xlinkArcrole" -> "xlink:arcrole";
                    case "xlinkHref" -> "xlink:href";
                    case "xlinkRole" -> "xlink:role";
                    case "xlinkShow" -> "xlink:show";
                    case "xlinkTitle" -> "xlink:title";
                    case "xlinkType" -> "xlink:type";
                    case "xmlBase" -> "xml:base";
                    case "xmlLang" -> "xml:lang";
                    case "xmlSpace" -> "xml:space";
                    default ->
                        html ? name.toLowerCase(Locale.ROOT) : SVG_HYPHENATED.contains(name) ? hyphenate(name) : name;
                };
        final var namespace = attribute.startsWith("xlink:") ? XLINK : attribute.startsWith("xml:") ? XML : null;
        final boolean booleanAttribute = BOOLEAN_ATTRIBUTES.contains(name);
        final boolean overloaded = "capture".equals(name) || "download".equals(name);
        final var type = Js.typeof(value);
        if (null == value
                || "function".equals(type)
                || "symbol".equals(type)
                || POSITIVE_ATTRIBUTES.contains(name) && !(1 <= number(value))
                || NUMERIC_ATTRIBUTES.contains(name) && Double.isNaN(number(value))
                || booleanAttribute && !Js.isTruthy(value)
                || "boolean".equals(type)
                        && (overloaded
                                ? !Js.isTruthy(value)
                                : !booleanAttribute
                                        && !BOOLEAN_STRINGS.contains(name)
                                        && !name.regionMatches(true, 0, "aria-", 0, 5)
                                        && !name.regionMatches(true, 0, "data-", 0, 5))) {
            if (null == namespace) {
                element.removeAttribute(attribute);
            } else {
                element.removeAttributeNS(namespace, attribute.substring(attribute.indexOf(':') + 1));
            }
        } else {
            final var text = booleanAttribute || overloaded && Boolean.TRUE.equals(value) ? "" : stringify(value);
            if (null == namespace) {
                element.setAttribute(attribute, text);
            } else {
                element.setAttributeNS(namespace, attribute, text);
            }
        }
    }

    private static void setStyles(final Element element, @Nullable final Object previous, @Nullable final Object next) {
        final var style = element.style;
        final var before = null == previous ? null : properties(previous);
        final var after = null == next ? null : properties(next);
        if (null != before) {
            for (final var name : ownKeys(before)) {
                if (null == after || !hasOwn(after, name)) {
                    style.removeProperty(styleName(name));
                }
            }
        }
        if (null != after) {
            for (final var name : ownKeys(after)) {
                final var value = after.get(name);
                if (null == value || "boolean".equals(Js.typeof(value))) {
                    style.removeProperty(styleName(name));
                } else if (null == before || !Js.isTripleEqual(before.get(name), value)) {
                    final boolean pixels = !name.startsWith("--")
                            && "number".equals(Js.typeof(value))
                            && 0 != Js.asDouble(value)
                            && !UNITLESS_STYLES.contains(unprefixed(name));
                    style.setProperty(styleName(name), stringify(value).trim() + (pixels ? "px" : ""));
                }
            }
        }
    }

    private static String unprefixed(final String name) {
        for (final var prefix : new String[] {"Webkit", "WebKit", "Moz", "ms", "O"}) {
            final int length = prefix.length();
            if (name.startsWith(prefix) && name.length() > length && Character.isUpperCase(name.charAt(length))) {
                return Character.toLowerCase(name.charAt(length)) + name.substring(length + 1);
            }
        }
        return name;
    }

    private static String styleName(final String name) {
        if (name.startsWith("--")) {
            return name;
        }
        if (name.startsWith("WebKit")) {
            return "-webkit" + hyphenate(name.substring(6));
        }
        if ("cssFloat".equals(name)) {
            return "float";
        }
        return name.startsWith("ms") ? "-" + hyphenate(name) : hyphenate(name);
    }

    private static String hyphenate(final String name) {
        final var result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('-').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    static boolean isControlTag(final String tag) {
        return CONTROLLED_TAGS.contains(tag);
    }

    static void restoreControlled(final Element element, final DomEvents.State state) {
        final var tag = state.tag;
        final var inputs = state.inputs;
        final var value = inputs.get("value");
        final var defaultValue = inputs.get("defaultValue");
        final var props = properties(element);
        final boolean mounting = !state.controlInitialized;
        if (mounting && !"select".equals(tag)) {
            trackControl(element, state);
        }
        if ("select".equals(tag)) {
            final boolean multiple = Js.isTruthy(props.get("multiple"));
            if (null != value) {
                selectOptions(element, value, false);
            } else if (mounting || multiple != state.wasMultiple) {
                if (null != defaultValue) {
                    selectOptions(element, defaultValue, true);
                } else if (!mounting) {
                    selectOptions(element, multiple ? new String[0] : "", false);
                }
            }
            state.wasMultiple = multiple;
        } else if ("textarea".equals(tag)) {
            if (mounting) {
                final var initial = textareaInitialValue(inputs);
                if (!initial.isEmpty()) {
                    props.set("value", initial);
                    props.set("defaultValue", initial);
                }
            } else {
                if (null != value && !stringify(value).equals(props.get("value"))) {
                    props.set("value", stringify(value));
                }
                final var text = null != defaultValue ? defaultValue : value;
                if (null != text && !stringify(text).equals(props.get("defaultValue"))) {
                    props.set("defaultValue", stringify(text));
                }
            }
        } else {
            final var type = inputs.get("type");
            final var checked = inputs.get("checked");
            final var defaultChecked = inputs.get("defaultChecked");
            if (mounting) {
                state.initialChecked = null != checked ? checked : defaultChecked;
            }
            if (null != checked || mounting && null != defaultChecked) {
                props.set("checked", Js.isTruthy(null != checked ? checked : defaultChecked));
            }
            if (mounting && (null != checked || null != defaultChecked)) {
                props.set("defaultChecked", Js.isTruthy(null != checked ? checked : defaultChecked));
            } else if (null == checked && null != defaultChecked) {
                props.set("defaultChecked", Js.isTruthy(defaultChecked));
            }
            final var initial = null != value ? value : defaultValue;
            if (mounting) {
                state.controlledInput = null != (isCheckableType(props.get("type")) ? checked : value);
                state.initialValue = null == initial ? "" : stringify(initial);
            }
            if (null == value && ("submit".equals(type) || "reset".equals(type))) {
                element.removeAttribute("value");
            } else if (inputs.has("value") || inputs.has("defaultValue")) {
                final var text = null == initial || !mounting && inputs.has("value") && null == value
                        ? state.initialValue
                        : stringify(initial);
                final var current = props.get("value");
                final boolean sameNumber = !mounting
                        && "number".equals(type)
                        && "number".equals(Js.typeof(value))
                        && !"".equals(current)
                        && number(current) == Js.asDouble(value);
                if ((mounting || null != value) && !text.equals(current) && !sameNumber) {
                    props.set("value", text);
                }
                if ((!"number".equals(type) || element != element.document().activeElement)
                        && !text.equals(props.get("defaultValue"))) {
                    props.set("defaultValue", text);
                }
            }
        }
        state.controlInitialized = true;
    }

    private static String textareaInitialValue(final JsPropertyMap<@Nullable Object> inputs) {
        final var value = inputs.get("value");
        if (null != value) {
            return stringify(value);
        }
        final var defaultValue = inputs.get("defaultValue");
        var children = inputs.get("children");
        if (null == children) {
            return null == defaultValue ? "" : stringify(defaultValue);
        }
        if (null != defaultValue) {
            throw new IllegalArgumentException("A textarea cannot have both defaultValue and children");
        }
        if (isArray(children)) {
            final var array = Js.<@Nullable Object[]>uncheckedCast(children);
            if (1 < array.length) {
                throw new IllegalArgumentException("A textarea supports at most one child");
            }
            children = 0 == array.length ? null : array[0];
        }
        return null == children ? "" : stringify(children);
    }

    static void commitNumberOnBlur(final Element element, final DomEvents.State state) {
        final var props = properties(element);
        if (state.controlledInput && "number".equals(props.get("type"))) {
            props.set("defaultValue", props.get("value"));
        }
    }

    record Focus(
            Document document,
            Element element,
            @Nullable Object selectionStart,
            @Nullable Object selectionEnd,
            @Nullable EditableSelection editableSelection) {
        static @Nullable Focus read(final Document document) {
            final var active = document.activeElement;
            if (null == active || active == document.body) {
                return null;
            }
            final var props = properties(active);
            final var start = props.get("selectionStart");
            final var end = props.get("selectionEnd");
            final var editable = null == start && "true".equals(props.get("contentEditable"))
                    ? EditableSelection.read(document, active)
                    : null;
            return new Focus(document, active, start, end, editable);
        }

        void restore() {
            if (document.activeElement == element || !element.isConnected) {
                return;
            }
            final var props = properties(element);
            if (null != selectionStart && null != selectionEnd && null != props.get("selectionStart")) {
                element.setSelectionRange(Js.asInt(selectionStart), Js.asInt(selectionEnd));
            } else if (null != editableSelection && "true".equals(props.get("contentEditable"))) {
                editableSelection.restore(document, element);
            }
            if ("function".equals(Js.typeof(props.get("focus")))) {
                element.focus(JsPropertyMap.of("preventScroll", true));
            }
        }
    }

    private record EditableSelection(int anchor, int focus) {
        static @Nullable EditableSelection read(final Document document, final Element element) {
            final var selection = Js.<@Nullable Selection>uncheckedCast(document.getSelection());
            if (null == selection) {
                return null;
            }
            final var anchorNode = selection.anchorNode;
            final var focusNode = selection.focusNode;
            if (null == anchorNode
                    || null == focusNode
                    || !element.contains(anchorNode)
                    || !element.contains(focusNode)) {
                return null;
            }
            final var range = document.createRange();
            range.selectNodeContents(element);
            range.setEnd(anchorNode, selection.anchorOffset);
            final int anchor = stringify(range).length();
            range.setEnd(focusNode, selection.focusOffset);
            return new EditableSelection(anchor, stringify(range).length());
        }

        void restore(final Document document, final Element element) {
            final var selection = Js.<@Nullable Selection>uncheckedCast(document.getSelection());
            if (null == selection) {
                return;
            }
            final int length = element.textContent.length();
            final var anchorPoint = TextPoint.at(document, element, Math.min(anchor, length));
            final var focusPoint = TextPoint.at(document, element, Math.min(focus, length));
            if (null != anchorPoint && null != focusPoint) {
                selection.setBaseAndExtent(
                        anchorPoint.node(), anchorPoint.offset(), focusPoint.node(), focusPoint.offset());
            }
        }
    }

    private record TextPoint(Node node, int offset) {
        private static final int SHOW_TEXT = 4;

        static @Nullable TextPoint at(final Document document, final Element element, final int offset) {
            final var walker = document.createTreeWalker(element, SHOW_TEXT);
            int remaining = offset;
            for (var text = walker.nextNode(); null != text; text = walker.nextNode()) {
                final int length = text.nodeValue.length();
                if (remaining <= length) {
                    return new TextPoint(text, remaining);
                }
                remaining -= length;
            }
            return null;
        }
    }

    static void restoreAfterEvent(final Element element) {
        final var state = DomEvents.state(element);
        if (null == state || !state.controlInitialized) {
            return;
        }
        restoreControlled(element, state);
        final var props = properties(element);
        final var name = props.get("name");
        if (!"input".equals(state.tag) || !"radio".equals(props.get("type")) || null == name || "".equals(name)) {
            return;
        }
        Node tree = element;
        for (var parent = tree.parentNode; null != parent; parent = parent.parentNode) {
            tree = parent;
        }
        final var form = props.get("form");
        final var radios = tree.querySelectorAll("input[type=radio]");
        for (int i = 0; i < radios.length; i++) {
            final var radio = Js.<Element>uncheckedCast(radios.item(i));
            final var radioProps = properties(radio);
            if (radio != element
                    && name.equals(radioProps.get("name"))
                    && Js.isTripleEqual(form, radioProps.get("form"))) {
                final var radioState = DomEvents.state(radio);
                if (null != radioState && radioState.controlInitialized) {
                    radioState.lastValue = stringify(controlValue(radio, true));
                    restoreControlled(radio, radioState);
                }
            }
        }
    }

    private static void trackControl(final Element element, final DomEvents.State state) {
        final var property =
                "input".equals(state.tag) && isCheckableType(properties(element).get("type")) ? "checked" : "value";
        final var prototype = getPrototypeOf(element);
        final var descriptor = getOwnPropertyDescriptor(prototype, property);
        if (null != getOwnPropertyDescriptor(element, property) || null == descriptor) {
            return;
        }
        final var getter = Js.<NativeFunction>uncheckedCast(descriptor.get("get"));
        final var setter = Js.<NativeFunction>uncheckedCast(descriptor.get("set"));
        if (!"function".equals(Js.typeof(getter)) || !"function".equals(Js.typeof(setter))) {
            return;
        }
        state.lastValue = stringify(properties(element).get(property));
        state.trackedProperty = property;
        final var tracking = JsPropertyMap.<@Nullable Object>of();
        tracking.set("configurable", true);
        tracking.set("enumerable", descriptor.get("enumerable"));
        tracking.set("get", (ControlGetter) () -> getter.call(element));
        tracking.set("set", (ControlSetter) value -> {
            state.lastValue = stringify(value);
            setter.call(element, value);
        });
        defineProperty(element, property, tracking);
    }

    @JsFunction
    private interface ControlGetter {
        @Nullable
        Object get();
    }

    @JsFunction
    private interface ControlSetter {
        void set(@Nullable Object value);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Function")
    private static class NativeFunction {
        native @Nullable Object call(Object receiver, @Nullable Object... arguments);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.getPrototypeOf")
    private static native Object getPrototypeOf(Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.getOwnPropertyDescriptor")
    private static native @Nullable JsPropertyMap<@Nullable Object> getOwnPropertyDescriptor(Object value, String name);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    static native void defineProperty(Object value, String name, JsPropertyMap<@Nullable Object> descriptor);

    static boolean isCheckableType(@Nullable final Object type) {
        return "checkbox".equals(type) || "radio".equals(type);
    }

    @Nullable
    static Object controlValue(final Element element, final boolean checkable) {
        return properties(element).get(checkable ? "checked" : "value");
    }

    private static void selectOptions(final Element element, final Object value, final boolean setDefault) {
        final var options = element.querySelectorAll("option");
        if (Boolean.TRUE.equals(properties(element).get("multiple"))) {
            final var selected = new HashSet<String>();
            if (isArray(value)) {
                for (final var item : Js.<@Nullable Object[]>uncheckedCast(value)) {
                    if (null != item) {
                        selected.add(stringify(item));
                    }
                }
            } else {
                selected.add(stringify(value));
            }
            for (int i = 0; i < options.length; i++) {
                final var option = properties(options.item(i));
                final var optionValue = option.get("value");
                final boolean matches = null != optionValue && selected.contains(stringify(optionValue));
                option.set("selected", matches);
                if (matches && setDefault) {
                    option.set("defaultSelected", true);
                }
            }
        } else {
            final var desired = stringify(value);
            JsPropertyMap<@Nullable Object> fallback = null;
            for (int i = 0; i < options.length; i++) {
                final var option = properties(options.item(i));
                if (desired.equals(option.get("value"))) {
                    option.set("selected", true);
                    if (setDefault) {
                        option.set("defaultSelected", true);
                    }
                    return;
                }
                if (null == fallback && !Boolean.TRUE.equals(option.get("disabled"))) {
                    fallback = option;
                }
            }
            if (null != fallback) {
                fallback.set("selected", true);
            }
        }
    }

    static JsPropertyMap<@Nullable Object> properties(final Object value) {
        return Js.uncheckedCast(value);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.keys")
    private static native String[] ownKeys(Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.prototype.hasOwnProperty.call")
    private static native boolean hasOwn(Object value, String key);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Number")
    private static native double number(@Nullable Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Array.isArray")
    private static native boolean isArray(Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    static native String stringify(@Nullable Object value);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "RegExp")
    private static class AttributeName {
        AttributeName(final String pattern) {}

        native boolean test(String value);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Node")
    @SuppressWarnings("NullAway.Init")
    static class Node {
        @Nullable
        Node parentNode;

        @Nullable
        Node firstChild;

        @Nullable
        Node nextSibling;

        @Nullable
        Document ownerDocument;

        int nodeType;
        String nodeValue;
        String textContent;

        native Node appendChild(Node node);

        native Node insertBefore(Node node, @Nullable Node reference);

        native Node removeChild(Node node);

        native boolean contains(Node node);

        native NodeList querySelectorAll(String selectors);

        native void addEventListener(String type, DomEvents.Listener listener, boolean capture);

        native void removeEventListener(String type, DomEvents.Listener listener, boolean capture);

        @JsOverlay
        final Document document() {
            return 9 == nodeType ? Js.uncheckedCast(this) : Objects.requireNonNull(ownerDocument);
        }
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Element")
    @SuppressWarnings("NullAway.Init")
    static class Element extends Node {
        String localName;
        String tagName;

        @Nullable
        String namespaceURI;

        String innerHTML;
        Style style;

        native void setAttribute(String name, String value);

        native void removeAttribute(String name);

        native void setAttributeNS(String namespace, String name, String value);

        native void removeAttributeNS(String namespace, String name);

        boolean isConnected;

        native void focus(@Nullable JsPropertyMap<@Nullable Object> options);

        native void setSelectionRange(int start, int end);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Document")
    @SuppressWarnings("NullAway.Init")
    static class Document extends Node {
        @Nullable
        Element activeElement;

        @Nullable
        Element body;

        @Nullable
        Element documentElement;

        native @Nullable JsPropertyMap<@Nullable Object> getSelection();

        native Range createRange();

        native TreeWalker createTreeWalker(Node root, int whatToShow);

        native Element createElement(String name);

        native Element createElement(String name, JsPropertyMap<@Nullable Object> options);

        native Element createElementNS(String namespace, String name);

        native Node createTextNode(String text);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Selection")
    static class Selection {
        @Nullable
        Node anchorNode;

        @Nullable
        Node focusNode;

        int anchorOffset;
        int focusOffset;

        native void setBaseAndExtent(Node anchorNode, int anchorOffset, Node focusNode, int focusOffset);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Range")
    static class Range {
        native void selectNodeContents(Node node);

        native void setEnd(Node node, int offset);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "TreeWalker")
    static class TreeWalker {
        native @Nullable Node nextNode();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "CSSStyleDeclaration")
    static class Style {
        native void setProperty(String name, String value);

        native String removeProperty(String name);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "NodeList")
    static class NodeList {
        int length;

        native Node item(int index);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakMap")
    static class WeakMap<K, V> {
        native @Nullable V get(K key);

        native void set(K key, V value);

        native void delete(K key);
    }
}
