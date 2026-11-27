package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.dom.events.ReactEventHandler;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class FormsBrowserTest {
    private static final ReactEventHandler IGNORE = event -> {};
    private static final ViewConstructorFunction CONTROLLED = Controlled::new;

    private FormsBrowserTest() {}

    @JsMethod
    public static void optionChildren(boolean production) {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(host("option", "children", new Object[] {Browser.number(1), " ", "foo"}), root);
            equal("1 foo", html(node(root)));
            reset(root);
            final var mixed = host(
                    "option", "children", new Object[] {Browser.number(1), " ", DOM.div(), " ", Browser.number(2)});
            final var warnings = captureWarnings(() -> ReactDOM.render(mixed, root));
            if (!production) {
                hasWarning(warnings, "Only strings and numbers are supported as <option> children.");
                hasWarning(warnings, "\n    in option");
            }
            equal("1 [object Object] 2", html(node(root)));
            check(
                    0 == captureWarnings(() -> ReactDOM.render(mixed, root)).length,
                    "Unsupported option children are reported once");

            final var ignored = host("option", "children", new @Nullable Object[] {
                Browser.number(1), " ", false, true, null, Js.undefined(), " ", Browser.number(2)
            });
            check(
                    0 == captureWarnings(() -> ReactDOM.render(ignored, root)).length,
                    "Nullish and boolean option children do not warn");
            equal("1  2", html(node(root)));

            final var object = JsPropertyMap.of();
            for (final var invalid : new Object[] {
                object,
                new Object[] {object},
                new Object[] {object, DOM.span()},
                new Object[] {"1", object, Browser.number(2)}
            }) {
                fails(
                        root,
                        host("option", "children", invalid),
                        "Objects are not valid as a React child",
                        31,
                        production);
            }

            final var shaped = Js.<ReactElement>uncheckedCast(JsPropertyMap.of(
                    "$$typeof",
                    symbolFor("react.element"),
                    "type",
                    (Stringifier) () -> "unused",
                    "props",
                    JsPropertyMap.of()));
            properties(shaped).set("toString", (Stringifier) () -> "hello");
            for (final var child : new Object[] {shaped, new Object[] {shaped}}) {
                ReactDOM.render(host("option", "children", child), root);
                equal("hello", html(node(root)));
            }
            ReactDOM.render(host("option", "children", new Object[] {shaped, DOM.span()}), root);
            equal("hello[object Object]", html(node(root)));
            ReactDOM.render(host("option", "children", new Object[] {"1", shaped, Browser.number(2)}), root);
            equal("1hello2", html(node(root)));
            ReactDOM.render(host("option", "dangerouslySetInnerHTML", JsPropertyMap.of("__html", "foobar")), root);
            equal("foobar", html(node(root)));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void optionValues() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(host("option", "value", ""), root);
            final var option = node(root);
            attribute(option, "value", "");
            ReactDOM.render(host("option", "value", "lava"), root);
            attribute(option, "value", "lava");
            final var children = ReactNode.of(
                    host("option", "children", "monkey"),
                    host("option", "children", new String[] {"gir", "a", "ffe"}),
                    host("option", "children", new String[] {"gorill", "a"}));
            ReactDOM.render(select("value", "giraffe", false, children), root);
            final var select = node(root);
            numberProperty(select, "selectedIndex", 1);
            ReactDOM.render(select("value", "gorilla", false, children), root);
            numberProperty(select, "selectedIndex", 2);
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void selectDefaults() {
        final var root = Browser.append("div");
        try {
            final var animals = choices("monkey", "giraffe", "gorilla");
            ReactDOM.render(select("defaultValue", "giraffe", false, animals), root);
            final var single = node(root);
            equal("giraffe", single.getValue());
            ReactDOM.render(select("defaultValue", "gorilla", false, animals), root);
            equal("giraffe", single.getValue());
            properties(single).set("value", "monkey");
            ReactDOM.render(select("defaultValue", "giraffe", false, animals), root);
            equal("monkey", single.getValue());
            reset(root);
            ReactDOM.render(select("defaultValue", "dummy", false, null), root);
            equal("", node(root).getValue());

            reset(root);
            final var twoAnimals = new String[] {"giraffe", "gorilla"};
            ReactDOM.render(select("defaultValue", twoAnimals, true, animals), root);
            final var multiple = node(root);
            selections(multiple, false, true, true);
            ReactDOM.render(select("defaultValue", new String[] {"monkey"}, true, animals), root);
            selections(multiple, false, true, true);

            reset(root);
            final var disabled = host("option", "children", "disabled").input("disabled", true);
            final var otherDisabled =
                    host("option", "children", "still disabled").input("disabled", true);
            ReactDOM.render(
                    select(
                            "defaultValue",
                            "",
                            false,
                            ReactNode.of(disabled, otherDisabled, host("option", "children", "0"), disabled.dup())),
                    root);
            selections(node(root), false, false, true, false);

            reset(root);
            ReactDOM.render(select("defaultValue", "giraffe", false, animals), root);
            selections(node(root), false, true, false);
            ReactDOM.render(select("defaultValue", twoAnimals, true, animals), root);
            selections(node(root), false, true, true);
            ReactDOM.render(select("defaultValue", "gorilla", false, animals), root);
            selections(node(root), false, false, true);

            reset(root);
            ReactDOM.render(select("defaultValue", new String[] {"giraffe"}, true, animals), root);
            selections(node(root), false, true, false);
            ReactDOM.render(select("defaultValue", new String[] {"giraffe"}, true, choices("monkey", "gorilla")), root);
            selections(node(root), false, false);
            ReactDOM.render(select("defaultValue", new String[] {"giraffe"}, true, animals), root);
            selections(node(root), false, false, false);

            reset(root);
            ReactDOM.render(select("defaultValue", Js.undefined(), true, choices("a", "b", "c")), root);
            selections(node(root), false, false, false);
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void selectControlled() {
        final var root = Browser.append("div");
        try {
            for (final var middle : new String[] {"giraffe", "__proto__"}) {
                final var options = choices("monkey", middle, "gorilla");
                ReactDOM.render(select("value", middle, false, options), root);
                final var control = node(root);
                equal(middle, control.getValue());
                ReactDOM.render(select("value", "gorilla", false, options), root);
                equal("gorilla", control.getValue());
                ReactDOM.render(select("value", new String[] {middle, "gorilla"}, true, options), root);
                selections(control, false, true, true);
                ReactDOM.render(select("value", new String[] {"monkey"}, true, options), root);
                selections(control, true, false, false);
                reset(root);
            }
            ReactDOM.render(select("value", "dummy", false, null), root);
            equal("", node(root).getValue());
            ReactDOM.render(select("value", new String[] {"12"}, true, choices("1", "2", "12")), root);
            selections(node(root), false, false, true);
            ReactDOM.render(select("value", new String[] {"a", "b"}, true, null), root);
            ReactDOM.render(select("value", new String[] {"a", "b"}, true, choices("a", "b", "c")), root);
            selections(node(root), true, true, false);

            final var animals = choices("monkey", "giraffe", "gorilla");
            final var object = stringable("giraffe");
            ReactDOM.render(select("value", new Object[] {object}, true, animals), root);
            selections(node(root), false, true, false);
            properties(object).set("toString", (Stringifier) () -> "monkey");
            ReactDOM.render(select("value", new Object[] {object}, true, animals), root);
            selections(node(root), true, false, false);
            reset(root);

            ReactDOM.render(select("value", "giraffe", false, animals), root);
            selections(node(root), false, true, false);
            ReactDOM.render(select("value", Js.undefined(), false, animals), root);
            selections(node(root), false, true, false);
            ReactDOM.render(select("value", "giraffe", false, animals), root);
            ReactDOM.render(select("value", "gorilla", false, animals), root);
            selections(node(root), false, false, true);
            ReactDOM.render(select("value", Js.undefined(), false, animals), root);
            selections(node(root), false, false, true);

            ReactDOM.render(select("value", "monkey", false, dynamicAnimals("monkey")), root);
            selections(node(root), true, false, false);
            ReactDOM.render(select("value", "giraffe", false, dynamicAnimals("giraffe")), root);
            selections(node(root), false, true, false);

            ReactDOM.render(
                    select(
                            "value",
                            "b",
                            false,
                            host("optgroup", "label", "group").input("children", choices("a", "b", "c"))),
                    root);
            selections(node(root), false, true, false);
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void selectChanges() {
        final var root = Browser.append("div");
        try {
            final var animals = choices("monkey", "giraffe", "gorilla");
            ReactDOM.render(select("value", "giraffe", false, animals), root);
            final var control = node(root);
            Browser.setValue(control, "gorilla");
            equal("giraffe", control.getValue());

            final ReactEventHandler remove = event -> ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(select("value", "giraffe", false, animals).input("onChange", remove), root);
            Browser.setValue(control, "monkey");
            check(null == Browser.property(root, "firstElementChild"), "A select change can unmount its root");

            ReactDOM.render(host("div", "id", "nested-form-root"), root);
            final var nested = node(root);
            try {
                final ReactEventHandler update = event -> ReactDOM.render(
                        select("value", Browser.property(event.getTarget(), "value"), false, animals), nested);
                ReactDOM.render(select("value", "giraffe", false, animals).input("onChange", update), nested);
                final var nestedSelect = node(nested);
                equal("giraffe", nestedSelect.getValue());
                properties(nestedSelect).set("value", "gorilla");
                elementEvent(nestedSelect, "input");
                equal("gorilla", nestedSelect.getValue());
                elementEvent(nestedSelect, "change");
                equal("gorilla", nestedSelect.getValue());
            } finally {
                reset(nested);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void selectNonTextValues() {
        final var root = Browser.append("div");
        try {
            for (final var unsupported : new Object[] {symbol("option"), (Stringifier) () -> "unused"}) {
                for (final var property : new String[] {"value", "defaultValue"}) {
                    final var options = ReactNode.of(
                            host("option", "value", unsupported).input("children", "other"),
                            choice("monkey"),
                            choice("giraffe"));
                    reset(root);
                    ReactDOM.render(select(property, unsupported, false, options), root);
                    equal("", node(root).getValue());
                    reset(root);
                    ReactDOM.render(select(property, "monkey", false, options), root);
                    equal("monkey", node(root).getValue());
                    reset(root);
                    ReactDOM.render(select(property, unsupported, false, options), root);
                    equal("", node(root).getValue());
                }
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void textareaValues() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(host("textarea", "defaultValue", "giraffe"), root);
            final var text = node(root);
            equal("giraffe", text.getValue());
            ReactDOM.render(host("textarea", "defaultValue", "gorilla"), root);
            equal("giraffe", text.getValue());
            Browser.setValue(text, "cat");
            ReactDOM.render(host("textarea", "defaultValue", "monkey"), root);
            equal("cat", text.getValue());
            final Object[][] scalars = {{Browser.number(0), "0"}, {false, "false"}, {stringable("foobar"), "foobar"}};
            for (final var scalar : scalars) {
                reset(root);
                ReactDOM.render(host("textarea", "defaultValue", scalar[0]), root);
                equal(Js.asString(scalar[1]), node(root).getValue());
            }

            reset(root);
            for (final var value : new String[] {"foo", "bar", "noise", ""}) {
                ReactDOM.render(host("textarea", "defaultValue", value), root);
                equal(value, defaultValue(node(root)));
            }
            check(null == node(root).getAttribute("value"), "Textarea value is a property, not an attribute");

            reset(root);
            ReactDOM.render(control("textarea", "value", Browser.number(0)), root);
            equal("0", node(root).getValue());
            Browser.setValue(node(root), "35");
            equal("0", node(root).getValue());
            for (final var value : new Object[] {"gorilla", true, false, stringable("foo")}) {
                ReactDOM.render(control("textarea", "value", "giraffe"), root);
                equal("giraffe", node(root).getValue());
                ReactDOM.render(control("textarea", "value", value), root);
                equal(stringValue(value), node(root).getValue());
            }

            reset(root);
            ReactDOM.render(host("textarea", "defaultValue", "0"), root);
            equal("0", node(root).getValue());
            ReactDOM.render(host("textarea", "defaultValue", "1"), root);
            equal("0", node(root).getValue());
            reset(root);
            ReactDOM.render(host("textarea", "defaultValue", "0"), root);
            ReactDOM.render(host("textarea", "children", "1"), root);
            equal("0", node(root).getValue());

            for (final boolean changed : new boolean[] {false, true}) {
                reset(root);
                ReactDOM.render(control("textarea", "value", "kitten"), root);
                equal("kitten", node(root).getValue());
                if (changed) {
                    ReactDOM.render(control("textarea", "value", "puppies"), root);
                    equal("puppies", node(root).getValue());
                }
                ReactDOM.render(host("textarea", "defaultValue", "gorilla"), root);
                equal(changed ? "puppies" : "kitten", node(root).getValue());
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void textareaChildren(boolean production) {
        final var root = Browser.append("div");
        try {
            final var warnings = captureWarnings(() -> ReactDOM.render(host("textarea", "children", "giraffe"), root));
            if (!production) {
                hasWarning(warnings, "Use the `defaultValue` or `value` props");
            }
            equal("giraffe", node(root).getValue());
            ReactDOM.render(host("textarea", "children", "gorilla"), root);
            equal("giraffe", node(root).getValue());
            for (final var value :
                    new Object[] {Browser.number(17), false, stringable("sharkswithlasers"), DOM.strong()}) {
                reset(root);
                ReactDOM.render(host("textarea", "children", value), root);
                equal(stringValue(value), node(root).getValue());
            }
            reset(root);
            fails(
                    root,
                    host("textarea", "children", new String[] {"hello", "there"}),
                    "at most one child",
                    93,
                    production);
            ReactDOM.render(host("textarea", "value", Js.undefined()), root);
            check(ReactDOM.unmountComponentAtNode(root), "Textarea unmount succeeds");
            check(null == Browser.property(root, "firstElementChild"), "Textarea is removed from the container");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void textareaMutations() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(control("textarea", "value", "a"), root);
            final var textarea = node(root);
            check(
                    0
                            == propertyWrites(
                                    textarea, "value", () -> ReactDOM.render(control("textarea", "value", "a"), root)),
                    "Unchanged controlled textarea does not set value");
            check(
                    1
                            == propertyWrites(
                                    textarea, "value", () -> ReactDOM.render(control("textarea", "value", "b"), root)),
                    "Changed controlled textarea sets value once");
            reset(root);
            final var child = control("textarea", "value", "foo");
            ReactDOM.render(ReactNode.of(DOM.span("0"), child), root);
            check(
                    0
                            == propertyWrites(
                                    Browser.query(root, "textarea"),
                                    "defaultValue",
                                    () -> ReactDOM.render(
                                            ReactNode.of(DOM.span("1"), control("textarea", "value", "foo")), root)),
                    "An unrelated render does not recreate the textarea text");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void textareaNonTextValues() {
        final var root = Browser.append("div");
        try {
            for (final var unsupported : new Object[] {symbol("textarea"), (Stringifier) () -> "unused"}) {
                for (final var name : new String[] {"value", "children", "defaultValue"}) {
                    reset(root);
                    captureWarnings(() -> ReactDOM.render(control("textarea", name, unsupported), root));
                    equal("", node(root).getValue());
                }
                for (final var name : new String[] {"value", "defaultValue"}) {
                    reset(root);
                    ReactDOM.render(control("textarea", name, "foo"), root);
                    captureWarnings(() -> ReactDOM.render(control("textarea", name, unsupported), root));
                    equal("value".equals(name) ? "" : "foo", node(root).getValue());
                }
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputValues() {
        final var root = Browser.append("div");
        try {
            for (final var value : new Object[] {Browser.number(0), true, false, stringable("foobar")}) {
                reset(root);
                ReactDOM.render(input("text", "defaultValue", value), root);
                final var expected = stringValue(value);
                equal(expected, node(root).getValue());
                equal(expected, defaultValue(node(root)));
                attribute(node(root), "value", expected);
                reset(root);
                final var initial = "object".equals(Js.typeof(value)) ? "foo" : "yolo";
                ReactDOM.render(input("text", "value", initial), root);
                equal(initial, node(root).getValue());
                ReactDOM.render(input("text", "value", value), root);
                equal(expected, node(root).getValue());
            }
            reset(root);
            ReactDOM.render(input("text", "name", "name"), root);
            equal("name", Js.asString(Browser.property(node(root), "name")));
            attribute(node(root), "name", "name");
            ReactDOM.render(input("text", "name", Js.undefined()), root);
            check(!node(root).hasAttribute("name"), "An absent input name does not set an attribute");

            reset(root);
            ReactDOM.render(input("text", "defaultValue", "0"), root);
            final var stable = node(root);
            check(
                    0
                            == propertyWrites(
                                    stable,
                                    "defaultValue",
                                    () -> ReactDOM.render(input("text", "defaultValue", "0"), root)),
                    "An unchanged input default never reaches its setter");
            equal("0", stable.getValue());
            equal("0", defaultValue(stable));
            ReactDOM.render(input("text", "defaultValue", "1"), root);
            equal("0", stable.getValue());
            equal("1", defaultValue(stable));
            ReactDOM.render(input("text", "defaultValue", ""), root);
            equal("", defaultValue(stable));

            reset(root);
            ReactDOM.render(input("date", "defaultValue", "1980-01-01"), root);
            final var date = node(root);
            equal("1980-01-01", date.getValue());
            equal("1980-01-01", defaultValue(date));
            ReactDOM.render(input("date", "defaultValue", "2000-01-01"), root);
            equal("1980-01-01", date.getValue());
            equal("2000-01-01", defaultValue(date));
            ReactDOM.render(input("date", "defaultValue", Js.undefined()), root);

            reset(root);
            ReactDOM.render(input("text", "value", "0"), root);
            equal("0", node(root).getValue());
            captureWarnings(() -> ReactDOM.render(input("text", "defaultValue", "1"), root));
            equal("0", node(root).getValue());
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputNumbers() {
        final var root = Browser.append("div");
        try {
            for (final var initial : new Object[] {Browser.number(2), "2"}) {
                reset(root);
                ReactDOM.render(input("text", "value", initial), root);
                final var text = node(root);
                Browser.setValue(text, "2.0");
                equal("2", text.getValue());
                attribute(text, "value", "2");
            }
            reset(root);
            final ReactEventHandler update = event ->
                    ReactDOM.render(input("text", "value", Browser.property(event.getTarget(), "value")), root);
            ReactDOM.render(input("text", "value", Browser.number(2)).input("onChange", update), root);
            Browser.setValue(node(root), "2.0");
            equal("2.0", node(root).getValue());
            attribute(node(root), "value", "2.0");

            for (final var type : new String[] {"text", "number", "radio", "checkbox"}) {
                reset(root);
                ReactDOM.render(input(type, "value", ""), root);
                equal("", node(root).getValue());
                ReactDOM.render(input(type, "value", Browser.number(0)), root);
                equal("0", node(root).getValue());
                attribute(node(root), "value", "0");
                if ("number".equals(type) || "text".equals(type)) {
                    Browser.setValue(node(root), "0.0");
                    equal("number".equals(type) ? "0.0" : "0", node(root).getValue());
                    ReactDOM.render(input(type, "value", ""), root);
                    equal("", node(root).getValue());
                    ReactDOM.render(input(type, "value", "0"), root);
                    equal("0", node(root).getValue());
                }
            }
            reset(root);
            for (final var decimal : new String[] {".98", "0.98", "3.0000", "3"}) {
                ReactDOM.render(input("number", "value", decimal), root);
                equal(decimal, node(root).getValue());
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputMutations() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(input("text", "value", "a"), root);
            final var text = node(root);
            check(
                    0 == propertyWrites(text, "value", () -> ReactDOM.render(input("text", "value", "a"), root)),
                    "An unchanged controlled input does not write value");
            check(
                    1 == propertyWrites(text, "value", () -> ReactDOM.render(input("text", "value", "b"), root)),
                    "A changed controlled input writes value once");
            for (final var value : new Object[] {Browser.number(0), true}) {
                ReactDOM.render(input("text", "value", stringValue(value)), root);
                check(
                        0 == propertyWrites(text, "value", () -> ReactDOM.render(input("text", "value", value), root)),
                        "Equivalent typed values do not write the value property");
            }

            for (final var type : new String[] {"submit", "reset"}) {
                reset(root);
                for (final var missing : new @Nullable Object[] {Js.undefined(), null}) {
                    reset(root);
                    captureWarnings(() -> ReactDOM.render(input(type, "value", missing), root));
                    check(!node(root).hasAttribute("value"), type + " does not add an empty implicit label");
                }
                ReactDOM.render(input(type, "value", "foo"), root);
                attribute(node(root), "value", "foo");
                ReactDOM.render(input(type, "value", Js.undefined()), root);
                check(!node(root).hasAttribute("value"), type + " removes an omitted label on update");
                reset(root);
                ReactDOM.render(input(type, "value", ""), root);
                attribute(node(root), "value", "");
            }

            reset(root);
            for (final var type : new String[] {"checkbox", "radio"}) {
                reset(root);
                ReactDOM.render(input(type, "checked", true), root);
                check(
                        !node(root).hasAttribute("value"),
                        type + " does not materialize the browser's implicit on value");
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputAttributes() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(input("text", "value", "1"), root);
            ReactDOM.render(input("text", "value", "2"), root);
            attribute(node(root), "value", "2");
            reset(root);

            ReactDOM.render(input("number", "value", "1"), root);
            final var numeric = node(root);
            focus(numeric);
            ReactDOM.render(input("number", "value", "2"), root);
            equal("2", numeric.getValue());
            attribute(numeric, "value", "1");
            blur(numeric);
            equal("2", numeric.getValue());
            attribute(numeric, "value", "2");
            for (final var type : new String[] {"text", "number"}) {
                reset(root);
                ReactDOM.render(input(type, "defaultValue", "1"), root);
                final var input = node(root);
                focus(input);
                Browser.setValue(input, "2");
                blur(input);
                attribute(input, "value", "1");
            }
            for (final var missing : new @Nullable Object[] {Js.undefined(), null}) {
                reset(root);
                ReactDOM.render(input("text", "value", "first"), root);
                ReactDOM.render(input("text", "value", "latest"), root);
                captureWarnings(() -> ReactDOM.render(input("text", "value", missing), root));
                attribute(node(root), "value", "first");
                equal("latest", node(root).getValue());
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputWriteOrder() {
        final var root = Browser.append("div");
        try {
            final var range = control("input", "value", "0")
                    .input("type", "range")
                    .input("min", "0")
                    .input("max", "100")
                    .input("step", "1");
            final var writes = inputWriteTrace(() -> ReactDOM.render(range, root));
            check(
                    writes.startsWith("type,min,max,step,write:0,"),
                    "Input type and constraints must be assigned before value: " + writes);
            check(writes.contains(",value,"), "The controlled value is also reflected as an attribute");
            equal("0", node(root).getValue());
            reset(root);

            ReactDOM.render(control("input", "value", "hi").input("type", "radio"), root);
            equal("hi", node(root).getValue());
            reset(root);

            ReactDOM.render(input("number", "value", Browser.number(1000)), root);
            final var warnings = captureWarnings(() -> ReactDOM.render(input("text", "value", "Test"), root));
            check(0 == warnings.length, "Changing type before writing text avoids invalid input diagnostics");
            equal("Test", node(root).getValue());
            reset(root);

            final var date = input("date", "defaultValue", "1980-01-01");
            final var dateWrites = inputWriteTrace(() -> ReactDOM.render(date, root));
            check(
                    dateWrites.startsWith("type,write:1980-01-01,value,"),
                    "Date initialization writes the live value before reflecting the default: " + dateWrites);
            equal("1980-01-01", node(root).getValue());
            attribute(node(root), "value", "1980-01-01");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputNonTextValues() {
        final var root = Browser.append("div");
        try {
            for (final var unsupported : new Object[] {symbol("input"), (Stringifier) () -> "unused"}) {
                for (final var name : new String[] {"value", "defaultValue"}) {
                    reset(root);
                    captureWarnings(() -> ReactDOM.render(input("text", name, unsupported), root));
                    equal("", node(root).getValue());
                    reset(root);
                    ReactDOM.render(input("text", name, "foo"), root);
                    captureWarnings(() -> ReactDOM.render(input("text", name, unsupported), root));
                    equal("value".equals(name) ? "" : "foo", node(root).getValue());
                }
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputChanges() {
        final var root = Browser.append("div");
        try {
            captureWarnings(() -> ReactDOM.render(host("input", "value", "lion").input("type", "text"), root));
            Browser.setValue(node(root), "giraffe");
            equal("lion", node(root).getValue());

            final var trace = new StringBuilder();
            final ReactEventHandler recordBlur = event -> trace.append(Browser.property(event.getTarget(), "value"));
            final var destination = host("input", "id", "destination");
            final ReactEventHandler change = event -> {
                final var first = Browser.query(root, "#editing");
                ReactDOM.render(
                        ReactNode.of(input("text", "value", first.getValue()).input("id", "editing"), destination),
                        root);
                elementEvent(first, "input");
                focus(Browser.query(root, "#destination"));
            };
            ReactDOM.render(
                    ReactNode.of(
                            input("text", "value", "lion")
                                    .input("id", "editing")
                                    .input("onChange", change)
                                    .input("onBlur", recordBlur),
                            destination),
                    root);
            final var first = Browser.query(root, "#editing");
            focus(first);
            Browser.setValue(first, "giraffe");
            equal("giraffe", trace.toString());
            equal("giraffe", first.getValue());

            final ReactEventHandler nested =
                    event -> Browser.query(root, "#nested-check").click();
            ReactDOM.render(
                    ReactNode.of(
                            input("text", "value", "lion").input("onChange", nested),
                            input("checkbox", "checked", true).input("id", "nested-check")),
                    root);
            final var changed = node(root);
            Browser.setValue(changed, "giraffe");
            equal("lion", changed.getValue());
            checked(Browser.query(root, "#nested-check"), true);

            final var valueTrace = new StringBuilder();
            final ReactEventHandler recordChange =
                    event -> valueTrace.append(Browser.property(event.getTarget(), "value"));
            ReactDOM.render(input("text", "value", Js.undefined()).input("onChange", recordChange), root);
            Browser.setValue(node(root), "giraffe");
            equal("giraffe", valueTrace.toString());
            final int[] receiverCalls = {0};
            ReactDOM.render(
                    input("text", "value", Js.undefined()).input("onChange", unbound(() -> receiverCalls[0]++)), root);
            Browser.setValue(node(root), "new text");
            check(1 == receiverCalls[0], "An unbound change handler has undefined this and is called once");

            ReactDOM.render(resetForm("default1"), root);
            final var field = Browser.query(root, "form input");
            equal("default1", field.getValue());
            Browser.setValue(field, "changed");
            equal("changed", field.getValue());
            ReactDOM.render(resetForm("default2"), root);
            equal("changed", field.getValue());
            Browser.query(root, "input[type=reset]").click();
            equal("default2", field.getValue());
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void inputRadioGroups() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(
                    ReactNode.of(
                            radio("first", "fruit", true),
                            radio("second", "fruit", Js.undefined()),
                            host(
                                    "form",
                                    "children",
                                    input("radio", "defaultChecked", true)
                                            .input("name", "fruit")
                                            .input("id", "separate"))),
                    root);
            final var first = Browser.query(root, "#first");
            final var second = Browser.query(root, "#second");
            final var separate = Browser.query(root, "#separate");
            checked(first, true);
            checked(second, false);
            checked(separate, true);
            checkedAttributes(first, second, separate);
            setUntracked(second, "checked", true);
            checked(first, false);
            checked(separate, true);
            checkedAttributes(first, second, separate);
            elementEvent(second, "click");
            checked(first, true);
            checked(separate, true);

            reset(root);
            final ReactEventHandler rename = event -> ReactDOM.render(
                    ReactNode.of(
                            host("button", "type", "button"),
                            radio("first", "secondName", true),
                            radio("second", "secondName", false)),
                    root);
            ReactDOM.render(
                    ReactNode.of(
                            host("button", "onClick", rename).input("type", "button"),
                            radio("first", "firstName", false),
                            radio("second", "firstName", true)),
                    root);
            final var moved = Browser.query(root, "#first");
            checked(moved, false);
            Browser.query(root, "button").click();
            checked(moved, true);

            reset(root);
            final var detached = Browser.append("div");
            detached.remove();
            ReactDOM.render(radio("b", "fruit", true), detached);
            final var b = node(detached);
            final ReactEventHandler attach = event -> {
                append(root, detached);
                ReactDOM.render(radio("a", "fruit", false), root);
            };
            try {
                ReactDOM.render(radio("a", "fruit", false).input("onChange", attach), root);
                final var a = node(root);
                checked(a, false);
                checked(b, true);
                setUntracked(a, "checked", true);
                setUntracked(b, "checked", false);
                elementEvent(a, "click");
                checked(a, false);
                checked(b, true);
            } finally {
                close(detached);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void formWarnings(boolean production) {
        final var root = Browser.append("div");
        try {
            final var missingValue = captureWarnings(() -> {
                for (final var value : new Object[] {Browser.number(0), "", "0"}) {
                    reset(root);
                    ReactDOM.render(host("input", "value", value).input("type", "text"), root);
                }
                reset(root);
                ReactDOM.render(host("input", "value", "read only").input("readOnly", true), root);
                reset(root);
                ReactDOM.render(host("input", "value", "editable").input("readOnly", false), root);
            });
            final var missingChecked = captureWarnings(() -> {
                reset(root);
                ReactDOM.render(
                        host("input", "checked", false)
                                .input("type", "checkbox")
                                .input("readOnly", true),
                        root);
                reset(root);
                ReactDOM.render(
                        host("input", "checked", false)
                                .input("type", "checkbox")
                                .input("readOnly", false),
                        root);
            });
            final var uncontrolled = captureWarnings(() -> {
                for (final var tag : new String[] {"input", "textarea", "select"}) {
                    for (final var name : new String[] {"children", "value"}) {
                        reset(root);
                        ReactDOM.render(host(tag, name, Js.undefined()), root);
                    }
                }
            });
            check(0 == uncontrolled.length, "Uncontrolled fields with omitted values are quiet");
            final var nullValue = warnOnFresh(root, host("input", "value", null));
            final var invalidValue = warnOnFresh(root, input("text", "value", symbol("input")));
            final var repeatedNull = captureWarnings(() -> {
                for (final var tag : new String[] {"input", "textarea", "select"}) {
                    reset(root);
                    ReactDOM.render(host(tag, "value", null), root);
                }
                reset(root);
                ReactDOM.render(select("value", null, true, choices("test")), root);
            });
            check(0 == repeatedNull.length, "Renderer-wide null value warnings are deduplicated");
            final var inputValueConflict =
                    warnOnFresh(root, input("text", "value", "control").input("defaultValue", "default"));
            final var checkedConflict =
                    warnOnFresh(root, input("checkbox", "checked", true).input("defaultChecked", false));
            final var selectConflict =
                    warnOnFresh(root, select("value", "a", false, choices("a")).input("defaultValue", "a"));
            final var textareaConflict =
                    warnOnFresh(root, control("textarea", "value", "a").input("defaultValue", "b"));
            final var optionSelected = warnOnFresh(
                    root,
                    host(
                            "select",
                            "children",
                            ReactNode.of(host("option", "selected", true), host("option", "selected", true))));
            final var transitions = captureWarnings(() -> {
                for (final var type : new String[] {"text", "checkbox", "radio"}) {
                    final var property = "text".equals(type) ? "value" : "checked";
                    final Object value = "text".equals(type) ? "example" : false;
                    for (final var missing : new @Nullable Object[] {Js.undefined(), null}) {
                        reset(root);
                        ReactDOM.render(input(type, property, value), root);
                        ReactDOM.render(input(type, property, missing), root);
                        reset(root);
                        ReactDOM.render(input(type, property, missing), root);
                        ReactDOM.render(input(type, property, value), root);
                    }
                    reset(root);
                    ReactDOM.render(input(type, property, value), root);
                    final var defaultName = "text".equals(type) ? "defaultValue" : "defaultChecked";
                    ReactDOM.render(input(type, defaultName, value), root);
                }
            });
            final var radios = captureWarnings(() -> {
                reset(root);
                ReactDOM.render(input("radio", "value", "first"), root);
                ReactDOM.render(input("radio", "value", "second"), root);
                reset(root);
                ReactDOM.render(input("radio", "value", "first").input("checked", false), root);
                ReactDOM.render(input("radio", "value", "second").input("checked", true), root);
            });
            check(0 == radios.length, "Changing a radio value without changing its controlledness is quiet");
            if (!production) {
                hasWarning(missingValue, "You provided a `value` prop to a form field without an `onChange` handler");
                hasWarning(
                        missingChecked, "You provided a `checked` prop to a form field without an `onChange` handler");
                hasWarning(nullValue, "`value` prop on `input` should not be null");
                hasWarning(invalidValue, "Invalid value for prop `value` on <input>");
                hasWarning(inputValueConflict, "with both value and defaultValue props");
                hasWarning(checkedConflict, "with both checked and defaultChecked props");
                hasWarning(selectConflict, "Select elements must be either controlled or uncontrolled");
                hasWarning(textareaConflict, "with both value and defaultValue props");
                hasWarning(optionSelected, "instead of setting `selected` on <option>");
                hasWarning(transitions, "controlled input of type text to be uncontrolled");
                hasWarning(transitions, "uncontrolled input of type text to be controlled");
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void cleanInputs(boolean production) {
        final var root = Browser.append("div");
        try {
            for (final var tag : new String[] {"input", "textarea"}) {
                final var value = "foo";
                final var conflict = "with both value and defaultValue props";
                final var missingHandler = "You provided a `value` prop to a form field without an `onChange` handler";
                final boolean input = "input".equals(tag);
                cleanValue(cleanMount(root, control(tag, "value", value), production), input, value);
                cleanValue(
                        cleanMount(root, host(tag, "value", value).input("readOnly", true), production), input, value);
                cleanValue(cleanMount(root, host(tag, "value", value), production, missingHandler), input, value);
                cleanValue(cleanMount(root, host(tag, "defaultValue", value), production), input, value);
                cleanValue(
                        cleanMount(
                                root,
                                host(tag, "value", value)
                                        .input("defaultValue", "bar")
                                        .input("readOnly", true),
                                production,
                                conflict),
                        input,
                        value);
                cleanValue(
                        cleanMount(
                                root,
                                host(tag, "defaultValue", "bar")
                                        .input("value", value)
                                        .input("readOnly", true),
                                production,
                                conflict),
                        input,
                        value);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void cleanCheckboxes(boolean production) {
        final var root = Browser.append("div");
        try {
            final var conflict = "with both checked and defaultChecked props";
            checked(cleanMount(root, input("checkbox", "checked", true), production), true);
            checked(cleanMount(root, checkbox("checked", true).input("readOnly", true), production), true);
            checked(
                    cleanMount(
                            root,
                            checkbox("checked", true),
                            production,
                            "You provided a `checked` prop to a form field without an `onChange` handler"),
                    true);
            cleanChecked(cleanMount(root, checkbox("defaultChecked", true), production));
            cleanChecked(cleanMount(
                    root,
                    checkbox("checked", true).input("defaultChecked", false).input("readOnly", true),
                    production,
                    conflict));
            cleanChecked(cleanMount(
                    root,
                    checkbox("defaultChecked", false).input("checked", true).input("readOnly", true),
                    production,
                    conflict));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void cleanSelects(boolean production) {
        final var root = Browser.append("div");
        try {
            final var choices = choices("foo", "bar", "baz");
            final var conflict = "Select elements must be either controlled or uncontrolled";
            final var missingHandler = "You provided a `value` prop to a form field without an `onChange` handler";
            cleanSelection(cleanMount(root, select("value", "bar", false, choices), production), false);
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "value", "bar")
                                    .input("readOnly", true)
                                    .input("children", choices),
                            production),
                    false);
            final var multiple = new String[] {"bar", "baz"};
            cleanSelection(cleanMount(root, select("value", multiple, true, choices), production), true);
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "value", multiple)
                                    .input("multiple", true)
                                    .input("readOnly", true)
                                    .input("children", choices),
                            production),
                    true);
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "value", "bar").input("children", choices),
                            production,
                            missingHandler),
                    false);
            cleanSelection(
                    cleanMount(root, host("select", "defaultValue", "bar").input("children", choices), production),
                    false);
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "value", "bar")
                                    .input("defaultValue", "baz")
                                    .input("readOnly", true)
                                    .input("children", choices),
                            production,
                            conflict),
                    false);
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "defaultValue", "baz")
                                    .input("value", "bar")
                                    .input("readOnly", true)
                                    .input("children", choices),
                            production,
                            conflict),
                    false);

            final var rawOptions = ReactNode.of(
                    rawOption("foo", "Foo").input("children", Js.undefined()),
                    rawOption("bar", "Bar").input("children", null),
                    rawOption("baz", "Baz"));
            cleanSelection(
                    cleanMount(
                            root,
                            host("select", "defaultValue", "baz")
                                    .input("value", "bar")
                                    .input("readOnly", true)
                                    .input("children", rawOptions),
                            production,
                            conflict),
                    false);
            for (final var child : new Object[] {Browser.number(0), ""}) {
                reset(root);
                fails(
                        root,
                        host("select", "defaultValue", "baz")
                                .input("value", "foo")
                                .input("readOnly", true)
                                .input("children", rawOption("foo", "Foo").input("children", child)),
                        "Can only set one of `children` or `props.dangerouslySetInnerHTML`.",
                        60,
                        production);
            }

            final var flat = cleanMount(
                    root,
                    host("select", "value", "bar")
                            .input("readOnly", true)
                            .input("children", choice("bar").input("children", new String[] {"A ", "B"})),
                    production);
            final var text = node(flat);
            final var childNodes = Js.<JsArrayLike<Object>>uncheckedCast(Browser.property(text, "childNodes"));
            check(1 == childNodes.getLength(), "Flattened option contains one text node");
            final var child = Objects.requireNonNull(childNodes.getAt(0));
            check(3 == Js.asInt(Browser.property(child, "nodeType")), "Flattened child is a text node");
            equal("A B", Browser.string(Browser.property(child, "nodeValue")));

            final var mixed = cleanMount(
                    root,
                    host("select", "value", "bar")
                            .input("readOnly", true)
                            .input("children", choice("bar").input("children", new Object[] {
                                "Bar", false, "Foo", DOM.div(), "Baz"
                            })),
                    production,
                    "Only strings and numbers are supported as <option> children.");
            cleanSelection(mixed, false, true);
            equal("BarFoo[object Object]Baz", html(node(mixed)));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void cleanUserInteraction(boolean production) {
        final var root = Browser.append("div");
        try {
            for (final var mode : new String[] {"input", "textarea", "checkbox", "select"}) {
                final var changes = new int[1];
                final Probe changed = () -> changes[0]++;
                final var control = cleanMount(
                        root,
                        ReactElement.createViewElement(CONTROLLED)
                                .input("mode", mode)
                                .input("changed", changed),
                        production);
                check(0 == changes[0], "Mounting does not cause an onChange");
                if ("checkbox".equals(mode)) {
                    checked(control, true);
                    control.click();
                    checked(control, false);
                } else {
                    equal("Hello", control.getValue());
                    setUntracked(control, "value", "Goodbye");
                    elementEvent(control, "select".equals(mode) ? "change" : "input");
                    equal("Goodbye", control.getValue());
                }
                check(1 == changes[0], "The user interaction causes exactly one onChange");
            }
        } finally {
            close(root);
        }
    }

    private static ReactElement checkbox(String property, boolean value) {
        return host("input", "type", "checkbox").input(property, value);
    }

    private static void cleanChecked(Browser.Element element) {
        checked(element, true);
        check(!element.hasAttribute("defaultChecked"), "defaultChecked is not serialized");
    }

    private static void cleanValue(Browser.Element element, boolean input, String value) {
        equal(value, element.getValue());
        if (input) {
            equal(value, element.getAttribute("value"));
        } else {
            check(!element.hasAttribute("value"), "Textarea value is not serialized as an attribute");
        }
        check(!element.hasAttribute("defaultValue"), "defaultValue is not serialized");
    }

    private static void cleanSelection(Browser.Element element, boolean multiple) {
        cleanSelection(element, multiple, false);
    }

    private static void cleanSelection(Browser.Element element, boolean multiple, boolean singleOption) {
        check(!element.hasAttribute("value"), "Select value is not serialized as an attribute");
        check(!element.hasAttribute("defaultValue"), "defaultValue is not serialized");
        selections(element, singleOption ? new boolean[] {true} : new boolean[] {false, true, multiple});
    }

    private static ReactElement rawOption(String value, String markup) {
        return host("option", "value", value)
                .input("id", value)
                .input("dangerouslySetInnerHTML", JsPropertyMap.of("__html", markup));
    }

    private static Browser.Element cleanMount(
            Browser.Element root, ReactElement input, boolean production, String... expected) {
        final var warnings = warnOnFresh(root, input);
        check(warnings.length <= (production ? 0 : expected.length), "Unexpected diagnostics count");
        for (final var warning : warnings) {
            check(warning.contains(expected[0]), "Unexpected diagnostic " + warning);
        }
        return node(root);
    }

    private static final class Controlled extends NativeView {
        private final String _mode;
        private final Probe _changed;

        @JsConstructor
        private Controlled(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _mode = Js.asString(inputs.get("mode"));
            _changed = Js.uncheckedCast(Objects.requireNonNull(inputs.get("changed")));
            properties(this).set("state", JsPropertyMap.of("value", "checkbox".equals(_mode) ? true : "Hello"));
        }

        @Override
        public ReactNode render() {
            final var state = Objects.requireNonNull(state()).get("value");
            final var property = "checkbox".equals(_mode) ? "checked" : "value";
            final var control = "checkbox".equals(_mode)
                    ? host("input", "type", "checkbox").input(property, state)
                    : host(_mode, property, state);
            if ("select".equals(_mode)) {
                control.input("children", choices("Hello", "Goodbye"));
            }
            return control.input("onChange", (ReactEventHandler) event -> {
                _changed.run();
                setState(JsPropertyMap.of("value", Browser.property(event.getTarget(), property)));
            });
        }
    }

    private static String[] warnOnFresh(Browser.Element root, ReactElement element) {
        reset(root);
        return captureWarnings(() -> ReactDOM.render(element, root));
    }

    private static ReactElement resetForm(String value) {
        return host(
                "form", "children", ReactNode.of(input("text", "defaultValue", value), host("input", "type", "reset")));
    }

    private static ReactElement radio(String id, String name, @Nullable Object checked) {
        return input("radio", "checked", checked).input("id", id).input("name", name);
    }

    private static void checkedAttributes(Browser.Element first, Browser.Element second, Browser.Element separate) {
        check(first.hasAttribute("checked"), "First checked radio retains the checked attribute");
        check(!second.hasAttribute("checked"), "Second radio has no checked attribute");
        check(separate.hasAttribute("checked"), "Separate form's default radio retains the checked attribute");
    }

    private static void checked(Browser.Element input, boolean expected) {
        check(
                Boolean.valueOf(expected).equals(Browser.property(input, "checked")),
                "Expected radio checked state " + expected);
    }

    private static ReactElement input(String type, String property, @Nullable Object value) {
        return control("input", property, value).input("type", type);
    }

    private static void selections(Browser.Element select, boolean... expected) {
        final var options = Js.<JsArrayLike<Browser.Element>>uncheckedCast(Browser.property(select, "options"));
        check(expected.length == options.getLength(), "Option count");
        for (int i = 0; i < expected.length; i++) {
            check(
                    Boolean.valueOf(expected[i])
                            .equals(Browser.property(Objects.requireNonNull(options.getAt(i)), "selected")),
                    "Unexpected selected state for option " + i);
        }
    }

    private static ReactNode choices(String... values) {
        final var nodes = new ReactNode[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = choice(values[i]);
        }
        return ReactNode.of(nodes);
    }

    private static ReactElement choice(String value) {
        return ReactElement.createHostElement(
                "option", value, null, JsPropertyMap.of("value", value, "children", value));
    }

    private static ReactNode dynamicAnimals(String current) {
        final var nodes = new ReactNode[3];
        final String[] values = {"monkey", "giraffe", "gorilla"};
        for (int i = 0; i < values.length; i++) {
            final var value = values[i];
            nodes[i] = choice(value).input("children", new @Nullable Object[] {
                "A " + value + " ", value.equals(current) ? "is chosen" : null, "!"
            });
        }
        return ReactNode.of(nodes);
    }

    private static Object stringable(String text) {
        return JsPropertyMap.of("toString", (Stringifier) () -> text);
    }

    private static ReactElement select(
            String property, @Nullable Object value, boolean multiple, @Nullable ReactNode options) {
        return host("select", property, value)
                .input("multiple", multiple)
                .input("children", options)
                .input("onChange", IGNORE);
    }

    private static void fails(
            Browser.Element root, ReactNode element, String expected, int stockCode, boolean production) {
        final var minified = "Minified React error #" + stockCode + ";";
        withoutExpectedWindowError(
                expected,
                minified,
                () -> captureWarnings(() -> {
                    try {
                        ReactDOM.render(element, root);
                    } catch (Throwable failure) {
                        final var message = failure.getMessage();
                        check(
                                null != message
                                        && (message.contains(expected) || production && message.contains(minified)),
                                "Unexpected form error: " + message);
                        return;
                    }
                    throw new AssertionError("Expected form error: " + expected);
                }));
    }

    @JsMethod
    private static native void withoutExpectedWindowError(String message, String minified, Probe action);

    private static void hasWarning(String[] warnings, String expected) {
        check(String.join("\n", warnings).contains(expected), "Missing form diagnostic: " + expected);
    }

    private static void numberProperty(Browser.Element element, String name, int expected) {
        check(
                Js.isTripleEqual(Browser.number(expected), Browser.property(element, name)),
                name + " should be " + expected);
    }

    private static void attribute(Browser.Element element, String name, String expected) {
        check(element.hasAttribute(name), name + " should be present");
        equal(expected, element.getAttribute(name));
    }

    private static String html(Browser.Element element) {
        return Js.asString(Browser.property(element, "innerHTML"));
    }

    private static ReactElement host(String tag, String name, @Nullable Object value) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of(name, value));
    }

    private static ReactElement control(String tag, String name, @Nullable Object value) {
        return host(tag, name, value).input("onChange", IGNORE);
    }

    private static String defaultValue(Browser.Element element) {
        return Js.asString(Browser.property(element, "defaultValue"));
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native String stringValue(Object value);

    @JsMethod
    private static native int propertyWrites(Browser.Element element, String property, Probe action);

    @JsMethod
    private static native String inputWriteTrace(Probe action);

    @JsMethod
    private static native void focus(Browser.Element element);

    @JsMethod
    private static native void blur(Browser.Element element);

    @JsMethod
    private static native void setUntracked(Browser.Element element, String property, Object value);

    @JsMethod
    private static native ReactEventHandler unbound(Probe action);

    @JsMethod
    private static native void append(Browser.Element parent, Browser.Element child);

    private static Browser.Element node(Browser.Element root) {
        return root.getFirstElementChild();
    }

    private static void close(Browser.Element root) {
        reset(root);
        root.remove();
    }

    private static void reset(Browser.Element root) {
        ReactDOM.unmountComponentAtNode(root);
    }

    private static JsPropertyMap<@Nullable Object> properties(Object value) {
        return Js.uncheckedCast(value);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol")
    private static native Object symbol(String name);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbolFor(String name);

    @JsMethod
    private static native String[] captureWarnings(Probe action);

    @JsMethod
    private static native void elementEvent(Browser.Element element, String type);

    @JsFunction
    private interface Stringifier {
        String stringify();
    }

    @JsFunction
    private interface Probe {
        void run();
    }
}
