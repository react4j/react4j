# React 16.6 source tests and React4j

Use this map to find the upstream assertion for a change to React4j, see how it ran against the Java renderer, and choose the corresponding Chromium regression. The reference is React v16.6.0 at `8b97a9c36f2ef7d2c570b25e988b8ca832d9b436`. [Upstream source](https://github.com/facebook/react/tree/8b97a9c36f2ef7d2c570b25e988b8ca832d9b436/packages)

## Finding an assertion

Start with the suite name in the [stock inventory](inventory.tsv). It covers all 181 suites. Stock passed 3,585 cases in development and 3,575 in production, with 26 skipped in each. The union contains 3,612 cases. A case may have a different ID or be absent in the other mode. The occurrence column distinguishes repeated titles within a suite.

Each location is the original line and column of a direct `expect(...)` call reached by that case. A path identifies an assertion in a shared fixture file. The collector records a repeated assertion site once per case and build. An empty site list can mean that the case checks for successful completion, checks an invariant by throwing, or uses an assertion helper outside the test sources. Reading the case also reveals conditional assertions in paths the stock configuration did not enter.

The assertion maps link original checks to Java methods. **Ported** and **covered** identify direct checks. **Adapted** explains how a Java fixture exercises the same observation. Excluded assertions identify the contract involved. Cases outside the port's scope can be excluded as a group in the stock inventory. The audit fails for a missing applicable case, an unmapped executed assertion in a reviewed case, a partial mapping or a missing Java fixture.

| Upstream behavior | Checked-in assertion map |
| --- | --- |
| Element creation, JSX records and cloning | [Elements](element-assertions.tsv), [element validation](element-validator-assertions.tsv), [component records](component-records-assertions.tsv) |
| Class lifecycle, snapshots, context and state updates | [Class lifecycle](component-lifecycle-assertions.tsv), [class context](class-context-assertions.tsv), [class state](class-state-assertions.tsv), [class diagnostics](class-shape-assertions.tsv), [batched and synchronous updates](update-scheduling-assertions.tsv) |
| Derived and legacy error boundaries, lifecycle order and recovery | [Error boundaries](error-boundary-assertions.tsv) |
| Component integration and development diagnostics | [Class variants](class-integration-assertions.tsv), [composite diagnostics](composite-diagnostics-assertions.tsv), [functions](function-component-assertions.tsv), [forwarded refs](forward-ref-assertions.tsv), [StrictMode](strict-mode-assertions.tsv) |
| Event emitter and synthetic events | [Events](event-assertions.tsv) |
| DOM attributes, SVG, styles, text and raw HTML | [DOM host](host-assertions.tsv) |
| Input, select, option and textarea | [Forms](forms-assertions.tsv) |
| Mounting and DOM integration | [DOM roots](dom-root-assertions.tsv), [client entry points](dom-client-candidates-assertions.tsv), [clean client mounts](clean-client-mount-assertions.tsv), [elements](clean-client-elements-assertions.tsv), [forms](clean-client-forms-assertions.tsv), [context](clean-client-context-assertions.tsv) |
| Programmatic Profiler callbacks and timing | [Profiler](profiler-assertions.tsv), [DevTools commits](profiler-devtools-assertions.tsv), [test host instances](test-renderer-host-assertions.tsv) |
| JavaScript component props and context diagnostics | [PropTypes](prop-type-assertions.tsv) |
| Behavior shared with other renderers and packages | [Shallow renderer](shallow-renderer-assertions.tsv), [ART and React Native](other-host-assertions.tsv), [subscriptions, cache and record consumers](cross-package-client-assertions.tsv) |
| In-memory reconciliation | [Fragments and boundaries](reconciler-fragment-assertions.tsv), [persistence](reconciler-persistence-assertions.tsv), [scheduling](reconciler-scheduling-assertions.tsv), [state queues](reconciler-updates-assertions.tsv), [lazy and memo](reconciler-wrapped-assertions.tsv), [Suspense](reconciler-suspense-assertions.tsv), [no-op host Suspense](reconciler-suspense-noop-assertions.tsv) |
| In-memory commits and interrupted work | [Reflection](reconciler-reflection-assertions.tsv), [error logging and replay](reconciler-error-logging-assertions.tsv), [triangle workloads](reconciler-triangle-assertions.tsv), [side effects](reconciler-side-effects-assertions.tsv), [incremental work](reconciler-incremental-assertions.tsv), [error handling](reconciler-error-handling-assertions.tsv), [context](reconciler-context-assertions.tsv), [User Timing](reconciler-timing-assertions.tsv) |

The Java port covers client rendering, including ordinary batched updates, synchronous flushing and Profiler callbacks. The scope excludes hydration, server rendering, the experimental `root.createBatch()` manual commit API, scheduler tracing subscribers and the numeric element protocol used without `Symbol`. Mixed cases retain their applicable client assertions. Development diagnostics are compared by condition, affected component and diagnostic information, with exact text where the Java API promises it. The unmodified Jest suite can still fail an exact upstream wording check.

To regenerate the stock inventory, run these commands from React4j with Node 10.24.1 and Yarn Classic 1.22.22 on `PATH` and the upstream checkout's locked dependencies installed. The collector instruments test sources before the stock compiler runs, including TypeScript and CoffeeScript. Both ordinary and instrumented stock runs passed the same cases and snapshots.

```sh
work=$(mktemp -d)
for mode in development production; do
  python3 tools/verification/runtime/upstream/inventory.py collect /tmp/react-16.6.0 "$work" --mode "$mode"
done
python3 tools/verification/runtime/upstream/inventory.py report "$work" > "$work/inventory.tsv"
python3 tools/verification/runtime/upstream/inventory.py audit
```

The audit covers 2,246 individually mapped cases. In 2,028 cases, every applicable assertion has a Java counterpart. The other 218 contain only excluded or upstream-skipped assertions. All 1,366 cases without an individual map have an explicit scope exclusion in the stock inventory. The audit checks source locations and references to 401 Java fixture methods. To see what a test observes, follow the assertion row to that method.

## Running original cases against both runtimes

[Individual upstream cases and both build modes](cases.tsv) identify each original Jasmine spec by suite, ID and full name. A paired assertion means both implementations passed, the fixture called the Java renderer, and Jest counted at least one effective assertion. A production `toWarnDev` runs its callback without checking a warning and contributes zero effective assertions. A smoke case reached Java with no effective assertion. A helper-only pass never entered the renderer. Mixed tests can also assert on upstream JSX, server rendering, or fixture helpers. Failures record an explicit adapter or runtime limit when known and indicate whether execution reached a Java renderer entry point. The failure columns show the first part of the Jest diagnostic. The call and assertion columns give the counts used to label each case. The run directory retains Jest’s full diagnostics. Line and branch coverage use the repository’s separate instrumented browser run.

The shared Chromium case index maps original names to Java fixtures. Rows marked “related scenario” cover part of an upstream assertion set. ReactMultiChildReconcile has 27 array-applicable cases and two iterator-only cases. The shared Java fixture runs its array transitions on both renderers. [Chromium case index](chromium.tsv) · [License for upstream case names, diagnostic text and derived vectors](../../../../third_party/react/LICENSE)

The selected suites include forwardRef, the React core files that import ReactDOM, client DOM and event files, clean-client assertions from the server integration files, and all 27 reconciler suites in the pinned checkout. The full upstream development coverage run passed 3,585 tests across 181 suites, with 26 skipped. To reproduce that separate measurement in the pinned checkout, use Node 10 and run `yarn test --coverage --maxWorkers=2`.

## Results

The main comparison at `a66dd4212` ran 1,900 cases in 118 suites. Stock React passed all 1,876 enabled cases in each mode. With the Java adapter, 1,837 passed in development and 1,843 in production. Compared with `d9e4b006e`, 67 more development and 18 more production originals passed. No previous pass or level of Java assertion coverage was lost. The mode names refer to Jest and the Java runtime environment defines. Chromium separately tests the optimized production applications.

| Outcome of original case | Development | Production |
| --- | ---: | ---: |
| Passed with a Java renderer call and an effective assertion | 1,637 | 1,455 |
| Passed with a Java renderer call and no effective assertion | 49 | 237 |
| Passed without entering the Java renderer | 151 | 151 |
| Failed after entering the Java renderer | 24 | 17 |
| Failed before entering the Java renderer | 2 | 2 |
| Failed with an explicit test-adapter limit | 13 | 14 |
| Failed on an unsupported runtime feature | 0 | 0 |
| Skipped upstream | 24 | 24 |

All 84 original DOM error-boundary cases passed on both runtimes and in both modes. The Java translations exercise both recovery strategies in 77 Chromium tests sharing seven identical scenarios. Five original event-emitter cases now look up listeners in committed Java event state, disable and re-enable dispatch, and pass using native clicks. Three other emitter originals use ReactDOM’s private event-plugin registration API. Their Java counterparts observe native listener registrations and change delivery.

All 198 cases in the input, select, option and textarea suites passed on both sides. Of those, 188 entered the Java renderer. All seven BeforeInput cases exercised Java, including the legacy composition capabilities simulated in Chromium. Of the 151 cases in ReactDOMComponent, 133 entered Java. All 133 passed in both modes, including the two development diagnostics for the legacy ShadyDOM polyfill.

The main matrix retains 39 development and 33 production failures. The originals rely on hydration or server adoption, manual root commits, scheduler tracing, private event registries, missing global `Symbol`, a JavaScript callback of the wrong type or a destroyed jsdom environment. The Java mappings retain mixed-case observations such as stable document bodies, adjacent text, fallback visibility and listener delivery. The per-case report gives the first failing assertion in each mode. [Original outcomes](cases.tsv) · [DOM roots](dom-root-assertions.tsv) · [Events](event-assertions.tsv)

All 324 enabled cases in the 27 upstream reconciler suites passed against both runtimes in both modes. Both runtimes skipped the same 17 upstream cases. In development, 307 passes entered Java and made an effective assertion, 13 entered Java without one, and four exercised helpers. The production counts were 301, 19 and four. The matrix includes every original Suspense, work expiration, persistence and User Timing case. The six additional forwardRef cases also passed in both modes.

The second matrix covers 30 suites. Stock passed all 373 development and 371 production cases. With the Java adapter, 302 and 300 passed respectively. In both modes all 47 test-renderer originals passed, including traversal, asynchronous updates, public instances, host mocks and portal interoperation. The 19 cache and subscription originals and all three DevTools integration originals passed. [Individual results](additional-cases.tsv) · [Installed browser extension](../README.md)

Sixteen of the 71 remaining failures per mode require scheduler tracing or a held commit. Five ref tests and one TestUtils test start with hydration. Two TestUtils tests snapshot the upstream helper's catalog of event names. Seven tests use legacy subtree mounting. The remaining 40 come from the shallow, ART and React Native fixtures. Of their 107 originals, 67 passed entirely in upstream helpers, 31 failed before entering Java and nine failed after an ART fixture entered the DOM adapter. Java fixtures exercise the shared full-renderer assertions in 43 shallow and 25 other-host cases. The maps identify each shared observation and each platform-specific exclusion. [Shallow assertions](shallow-renderer-assertions.tsv) · [ART and React Native assertions](other-host-assertions.tsv)

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

The DOM and in-memory renderers use the same Java reconciler. The standalone `react4j.reconciler` package depends on React4j core. Its `Root` schedules work and commits effects. The package-private `Fiber` inside `Reconciler` holds component state, matches children and tracks pending work. Hosts receive a read-only view of the fiber. `DomReconcilerHost` supplies browser operations. The memory host supports mutation and persistence. Persistence builds a new tree at commit, reuses unchanged branches and leaves earlier snapshots readable. Another platform can supply host operations and a scheduler while reusing this engine. The existing DOM Maven artifact includes the engine. [Java reconciler](../../../../reconciler/src/main/java/react4j/reconciler/Reconciler.java) · [Host interface](../../../../reconciler/src/main/java/react4j/reconciler/ReconcilerHost.java) · [Upstream reconciler](https://github.com/facebook/react/tree/v16.6.0/packages/react-reconciler/src)

All 341 cases in the 27 reconciler suites have individual assertion maps. Of the 324 enabled originals, 319 have Java counterparts for every applicable assertion. The other five exercise private Fiber-copy or Map-polyfill machinery, the unbound memo factory warning, or scheduler tracing bundle compatibility. Stock skips 17 cases in both modes.

The original ReactNoop and test-renderer fixtures run without a DOM. The checked-in Java translations also run directly on the memory host in Node and in the repository’s Chromium gate. [Isolated Java cases](../java/react4j/reconciler) · [Test entry points](../isolated_cases.js) Jest supplies the clock and controls when a slice yields. The Java scheduler owns priority, expiration and selection of the next root. Tests read committed in-memory records, host counters and callback order. The original host-context fixture supplies its own host and verifies the container passed to its commit hooks. Four helper-only passes check two JSX memo warnings, the private Fiber replay helper and the compatibility of the upstream scheduler tracing bundle with a profiling renderer. Separate Chromium fixtures exercise Java error recovery, User Timing, immutable snapshots and profiler durations. [Memory host](../java/react4j/reconciler/MemoryRenderer.java) · [Jest adapter](noop.js)

The full upstream coverage run executed 3,416 of 3,677 reconciler lines (92.9%) and 2,999 of 3,963 branches (75.7%). At `a66dd4212`, React4j's instrumented Chromium fixtures executed 3,175 of 3,331 standalone reconciler lines (95.3%) and 2,398 of 2,550 branches (94.0%). The upstream denominator spans 42 source files, including additional hosts and scheduling facilities. The Java denominator covers eight sources and uses J2CL source maps. The 324 enabled originals run against both implementations provide the direct comparison of observable behavior.

## Reproduce a comparison

Use a clean React 16.6.0 source checkout. Install its locked dependencies with Yarn Classic 1.22.22 and Node 10.24.1. Keep modern Node (verified with 26.12.1) on `PATH` for J2CL. Set `STOCK_NODE` to the absolute path of Node 10.24.1 for stock Jest. Some original tests assert V8’s exact diagnostic text. The command also requires Python 3 and the Bazel version pinned in this repository. The comparison requires write access under the upstream `scripts/jest` directory for a temporary Jest configuration.

```sh
git clone --depth 1 --branch v16.6.0 https://github.com/facebook/react.git /tmp/react-16.6.0
(cd /tmp/react-16.6.0 && yarn install --frozen-lockfile)
bazel build //tools/verification/runtime:upstream_renderer //tools/verification/runtime:upstream_memory_renderer
bundles="$(bazel info bazel-bin)/tools/verification/runtime"
work=$(mktemp -d)
for mode in development production; do
  python3 tools/verification/runtime/upstream/compare.py /tmp/react-16.6.0 \
    "$bundles/upstream_renderer.js" "$work/$mode" --mode "$mode" \
    --stock-node "$STOCK_NODE" --reconciler-bundle "$bundles/upstream_memory_renderer.js"
done
python3 tools/verification/runtime/upstream/report.py "$work/development/cases.json" "$work/production/cases.json" \
  --baseline tools/verification/runtime/upstream/cases.tsv > "$work/cases.tsv"
```

The baseline check reports each missing case, lost pass, or loss of a Java renderer call or effective assertion in a previously passing case. It permits improvements and variation in the fuzz test's number of calls. Use `additional-cases.tsv` as the baseline for the second matrix.

Pass `--additional` for the second matrix, using a fresh output directory. Pass `--suite packages/react-dom/src/__tests__/ReactDOMInput-test.js` to run a single original suite. Reconciler suites require the isolated bundle flag and run after a Node smoke test verifies memory rendering, scheduling and host bookkeeping. The output contains the Jest logs, failures, test-side adaptations and per-case results for both runtimes. The generated ReactNewContext fuzz case chooses random actions, so its call and assertion counts can vary between runs. Both Jest modes use the inspectable J2CL BUNDLE with the requested environment defines. The separate shared Chromium suites compile and execute the actual optimized Java application and stock ReactDOM production script. [Chromium commands](../README.md)

The source fixtures use their original React JSX constructor. JavaScript class fixtures construct a Java NativeView and forward supported lifecycle methods. The bridge preserves JSX owners so Java can attach string refs to the originating instance, even across renderer copies. TestUtils queries and findDOMNode read committed Java fibers. Private context reads in render use the active Java Fiber's scope. This lets the original react-cache resource suspend or resolve inside a Java render. The bridge forwards render, batching, portals, unmount and concurrent work to compiled Java. Separate Chromium fixtures start from React4j's typed API and generated views.

The original ReactElement fixture deletes the global `Symbol` before every test. Its numeric `$$typeof` assertion and JSON round-trip depend on the legacy protocol. For the Java run, the adapter initializes J2CL and the upstream JSX constructor with the native `Symbol`, then restores the fixture's global before its assertions. The per-case adaptations column identifies these runs. The two legacy protocol cases remain excluded. React4j’s Java `dup()` captures the current creator. Incoming JSX clones can carry a different creator, which the renderer uses to assign refs and produce component stacks. Both owner paths have Java tests.

The main matrix includes original JSX and DOM tests beyond the APIs bound by React4j. Some depend on hydration, held root commits or ReactDOM's private plugin registry. React4j's Java fixtures check the client updates, event delivery, callback timing and host identity from those mixed cases. The CoffeeScript, TypeScript and ES6 class fixtures also run together to compare case names, outcomes and effective assertion counts across the three original languages.

The separate public JSX Profiler suite also exercises scheduler tracing subscribers and deferred commits. Its ordinary render callbacks, including recovery timing and interaction sets, run in the Java memory renderer. In the second matrix, 61 of 77 enabled cases passed in development and 59 of 75 in production. The remaining 16 cases per mode exercise tracing subscribers and deferred commits. The browser extension’s commit profiler has its own application-level verification. [Browser measurements and DevTools](../README.md)
