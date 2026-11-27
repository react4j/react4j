package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class ChildReconciliationTest {
    private ChildReconciliationTest() {}

    // Array inputs from ReactMultiChildReconcile-test.js. Letters are stable keys; underscores are empty slots.
    private static final String[][] SEQUENCES = {
        {"a", "", "a"},
        {"abc"},
        {"ab", "ab"},
        {"", "a"},
        {"a", ""},
        {"", ""},
        {"ab", "_b"},
        {"ab", "a_"},
        {"ab", "ba"},
        {"abc", "cba"},
        {"abcd", "bcda", "cdab", "dabc", "abcd"},
        {"abcd", "dabc", "cdab", "bcda", "abcd"},
        {"ab", "b__"},
        {"abcd", "bda"},
        {"ab", "abc"},
        {"ab", "abcd"},
        {"ab", "cab"},
        {"ab", "cdab"},
        {"ab", "_ab"},
        {"ab", "ab_"},
        {"ab", "a___b"},
        {"ab", "acb"},
        {"ab", "acdeb"},
        {"a___e", "abcde"}
    };

    @JsMethod
    public static void run() {
        final var container = Browser.append("div");
        try {
            for (int kind = 0; kind < 3; kind++) {
                for (final var sequence : SEQUENCES) {
                    checkSequence(container, sequence, kind);
                    ReactDOM.unmountComponentAtNode(container);
                }
            }
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
        }
    }

    private static void checkSequence(Browser.Element container, String[] sequence, int kind) {
        final ViewConstructorFunction type = Row::new;
        final var refs = new HashMap<String, JsPropertyMap<@Nullable Object>>();
        final var lastInstance = new HashMap<String, Object>();
        final var lastHost = new HashMap<String, Browser.Element>();
        String previous = "";
        int revision = 0;
        for (final var order : sequence) {
            final var children = new ReactElement[order.length()];
            for (int i = 0; i < children.length; i++) {
                final var key = order.substring(i, i + 1);
                if (!"_".equals(key)) {
                    final var earlier = order.substring(0, i);
                    final Runnable verify = () -> {
                        for (final var previousKey : earlier.split("")) {
                            if (!previousKey.isEmpty() && !"_".equals(previousKey)) {
                                check(
                                        null
                                                != Objects.requireNonNull(refs.get(previousKey))
                                                        .get("current"),
                                        "The earlier ref is attached before mounting this row");
                            }
                        }
                    };
                    final var child = ReactElement.createViewElement(type)
                            .input("text", key + revision)
                            .input("verify", verify);
                    child.setKey(key);
                    final var ref = refs.computeIfAbsent(key, ignored -> JsPropertyMap.of("current", null));
                    Js.<JsPropertyMap<Object>>uncheckedCast(child).set("ref", ref);
                    children[i] = child;
                }
            }
            final var prepared = 0 == kind ? ReactNode.of(children) : DomRootBrowserTest.iterable(children, 2 == kind);
            ReactDOM.render(
                    ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", prepared)),
                    container);
            var host = container.getFirstElementChild().getFirstElementChild();
            for (int i = 0; i < children.length; i++) {
                final var key = order.substring(i, i + 1);
                if (!"_".equals(key)) {
                    final var ref = Objects.requireNonNull(refs.get(key));
                    final var instance = ref.get("current");
                    check(instance instanceof Row, "The class ref resolves after rendering " + key);
                    equal(key + revision, host.getTextContent());
                    if (lastInstance.containsKey(key)) {
                        final var retained = previous.contains(key);
                        check(
                                retained == (instance == lastInstance.get(key)),
                                "View identity for " + previous + " to " + order);
                        check(
                                retained == (host == lastHost.get(key)),
                                "Host identity for " + previous + " to " + order);
                    }
                    lastInstance.put(key, instance);
                    lastHost.put(key, host);
                    host = host.getNextElementSibling();
                }
            }
            check(null == host, "Only rendered keys have host nodes");
            for (Map.Entry<String, JsPropertyMap<@Nullable Object>> entry : refs.entrySet()) {
                if (!order.contains(entry.getKey())) {
                    check(null == entry.getValue().get("current"), "The removed row's ref clears");
                }
            }
            previous = order;
            revision++;
        }
    }

    private static final class Row extends NativeView implements OnComponentDidMount {
        @JsConstructor
        Row(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return DOM.span(String.valueOf(requireInputs().get("text")));
        }

        @Override
        public void componentDidMount() {
            ((Runnable) Objects.requireNonNull(requireInputs().get("verify"))).run();
        }
    }
}
