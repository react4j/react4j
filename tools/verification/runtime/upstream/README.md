# React 16.6 source tests and React4j

Use this map to find the upstream assertion for a change to React4j, see how it ran against the Java renderer, and choose the corresponding Chromium regression. The reference is React v16.6.0 at `8b97a9c36f2ef7d2c570b25e988b8ca832d9b436`. [Upstream source](https://github.com/facebook/react/tree/8b97a9c36f2ef7d2c570b25e988b8ca832d9b436/packages)

[Individual upstream cases and both build modes](cases.tsv) identify each original Jasmine spec by suite, ID and full name. A paired assertion means both implementations passed, the fixture called the Java renderer, and Jest counted at least one effective assertion. A production `toWarnDev` runs its callback without checking a warning and contributes zero effective assertions. A smoke case reached Java with no effective assertion. A helper-only pass never entered the renderer. Mixed tests can also assert on upstream JSX, server rendering, or fixture helpers. Failures record an explicit adapter or runtime limit when known and indicate whether execution reached a Java renderer entry point. The failure columns show the first part of the Jest diagnostic. The call and assertion columns give the counts used to label each case. The run directory retains Jest’s full diagnostics. Line and branch coverage use the repository’s separate instrumented browser run.

The shared Chromium case index maps original names to Java fixtures. Rows marked “related scenario” cover part of an upstream assertion set. ReactMultiChildReconcile has 27 array-applicable cases and two iterator-only cases. The shared Java fixture runs its array transitions on both renderers. [Chromium case index](chromium.tsv) · [License for upstream case names, diagnostic text and derived vectors](../../../../third_party/react/LICENSE)

The selected suites include the React core files that import ReactDOM, client DOM and event files, clean-client assertions from the server integration files, and all 27 reconciler suites in the pinned checkout. The full upstream development coverage run passed 3,585 tests across 181 suites, with 26 skipped. To reproduce that separate measurement in the pinned checkout, use Node 10 and run `yarn test --coverage --maxWorkers=2`.

## Results

The comparison ran 1,894 cases in 117 suites at runtime commit `f58188962`. Stock React passed all 1,870 enabled cases in both modes. The mode names refer to Jest and the Java runtime environment defines. The optimized production application is tested separately in Chromium.

| Outcome of original case | Development | Production |
| --- | ---: | ---: |
| Passed with a Java renderer call and an effective assertion | 1,015 | 972 |
| Passed with a Java renderer call and no effective assertion | 43 | 204 |
| Passed without entering the Java renderer | 124 | 125 |
| Failed after entering the Java renderer | 467 | 348 |
| Failed before entering the Java renderer | 45 | 44 |
| Failed with an explicit test-adapter limit | 157 | 158 |
| Failed on an unsupported runtime feature | 19 | 19 |
| Skipped upstream | 24 | 24 |

All 198 cases in the input, select, option and textarea suites passed on both sides. Of those, 188 entered the Java renderer. Of the 151 cases in ReactDOMComponent, 133 entered Java. All 133 passed in production and 131 passed in development. The other two expect warnings for the legacy ShadyDOM polyfill.

Across 341 cases in all 27 reconciler suites, 115 had paired assertions in development and 132 in production. Every Java case that passed in the previous 113-suite comparison still passed. Among those same suites, 49 more cases passed in development and 28 more in production. The two original host-context cases verify host creation and the root container passed to both commit callbacks. The original Suspense case that resolves a thenable during a partially flushed render verifies that rendering restarts before the sibling runs. ReactSuspense-test.internal.js passed nine of ten cases in each mode. The remaining case requires React.lazy class loading.

## Which tests exercise which contract?

| React4j contract | Upstream suites and cases | React4j checks |
| --- | --- | --- |
| Element records, generated inputs and stateful views | ReactElement, ReactJSXElement, ReactElementClone, ReactES6Class and ReactCompositeComponentState. The original ReactElement suite removes the global Symbol before loading React. J2CL requires Symbol. Creating elements in a JSX fixture alone exercises upstream React. | ElementBrowserTest uses the typed Java API on both runtimes to check the marker, type, key, ref, owner, copied inputs, shallow children and the rendered result. Generated input tests cover defaults and equality. RuntimeParityBrowserTest exercises object state patches and updates skipped by a predicate. |
| Mount, update, snapshot and unmount | ReactComponentLifeCycle and ReactUpdates include nested mount-ready callbacks, updates queued in mount and updates blocked by an ancestor. Several original fixtures also assert deprecated class hooks. | RuntimeBrowserTest.nestedViewLifecycle observes what is mounted at each hook and ref callback. RuntimeParityBrowserTest.lifecycle, committedChildren and skippedUpdates exercise children behind a skipped parent. |
| Root callbacks, portals and batching | ReactMount, ReactMountDestruction, ReactDOMFiber and ReactUpdates cover callback receivers, reuse, changing keys, portal containers and nested roots. | rootIdentity, mountWithinBatch, rootUpdates, alternateContainers, iframePortal and batchedRootState use the public Java entry points. nestedUpdateLimit checks bounded updates and subsequent root recovery. |
| Child identity and text | ReactIdentity, ReactMultiChildReconcile, ReactFragment, ReactTopLevelFragment and ReactTopLevelText. The list suite combines arrays and two JavaScript iterator protocols within the same test. | ChildReconciliationTest runs its array permutations in both directions, compares host and component identity, and checks ref clearing and remount. fragmentIdentity and keyedAndImplicitSlots exercise keys that look like implicit indices, sparse arrays and nested fragments. |
| Internal nodes | ReactChildFiber and the ReactNoop reconciler suites test logical children, state preservation, host output and scheduled work. | FiberReconciliationTest inspects actual parent/child links and object identity across commits. Its tree contains only fragments and never creates a host child. Original ReactNoop cases inspect committed host records and their identity directly. |
| Context and refs | ReactNewContext, ReactContextValidator, refs, refs-destruction and ReactComponent. Legacy context, changed-bit masks and string refs appear in the originals. | contextThroughBailout and contextUnwindsOnRenderError cover nested providers, missed updates, NaN and signed zero. replaceRefs, nestedViewLifecycle and abortedRefs cover callback/object refs, order, cleanup and abandoned mounts. |
| Scheduling and error recovery | ReactIncrementalScheduling, ReactIncrementalErrorHandling, ReactIncrementalErrorReplay, ErrorBoundaryReconciliation and ReactLegacyErrorBoundaries. | Concurrent Chromium cases observe uncommitted DOM and refs. renderFailureSiblings, concurrentRootErrorIsolation and concurrentErrorWithPendingState exercise multiple render faults, replay, priority and recovery in an unrelated root. commitErrors covers a failing snapshot, callback or ref. failedRenderTeardown and errorLoggerRecovery check the state visible at unmount, failed render replay, one log per failure and recovery when the logger throws. |
| Suspense | ReactSuspense, ReactSuspenseWithNoopRenderer and ReactDOMSuspensePlaceholder cover fallback selection, hidden hosts, retries and multiple boundaries. | suspenseWithoutFallback distinguishes omitted and explicit null fallback. independentSuspenseSiblings resolves two boundaries in reverse order. suspendedListVisitsSiblings exercises multiple pending children in one boundary and checks when their refs attach. hiddenPrimaryUpdates follows direct and nested stateful children through two hide/reveal cycles. It checks which updates apply while hidden, when the fallback remounts, and whether the original instance and host node survive. |
| Attributes, style, text, HTML and namespaces | DOMPropertyOperations, CSSPropertyOperations, ReactDOMAttribute, ReactDOMComponent, ReactDOMSVG and the clean-client cases of ReactDOMServerIntegration*. | EventsBrowserTest.runHostProperties and OriginalHostCasesBrowserTest exercise typed and raw properties, invalid content, option text updates, noscript content, namespace attributes and native mutation ordering. |
| Inputs, selection, radios and form reset | ReactDOMInput, ReactDOMSelect, ReactDOMOption, ReactDOMTextarea and ChangeEventPlugin include nested change dispatch, radio coordination, missing options and form resets. | EventsBrowserTest checks DOM properties, attributes, native events, different roots/forms, processing order and the final controlled value. |
| Synthetic events and delegation | ReactDOMEventListener, ReactDOMComponentTree, ReactBrowserEventEmitter, SelectEventPlugin, EnterLeaveEventPlugin and Synthetic* include private plugin operations and public dispatch. | EventsBrowserTest dispatches native events. Its checks observe listener installation, rejection of invalid handlers, event-pool cleanup, target removal during dispatch, propagation through portals, keyboard fields, event release, and media/form events that do not bubble. |
| DevTools, Arez and Sting | The JSX Profiler and interaction tracing suites use their own React APIs. | React4j checks the extension protocol, then loads the installed extension to inspect, highlight and profile an application. Application tests exercise generated views and both integrations. [Browser and extension verification](../README.md) |

## Reconciler isolation

Upstream separates the Fiber data structure, child matching, scheduling and commit phases and supplies mutation and persistence host configurations. React4j keeps its Root, Fiber and child-list reconciliation in Reconciler. The browser supplies native DOM mutation, validation and scheduling through DomReconcilerHost and BrowserScheduler. The memory host supplies plain host records with stable identities, a controllable scheduler and host operation counters. [Upstream reconciler](https://github.com/facebook/react/tree/v16.6.0/packages/react-reconciler/src) · [Java reconciler](../../../../dom/src/main/java/react4j/dom/Reconciler.java)

The ReactNoop and react-test-renderer adapters call the same compiled Java Reconciler in Jest's Node environment. They consume actual committed arrays and records from MemoryRenderer. The adapters drive finite slices or pause on yielded values at the reconciler's checkpoints, and Jest's clock drives delayed Suspense work alongside the tests' timer fixtures. In development, a memory root reads the upstream fixture's StrictMode replay flag. React4j's own work units and priorities can produce different results on tests that require React's exact scheduler behavior. Persistence, independent work expiration and the upstream Scheduler tracing bundle check remain outside the adapter. ReactFiberHostContext supplies its original host callbacks to the Java reconciler, including the container passed to commit hooks. The private ReactIncrementalErrorReplay case checks the original JavaScript Fiber's stash utility and has no Java calls. React.lazy descriptors require runtime support that React4j lacks. [Memory host](../java/react4j/dom/MemoryRenderer.java) · [Jest adapter](noop.js)

The full upstream coverage run executed 3,416 of 3,677 reconciler source lines (92.9%) and 2,999 of 3,963 branches (75.7%). React4j’s instrumented Chromium fixtures at `f58188962` executed 1,532 of 1,618 lines (94.7%) and 1,005 of 1,073 branches (93.7%) in Reconciler.java. The upstream denominator spans 42 source files, including additional host configurations and scheduling facilities. The Java counters use J2CL source maps. The paired case matrix above measures observable test agreement.

## Reproduce a comparison

Use a clean React 16.6.0 source checkout. Install its locked dependencies with Yarn Classic 1.22.22 and Node 10.24.1. Run the comparison itself with modern Node (verified with 26.12.1), Python 3 and the Bazel version pinned in this repository. The comparison requires write access under the upstream `scripts/jest` directory for a temporary Jest configuration.

```sh
git clone --depth 1 --branch v16.6.0 https://github.com/facebook/react.git /tmp/react-16.6.0
(cd /tmp/react-16.6.0 && yarn install --frozen-lockfile)
bazel build //tools/verification/runtime:upstream_renderer //tools/verification/runtime:upstream_memory_renderer
bundles="$(bazel info bazel-bin)/tools/verification/runtime"
work=$(mktemp -d)
for mode in development production; do
  python3 tools/verification/runtime/upstream/compare.py /tmp/react-16.6.0 \
    "$bundles/upstream_renderer.js" "$work/$mode" --mode "$mode" \
    --reconciler-bundle "$bundles/upstream_memory_renderer.js"
done
python3 tools/verification/runtime/upstream/report.py "$work/development/cases.json" "$work/production/cases.json" > "$work/cases.tsv"
diff -u tools/verification/runtime/upstream/cases.tsv "$work/cases.tsv"
```

Pass `--suite packages/react-dom/src/__tests__/ReactDOMInput-test.js` to run a single original suite. Reconciler suites require the isolated bundle flag and run after a Node smoke test verifies memory rendering, scheduling and host bookkeeping. The output contains the Jest logs, failures, test-side adaptations and per-case results for both runtimes. The generated ReactNewContext fuzz case chooses random actions, so its call count before reaching an adapter limit can vary between runs. Both Jest modes use the inspectable J2CL BUNDLE with the requested environment defines. The separate shared Chromium suites compile and execute the actual optimized Java application and stock ReactDOM production script. [Chromium commands](../README.md)

The source fixtures use their original React JSX constructor. JavaScript class fixtures construct a Java NativeView and forward supported lifecycle methods. String refs and findDOMNode are fixture adaptations used to obtain real committed instances. Private context reads in render use the active Java Fiber's scope. This lets the original react-cache resource suspend or resolve inside a Java render. The bridge forwards render, batching, portals, unmount and concurrent work to compiled Java. Separate Chromium fixtures start from React4j's typed API and generated views.

Some original cases require server rendering, hydration, JavaScript iterables, legacy context or lifecycle methods, functional setState and completion callbacks, or wrappers such as React.memo, forwardRef and the public JSX Profiler. The server integration suites contribute only their clean-client cases. Development diagnostics are recorded separately. The external container mutation case also differs. After outside code overwrites a managed container with `innerHTML`, stock ReactDOM throws when replacing the old key, while the Java renderer tolerates the missing node. Upstream marks external removal of React-managed contents unsupported.
