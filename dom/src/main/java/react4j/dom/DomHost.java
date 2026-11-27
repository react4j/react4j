package react4j.dom;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;

final class DomHost {
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final String MATHML = "http://www.w3.org/1998/Math/MathML";
    private static final String HTML = "http://www.w3.org/1999/xhtml";
    private static final String XLINK = "http://www.w3.org/1999/xlink";
    private static final String XML = "http://www.w3.org/XML/1998/namespace";
    private static final JsPropertyMap<AttributeInfo> ATTRIBUTES = buildAttributes();
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
    private static final Set<String> CONTROLLED_TAGS = Set.of("input", "select", "textarea");
    private static final Set<String> VOID_TAGS = Set.of(
            "area",
            "base",
            "br",
            "col",
            "embed",
            "hr",
            "img",
            "input",
            "keygen",
            "link",
            "menuitem",
            "meta",
            "param",
            "source",
            "track",
            "wbr");
    private static final DomEvents.Listener CLICK_TRAP = event -> {};
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
            return Js.uncheckedCast(wrapper.removeChild(requireNode(wrapper.firstChild)));
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

    static Node requireNode(@Nullable final Node node) {
        // DOM nodes can come from another window's constructors.
        if (null == node) {
            throw new NullPointerException();
        }
        return node;
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

    static @Nullable String optionText(@Nullable final Object children) {
        final var type = Js.typeof(children);
        if ("string".equals(type) || "number".equals(type)) {
            return stringify(children);
        }
        final var text = new StringBuilder();
        appendOptionText(text, children);
        return 0 == text.length() ? null : text.toString();
    }

    private static void appendOptionText(final StringBuilder text, @Nullable final Object child) {
        if (null == child) {
            return;
        }
        final var type = Js.typeof(child);
        if ("string".equals(type)
                || "number".equals(type)
                || ReactElement.isElement(child)
                || ReactPortal.isPortal(child)) {
            text.append(stringify(child));
        } else if (isArray(child)) {
            for (final var entry : Js.<@Nullable Object[]>uncheckedCast(child)) {
                appendOptionText(text, entry);
            }
        } else if ("object".equals(type)) {
            throw new IllegalArgumentException("Objects are not valid as a React child");
        }
    }

    static DomEvents.State prepareInitial(
            final Element element,
            final String tag,
            final JsPropertyMap<@Nullable Object> inputs,
            @Nullable final String text) {
        final var state = new DomEvents.State(inputs, tag);
        applyProperties(element, state, null, inputs, text);
        if (isControlTag(tag)) {
            restoreControlled(element, state);
        } else if ("option".equals(tag)) {
            final var value = inputs.get("value");
            if (null != value) {
                element.setAttribute("value", formValue(value));
            }
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
        DomEvents.updateDocumentSubscriptions(state);
    }

    private static void applyProperties(
            final Element element,
            final DomEvents.State state,
            @Nullable final JsPropertyMap<@Nullable Object> previous,
            final JsPropertyMap<@Nullable Object> next,
            @Nullable final String text) {
        final var tag = state.tag;
        if (!CONTROLLED_TAGS.contains(tag)
                && !"option".equals(tag)
                && "function".equals(Js.typeof(next.get("onClick")))
                && (null == previous || !"function".equals(Js.typeof(previous.get("onClick"))))) {
            properties(element).set("onclick", CLICK_TRAP);
        }
        final var custom = isCustom(tag, next);
        if ("input".equals(tag) && null != previous) {
            final var checked = next.get("checked");
            if (null == checked) {
                final var previousChecked = previous.get("checked");
                if (null != previousChecked && !Js.isTripleEqual(previousChecked, state.initialChecked)) {
                    properties(element).set("checked", Js.isTruthy(state.initialChecked));
                }
            } else if ("radio".equals(next.get("type"))
                    && null != next.get("name")
                    && !Js.isTripleEqual(previous.get("name"), next.get("name"))) {
                // A checked radio entering its new group can clear another member's selection.
                properties(element).set("checked", Js.isTruthy(checked));
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

    static void trapPortalClick(final Node container) {
        final var props = properties(container);
        if (Js.isTripleEqual(null, props.get("onclick"))) {
            props.set("onclick", CLICK_TRAP);
        }
    }

    static void validate(
            final String tag, final JsPropertyMap<@Nullable Object> inputs, @Nullable final Supplier<String> stack) {
        final var children = inputs.get("children");
        final var html = inputs.get("dangerouslySetInnerHTML");
        if ((null != children || null != html) && VOID_TAGS.contains(tag)) {
            throw new IllegalArgumentException(
                    tag + " is a void element tag and must neither have `children` nor use `dangerouslySetInnerHTML`."
                            + (null == stack ? "" : stack.get()));
        }
        if (null != html) {
            if (null != children) {
                throw new IllegalArgumentException(
                        "Can only set one of `children` or `props.dangerouslySetInnerHTML`.");
            }
            if (!"object".equals(Js.typeof(html)) || !properties(html).has("__html")) {
                throw new IllegalArgumentException(
                        "`props.dangerouslySetInnerHTML` must be in the form `{__html: ...}`. Please visit"
                                + " https://fb.me/react-invariant-dangerously-set-inner-html for more information.");
            }
        }
        final var style = inputs.get("style");
        if (null != style && !"object".equals(Js.typeof(style))) {
            throw new IllegalArgumentException(
                    "The `style` prop expects a mapping from style properties to values, not a string. "
                            + "For example, style={{marginRight: spacing + 'em'}} when using JSX."
                            + (null == stack ? "" : stack.get()));
        }
    }

    private static final class PropertyWarnings {
        private static final Set<String> WARNED = new HashSet<>();
        private static final Set<String> INVALID_ATTRIBUTES = new HashSet<>();
        private static final Set<String> UNKNOWN_TAGS = new HashSet<>(Set.of("time", "dialog"));
        private static final Set<String> BOOLEAN_PROPERTIES = Set.of("checked", "multiple", "muted", "selected");
        private static final Set<String> CAMEL_CASE = Set.of(
                ("accessKey allowReorder attributeName attributeType autoCapitalize autoComplete autoCorrect autoFocus"
                    + " autoSave baseFrequency baseProfile calcMode cellPadding cellSpacing charSet classID"
                    + " clipPathUnits colSpan contentScriptType contentStyleType contextMenu controlsList crossOrigin"
                    + " dateTime diffuseConstant edgeMode encType filterRes filterUnits formAction formEncType"
                    + " formMethod formTarget frameBorder glyphRef gradientTransform gradientUnits hrefLang inputMode"
                    + " itemID itemProp itemRef itemType kernelMatrix kernelUnitLength keyParams keyPoints keySplines"
                    + " keyTimes keyType lengthAdjust limitingConeAngle marginHeight marginWidth markerHeight"
                    + " markerUnits markerWidth maskContentUnits maskUnits maxLength mediaGroup minLength numOctaves"
                    + " pathLength patternContentUnits patternTransform patternUnits pointsAtX pointsAtY pointsAtZ"
                    + " preserveAspectRatio primitiveUnits radioGroup refX refY referrerPolicy repeatCount repeatDur"
                    + " requiredExtensions requiredFeatures specularConstant specularExponent spreadMethod srcDoc"
                    + " srcLang srcSet startOffset stdDeviation stitchTiles surfaceScale systemLanguage tableValues"
                    + " targetX targetY textLength useMap viewBox viewTarget xChannelSelector yChannelSelector"
                    + " zoomAndPan")
                        .split(" "));
        private static final JsPropertyMap<String> STANDARD_NAMES = standardNames();
        private static boolean invalidOptionChild;
        private static boolean selectedOption;

        private static void warn(
                final String tag, final JsPropertyMap<@Nullable Object> inputs, final Supplier<String> stack) {
            final var invalid = new StringBuilder();
            int count = 0;
            for (final var name : ownKeys(inputs)) {
                if (WARNED.contains(name) || AriaWarnings.applies(name)) {
                    continue;
                }
                final var value = inputs.get(name);
                final var type = Js.typeof(value);
                final var info = ATTRIBUTES.get(name);
                final var lower = name.toLowerCase(Locale.ROOT);
                final var data = lower.startsWith("data-") || lower.startsWith("aria-");
                final var standard = STANDARD_NAMES.get(lower);
                String message = null;
                if (isEventProperty(name)) {
                    message = eventWarning(name, lower);
                } else if ("innerhtml".equals(lower)) {
                    message = "Directly setting property `innerHTML` is not permitted. For more information, lookup"
                            + " documentation on `dangerouslySetInnerHTML`.";
                } else if ("aria".equals(lower)) {
                    message = "The `aria` attribute is reserved for future use in React. Pass individual `aria-`"
                            + " attributes instead.";
                } else if ("is".equals(lower) && null != value && !"string".equals(type)) {
                    message = "Received a `" + type
                            + "` for a string attribute `is`. If this is expected, cast the value to a string.";
                } else if ("number".equals(type) && Double.isNaN(Js.asDouble(value))) {
                    message = "Received NaN for the `" + name
                            + "` attribute. If this is expected, cast the value to a string.";
                } else if (null != standard && !name.equals(standard)) {
                    message = "Invalid DOM property `" + name + "`. Did you mean `" + standard + "`?";
                } else if (reserved(name)) {
                    continue;
                } else if (null == standard && !name.equals(lower)) {
                    message = "React does not recognize the `" + name
                            + "` prop on a DOM element. If you intentionally want"
                            + " it to appear in the DOM as a custom attribute, spell it as lowercase `" + lower
                            + "` instead. If you accidentally passed it from a parent component, remove it from the"
                            + " DOM element.";
                } else if ("boolean".equals(type)
                        && !data
                        && !BOOLEAN_PROPERTIES.contains(name)
                        && (null == info
                                || AttributeType.BOOLEAN != info.type()
                                        && AttributeType.BOOLEAN_STRING != info.type()
                                        && AttributeType.OVERLOADED_BOOLEAN != info.type())) {
                    final var booleanValue = stringify(value);
                    message = "Received `" + booleanValue + "` for a non-boolean attribute `" + name
                            + "`.\n\nIf you want to write it to the DOM, pass a string instead: "
                            + name + "=\"" + booleanValue + "\" or " + name + "={value.toString()}.";
                    if ("false".equals(booleanValue)) {
                        message += "\n\nIf you used to conditionally omit it with " + name
                                + "={condition && value}, pass " + name + "={condition ? value : undefined} instead.";
                    }
                } else if ("function".equals(type) || "symbol".equals(type)) {
                    WARNED.add(name);
                    if (0 != count++) {
                        invalid.append(", ");
                    }
                    invalid.append('`').append(name).append('`');
                } else if ((BOOLEAN_PROPERTIES.contains(name) || null != info && AttributeType.BOOLEAN == info.type())
                        && ("false".equals(value) || "true".equals(value))) {
                    message = "Received the string `" + value + "` for the boolean attribute `" + name + "`. "
                            + ("false".equals(value)
                                    ? "The browser will interpret it as a truthy value."
                                    : "Although this works, it will not work as expected if you pass the string"
                                            + " \"false\".")
                            + " Did you mean " + name + "={" + value + "}?";
                }
                if (null != message) {
                    WARNED.add(name);
                    reportHostWarning(message, stack);
                }
            }
            if (0 != count) {
                final var singular = 1 == count;
                reportHostWarning(
                        (singular ? "Invalid value for prop " : "Invalid values for props ") + invalid + " on <" + tag
                                + "> tag. Either remove " + (singular ? "it" : "them")
                                + " from the element, or pass a string or number value to keep "
                                + (singular ? "it" : "them")
                                + " in the DOM. For details, see https://fb.me/react-attribute-behavior",
                        stack);
            }
        }

        private static JsPropertyMap<String> standardNames() {
            final JsPropertyMap<String> names = Js.uncheckedCast(newAttributeTable(null));
            for (final var name : CAMEL_CASE) {
                names.set(name.toLowerCase(Locale.ROOT), name);
            }
            for (final var name : new String[] {
                "children",
                "style",
                "ref",
                "key",
                "autoFocus",
                "defaultValue",
                "defaultChecked",
                "dangerouslySetInnerHTML",
                "suppressContentEditableWarning",
                "suppressHydrationWarning"
            }) {
                names.set(name.toLowerCase(Locale.ROOT), name);
            }
            ATTRIBUTES.forEach(name -> {
                names.set(name.toLowerCase(Locale.ROOT), name);
                final var info = ATTRIBUTES.get(name);
                if (null != info) {
                    names.set(info.name().toLowerCase(Locale.ROOT), name);
                }
            });
            return names;
        }

        private static @Nullable String eventWarning(String name, String lower) {
            if (DomEvents.isRegistered(name)) {
                return null;
            }
            if ("onfocusin".equals(lower) || "onfocusout".equals(lower)) {
                return "React uses onFocus and onBlur instead of onFocusIn and onFocusOut. All React events are"
                        + " normalized to bubble, so onFocusIn and onFocusOut are not needed/supported by React.";
            }
            final var standard = DomEvents.standardHandler(lower);
            if (null != standard) {
                return "Invalid event handler property `" + name + "`. Did you mean `" + standard + "`?";
            }
            return name.startsWith("on") ? "Unknown event handler property `" + name + "`. It will be ignored." : null;
        }

        private static boolean reserved(String name) {
            return switch (name) {
                case "children",
                        "dangerouslySetInnerHTML",
                        "key",
                        "ref",
                        "style",
                        "autoFocus",
                        "defaultValue",
                        "defaultChecked",
                        "innerHTML",
                        "suppressContentEditableWarning",
                        "suppressHydrationWarning" -> true;
                default -> false;
            };
        }

        private static boolean hasHostChild(@Nullable Object child) {
            if (ReactElement.isElement(child)) {
                return "string"
                        .equals(Js.typeof(Js.<ReactElement>uncheckedCast(child).type()));
            }
            if (null != child && isArray(child)) {
                for (final var entry : Js.<@Nullable Object[]>uncheckedCast(child)) {
                    if (hasHostChild(entry)) {
                        return true;
                    }
                }
            } else if (null != child && "object".equals(Js.typeof(child)) && !ReactPortal.isPortal(child)) {
                throw new IllegalArgumentException("Objects are not valid as a React child");
            }
            return false;
        }
    }

    private static final class AriaWarnings {
        private static final Set<String> NAMES = Set.of(
                ("current details disabled hidden invalid keyshortcuts label roledescription autocomplete checked"
                    + " expanded haspopup level modal multiline multiselectable orientation placeholder pressed"
                    + " readonly required selected sort valuemax valuemin valuenow valuetext atomic busy live relevant"
                    + " dropeffect grabbed activedescendant colcount colindex colspan controls describedby"
                    + " errormessage flowto labelledby owns posinset rowcount rowindex rowspan setsize")
                        .split(" "));
        private static final Set<String> WARNED = new HashSet<>();

        private static boolean applies(String name) {
            return (name.startsWith("aria-") || camelCase(name)) && ATTRIBUTE_NAME.test(name);
        }

        private static boolean camelCase(String name) {
            return name.startsWith("aria") && upperCaseAt(name, 4);
        }

        private static void warn(String tag, JsPropertyMap<@Nullable Object> inputs, Supplier<String> stack) {
            final var invalid = new StringBuilder();
            int count = 0;
            for (final var name : ownKeys(inputs)) {
                if (WARNED.contains(name) || !applies(name)) {
                    continue;
                }
                final var camel = camelCase(name);
                final var suffix = name.substring(camel ? 4 : 5).toLowerCase(Locale.ROOT);
                if (NAMES.contains(suffix)) {
                    final var standard = "aria-" + suffix;
                    if (!name.equals(standard)) {
                        WARNED.add(name);
                        reportHostWarning(
                                (camel ? "Invalid" : "Unknown") + " ARIA attribute `" + name + "`. Did you mean `"
                                        + standard + "`?",
                                stack);
                    }
                } else {
                    WARNED.add(name);
                    if (camel) {
                        reportHostWarning(
                                "Invalid ARIA attribute `" + name
                                        + "`. ARIA attributes follow the pattern aria-* and must be lowercase.",
                                stack);
                    } else {
                        if (0 != count++) {
                            invalid.append(", ");
                        }
                        invalid.append('`').append(name).append('`');
                    }
                }
            }
            if (0 != count) {
                reportHostWarning(
                        (1 == count ? "Invalid aria prop " : "Invalid aria props ") + invalid + " on <" + tag
                                + "> tag. For details, see https://fb.me/invalid-aria-prop",
                        stack);
            }
        }
    }

    private static boolean upperCaseAt(String name, int index) {
        return name.length() > index && name.charAt(index) >= 'A' && name.charAt(index) <= 'Z';
    }

    private static final class StyleWarnings {
        private static final Set<String> NAMES = new HashSet<>();
        private static final Set<String> VALUES = new HashSet<>();
        private static boolean nan;
        private static boolean infinity;

        private static void warn(@Nullable Object style, Supplier<String> stack) {
            if (null == style || !"object".equals(Js.typeof(style))) {
                return;
            }
            final var properties = properties(style);
            for (final var name : ownKeys(properties)) {
                if (name.startsWith("--")) {
                    continue;
                }
                final var value = properties.get(name);
                if (name.contains("-")) {
                    if (NAMES.add(name)) {
                        reportHostWarning(
                                "Unsupported style property " + name + ". Did you mean " + camelize(name) + "?", stack);
                    }
                } else if (badPrefix(name)) {
                    if (NAMES.add(name)) {
                        reportHostWarning(
                                "Unsupported vendor-prefixed style property " + name + ". Did you mean "
                                        + Character.toUpperCase(name.charAt(0)) + name.substring(1) + "?",
                                stack);
                    }
                } else if (value instanceof String text) {
                    final var trimmed = text.stripTrailing();
                    if (trimmed.endsWith(";") && VALUES.add(text)) {
                        reportHostWarning(
                                "Style property values shouldn't contain a semicolon. Try \"" + name + ": "
                                        + trimmed.substring(0, trimmed.length() - 1) + "\" instead.",
                                stack);
                    }
                }
                if ("number".equals(Js.typeof(value))) {
                    final var number = Js.asDouble(value);
                    if (Double.isNaN(number) && !nan) {
                        nan = true;
                        reportHostWarning(
                                "`NaN` is an invalid value for the `" + name + "` css style property.", stack);
                    } else if (Double.isInfinite(number) && !infinity) {
                        infinity = true;
                        reportHostWarning(
                                "`Infinity` is an invalid value for the `" + name + "` css style property.", stack);
                    }
                }
            }
        }

        private static boolean badPrefix(String name) {
            return name.startsWith("webkit") && upperCaseAt(name, 6)
                    || name.startsWith("moz") && upperCaseAt(name, 3)
                    || name.startsWith("o") && upperCaseAt(name, 1);
        }

        private static String camelize(String name) {
            final var result = new StringBuilder();
            for (int i = name.startsWith("-ms-") ? 1 : 0; i < name.length(); i++) {
                final var ch = name.charAt(i);
                result.append('-' == ch && i + 1 < name.length() ? Character.toUpperCase(name.charAt(++i)) : ch);
            }
            return result.toString();
        }
    }

    static void warnProperties(
            final String tag,
            final JsPropertyMap<@Nullable Object> inputs,
            @Nullable final Node host,
            final String owner,
            final Supplier<String> stack) {
        if (!isCustom(tag, inputs)) {
            AriaWarnings.warn(tag, inputs, stack);
            PropertyWarnings.warn(tag, inputs, stack);
        }
        for (final var name : ownKeys(inputs)) {
            if (!ATTRIBUTE_NAME.test(name) && PropertyWarnings.INVALID_ATTRIBUTES.add(name)) {
                reportHostWarning("Invalid attribute name: `" + name + "`", stack);
            }
        }
        final var style = inputs.get("style");
        final var state = null == host ? null : DomEvents.state(Js.uncheckedCast(host));
        if (null == state || !Js.isTripleEqual(state.inputs.get("style"), style)) {
            StyleWarnings.warn(style, stack);
            if (null != style && "object".equals(Js.typeof(style))) {
                freezeStyle(style);
            }
        }
        if (Js.isTruthy(inputs.get("contentEditable"))
                && !Js.isTruthy(inputs.get("suppressContentEditableWarning"))
                && null != inputs.get("children")) {
            reportHostWarning(
                    "A component is `contentEditable` and contains `children` managed by React. It is now your"
                            + " responsibility to guarantee that none of those nodes are unexpectedly modified or"
                            + " duplicated. This is probably not intentional.",
                    stack);
        }
        warnEventProps(inputs, host, stack);
        if (null == host && "option".equals(tag)) {
            if (!PropertyWarnings.invalidOptionChild && PropertyWarnings.hasHostChild(inputs.get("children"))) {
                PropertyWarnings.invalidOptionChild = true;
                reportHostWarning("Only strings and numbers are supported as <option> children.", stack);
            }
            if (!PropertyWarnings.selectedOption && null != inputs.get("selected")) {
                PropertyWarnings.selectedOption = true;
                reportHostWarning(
                        "Use the `defaultValue` or `value` props on <select> instead of setting `selected` on"
                                + " <option>.",
                        stack);
            }
        }
        warnFormProps(tag, inputs, host, owner, stack);
    }

    static void warnTag(
            Element host,
            String namespace,
            String tag,
            JsPropertyMap<@Nullable Object> inputs,
            Supplier<String> stack) {
        if (!HTML.equals(namespace) || isCustom(tag, inputs)) {
            return;
        }
        if (!tag.equals(tag.toLowerCase(Locale.ROOT))) {
            reportHostWarning(
                    "<" + tag + " /> is using incorrect casing. Use PascalCase for React components,"
                            + " or lowercase for HTML elements.",
                    stack);
        }
        if ("[object HTMLUnknownElement]".equals(objectTag(host)) && PropertyWarnings.UNKNOWN_TAGS.add(tag)) {
            reportHostWarning(
                    "The tag <" + tag + "> is unrecognized in this browser. If you meant to render a React"
                            + " component, start its name with an uppercase letter.",
                    stack);
        }
    }

    private static boolean isCustom(String tag, JsPropertyMap<@Nullable Object> inputs) {
        return tag.contains("-")
                ? !RESERVED_HYPHENATED_TAGS.contains(tag)
                : "string".equals(Js.typeof(inputs.get("is")));
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.freeze")
    private static native void freezeStyle(Object style);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.prototype.toString.call")
    private static native String objectTag(Object value);

    private static void warnEventProps(
            final JsPropertyMap<@Nullable Object> inputs, final @Nullable Node host, final Supplier<String> stack) {
        final var previous = null == host ? null : DomEvents.state(Js.uncheckedCast(host));
        for (final var name : ownKeys(inputs)) {
            if (!DomEvents.isRegistered(name)) {
                continue;
            }
            final var handler = inputs.get(name);
            final var type = Js.typeof(handler);
            if (null != handler
                    && !"function".equals(type)
                    && (null == previous || !Js.isTripleEqual(previous.inputs.get(name), handler))) {
                reportHostWarning(
                        Js.isTripleEqual(false, handler)
                                ? "Expected `" + name + "` listener to be a function, instead got `false`.\n\n"
                                        + "If you used to conditionally omit it with " + name
                                        + "={condition && value}, "
                                        + "pass " + name + "={condition ? value : undefined} instead."
                                : DomEvents.invalidListener(name, type),
                        stack);
            }
        }
    }

    private static void reportHostWarning(final String message, final Supplier<String> stack) {
        consoleError("Warning: " + message + stack.get());
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void consoleError(String message);

    static boolean hasHtml(final JsPropertyMap<@Nullable Object> inputs) {
        return null != rawHtml(inputs.get("dangerouslySetInnerHTML"));
    }

    private static void warnFormProps(
            final String tag,
            final JsPropertyMap<@Nullable Object> inputs,
            final @Nullable Node host,
            final String owner,
            final Supplier<String> stack) {
        if (!isControlTag(tag)) {
            return;
        }
        final var value = inputs.get("value");
        final boolean multiple = "select".equals(tag) && Js.isTruthy(inputs.get("multiple"));
        if (Js.isTripleEqual(null, value)) {
            FormWarning.NULL_VALUE.report(
                    "`value` prop on `" + tag + "` should not be null. Consider using "
                            + (multiple ? "an empty array when `multiple` is set to `true`" : "an empty string")
                            + " to clear the component or `undefined` for uncontrolled components.",
                    stack);
        }
        final var type = inputs.get("type");
        final var state = null == host ? null : DomEvents.state(Js.uncheckedCast(host));
        if (null == state) {
            final var checked = inputs.get("checked");
            if (!Js.isTruthy(inputs.get("onChange"))
                    && !Js.isTruthy(inputs.get("readOnly"))
                    && !Js.isTruthy(inputs.get("disabled"))) {
                if (null != value && !FormWarning.READ_ONLY_VALUE_TYPES.contains(stringify(type))) {
                    FormWarning.MISSING_VALUE_HANDLER.missingHandler("value", "defaultValue", stack);
                }
                if (null != checked) {
                    FormWarning.MISSING_CHECKED_HANDLER.missingHandler("checked", "defaultChecked", stack);
                }
            }
            if (hasDefined(inputs, "value") && hasDefined(inputs, "defaultValue")) {
                switch (tag) {
                    case "input" ->
                        FormWarning.INPUT_VALUE_DEFAULT.conflicting(
                                owner + " contains an input of type " + stringify(type)
                                        + " with both value and defaultValue props. ",
                                "Input",
                                "input",
                                "value",
                                "defaultValue",
                                stack);
                    case "textarea" ->
                        FormWarning.TEXTAREA_VALUE_DEFAULT.conflicting(
                                owner + " contains a textarea with both value and defaultValue props. ",
                                "Textarea",
                                "textarea",
                                "value",
                                "defaultValue",
                                stack);
                    default ->
                        FormWarning.SELECT_VALUE_DEFAULT.conflicting(
                                "", "Select", "select", "value", "defaultValue", stack);
                }
            }
            if ("input".equals(tag) && hasDefined(inputs, "checked") && hasDefined(inputs, "defaultChecked")) {
                FormWarning.INPUT_CHECKED_DEFAULT.conflicting(
                        owner + " contains an input of type " + stringify(type)
                                + " with both checked and defaultChecked props. ",
                        "Input",
                        "input",
                        "checked",
                        "defaultChecked",
                        stack);
            }
            if ("select".equals(tag)) {
                for (final var name : FormWarning.SELECT_VALUE_PROPS) {
                    final var supplied = inputs.get(name);
                    if (null != supplied && multiple != isArray(supplied)) {
                        FormWarning.log(
                                "The `" + name + "` prop supplied to <select> must be "
                                        + (multiple
                                                ? "an array if `multiple` is true."
                                                : "a scalar value if `multiple` is false."),
                                stack);
                    }
                }
            }
            if ("textarea".equals(tag) && null == value && null != inputs.get("children")) {
                FormWarning.log(
                        "Use the `defaultValue` or `value` props instead of setting children on <textarea>.", stack);
            }
        } else if ("input".equals(tag)) {
            final boolean controlled = null != (isCheckableType(type) ? inputs.get("checked") : value);
            if (controlled != state.controlledInput) {
                final var before = controlled ? "uncontrolled" : "controlled";
                final var after = controlled ? "controlled" : "uncontrolled";
                (controlled ? FormWarning.UNCONTROLLED_TO_CONTROLLED : FormWarning.CONTROLLED_TO_UNCONTROLLED)
                        .report(
                                "A component is changing " + (controlled ? "an " : "a ") + before + " input of type "
                                        + stringify(type) + " to be " + after
                                        + ". Input elements should not switch from " + before + " to " + after
                                        + " (or vice versa). Decide between using a controlled or uncontrolled input"
                                        + " element for the lifetime of the component. More info:"
                                        + " https://fb.me/react-controlled-components",
                                stack);
            }
        }
    }

    private static boolean hasDefined(final JsPropertyMap<@Nullable Object> inputs, final String name) {
        return !Js.isTripleEqual(Js.undefined(), inputs.get(name));
    }

    private enum FormWarning {
        NULL_VALUE,
        MISSING_VALUE_HANDLER,
        MISSING_CHECKED_HANDLER,
        INPUT_VALUE_DEFAULT,
        INPUT_CHECKED_DEFAULT,
        TEXTAREA_VALUE_DEFAULT,
        SELECT_VALUE_DEFAULT,
        CONTROLLED_TO_UNCONTROLLED,
        UNCONTROLLED_TO_CONTROLLED;

        private static final Set<String> READ_ONLY_VALUE_TYPES =
                Set.of("button", "checkbox", "hidden", "image", "radio", "reset", "submit");
        private static final String[] SELECT_VALUE_PROPS = {"value", "defaultValue"};
        private boolean reported;

        void report(final String message, final Supplier<String> stack) {
            if (!reported) {
                reported = true;
                log(message, stack);
            }
        }

        void missingHandler(final String prop, final String defaultProp, final Supplier<String> stack) {
            report(
                    "Failed prop type: You provided a `" + prop
                            + "` prop to a form field without an `onChange` handler. "
                            + "This will render a read-only field. If the field should be mutable use `" + defaultProp
                            + "`. Otherwise, set either `onChange` or `readOnly`.",
                    stack);
        }

        void conflicting(
                final String prefix,
                final String kind,
                final String tag,
                final String prop,
                final String defaultProp,
                final Supplier<String> stack) {
            report(
                    prefix + kind + " elements must be either controlled or uncontrolled (specify either the " + prop
                            + " prop, or the " + defaultProp
                            + " prop, but not both). Decide between using a controlled or uncontrolled "
                            + tag + ("textarea".equals(tag) ? "" : " element")
                            + " and remove one of these props. More info: https://fb.me/react-controlled-components",
                    stack);
        }

        static void log(final String message, final Supplier<String> stack) {
            reportHostWarning(message, stack);
        }
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
                        setUnknownAttribute(element, name, value, true);
                    } else {
                        properties(element).set(name, Js.isTruthy(value));
                    }
                }
            }
            case "defaultValue", "defaultChecked" -> {}
            case "value" -> {
                if (!CONTROLLED_TAGS.contains(state.tag)) {
                    setAttribute(element, name, value, custom);
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
                setAttribute(element, name, value, custom);
            }
        }
    }

    static boolean isEventProperty(final String name) {
        return name.length() > 2
                && ('o' == name.charAt(0) || 'O' == name.charAt(0))
                && ('n' == name.charAt(1) || 'N' == name.charAt(1));
    }

    private enum AttributeType {
        STRING,
        BOOLEAN_STRING,
        BOOLEAN,
        OVERLOADED_BOOLEAN,
        POSITIVE_NUMERIC,
        NUMERIC
    }

    private record AttributeInfo(String name, @Nullable String namespace, AttributeType type) {}

    private static JsPropertyMap<AttributeInfo> buildAttributes() {
        final var table = newAttributeTable(null);
        table.set("acceptCharset", new AttributeInfo("accept-charset", null, AttributeType.STRING));
        table.set("className", new AttributeInfo("class", null, AttributeType.STRING));
        table.set("htmlFor", new AttributeInfo("for", null, AttributeType.STRING));
        table.set("httpEquiv", new AttributeInfo("http-equiv", null, AttributeType.STRING));
        table.set("tabIndex", new AttributeInfo("tabindex", null, AttributeType.STRING));
        addHtmlAttributes(table, AttributeType.BOOLEAN_STRING, "contentEditable", "draggable", "spellCheck", "value");
        addHtmlAttributes(
                table,
                AttributeType.BOOLEAN,
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
        addHtmlAttributes(table, AttributeType.OVERLOADED_BOOLEAN, "capture", "download");
        addHtmlAttributes(table, AttributeType.POSITIVE_NUMERIC, "cols", "rows", "size", "span");
        addHtmlAttributes(table, AttributeType.NUMERIC, "rowSpan", "start");
        addSvgAttributes(
                table,
                AttributeType.BOOLEAN_STRING,
                null,
                "autoReverse",
                "externalResourcesRequired",
                "focusable",
                "preserveAlpha");
        addSvgAttributes(
                table,
                AttributeType.STRING,
                null,
                "accent-height",
                "alignment-baseline",
                "arabic-form",
                "baseline-shift",
                "cap-height",
                "clip-path",
                "clip-rule",
                "color-interpolation",
                "color-interpolation-filters",
                "color-profile",
                "color-rendering",
                "dominant-baseline",
                "enable-background",
                "fill-opacity",
                "fill-rule",
                "flood-color",
                "flood-opacity",
                "font-family",
                "font-size",
                "font-size-adjust",
                "font-stretch",
                "font-style",
                "font-variant",
                "font-weight",
                "glyph-name",
                "glyph-orientation-horizontal",
                "glyph-orientation-vertical",
                "horiz-adv-x",
                "horiz-origin-x",
                "image-rendering",
                "letter-spacing",
                "lighting-color",
                "marker-end",
                "marker-mid",
                "marker-start",
                "overline-position",
                "overline-thickness",
                "paint-order",
                "panose-1",
                "pointer-events",
                "rendering-intent",
                "shape-rendering",
                "stop-color",
                "stop-opacity",
                "strikethrough-position",
                "strikethrough-thickness",
                "stroke-dasharray",
                "stroke-dashoffset",
                "stroke-linecap",
                "stroke-linejoin",
                "stroke-miterlimit",
                "stroke-opacity",
                "stroke-width",
                "text-anchor",
                "text-decoration",
                "text-rendering",
                "underline-position",
                "underline-thickness",
                "unicode-bidi",
                "unicode-range",
                "units-per-em",
                "v-alphabetic",
                "v-hanging",
                "v-ideographic",
                "v-mathematical",
                "vector-effect",
                "vert-adv-y",
                "vert-origin-x",
                "vert-origin-y",
                "word-spacing",
                "writing-mode",
                "xmlns:xlink",
                "x-height");
        addSvgAttributes(
                table,
                AttributeType.STRING,
                XLINK,
                "xlink:actuate",
                "xlink:arcrole",
                "xlink:href",
                "xlink:role",
                "xlink:show",
                "xlink:title",
                "xlink:type");
        addSvgAttributes(table, AttributeType.STRING, XML, "xml:base", "xml:lang", "xml:space");
        return table;
    }

    private static void addHtmlAttributes(JsPropertyMap<AttributeInfo> table, AttributeType type, String... names) {
        for (final var name : names) {
            table.set(name, new AttributeInfo(name.toLowerCase(Locale.ROOT), null, type));
        }
    }

    private static void addSvgAttributes(
            JsPropertyMap<AttributeInfo> table, AttributeType type, @Nullable String namespace, String... names) {
        for (final var name : names) {
            final var property = new StringBuilder(name.length());
            for (int i = 0; i < name.length(); i++) {
                final var ch = name.charAt(i);
                if (('-' == ch || ':' == ch) && i + 1 < name.length()) {
                    final var next = name.charAt(i + 1);
                    if (next >= 'a' && next <= 'z') {
                        property.append(Character.toUpperCase(next));
                        i++;
                        continue;
                    }
                }
                property.append(ch);
            }
            table.set(property.toString(), new AttributeInfo(name, namespace, type));
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.create")
    private static native JsPropertyMap<AttributeInfo> newAttributeTable(@Nullable Object prototype);

    private static void setUnknownAttribute(Element element, String name, @Nullable Object value, boolean custom) {
        if (!ATTRIBUTE_NAME.test(name)) {
            return;
        }
        final var type = Js.typeof(value);
        if (null == value
                || "function".equals(type)
                || "symbol".equals(type)
                || !custom
                        && "boolean".equals(type)
                        && !name.regionMatches(true, 0, "aria-", 0, 5)
                        && !name.regionMatches(true, 0, "data-", 0, 5)) {
            element.removeAttribute(name);
        } else {
            element.setAttribute(name, stringify(value));
        }
    }

    private static void setAttribute(Element element, String name, @Nullable Object value, boolean custom) {
        final var info = custom ? null : ATTRIBUTES.get(name);
        if (null == info) {
            setUnknownAttribute(element, name, value, custom);
            return;
        }
        final var valueType = Js.typeof(value);
        if (null == value
                || "function".equals(valueType)
                || "symbol".equals(valueType)
                || removeAttributeValue(info.type(), value, valueType)) {
            element.removeAttribute(info.name());
            return;
        }
        final var text = AttributeType.BOOLEAN == info.type()
                        || AttributeType.OVERLOADED_BOOLEAN == info.type() && Js.isTripleEqual(Boolean.TRUE, value)
                ? ""
                : stringify(value);
        final var namespace = info.namespace();
        if (null == namespace) {
            element.setAttribute(info.name(), text);
        } else {
            element.setAttributeNS(namespace, info.name(), text);
        }
    }

    private static boolean removeAttributeValue(AttributeType type, Object value, String valueType) {
        return switch (type) {
            case BOOLEAN -> !Js.isTruthy(value);
            case BOOLEAN_STRING -> false;
            case OVERLOADED_BOOLEAN -> Js.isTripleEqual(Boolean.FALSE, value);
            case POSITIVE_NUMERIC -> "boolean".equals(valueType) || !(1 <= number(value));
            case NUMERIC -> "boolean".equals(valueType) || Double.isNaN(number(value));
            case STRING -> "boolean".equals(valueType);
        };
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
                if (null != value) {
                    final var text = formValue(value);
                    if (!text.equals(props.get("value"))) {
                        props.set("value", text);
                    }
                }
                final var text = null != defaultValue ? defaultValue : value;
                if (null != text) {
                    final var reset = formValue(text);
                    if (!reset.equals(props.get("defaultValue"))) {
                        props.set("defaultValue", reset);
                    }
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
            if (!mounting && null == checked && null != defaultChecked) {
                props.set("defaultChecked", Js.isTruthy(defaultChecked));
            }
            final var initial = null != value ? value : defaultValue;
            if (mounting) {
                state.controlledInput = null != (isCheckableType(props.get("type")) ? checked : value);
                state.initialValue = null == initial ? "" : formValue(initial);
            }
            final boolean button = "submit".equals(type) || "reset".equals(type);
            if (null == value && button) {
                element.removeAttribute("value");
            } else if (inputs.has("value") || inputs.has("defaultValue")) {
                final var text = null == initial || !mounting && inputs.has("value") && null == value
                        ? state.initialValue
                        : formValue(initial);
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
                        && (mounting || !text.equals(props.get("defaultValue")))) {
                    props.set("defaultValue", text);
                }
            }
            if (mounting) {
                final var name = props.get("name");
                // Reflect checkedness only after value, without disturbing a named radio group.
                if (Js.isTruthy(name)) {
                    props.set("name", "");
                }
                props.set("defaultChecked", !Js.isTruthy(props.get("defaultChecked")));
                props.set("defaultChecked", Js.isTruthy(state.initialChecked));
                if (Js.isTruthy(name)) {
                    props.set("name", name);
                }
            }
        }
        state.controlInitialized = true;
    }

    private static String textareaInitialValue(final JsPropertyMap<@Nullable Object> inputs) {
        final var value = inputs.get("value");
        if (null != value) {
            return formValue(value);
        }
        final var defaultValue = inputs.get("defaultValue");
        var children = inputs.get("children");
        if (null == children) {
            return null == defaultValue ? "" : formValue(defaultValue);
        }
        if (null != defaultValue) {
            throw new IllegalArgumentException("A textarea cannot have both defaultValue and children");
        }
        if (isArray(children)) {
            final var array = Js.<@Nullable Object[]>uncheckedCast(children);
            if (1 < array.length) {
                throw new IllegalArgumentException("<textarea> can only have at most one child");
            }
            children = 0 == array.length ? null : array[0];
        }
        return null == children ? "" : formValue(children);
    }

    private static String formValue(final Object value) {
        return switch (Js.typeof(value)) {
            case "function", "symbol" -> "";
            default -> stringify(value);
        };
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
            final var desired = formValue(value);
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
    static native double number(@Nullable Object value);

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
            if (9 == nodeType) {
                return Js.uncheckedCast(this);
            }
            final var document = ownerDocument;
            if (null == document) {
                throw new NullPointerException();
            }
            return document;
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
