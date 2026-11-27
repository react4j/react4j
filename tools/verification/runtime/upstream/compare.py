"""Run original React 16.6 client DOM specs against stock ReactDOM and the J2CL renderer."""

import argparse
import concurrent.futures
import json
import os
from pathlib import Path
import re
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
        if path.is_file() and "require('react-dom')" in path.read_text():
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
        if (path.name in {"ErrorBoundaryReconciliation-test.internal.js", "ReactSuspense-test.internal.js"}
                or re.search(r"require\(['\"]react-noop-renderer(?:/persistent)?['\"]\)", path.read_text())):
            yield path.relative_to(checkout)


def run_suite(checkout, config, bundle, output, spec, runtime, mode):
    directory = output / runtime / spec.parent
    directory.mkdir(parents=True, exist_ok=True)
    events = directory / (spec.name + ".jsonl")
    log = directory / (spec.name + ".log")
    env = dict(os.environ, CI="true", NODE_ENV=mode, REACT4J_CASE_RUNTIME=runtime,
               REACT4J_CASE_LOG=str(events), REACT4J_BUNDLE=str(bundle))
    if spec.parts[:2] == ("packages", "react-reconciler"):
        env["REACT4J_RECONCILER_CASE"] = "1"
    command = ["node", "node_modules/jest/bin/jest.js", "--config", str(config), "--runInBand",
               "--runTestsByPath", str(spec)]
    if spec.name.startswith("ReactDOMServerIntegration"):
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
    parser.add_argument("--mode", choices=("development", "production"), default="production")
    parser.add_argument("--suite", action="append", type=Path, help="Relative upstream suite path; may repeat")
    args = parser.parse_args()
    checkout = args.checkout.resolve()
    bundle = args.bundle.resolve(strict=True)
    output = args.output.resolve()
    output.mkdir()
    if json.loads((checkout / "package.json").read_text())["version"] != "16.6.0":
        raise ValueError("Expected a React 16.6.0 source checkout")
    pairs = {}
    with tempfile.TemporaryDirectory(dir=checkout / "scripts/jest") as temp:
        folder = Path(temp)
        shutil.copyfile(Path(__file__).with_name("setup.js"), folder / "setup.js")
        shutil.copyfile(Path(__file__).with_name("noop.js"), folder / "noop.js")
        config = folder / "config.js"
        config.write_text("module.exports = Object.assign({}, require('../config.source'), "
                          "{setupTestFrameworkScriptFile:require.resolve('./setup'), "
                          "transformIgnorePatterns:['/node_modules/',require.resolve('./setup'),require.resolve('./noop')]});\n")
        with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
            jobs = [pool.submit(run_suite, checkout, config, bundle, output, spec, runtime, args.mode)
                    for spec in (args.suite or list(specs(checkout))) for runtime in ("stock", "java")]
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
            if Path(spec).name.startswith("ReactDOMServerIntegration") and "with clean client render" not in name:
                continue
            rows.append(dict(suite=spec, id=test_id, case=name, stock=stock["status"], java=java["status"],
                             calls=java.get("calls", 0), assertions=java.get("assertions", 0),
                             adaptations=java.get("adaptations", []), failure=java.get("failure", "")))
    (output / "cases.json").write_text(json.dumps(rows, indent=2) + "\n")


if __name__ == "__main__":
    main()
