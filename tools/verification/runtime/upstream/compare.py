"""Run original React 16.6 DOM and reconciler specs against stock React and J2CL."""

import argparse
import concurrent.futures
from contextlib import ExitStack
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile


def specs(checkout):
    # These suites exercise other public APIs (e.g. legacy subtree mounting, SSR or hydration).
    excluded = {"findDOMNode-test.js", "renderSubtreeIntoContainer-test.js", "ReactTestUtils-test.js",
                "escapeTextForBrowser-test.js", "quoteAttributeValueForBrowser-test.js",
                "multiple-copies-of-react-test.js", "ReactDOMServerIntegrationRefs-test.js",
                "ReactDOMServerIntegrationReconnecting-test.js"}
    for path in sorted((checkout / "packages/react/src/__tests__").glob("*-test*")):
        if path.is_file() and ("require('react-dom')" in path.read_text() or path.name == "forwardRef-test.internal.js"):
            yield path.relative_to(checkout)
    for folder in ("packages/react-dom/src/__tests__", "packages/react-dom/src/events/__tests__",
                   "packages/react-dom/src/client/__tests__"):
        for path in sorted((checkout / folder).glob("*-test*.js")):
            name = path.name.lower()
            if path.name in excluded:
                continue
            if path.name.startswith("ReactDOMServerIntegration"):
                yield path.relative_to(checkout)
            elif not any(word in name for word in ("server", "hydration")):
                yield path.relative_to(checkout)
    for path in sorted((checkout / "packages/react-reconciler/src/__tests__").glob("*-test*.js")):
        yield path.relative_to(checkout)


def additional_specs():
    groups = {
        "react": ["ReactCoffeeScriptClass-test.coffee", "ReactCreateRef-test.js", "ReactProfiler-test.internal.js",
                  "ReactProfilerDevToolsIntegration-test.internal.js", "ReactStrictMode-test.internal.js",
                  "forwardRef-test.js"],
        "react-dom": ["ReactDOMserverIntegrationProgress-test.js", "ReactTestUtils-test.js", "findDOMNode-test.js",
                      "multiple-copies-of-react-test.js", "renderSubtreeIntoContainer-test.js"],
        "react-cache": ["ReactCache-test.internal.js"],
        "create-subscription": ["createSubscription-test.internal.js"],
        "react-test-renderer": ["ReactTestRenderer-test.internal.js", "ReactTestRenderer-test.js", "ReactTestRendererAsync-test.js",
                                "ReactTestRendererTraversal-test.js"],
    }
    for package, names in groups.items():
        for name in names:
            yield Path("packages", package, "src", "__tests__", name)
    yield Path("packages/shared/__tests__/describeComponentFrame-test.js")


def requires_reconciler_parity(spec):
    return spec.parts[:2] == ("packages", "react-reconciler") or spec.name == "forwardRef-test.internal.js"


def is_reconciler_spec(spec):
    return (requires_reconciler_parity(spec)
            or spec.parts[1] in {"create-subscription", "react-cache", "react-test-renderer"}
            or spec.name in {"ReactCreateRef-test.js", "ReactProfiler-test.internal.js",
                             "ReactProfilerDevToolsIntegration-test.internal.js", "ReactStrictMode-test.internal.js",
                             "forwardRef-test.js"})


def uses_isolated_host(spec):
    # The portal interoperability fixture imports both renderers and constructs a real DOM container.
    return is_reconciler_spec(spec) and spec.name != "ReactTestRenderer-test.js"


def uses_server_integration_variants(spec):
    return spec.name.lower().startswith("reactdomserverintegration")


def run_suite(checkout, config, bundle, output, spec, runtime, mode, node):
    directory = output / runtime / spec.parent
    directory.mkdir(parents=True, exist_ok=True)
    events = directory / (spec.name + ".jsonl")
    log = directory / (spec.name + ".log")
    env = dict(os.environ, CI="true", NODE_ENV=mode,
               REACT4J_CASE_LOG=str(events), REACT4J_BUNDLE=str(bundle))
    memory_case = is_reconciler_spec(spec)
    if memory_case:
        env["REACT4J_RECONCILER_CASE"] = spec.name
    command = [node, "node_modules/jest/bin/jest.js", "--config", str(config), "--runInBand",
               "--runTestsByPath", str(spec)]
    if uses_isolated_host(spec) and runtime == "java":
        command += ["--env", "node"]
    if uses_server_integration_variants(spec):
        command += ["--testNamePattern", "with clean client render"]
    with log.open("w") as transcript:
        result = subprocess.run(command, cwd=checkout, env=env, stdout=transcript,
                                stderr=subprocess.STDOUT, timeout=240)
    finished = {}
    if events.exists():
        for line in events.read_text().splitlines():
            event = json.loads(line)
            if "name" in event:
                if event["id"] in finished:
                    raise RuntimeError(f"Duplicate case ID in {log}")
                finished[event["id"]] = event
    if not finished:
        raise RuntimeError(f"No original cases reported for {spec} ({runtime}); see {log}")
    if result.returncode and not any(x["status"] == "failed" for x in finished.values()):
        raise RuntimeError(f"Suite failed outside a test in {spec} ({runtime}); see {log}")
    return str(spec), runtime, finished


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("checkout", type=Path)
    parser.add_argument("bundle", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--reconciler-bundle", type=Path, help="Use a separate J2CL memory-host bundle for reconciler suites")
    parser.add_argument("--mode", choices=("development", "production"), default="production")
    parser.add_argument("--stock-node", default="node",
                        help="Node executable for stock fixtures (use Node 10.24.1 for historical V8 diagnostics)")
    selection = parser.add_mutually_exclusive_group()
    selection.add_argument("--suite", action="append", type=Path, help="Relative upstream suite path; may repeat")
    selection.add_argument("--additional", action="store_true",
                           help="Run additional original core, profiling, test renderer and integration suites")
    selection.add_argument("--foreign-root", action="store_true",
                           help="Run the supplementary two-renderer root ownership test")
    args = parser.parse_args()
    checkout = args.checkout.resolve()
    bundle = args.bundle.resolve(strict=True)
    if json.loads((checkout / "package.json").read_text())["version"] != "16.6.0":
        raise ValueError("Expected a React 16.6.0 source checkout")
    selected = [] if args.foreign_root else args.suite or list(
        additional_specs() if args.additional else specs(checkout))
    reconciler_bundle = None
    if any(map(uses_isolated_host, selected)):
        if args.reconciler_bundle is None:
            parser.error("reconciler suites require --reconciler-bundle")
        reconciler_bundle = args.reconciler_bundle.resolve(strict=True)
        subprocess.run(["node", str(Path(__file__).with_name("memory-smoke.js")), str(reconciler_bundle)],
                       check=True, timeout=30)
    output = args.output.resolve()
    output.mkdir()
    pairs = {}
    with ExitStack() as resources:
        folder = Path(resources.enter_context(tempfile.TemporaryDirectory(dir=checkout / "scripts/jest")))
        if args.foreign_root:
            fixture = resources.enter_context(tempfile.NamedTemporaryFile(
                dir=checkout / "packages/react-dom/src/__tests__", prefix="react4j_foreign_root_", suffix=".js"))
            shutil.copyfile(Path(__file__).with_name("foreign-root-test.js"), fixture.name)
            selected = [Path(fixture.name).relative_to(checkout)]
        for name in ("setup.js", "noop.js", "reporter.js"):
            shutil.copyfile(Path(__file__).with_name(name), folder / name)
        configs = {}
        for runtime, setup in (("stock", "reporter"), ("java", "setup")):
            config = folder / (runtime + ".js")
            config.write_text("module.exports = Object.assign({}, require('../config.source'), "
                              "{setupTestFrameworkScriptFile:require.resolve('./" + setup + "'), "
                              "transformIgnorePatterns:['/node_modules/',require.resolve('./setup'),"
                              "require.resolve('./noop'),require.resolve('./reporter')]});\n")
            configs[runtime] = config
        with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
            jobs = [pool.submit(run_suite, checkout, configs[runtime],
                                reconciler_bundle if uses_isolated_host(spec) else bundle,
                                output, spec, runtime, args.mode, args.stock_node if runtime == "stock" else "node")
                    for spec in selected for runtime in ("stock", "java")]
            for job in concurrent.futures.as_completed(jobs):
                spec, runtime, cases = job.result()
                pairs.setdefault(spec, {})[runtime] = cases
                print(runtime, spec, len(cases), flush=True)
    rows = []
    for spec, both in sorted(pairs.items()):
        if both["stock"].keys() != both["java"].keys():
            raise RuntimeError(f"Stock and Java discovered different cases in {spec}")
        for test_id, stock in both["stock"].items():
            name = stock["name"]
            java = both["java"][test_id]
            if name != java["name"]:
                raise RuntimeError(f"Case {test_id} changed name between runtimes in {spec}")
            if uses_server_integration_variants(Path(spec)) and "with clean client render" not in name:
                continue
            rows.append(dict(suite="foreign-root-test.js" if args.foreign_root else spec,
                             id=test_id, case=name, stock=stock["status"], java=java["status"],
                             calls=java.get("calls", 0), assertions=java.get("assertions", 0),
                             adaptations=java.get("adaptations", []), failure=java.get("failure", "")))
    (output / "cases.json").write_text(json.dumps(rows, indent=2) + "\n")
    if args.foreign_root and (len(rows) != 2 or any(
            row["stock"] != "passed" or row["java"] != "passed" or not row["calls"] or not row["assertions"]
            for row in rows)):
        raise SystemExit(f"Foreign root ownership comparison failed; see {output / 'cases.json'}")
    mismatches = [row for row in rows if requires_reconciler_parity(Path(row["suite"]))
                  and (row["stock"] not in ("passed", "pending") or row["java"] != row["stock"])]
    if mismatches:
        for row in mismatches:
            print(f'{row["suite"]} {row["id"]}: {row["case"]}: stock {row["stock"]}, Java {row["java"]}')
        raise SystemExit(f"{len(mismatches)} reconciler case outcomes differ; see {output / 'cases.json'}")


if __name__ == "__main__":
    main()
