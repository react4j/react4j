package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedReconcilerTest.rootShape;
import static react4j.dom.IsolatedSchedulingTest.untilYield;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.CurrentOwner;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedErrorHandlingTest {
    private static final PropTypeValidator.Validator ANY_CONTEXT =
            (values, property, name, location, publicName, secret) -> null;

    private IsolatedErrorHandlingTest() {}

    public static void cases(int number, boolean development) {
        final var f = new Fixture();
        switch (number) {
            case 0, 1 -> concurrentRecovery(f, 0 == number);
            case 2, 3 -> lowerPriority(f, 3 == number);
            case 4 -> finalRetry(f);
            case 5 -> multipleCommitFailures(f);
            case 6, 7, 8, 9, 10, 11, 12, 13 -> mounting(f, number);
            case 14, 15, 16 -> schedulingFailure(f, number);
            case 17, 18, 19 -> rootReusable(f, number);
            case 20, 21 -> abandonedBoundary(f, 20 == number);
            case 22, 23 -> otherRoots(f, 23 == number);
            case 24 -> unwindLegacyContext(f, development);
            case 25, 26, 27 -> reconcilerFailures(f, number, development);
            case 28 -> disposal(f);
            case 29 -> detachRef(f);
            case 30 -> IsolatedErrorLoggingTest.replay(true);
            case 31 -> {
                f.root.render(host("div", null, null), () -> raise(new JsFailure("Error!")));
                f.reject(f.harness::flush, "Error!");
            }
            case 32 -> arbitraryThrow(f, development);
            case 33 -> siblings(f);
            case 34 -> boundaryLifecycle(f);
            case 35, 36 -> errorStack(f, 36 == number);
            case 37 -> moduleProvider(f, development);
            default -> throw new AssertionError(number);
        }
    }

    private static void concurrentRecovery(Fixture f, boolean derived) {
        final var begin = List.of("ErrorBoundary (try)", "Indirection", "Indirection", "Indirection", "throw");
        final var tail = List.of("Indirection", "Indirection");
        final var recover = List.of(
                derived ? "getDerivedStateFromError" : "componentDidCatch", "ErrorBoundary (catch)", "ErrorMessage");
        final var failure = f.fail("throw", "oops!");
        final Function<@Nullable ReactNode, ReactNode> nest = child -> f.view(() -> f.log("Indirection", child));
        final var contents = array(failure, nest.apply(null), nest.apply(null));
        final var child = nest.apply(nest.apply(nest.apply(contents)));
        final var fallback = (Function<Object, @Nullable ReactNode>)
                error -> f.log("ErrorMessage", span("Caught an error: " + message(error)));
        final var caught = derived
                ? f.derived(child, fallback, "ErrorBoundary (try)", "ErrorBoundary (catch)")
                : f.boundary(
                        child,
                        "ErrorBoundary (try)",
                        "ErrorBoundary (catch)",
                        fallback,
                        (boundary, error) -> f.trace.add("componentDidCatch"));
        f.root.render(caught, null);
        untilYield(f.harness, f.trace, 5);
        f.expect(begin);
        untilYield(f.harness, f.trace, 1);
        f.expect("Indirection");
        if (derived) {
            untilYield(f.harness, f.trace, 4);
            f.expect("Indirection", "getDerivedStateFromError", "ErrorBoundary (catch)", "ErrorMessage");
            f.empty();
            f.flush();
            f.expect(join(begin, tail, recover));
        } else {
            f.flush();
            f.expect(join(List.of("Indirection"), begin, tail, recover));
        }
        f.output("Caught an error: oops!");
    }

    @SafeVarargs
    private static List<String> join(List<String>... sections) {
        final var lines = new ArrayList<String>();
        for (final var section : sections) {
            lines.addAll(section);
        }
        return lines;
    }

    private static void lowerPriority(Fixture f, boolean syncFailure) {
        if (syncFailure) {
            final var parent = f.effects(view -> {
                        if (Boolean.TRUE.equals(view.current().get("hideChild"))) {
                            return f.log("(empty)", span("(empty)"));
                        }
                        return Boolean.TRUE.equals(view.requireInputs().get("broken"))
                                ? f.fail("Error!", "Error!")
                                : f.view(() -> f.log("Child", span("Child")));
                    })
                    .input("update", (Action)
                            view -> f.trace.add("commit: " + view.current().get("hideChild")));
            final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
            props(parent).set("ref", ref);
            f.render(parent);
            f.expect("Child");
            f.output("Child");
            Js.<Effects>uncheckedCast(Objects.requireNonNull(ref.get("current")))
                    .setState(JsPropertyMap.of("hideChild", true));
            captureWarnings(() ->
                    f.harness.memory.flushSync(() -> f.root.render(parent.dup().input("broken", true), null)));
            f.expect("Error!", "(empty)", "commit: true");
            f.output("(empty)");
        } else {
            f.root.render(f.fail("error", "Oops!"), () -> f.trace.add("commit"));
            f.harness.advance(2000);
            f.harness.memory.flushExpired();
            f.root.render(f.view(() -> f.log("success", span("Everything is fine."))), () -> f.trace.add("commit"));
            f.flush();
            f.expect("error", "success", "commit", "commit");
            f.output("Everything is fine.");
        }
    }

    private static void finalRetry(Fixture f) {
        final var parent = f.view(() ->
                f.log("Parent", array(f.fail("BadRender", "oops"), f.view(() -> f.log("Sibling", span("Sibling"))))));
        f.root.render(parent, null);
        untilYield(f.harness, f.trace, 2);
        f.expect("Parent", "BadRender");
        untilYield(f.harness, f.trace, 1);
        f.expect("Sibling");
        f.reject(f.harness::flush, "oops");
        f.expect("Parent", "BadRender", "Sibling");
    }

    private static void multipleCommitFailures(Fixture f) {
        final int[] count = {0};
        final var broken = f.effects(view -> f.log("BadMount", null)).input("mount", (Action)
                view -> raise(new JsFailure("Error " + ++count[0])));
        final var tree = f.boundary(
                        array(broken, broken.dup(), broken.dup()),
                        "ErrorBoundary",
                        null,
                        error -> span("Number of errors: " + error),
                        (boundary, error) -> {
                            f.trace.add("componentDidCatch: " + message(error));
                            boundary.setStateWithCallback(
                                    (NativeView.StateUpdater) (state, props) -> {
                                        final var previous = null == state ? null : state.get("error");
                                        return JsPropertyMap.<@Nullable Object>of(
                                                "error", null == previous ? 1 : Js.asInt(previous) + 1);
                                    },
                                    null);
                        })
                .input("manual", true);
        f.render(tree);
        f.expect(
                "ErrorBoundary",
                "BadMount",
                "BadMount",
                "BadMount",
                "componentDidCatch: Error 1",
                "componentDidCatch: Error 2",
                "componentDidCatch: Error 3");
        f.output("Number of errors: 3");
    }

    private static void mounting(Fixture f, int number) {
        final boolean rethrow = number >= 10;
        final boolean synchronous = (number % 4) < 2;
        final boolean partial = 7 == number || 11 == number;
        final var boundaryName = rethrow ? "RethrowErrorBoundary render" : "ErrorBoundary render success";
        final var report = rethrow ? "RethrowErrorBoundary componentDidCatch" : "ErrorBoundary componentDidCatch";
        final var broken = f.fail("BrokenRender", "Hello");
        final var tree = f.boundary(
                broken,
                boundaryName,
                "ErrorBoundary render error",
                error -> span("Caught an error: " + message(error) + "."),
                (boundary, error) -> {
                    f.trace.add(report);
                    if (rethrow) {
                        raise(error);
                    }
                });
        if (synchronous) {
            final Reconciler.Task mount = () -> f.harness.memory.flushSync(() -> {
                if (9 == number || 13 == number) {
                    f.root.render(tree.dup().input("child", ReactNode.of("Before the storm.")), null);
                }
                f.root.render(tree, null);
            });
            if (rethrow) {
                f.reject(mount, "Hello");
                f.expect(boundaryName, "BrokenRender", report);
            } else {
                captureWarnings(mount);
                f.expect(boundaryName, "BrokenRender", report, "ErrorBoundary render error");
            }
        } else {
            f.root.render(tree, null);
            if (partial) {
                untilYield(f.harness, f.trace, 1);
                f.expect(boundaryName);
                f.empty();
            }
            if (rethrow) {
                f.reject(f.harness::flush, "Hello");
            } else {
                f.flush();
            }
            final var lines = new ArrayList<String>();
            if (!partial) {
                lines.add(boundaryName);
            }
            lines.addAll(List.of("BrokenRender", boundaryName, "BrokenRender", report));
            if (!rethrow) {
                lines.add("ErrorBoundary render error");
            }
            f.expect(lines);
        }
        if (rethrow) {
            f.empty();
        } else {
            f.output("Caught an error: Hello.");
        }
    }

    private static void schedulingFailure(Fixture f, int number) {
        f.root.render(span("a:1"), null);
        final Reconciler.Batch batch = () -> {
            f.root.render(span("a:2"), null);
            f.root.render(span("a:3"), null);
            if (15 == number) {
                f.harness.memory.batchedUpdates(() -> {
                    f.root.render(span("a:4"), null);
                    f.root.render(span("a:5"), null);
                    raise(new JsFailure("Hello"));
                });
            } else {
                raise(new JsFailure("Hello"));
            }
        };
        f.reject(
                () -> {
                    if (16 == number) {
                        f.harness.memory.flushSync(() -> f.harness.memory.batchedUpdates(batch));
                    } else {
                        f.harness.memory.batchedUpdates(batch);
                    }
                },
                "Hello");
        if (16 != number) {
            f.flush();
        }
        f.output(15 == number ? "a:5" : "a:3");
    }

    private static void rootReusable(Fixture f, int number) {
        if (19 == number) {
            f.render(f.effects(view -> host("div", null, null))
                    .input("unmount", (Action) view -> raise(new JsFailure("Hello"))));
            f.root.render(host("div", null, null), null);
            f.reject(f.harness::flush, "Hello");
        } else {
            if (18 == number) {
                f.render(f.view(() -> f.log("BrokenRender", null)));
                f.expect("BrokenRender");
            }
            f.root.render(f.fail("BrokenRender", "Hello"), null);
            f.reject(f.harness::flush, "Hello");
            f.expect("BrokenRender", "BrokenRender");
        }
        f.render(f.view(() -> f.log("Foo", null)));
        f.expect("Foo");
    }

    private static void abandonedBoundary(Fixture f, boolean unmountError) {
        if (unmountError) {
            final var unstable = f.effects(view -> null).input("unmount", (Action) view -> {
                f.trace.add("ThrowsOnUnmount componentWillUnmount");
                raise(new JsFailure("unmount error"));
            });
            final var boundary = f.boundary(
                    unstable,
                    null,
                    null,
                    error -> null,
                    (view, error) -> f.trace.add("Caught error: " + message(error)));
            final var root = f.effects(view -> boundary)
                    .input("unmount", (Action) view -> f.trace.add("Parent componentWillUnmount"));
            f.render(root);
            f.root.render(null, null);
            f.flush();
            f.expect("Parent componentWillUnmount", "ThrowsOnUnmount componentWillUnmount");
            f.empty();
            f.root.render(root.dup(), null);
        } else {
            final var parent = f.effects(view -> {
                if (Boolean.TRUE.equals(view.current().get("removed"))) {
                    return null;
                }
                final var child = f.effects(unused -> null).input("update", (Action) unused -> {
                    f.trace.add("child update");
                    view.setState(JsPropertyMap.of("removed", true));
                    raise(new JsFailure("update error"));
                });
                return f.boundary(child, null, null, error -> null, (boundary, error) -> f.trace.add("catch"))
                        .input("manual", true);
            });
            f.render(parent);
            captureWarnings(() -> f.harness.memory.flushSync(() -> f.root.render(parent.dup(), null)));
            f.flush();
            f.expect("child update");
            f.empty();
        }
    }

    private static void otherRoots(Fixture f, boolean uncaught) {
        final var a = f.root;
        final var b = f.harness.memory.createRoot(true);
        final var broken = f.fail("BrokenRender", "Hello");
        if (!uncaught) {
            a.render(
                    f.boundary(
                            broken,
                            null,
                            null,
                            error -> span("Caught an error: " + message(error) + "."),
                            (view, error) -> {}),
                    null);
            b.render(span("b:1"), null);
            f.flush();
            f.output("Caught an error: Hello.");
            rootShape(b, "span[b:1]()");
            return;
        }
        final var c = f.harness.memory.createRoot(true);
        final var d = f.harness.memory.createRoot(true);
        final var e = f.harness.memory.createRoot(true);
        final var g = f.harness.memory.createRoot(true);
        final var roots = List.of(a, b, c, d, e, g);
        final var update = List.of("x", "xb", "ax", "axc", "abcdx", "xbxdxf");
        for (int i = 0; update.size() > i; i++) {
            final var order = update.get(i);
            for (int j = 0; order.length() > j; j++) {
                final char key = order.charAt(j);
                roots.get(j).render('x' == key ? broken.dup() : span(key + ":" + (1 + i)), null);
            }
            drainRoots(f);
            for (int j = 0; order.length() > j; j++) {
                final char key = order.charAt(j);
                rootShape(roots.get(j), 'x' == key ? "" : "span[" + key + ":" + (1 + i) + "]()");
            }
        }
        final int[] completed = {0};
        roots.forEach(root -> root.unmount(() -> completed[0]++));
        f.flush();
        equal(roots.size(), completed[0]);
        roots.forEach(root -> rootShape(root, ""));
    }

    private static void drainRoots(Fixture f) {
        final var failures = new ArrayList<Object>();
        final boolean[] ran = {false};
        captureWarnings(() -> {
            for (int turns = 0; 100 > turns; turns++) {
                ran[0] = false;
                final var failure = thrownBy(() -> ran[0] = f.harness.turn());
                if (null != failure) {
                    failures.add(failure);
                } else if (!ran[0]) {
                    return;
                }
            }
            throw new AssertionError("Shared root scheduler should drain");
        });
        check(!failures.isEmpty(), "The pending root errors should propagate");
        for (final var failure : failures) {
            equal("Hello", message(failure));
        }
    }

    private static void unwindLegacyContext(Fixture f, boolean development) {
        final ViewConstructorFunction provider = LegacyProvider::new;
        props(provider).set("displayName", "Provider");
        final var keys = JsPropertyMap.of("message", ANY_CONTEXT);
        props(provider).set("contextTypes", keys);
        props(provider).set("childContextTypes", keys);
        final ViewConstructorFunction connector = LegacyConnector::new;
        props(connector).set("displayName", "Connector");
        props(connector).set("contextTypes", keys);
        final var deeper = legacy(
                provider,
                "b",
                legacy(
                        provider,
                        "c",
                        legacy(provider, "d", legacy(provider, "e", f.fail("BadRender", "render error")))));
        final var recovery = f.boundary(
                deeper,
                null,
                null,
                error -> legacy(
                        provider, "b", legacy(provider, "c", legacy(provider, "d", legacy(provider, "e", null)))),
                (view, error) -> {});
        final var warning = captureWarnings(() -> {
            f.root.render(legacy(provider, "a", array(recovery, ReactElement.createViewElement(connector))), null);
            f.harness.flush();
        });
        if (development) {
            check(
                    warning.contains("Legacy context API has been detected within a strict-mode tree")
                            && warning.contains("Please update the following components: Connector, Provider"),
                    "Expected a legacy context warning naming both components: " + warning);
        }
        f.output("a");
    }

    private static ReactElement legacy(ViewConstructorFunction provider, String value, @Nullable ReactNode child) {
        return ReactElement.createViewElement(provider).input("message", value).input("child", child);
    }

    private static void reconcilerFailures(Fixture f, int number, boolean development) {
        final var invalid = host("untagged", null, null);
        props(invalid).set("type", Js.undefined());
        final ViewConstructorFunction broken = Effects::new;
        props(broken).set("displayName", "BrokenRender");
        final var probe = ReactElement.createViewElement(broken)
                .input("output", (Function<Effects, @Nullable ReactNode>) view -> invalid);
        final var expected = "Element type is invalid: expected a string (for built-in components)"
                + " or a class/function (for composite components) but got: undefined.";
        if (27 == number) {
            final @Nullable Object[] failure = {null};
            f.root.render(invalid, null);
            captureWarnings(() -> failure[0] = thrownBy(f.harness::flush));
            final var error = message(Objects.requireNonNull(failure[0]));
            check(error.contains(expected), "Expected the invalid element error: " + error);
            if (development) {
                check(error.contains("forgot to export your component"), "Expected export guidance: " + error);
            }
            f.render(span("hi"));
            f.output("hi");
            return;
        }
        final var errors = new ArrayList<String>();
        final var tree = f.boundary(
                probe, null, null, error -> span(message(error)), (view, error) -> errors.add(message(error)));
        if (26 == number) {
            f.render(tree.dup().input("child", probe.dup().input("output", (Function<Effects, ReactNode>)
                    view -> host("span", null, null))));
        }
        f.render(tree);
        equal(1, errors.size());
        final var error = errors.get(0);
        check(error.contains(expected), "Expected the invalid element error: " + error);
        if (development) {
            check(
                    error.contains("forgot to export your component") && error.contains("BrokenRender"),
                    "Expected owner and export guidance: " + error);
        } else {
            equal(expected, error);
        }
        f.output(error);
    }

    private static void disposal(Fixture f) {
        final var broken = f.effects(view -> {
                    if (Boolean.TRUE.equals(view.current().get("fail"))) {
                        raise(new JsFailure("Hello."));
                    }
                    return null;
                })
                .input("unmount", (Action) view -> f.trace.add("BrokenRenderAndUnmount componentWillUnmount"));
        final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
        props(broken).set("ref", ref);
        final Function<ReactNode, ReactElement> parent =
                child -> f.effects(view -> child).input("unmount", (Action) view -> {
                    f.trace.add("Parent componentWillUnmount [!]");
                    raise(new JsFailure("One does not simply unmount me."));
                });
        f.render(parent.apply(parent.apply(broken)));
        Js.<Effects>uncheckedCast(Objects.requireNonNull(ref.get("current"))).setState(JsPropertyMap.of("fail", true));
        f.reject(f.harness::flush, "Hello.");
        f.expect(
                "Parent componentWillUnmount [!]",
                "Parent componentWillUnmount [!]",
                "BrokenRenderAndUnmount componentWillUnmount");
        f.empty();
    }

    private static void detachRef(Fixture f) {
        final var bar = f.effects(view -> span("Bar")).input("unmount", (Action) view -> f.trace.add("Bar unmount"));
        props(bar).set("ref", (Ref) value -> {
            if (null == value) {
                f.trace.add("barRef detach");
                raise(new JsFailure("Detach error"));
            } else {
                f.trace.add("barRef attach");
            }
        });
        f.render(f.view(() -> host("div", null, bar)));
        f.expect("barRef attach");
        rootShape(f.root, "div(span[Bar]())");
        f.root.render(f.view(() -> host("div", null, null)), null);
        f.reject(f.harness::flush, "Detach error");
        f.expect("barRef detach", "Bar unmount");
        f.empty();
    }

    private static void arbitraryThrow(Fixture f, boolean development) {
        final var value = JsPropertyMap.of("nonStandardMessage", "oops");
        final ViewConstructorFunction broken = Effects::new;
        props(broken).set("displayName", "BadRender");
        final var thrower = ReactElement.createViewElement(broken)
                .input("output", (Function<Effects, @Nullable ReactNode>) view -> {
                    f.trace.add("BadRender");
                    raise(value);
                    return null;
                });
        final var tree = f.boundary(
                f.view(() -> f.log("Indirection", thrower)),
                "ErrorBoundary (try)",
                "ErrorBoundary (catch)",
                error -> span("Caught an error: " + props(error).get("nonStandardMessage")),
                (view, error) -> {
                    check(value == error, "The boundary receives the original non-Error");
                    f.trace.add("componentDidCatch");
                });
        final var logs = new ArrayList<@Nullable Object>();
        f.root.render(tree, null);
        IsolatedErrorLoggingTest.observeLogs(f.harness::flush, logs::add);
        f.expect(
                "ErrorBoundary (try)",
                "Indirection",
                "BadRender",
                "ErrorBoundary (try)",
                "Indirection",
                "BadRender",
                "componentDidCatch",
                "ErrorBoundary (catch)");
        f.output("Caught an error: oops");
        equal(1, logs.size());
        if (development) {
            check(
                    Js.asString(logs.get(0)).contains("The above error occurred in the <BadRender> component:"),
                    "Development logs name the view for a raw thrown value");
        } else {
            check(value == logs.get(0), "Production logs receive the original non-Error");
        }
    }

    private static void siblings(Fixture f) {
        final var sibling = f.view(() -> f.log("BadRenderSibling", null));
        final var tree = f.boundary(
                array(f.fail("throw", "oops!"), sibling, sibling.dup()),
                "ErrorBoundary (try)",
                "ErrorBoundary (catch)",
                error -> f.log("ErrorMessage", span("Caught an error: " + message(error))),
                (view, error) -> f.trace.add("componentDidCatch"));
        f.render(tree);
        f.expect(
                "ErrorBoundary (try)",
                "throw",
                "BadRenderSibling",
                "BadRenderSibling",
                "ErrorBoundary (try)",
                "throw",
                "BadRenderSibling",
                "BadRenderSibling",
                "componentDidCatch",
                "ErrorBoundary (catch)",
                "ErrorMessage");
        f.output("Caught an error: oops!");
    }

    private static void boundaryLifecycle(Fixture f) {
        final ViewConstructorFunction type = UpdatingBoundary::new;
        final var tree = f.configure(
                        type,
                        f.fail("throw", "oops!"),
                        "render",
                        "render error message",
                        error -> span("Caught an error: " + message(error)))
                .input("report", (BiConsumer<Catching, Object>) (view, error) -> f.trace.add("did catch"));
        f.render(tree);
        f.expect("render", "throw", "render", "throw", "did catch", "render error message", "did update");
        f.output("Caught an error: oops!");
    }

    private static void errorStack(Fixture f, boolean derived) {
        final var broken = f.fail("BrokenRender", "Hello");
        props(f.view).set("displayName", "BrokenRender");
        if (derived) {
            props(f.derived).set("getDerivedStateFromError", (DeriveWithInfo) (error, info) -> {
                check(Js.isTripleEqual(Js.undefined(), info), "Derive never receives extra component information");
                return JsPropertyMap.<@Nullable Object>of("error", error);
            });
            final var tree =
                    f.configure(f.derived, broken, null, null, error -> span("Caught an error: " + message(error)));
            f.render(tree);
            f.output("Caught an error: Hello");
        } else {
            props(f.boundary).set("displayName", "ErrorBoundary");
            final var tree = f.boundary(
                            broken, null, null, error -> span("Caught an error:" + error + "."), (view, error) -> {})
                    .input("manual", true)
                    .input("info", (BiConsumer<Catching, ReactErrorInfo>)
                            (view, info) -> view.setState(JsPropertyMap.<@Nullable Object>of("error", info.componentStack)));
            f.render(tree);
            f.output("Caught an error:\n    in BrokenRender\n    in ErrorBoundary.");
        }
    }

    private static void moduleProvider(Fixture f, boolean development) {
        final ViewConstructorFunction provider = Effects::new;
        props(provider).set("displayName", "Provider");
        props(provider).set("functionComponent", true);
        props(provider).set("childContextTypes", JsPropertyMap.of("x", ANY_CONTEXT));
        props(provider).set("getDerivedStateFromProps", (Derive) state -> {
            raise(new JsFailure("Oops!"));
            return JsPropertyMap.<@Nullable Object>of();
        });
        final var module = ReactElement.createViewElement(provider)
                .input("output", (Function<Effects, ReactNode>) view -> {
                    final var owner = CurrentOwner.get();
                    ((Reconciler.Fiber) Objects.requireNonNull(owner)).initializeModuleComponent(view);
                    return ReactNode.of("Hi");
                })
                .input("module", true);
        f.root.render(module, null);
        final @Nullable Object[] failure = {null};
        final var warnings = captureWarnings(() -> failure[0] = thrownBy(f.harness::flush));
        equal("Oops!", message(Objects.requireNonNull(failure[0])));
        if (development) {
            check(
                    warnings.contains("Legacy context API has been detected within a strict-mode tree")
                            && warnings.contains("Please update the following components: Provider"),
                    "Expected warning for module-style provider: " + warnings);
        }
    }

    private static ReactElement span(Object text) {
        return host("span", null, null).input("prop", text);
    }

    private static String message(Object value) {
        return Js.asString(props(value).get("message"));
    }

    @JsMethod
    private static native void raise(Object error);

    @JsMethod
    private static native @Nullable Object thrownBy(Reconciler.Task task);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Error")
    private static final class JsFailure {
        JsFailure(String message) {}
    }

    @JsFunction
    private interface Derive {
        JsPropertyMap<@Nullable Object> apply(Object error);
    }

    @JsFunction
    private interface Action {
        void accept(Effects view);
    }

    @JsFunction
    private interface Ref {
        void accept(@Nullable Object value);
    }

    @JsFunction
    private interface DeriveWithInfo {
        JsPropertyMap<@Nullable Object> apply(Object error, @Nullable Object info);
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final ViewConstructorFunction view = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction effects = Effects::new;
        final ViewConstructorFunction boundary = Catching::new;
        final ViewConstructorFunction derived = Recovering::new;
        final List<String> trace = new ArrayList<>();

        ReactElement view(Supplier<@Nullable ReactNode> output) {
            return ReactElement.createViewElement(view).input("output", output);
        }

        ReactElement fail(String label, String failure) {
            return view(() -> {
                trace.add(label);
                raise(new JsFailure(failure));
                return null;
            });
        }

        ReactElement effects(Function<Effects, @Nullable ReactNode> output) {
            return ReactElement.createViewElement(effects).input("output", output);
        }

        ReactElement derived(
                ReactNode child, Function<Object, @Nullable ReactNode> fallback, String tryLabel, String catchLabel) {
            props(derived).set("getDerivedStateFromError", (Derive) error -> {
                trace.add("getDerivedStateFromError");
                return JsPropertyMap.<@Nullable Object>of("error", error);
            });
            return configure(derived, child, tryLabel, catchLabel, fallback);
        }

        ReactElement boundary(
                ReactNode child,
                @Nullable String tryLabel,
                @Nullable String catchLabel,
                Function<Object, @Nullable ReactNode> fallback,
                BiConsumer<Catching, Object> report) {
            return configure(boundary, child, tryLabel, catchLabel, fallback).input("report", report);
        }

        private ReactElement configure(
                ViewConstructorFunction type,
                ReactNode child,
                @Nullable String tryLabel,
                @Nullable String catchLabel,
                Function<Object, @Nullable ReactNode> fallback) {
            return ReactElement.createViewElement(type)
                    .input("child", child)
                    .input("trace", trace)
                    .input("try", tryLabel)
                    .input("catch", catchLabel)
                    .input("fallback", fallback);
        }

        @Nullable
        ReactNode log(String label, @Nullable ReactNode output) {
            trace.add(label);
            return output;
        }

        void render(ReactNode element) {
            root.render(element, null);
            flush();
        }

        void flush() {
            captureWarnings(harness::flush);
        }

        void reject(Reconciler.Task task, String error) {
            final @Nullable Object[] failure = {null};
            captureWarnings(() -> failure[0] = thrownBy(task));
            check(null != failure[0], "Expected failure " + error);
            equal(error, message(Objects.requireNonNull(failure[0])));
        }

        void expect(String... labels) {
            expect(List.of(labels));
        }

        void expect(List<String> lines) {
            equal(lines, trace);
            trace.clear();
        }

        void output(String text) {
            rootShape(root, "span[" + text + "]()");
        }

        void empty() {
            rootShape(root, "");
        }
    }

    private static class Recovering extends NativeView {
        @JsConstructor
        Recovering(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of());
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            final var error = Objects.requireNonNull(state()).get("error");
            final var label = inputs.get(null == error ? "try" : "catch");
            if (null != label) {
                Js.<List<String>>uncheckedCast(inputs.get("trace")).add(Js.asString(label));
            }
            return null == error
                    ? Js.uncheckedCast(inputs.get("child"))
                    : Js.<Function<Object, @Nullable ReactNode>>uncheckedCast(inputs.get("fallback"))
                            .apply(error);
        }
    }

    private static class Catching extends Recovering implements OnComponentDidCatch {
        @JsConstructor
        Catching(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var inputs = requireInputs();
            Js.<BiConsumer<Catching, Object>>uncheckedCast(inputs.get("report")).accept(this, error);
            final var receive = inputs.get("info");
            if (null != receive) {
                Js.<BiConsumer<Catching, ReactErrorInfo>>uncheckedCast(receive).accept(this, info);
            }
            if (!Boolean.TRUE.equals(inputs.get("manual"))) {
                setState(JsPropertyMap.of("error", error));
            }
        }
    }

    private static final class UpdatingBoundary extends Catching implements OnComponentDidUpdate {
        @JsConstructor
        UpdatingBoundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            Js.<List<String>>uncheckedCast(requireInputs().get("trace")).add("did update");
        }
    }

    @JsFunction
    private interface ChildContext {
        JsPropertyMap<@Nullable Object> get();
    }

    private static final class LegacyProvider extends NativeView {
        @JsConstructor
        LegacyProvider(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            props(this).set("getChildContext", (ChildContext) () -> {
                final var context = props(Objects.requireNonNull(props(this).get("context")));
                final var outer = context.get("message");
                final var value = (null == outer ? "" : Js.asString(outer))
                        + Js.asString(requireInputs().get("message"));
                return JsPropertyMap.<@Nullable Object>of("message", value);
            });
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.uncheckedCast(requireInputs().get("child"));
        }
    }

    private static final class LegacyConnector extends NativeView {
        @JsConstructor
        LegacyConnector(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var context = props(Objects.requireNonNull(props(this).get("context")));
            return span(Objects.requireNonNull(context.get("message")));
        }
    }

    private static final class Effects extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        Effects(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of());
            if (Boolean.TRUE.equals(inputs.get("module"))) {
                props(this)
                        .set("getChildContext", (ChildContext) () -> JsPropertyMap.<@Nullable Object>of("foo", "bar"));
            }
        }

        JsPropertyMap<@Nullable Object> current() {
            return Objects.requireNonNull(state());
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.<Function<Effects, @Nullable ReactNode>>uncheckedCast(
                            requireInputs().get("output"))
                    .apply(this);
        }

        @Override
        public void componentDidMount() {
            effect("mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            effect("update");
        }

        @Override
        public void componentWillUnmount() {
            effect("unmount");
        }

        private void effect(String name) {
            final var action = requireInputs().get(name);
            if (null != action) {
                Js.<Action>uncheckedCast(action).accept(this);
            }
        }
    }
}
