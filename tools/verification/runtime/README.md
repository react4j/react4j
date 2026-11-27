# Browser runtime comparison

Use these checks when changing React4j's renderer. The reference is the last implementation that shipped the pinned React and ReactDOM 16.6.0 scripts, commit `18e896de70f9e7ebae0ac6be8e9f5506a1949997`. Both implementations run the same React4j test code in Chromium.

## Behavior

From the repository root on Linux, create a reference checkout in a new directory and run both test configurations.

```sh
work=$(mktemp -d)
tools/verification/runtime/reference.sh "$work/reference"
for mode in fastbuild opt; do
  bazel test -c "$mode" --test_env=CHROME_DEVEL_SANDBOX //:browser_tests
done
```

The script copies the current browser scenarios to the reference. It supplies Closure with the error boundary callback signature and component stack field used by the original binaries. One reference page disables `TextEvent` before loading ReactDOM to exercise its keypress fallback.

The original Java bindings constructed element records, used React's element and owner symbols, created context providers and consumers, and derived generated views from `React.Component`. The DOM bindings called `render`, `unmountComponentAtNode`, `unstable_createRoot`, `createPortal`, and `unstable_batchedUpdates`. The Drumloop application exercises concurrent roots. The TodoMVC applications exercise stateful and Arez-tracked views, including Sting-generated services.

The browser suites cover DOM and component identity, lifecycle and callback order, batching, context, refs, portals, Suspense retries, error recovery, focus and selection, controlled and uncontrolled inputs, and synthetic event propagation. Concurrent-root cases interrupt rendering between components and inspect the DOM and refs before and after the commit. They also exercise delayed fallbacks and repeated clicks in one browser task. Custom-element callbacks and changed refs expose the order of DOM writes, keyed moves, and deletions. The tests retain a removed DOM subtree and check focus and selection after moving a keyed ancestor. Host property cases distinguish HTML, SVG and MathML attribute casing, raw qualified names and namespace aliases. Both builds run the application scenarios. The repository gate compiles generated Arez and Sting components.

The upstream case map records original React 16.6 Jest assertions on each runtime and identifies their React4j Chromium counterparts. [Source-test mapping and reproduction](upstream/README.md)

## Workloads and browser extension

Build the TodoMVC applications in both checkouts. Run the commands below from the candidate using Node 22 or later. Set `CHROME_DEVEL_SANDBOX` to the installed Chrome sandbox helper on machines that require it.

```sh
targets='//examples/todomvc/raw:raw //examples/todomvc/arez:arez //examples/todomvc/sting:sting'
for mode in fastbuild opt; do
  (cd "$work/reference" && bazel build -c "$mode" $targets)
  bazel build -c "$mode" $targets
done
chrome="$(bazel info output_base)/external/+browser_repo+react4j_chrome_linux/chrome-linux64/chrome"
opt=$(bazel info -c opt bazel-bin)
ref_opt=$(cd "$work/reference" && bazel info -c opt bazel-bin)
export REACT4J_BENCHMARK_PAIRS=30
for app in raw arez sting; do
  path="examples/todomvc/$app/$app"
  node tools/verification/runtime/measure.mjs "$chrome" "$ref_opt/$path" "$opt/$path" > "$work/$app.json"
  node tools/verification/runtime/compare.mjs "$work/$app.json"
done
```

Each run starts a fresh browser profile, serves the two applications on separate loopback ports, and alternates their order within each pair. It discards one warm-up pair and takes the median over the requested number of measured pairs (ten by default). Each sample loads an empty list, adds 50 rows using input and Enter events, toggles 25 checkboxes, and dispatches 1,000 navigation keys to the input handler. The two interactive measurements include time until the visible updates finish. The Arez and Sting variants include their scheduled updates. The harness checks every label in order, the final checkboxes and input value, and retained DOM identity.

Heap is the used V8 heap after a forced collection with the list mounted. Bundle sizes sum the scripts loaded by the production document, raw and individually gzipped at level 9. A comparison fails if a candidate median or size exceeds the reference by more than 10%, with a 1 ms minimum allowance for timings. The comparator converts median times to nanoseconds before applying the budget so binary timestamp subtraction preserves the 1 ms limit. Run measurements on an idle machine. To check measurement noise, pass the same production directory for both applications. On a hybrid Linux CPU, prefix the Node command with `taskset -c` and the CPU list from `/sys/devices/cpu_core/cpus` to run both copies of the workload on performance cores.

Set `REACT_DEVTOOLS` to the installed extension directory containing its manifest. The check runs against both development applications, the default production application, and an optimized production build with readable component and input names.

```sh
dev=$(bazel info bazel-bin)
ref_dev=$(cd "$work/reference" && bazel info bazel-bin)
path=examples/todomvc/raw/raw
bazel build -c opt //tools/verification/runtime:named_production
node tools/verification/runtime/measure.mjs "$chrome" "$ref_dev/$path" "$dev/$path" "$REACT_DEVTOOLS" \
  "$opt/$path" "$opt/tools/verification/runtime/named_production" > "$work/devtools.json"
```

This opens Chrome DevTools, inspects a generated component, checks the highlight overlay, and records component durations during the workload. Each profile must contain a timed update to the component that owns the input, and their commit counts must match. The default production build minimizes view names and input keys. To read them in a production profile, set `react4j.enable_view_names` to `'true'` and `react4j.minimize_input_keys` to `'false'` in the application's Closure defines, as in the [named production target](BUILD.bazel).

## Measured results

### Production at `a66dd4212`

Chrome 145.0.7632.117 ran 30 measured pairs per application and CPU configuration after the repository gate and upstream comparisons finished. Each cell gives stock React 16.6 followed by Java. All pairs agreed on row order, retained DOM nodes, input value and completed items. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 26.90 / 28.20 | 20.30 / 25.65 | 3.70 / 3.35 | 5.30 / 4.40 | 1,999,500 / 2,061,034 | 166,367 / 154,219 | 54,943 / 54,094 |
| 0-5 | Arez | 34.00 / 34.30 | 213.40 / 215.85 | 7.55 / 6.90 | 10.45 / 9.25 | 2,587,386 / 2,642,476 | 184,824 / 174,490 | 59,922 / 59,424 |
| 0-5 | Sting | 33.45 / 35.55 | 213.80 / 214.95 | 7.30 / 6.80 | 10.60 / 9.10 | 2,592,296 / 2,646,580 | 185,631 / 175,364 | 60,272 / 59,802 |
| 0-15 | Raw | 24.95 / 24.70 | 17.15 / 19.95 | 3.50 / 3.20 | 5.25 / 4.25 | 1,994,624 / 2,057,764 | 166,367 / 154,219 | 54,943 / 54,094 |
| 0-15 | Arez | 29.35 / 25.75 | 199.10 / 195.95 | 6.10 / 5.50 | 7.65 / 5.70 | 2,587,360 / 2,642,396 | 184,824 / 174,490 | 59,922 / 59,424 |
| 0-15 | Sting | 28.50 / 29.30 | 199.50 / 201.15 | 6.20 / 6.35 | 7.70 / 7.85 | 2,592,302 / 2,646,486 | 185,631 / 175,364 | 60,272 / 59,802 |

The Java applications met 40 of 42 budgets. Adding 50 Raw rows took 26% longer on performance cores and 16% longer with unrestricted scheduling. Arez and Sting met every budget. Retained heap was 2.1–3.2% higher across the three applications. All three optimized bundles were smaller than stock before and after gzip.

A CPU profile of Raw insertion showed time spread over child reconciliation, host preparation and JavaScript compilation. An experiment reusing immutable DOM mount plans saved 0.10 ms at the median paired difference over ten pairs and increased median paired heap by 16 KB. The shipped renderer retains the original allocation strategy.

React DevTools 8.0.0 inspected and highlighted a generated view and recorded 125 commits in each of stock development, Java development, ordinary Java production and named Java production. The respective counts with positive component durations were 103, 98, 60 and 65. Named production retained `TodoEntry` and `TodoItem.todo`. Chrome resolved all seven Java application source maps, which contained the exact reconciler, DOM renderer and TodoMVC view sources.

### Standalone reconciler at `1ce36e444`

Chrome for Testing 145.0.7632.117 ran 30 measured pairs per application and CPU configuration. Each cell gives the stock median followed by the Java median. The final list, input value and retained node identities matched in every pair. Other verification jobs shared the host during this run. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 25.80 / 28.45 | 18.95 / 25.00 | 4.40 / 4.30 | 6.00 / 5.40 | 1,995,886 / 2,050,428 | 166,367 / 152,777 | 54,943 / 53,481 |
| 0-5 | Arez | 37.20 / 42.85 | 218.95 / 217.45 | 9.05 / 8.85 | 12.00 / 9.90 | 2,587,360 / 2,634,460 | 184,824 / 173,049 | 59,922 / 58,801 |
| 0-5 | Sting | 35.00 / 31.90 | 215.05 / 213.30 | 9.20 / 7.30 | 11.45 / 9.80 | 2,592,344 / 2,637,856 | 185,631 / 173,923 | 60,272 / 59,179 |
| 0-15 | Raw | 33.75 / 30.45 | 23.35 / 30.00 | 6.65 / 6.70 | 9.80 / 8.15 | 1,994,766 / 2,050,018 | 166,367 / 152,777 | 54,943 / 53,481 |
| 0-15 | Arez | 47.05 / 42.70 | 238.05 / 237.65 | 8.20 / 7.55 | 10.70 / 9.75 | 2,587,360 / 2,633,334 | 184,824 / 173,049 | 59,922 / 58,801 |
| 0-15 | Sting | 37.55 / 37.05 | 231.00 / 234.65 | 8.00 / 7.95 | 9.60 / 10.00 | 2,592,296 / 2,637,844 | 185,631 / 173,923 | 60,272 / 59,179 |

The candidate met 38 of the 42 comparison budgets. Raw insertion of 50 rows exceeded the threshold in both CPU configurations. On CPUs 0–5, Raw and Arez startup also exceeded it. All three optimized bundles were smaller than stock, both raw and gzipped.

React DevTools 8.0.0 inspected the input component, highlighted its host and recorded 125 commits in each build. Java development, default production and named production recorded 98, 68 and 73 commits with positive component durations respectively. The named build displayed `TodoEntry` and `TodoItem.todo`. Chrome resolved all seven application source maps, and their contents matched the committed Java reconciler, DOM renderer and TodoMVC view sources.

### Production at `d9e4b006e`

Thirty measured pairs per application and CPU configuration ran on the same system as the previous comparison. Each cell gives stock React 16.6 followed by Java. The input events, retained nodes and final rows agreed in every pair. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 28.95 / 30.60 | 18.65 / 20.75 | 4.65 / 4.35 | 6.55 / 5.30 | 1,995,588 / 2,042,674 | 166,367 / 151,643 | 54,943 / 52,797 |
| 0-5 | Arez | 53.05 / 58.40 | 244.80 / 243.05 | 9.25 / 8.70 | 12.00 / 11.15 | 2,586,934 / 2,626,048 | 184,824 / 171,888 | 59,922 / 58,107 |
| 0-5 | Sting | 41.95 / 40.50 | 218.80 / 217.70 | 10.50 / 8.45 | 13.55 / 10.80 | 2,592,326 / 2,629,718 | 185,631 / 172,761 | 60,272 / 58,434 |
| 0-15 | Raw | 28.65 / 29.65 | 20.50 / 25.90 | 5.75 / 5.10 | 8.75 / 6.60 | 1,995,986 / 2,041,284 | 166,367 / 151,643 | 54,943 / 52,797 |
| 0-15 | Arez | 46.75 / 45.90 | 238.90 / 241.05 | 8.55 / 8.15 | 12.40 / 10.75 | 2,587,360 / 2,625,372 | 184,824 / 171,888 | 59,922 / 58,107 |
| 0-15 | Sting | 31.50 / 31.90 | 212.80 / 213.90 | 7.55 / 6.50 | 9.10 / 7.70 | 2,592,306 / 2,629,304 | 185,631 / 172,761 | 60,272 / 58,434 |

React DevTools 8.0.0 inspected and highlighted the component and profiled 125 commits in each of the four builds. Stock development, Java development, Java production and named Java production reported 95, 99, 68 and 61 commits with positive durations, respectively. Named production retained `TodoEntry` and `TodoItem.todo`. Chrome resolved all seven application source maps, including the optimized builds.

39 of 42 comparisons met the existing budgets. Raw 50-row insertion exceeded the budget under both CPU configurations. The pinned Arez startup also exceeded it. All three optimized application bundles were smaller than stock before and after gzip. The upstream Jest and Java browser runs shared this machine.

### Production at `2e2bfcbf5`

Chrome 145 ran 30 measured pairs for each application on CPUs 0–5 and 30 with unrestricted scheduling. Compilation and browser verification were running on the same machine. Each cell gives the stock React 16.6 median followed by the Java median. All pairs produced the same visible state and retained DOM identity. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 43.80 / 50.45 | 29.85 / 39.10 | 8.35 / 8.00 | 10.30 / 8.45 | 1,996,326 / 2,041,090 | 166,367 / 150,291 | 54,943 / 52,337 |
| 0-5 | Arez | 64.45 / 60.25 | 272.15 / 272.05 | 11.10 / 10.00 | 14.35 / 11.20 | 2,586,892 / 2,624,422 | 184,824 / 170,538 | 59,922 / 57,689 |
| 0-5 | Sting | 58.75 / 56.25 | 256.30 / 274.65 | 9.20 / 9.30 | 12.15 / 11.70 | 2,592,204 / 2,628,100 | 185,631 / 171,413 | 60,272 / 58,000 |
| 0-15 | Raw | 52.70 / 54.55 | 31.40 / 44.20 | 9.95 / 8.40 | 11.80 / 10.70 | 1,997,210 / 2,040,882 | 166,367 / 150,291 | 54,943 / 52,337 |
| 0-15 | Arez | 31.55 / 35.50 | 212.00 / 214.20 | 8.05 / 7.25 | 9.25 / 8.60 | 2,587,316 / 2,623,250 | 184,824 / 170,538 | 59,922 / 57,689 |
| 0-15 | Sting | 28.15 / 27.65 | 199.70 / 199.15 | 6.85 / 6.35 | 7.50 / 6.85 | 2,592,274 / 2,627,490 | 185,631 / 171,413 | 60,272 / 58,000 |

Thirty-eight of 42 measurements met the existing budgets. Adding 50 rows in Raw exceeded the budget in both runs. Raw startup exceeded it on CPUs 0–5, and Arez startup exceeded it in the unrestricted run. All three optimized bundles were smaller than stock, both raw and gzipped.

React DevTools 8.0.0 inspected and highlighted the input component and recorded 125 commits in each of stock development, Java development, Java production and named Java production. The ordinary Java production profile had 75 commits with positive durations, including eight timed updates to the input component. Named production exposed `TodoEntry` and `TodoItem.todo`. Chrome reported the source map URLs for all seven Java applications, and the maps contained the exact renderer and TodoMVC component sources.

### Production at `211e46fce`

Each cell gives the stock React 16.6 median followed by the Java median. Chrome ran 60 measured Raw pairs and 30 Arez and Sting pairs on CPUs 0–5 and 120 pairs per application under unrestricted scheduling. The same optimized Raw bundle was used in both runs. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 27.30 / 29.20 | 22.20 / 25.00 | 3.60 / 3.10 | 5.30 / 4.10 | 1,998,514 / 2,012,492 | 166,367 / 149,090 | 54,943 / 51,857 |
| 0-5 | Arez | 32.90 / 35.65 | 212.80 / 214.45 | 7.70 / 6.70 | 10.35 / 8.30 | 2,587,338 / 2,607,686 | 184,824 / 169,333 | 59,922 / 57,214 |
| 0-5 | Sting | 32.95 / 34.20 | 211.50 / 215.00 | 7.50 / 6.70 | 10.35 / 8.25 | 2,592,258 / 2,611,624 | 185,631 / 170,208 | 60,272 / 57,538 |
| 0-15 | Raw | 25.70 / 25.10 | 18.10 / 20.20 | 3.65 / 3.00 | 5.45 / 4.20 | 1,994,732 / 2,010,988 | 166,367 / 149,090 | 54,943 / 51,857 |
| 0-15 | Arez | 27.10 / 27.50 | 204.80 / 206.55 | 7.00 / 6.40 | 9.30 / 7.70 | 2,587,328 / 2,607,156 | 184,824 / 169,333 | 59,922 / 57,214 |
| 0-15 | Sting | 28.65 / 27.90 | 204.65 / 207.00 | 7.00 / 6.40 | 9.40 / 7.60 | 2,592,248 / 2,610,296 | 185,631 / 170,208 | 60,272 / 57,538 |

Forty of 42 checks met the existing budget. Adding 50 rows in Raw took 12.6% longer on CPUs 0–5 and 11.6% longer under unrestricted scheduling. The Java times exceeded the 10% cutoffs by 0.58 ms and 0.29 ms respectively. All three applications met the startup, toggle, keyboard dispatch, retained heap and bundle-size budgets. Arez and Sting also met the insertion budget. A fresh unrestricted comparison of the earlier `f58188962` build measured Raw insertion at 16.95 / 18.90 ms across 120 pairs.

React Developer Tools 8.0.0 inspected and highlighted the input component and profiled 125 commits in each build. In default Java production, 73 commits had positive component durations, including four input-component updates. Named production recorded 77 and 12 respectively and exposed `TodoEntry` and `TodoItem.todo`. Chrome loaded source maps in all seven Java application builds, with Java sources matching this commit.

### React 16.6 reconciler suite at `4c1343f95`

The initial parity build passed 41 of 42 workload budgets. On unrestricted CPUs, adding 50 rows in Raw took 16.85 ms with stock React and 21.30 ms with Java. All other unrestricted measurements passed. The 30-pair runs on CPUs 0–5 overlapped the repository gate, which raised absolute timings. The unrestricted runs used 120 pairs on an idle machine. Every run checked the rendered rows, checked boxes, retained DOM identity and input state. [Individual samples](measurements.csv)

React Developer Tools 8.0.0 inspected and highlighted the input component and recorded 125 commits on each of stock development, Java development, Java production and Java production with names. The default optimized application reported 79 commits with a positive component duration, including 13 timed input-component commits. The named application exposed `TodoEntry` and `TodoItem.todo`. Chrome reported a source map for each of the seven Java application builds. Their maps contained the exact Java sources used to build the reconciler, DOM host, NativeView and application views.

### Extracted reconciler at `f58188962`

Each cell gives stock React 16.6 followed by React4j. The production applications ran 30 measured pairs on CPUs 0–5 and 120 on CPUs 0–15 with the same Chrome, hardware and workload as the previous comparison. All 42 measurements met their budgets. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0-5 | Raw | 29.25 / 28.55 | 22.75 / 24.70 | 3.45 / 3.00 | 5.30 / 4.20 | 1,997,538 / 1,949,710 | 166,367 / 130,068 | 54,943 / 44,877 |
| 0-5 | Arez | 32.05 / 32.60 | 214.10 / 215.00 | 7.50 / 6.60 | 10.30 / 8.15 | 2,587,328 / 2,548,808 | 184,824 / 150,283 | 59,922 / 50,212 |
| 0-5 | Sting | 32.70 / 33.95 | 213.10 / 214.55 | 7.45 / 6.50 | 10.40 / 8.55 | 2,592,254 / 2,554,588 | 185,631 / 151,158 | 60,272 / 50,510 |
| 0-15 | Raw | 26.45 / 23.40 | 17.70 / 19.10 | 3.80 / 3.20 | 5.40 / 4.60 | 1,994,412 / 1,947,050 | 166,367 / 130,068 | 54,943 / 44,877 |
| 0-15 | Arez | 28.65 / 28.05 | 203.80 / 207.15 | 6.90 / 6.20 | 9.10 / 7.50 | 2,587,326 / 2,548,208 | 184,824 / 150,283 | 59,922 / 50,212 |
| 0-15 | Sting | 26.90 / 27.30 | 202.50 / 207.05 | 6.95 / 6.20 | 9.15 / 7.30 | 2,592,248 / 2,554,232 | 185,631 / 151,158 | 60,272 / 50,510 |

React Developer Tools 8.0.0 inspected and highlighted the Raw application's input component and recorded 125 commits in each of stock development, Java development, Java production, and Java production with names. In default production, 78 commits had a positive component duration, including 14 timed updates to the input component. The named build exposed `TodoEntry` and `TodoItem.todo`. Chrome resolved the source maps in all seven Java application builds. The maps contained the exact Java sources of the reconciler, DOM host and the application's `TodoEntry` and `TodoItem` views.

### Production at `d92506b6c`

Linux x86-64, Intel Core Ultra 7 255H, Chrome for Testing 145.0.7632.117 and Node 26.12.1. Each application ran 30 measured pairs on CPUs 0–5 and 120 pairs with unrestricted scheduling across CPUs 0–15. Each cell gives the stock React 16.6 value followed by the Java renderer value. The sample labels contain `d92506b6c` and the pair count. Run zero is the discarded warm-up. [Individual samples](measurements.csv)

| CPUs | Application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 0–5 | Raw | 27.75 / 27.90 | 22.90 / 24.60 | 3.35 / 3.30 | 5.20 / 4.40 | 1,998,256 / 1,952,520 | 166,367 / 128,457 | 54,943 / 44,536 |
| 0–5 | Arez | 33.45 / 34.15 | 212.80 / 214.90 | 7.40 / 6.70 | 10.55 / 8.15 | 2,587,358 / 2,527,396 | 184,824 / 148,687 | 59,922 / 49,853 |
| 0–5 | Sting | 31.70 / 33.75 | 212.55 / 214.60 | 7.70 / 6.60 | 10.30 / 8.25 | 2,592,248 / 2,533,434 | 185,631 / 149,561 | 60,272 / 50,176 |
| Unrestricted | Raw | 26.50 / 24.90 | 17.60 / 19.05 | 3.90 / 3.45 | 5.60 / 4.75 | 1,994,440 / 1,949,786 | 166,367 / 128,457 | 54,943 / 44,536 |
| Unrestricted | Arez | 28.25 / 26.95 | 204.85 / 206.95 | 7.00 / 6.30 | 9.40 / 7.10 | 2,587,328 / 2,526,640 | 184,824 / 148,687 | 59,922 / 49,853 |
| Unrestricted | Sting | 28.75 / 27.20 | 203.25 / 202.70 | 6.90 / 6.30 | 9.05 / 6.65 | 2,592,260 / 2,532,708 | 185,631 / 149,561 | 60,272 / 50,176 |

All 42 comparisons met their budgets. Raw insertion remained 7.4% slower than stock on CPUs 0–5 and 8.2% slower under unrestricted scheduling.

### Historical performance comparisons

The regression at `aec7b03ff` was Raw insertion on CPUs 0–5. Stock took 22.50 ms and Java took 25.00 ms, above the 24.75 ms cutoff. Of the intervening builds, `5084d4c76` exceeded the Arez startup budget and `073dfa723` exceeded the Raw insertion budget. At `9f3bec3c4`, all pinned measurements passed. A 120-pair unrestricted run confirmed a Raw insertion miss (17.60 / 19.85 ms) and an Arez startup pass (28.00 / 27.15 ms). All 42 comparisons passed at `3d3eeb629`. The sample file retains each run.

Candidate runtime at `2aa7b48f5`. Linux x86-64, Intel Core Ultra 7 255H, Chrome for Testing 145.0.7632.117 and Node 26.12.1. Both runtimes used CPUs 0–5 for the primary comparison. Each cell gives the pinned React 16.6 median followed by the Java renderer median. The sample file also includes the unrestricted comparison on CPUs 0–15, a Raw comparison on efficiency cores 6–13, and Raw controls with identical pinned bundles. Run zero is the discarded warm-up. [Individual samples](measurements.csv)

| Production application | Load (ms) | Add 50 (ms) | Toggle 25 (ms) | 1,000 keys (ms) | Heap (bytes) | JavaScript (bytes) | Gzip (bytes) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Raw | 27.70 / 28.70 | 22.45 / 23.55 | 3.40 / 3.10 | 5.30 / 4.70 | 1,998,756 / 1,935,274 | 166,367 / 123,795 | 54,943 / 42,218 |
| Arez | 32.25 / 34.50 | 214.50 / 214.20 | 7.45 / 6.70 | 10.30 / 8.90 | 2,587,350 / 2,514,514 | 184,824 / 143,995 | 59,922 / 47,611 |
| Sting | 32.95 / 34.60 | 214.45 / 214.60 | 7.45 / 6.70 | 10.25 / 9.15 | 2,592,212 / 2,519,798 | 185,631 / 144,869 | 60,272 / 47,927 |

All three applications passed every budget on CPUs 0–5 and under unrestricted scheduling. With unrestricted scheduling, adding 50 rows took 19.60 / 19.95 ms for Raw, 196.45 / 205.55 ms for Arez and 206.50 / 208.70 ms for Sting. Raw also passed on efficiency cores, where insertion took 14.90 / 15.40 ms.

The control runs loaded the same pinned Raw bundle on both sides. Every metric passed in the ten-pair run on CPUs 0–5 and the thirty-pair unrestricted run. The ten-pair unrestricted control crossed the keyboard cutoff at 5.70 / 7.05 ms. The samples also retain the earlier unrestricted Raw comparison at `fa7159007`, where insertion crossed the cutoff at 16.20 / 20.10 ms.

### React Developer Tools and source maps

React Developer Tools 8.0.0 inspected a generated component, highlighted its host element, and recorded 125 commits in each build below at candidate source `d92506b6c`. The table counts commits with a positive duration in the extension export. A commit can appear with zero duration at the browser's timer resolution. The extension probe ran alongside other verification work.

| Profile | Commits with a positive component duration | Updates with a positive TodoEntry duration | Peak TodoEntry duration (ms) |
| --- | ---: | ---: | ---: |
| React 16.6 development | 106 | 53 | 2.5 |
| Java development | 113 | 59 | 0.5 |
| Java production | 73 | 4 | 0.5 |
| Java production with names | 72 | 8 | 0.6 |

The named production build exposed `TodoEntry` and `TodoItem.todo`. In the earlier comparison at `2aa7b48f5`, the same extension could select the component on the stock ReactDOM 16.6.0 production script and recorded zero profiled commits there.

Chrome reported `app.js.map` as the source map URL for each candidate TodoMVC application in BUNDLE and ADVANCED, including the named production build. The maps resolved to files with mapped `Renderer.java`, `TodoEntry.java`, and `TodoItem.java` contents identical to the source at `d92506b6c`.
