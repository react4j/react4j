"""Collect original Jest case names and executed expect() locations from stock React 16.6."""

import argparse
from collections import Counter
import csv
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


PIN = "8b97a9c36f2ef7d2c570b25e988b8ca832d9b436"
MODES = ("development", "production")


def collect(checkout, output, mode):
    checkout = checkout.resolve()
    revision = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=checkout, text=True).strip()
    if revision != PIN:
        raise ValueError(f"Expected React v16.6.0 at {PIN}, found {revision}")
    directory = output.resolve() / mode
    directory.mkdir(parents=True)
    with tempfile.TemporaryDirectory(dir=checkout / "scripts/jest") as tmp:
        setup = Path(tmp) / "inventory.js"
        shutil.copyfile(Path(__file__).with_name(setup.name), setup)
        transform = setup.with_name("inventory-transform.js")
        shutil.copyfile(Path(__file__).with_name(transform.name), transform)
        config = setup.with_name("config.js")
        config.write_text("module.exports = Object.assign({}, require('../config.source'), {"
                          "setupTestFrameworkScriptFile: require.resolve('./inventory'), "
                          "transform: {'.*': require.resolve('./inventory-transform')}, "
                          "transformIgnorePatterns: ['/node_modules/', require.resolve('./inventory')]});\n")
        subprocess.run(["node", "node_modules/jest/bin/jest.js", "--config", str(config),
                        "--maxWorkers=2", "--json", "--outputFile", str(directory / "jest.json")],
                       cwd=checkout, env=dict(os.environ, CI="true", NODE_ENV=mode,
                                             REACT4J_INVENTORY_DIR=str(directory)), check=True)


def read(directory):
    results = json.loads((directory / "jest.json").read_text())
    rows = {}
    counts = Counter()
    for path in sorted(directory.glob("*.jsonl")):
        seen = Counter()
        cases = [json.loads(line) for line in path.read_text().splitlines()]
        for case in sorted(cases, key=lambda row: int(row["id"].removeprefix("spec"))):
            key = (case["suite"], case["name"])
            seen[key] += 1
            key = (*key, seen[key])
            if key in rows:
                raise ValueError(f"Duplicate stock result: {key}")
            rows[key] = case
            counts[case["status"]] += 1
    if (not results["success"] or len(rows) != results["numTotalTests"]
            or counts["passed"] != results["numPassedTests"]):
        raise ValueError(f"Reporter disagrees with Jest results in {directory}: {counts}")
    return rows


def scope(suite, name):
    file = Path(suite).name
    if suite.startswith("scripts/"):
        return "excluded: upstream development tooling"
    if "with clean client render" in name:
        return "client"
    if "with server string render" in name or "with server stream render" in name:
        return "excluded: server renderer"
    if ("with client render on top of " in name or "on successful reconnect" in name
            or file in {"ReactDOMServerIntegrationReconnecting-test.js", "ReactServerRenderingHydration-test.js"}):
        return "excluded: hydration"
    if file == "ReactDOMRoot-test.js":
        if name.endswith("supports hydration"):
            return "excluded: hydration"
        if "batch" in name:
            return "excluded: root.createBatch manual commit"
    if file == "ReactMount-test.js" and ("padded rendered markup" in name or "checksum mismatch" in name):
        return "excluded: hydration"
    if file in {"ReactTracing-test.internal.js", "Tracing-test.js", "Tracing-test.internal.js",
                "TracingSubscriptions-test.internal.js"}:
        return "excluded: scheduler tracing API"
    if ((file == "ReactElement-test.js" and "fallback value when in an environment without Symbol" in name)
            or (file == "ReactSymbols-test.internal.js" and "numeric values" in name)):
        return "excluded: numeric no-Symbol protocol"
    if file == "ReactServerRendering-test.js" and name.endswith("can import react-dom in Node environment"):
        return "review separately: client module loading in Node"
    if file in {"ReactDOMServerLifecycles-test.js", "ReactDOMServerLifecycles-test.internal.js",
                "ReactDOMServerSuspense-test.internal.js", "ReactServerRendering-test.js",
                "ReactServerRenderingBrowser-test.js", "escapeTextForBrowser-test.js",
                "quoteAttributeValueForBrowser-test.js"}:
        return "excluded: server renderer"
    if file == "ReactRenderDocument-test.js":
        if ("with old implicit hydration API" in name
                and name.endswith(("renders over an existing text child without throwing",
                                   "should throw on full document render w/ no markup"))):
            return "client"
        return "review separately: assertions following hydration"
    if file == "ReactDOMServerIntegrationRefs-test.js":
        return "review separately: ref assertions with server markup"
    if file == "onlyChild-test.js" or (file == "ReactChildren-test.js" and "with fragments enabled" not in name):
        return "excluded: React.Children traversal API"
    if file.startswith("ReactProfiler"):
        return "review separately: client profiling and tracing"
    if suite.startswith("packages/scheduler/"):
        return "excluded: standalone JavaScript scheduler API"
    if file in {"EventPluginRegistry-test.internal.js", "accumulateInto-test.internal.js"}:
        return "excluded: private event plugin registry or accumulation helper"
    if file == "ResponderEventPlugin-test.internal.js":
        return "excluded: native responder plugin"
    if file == "ReactErrorUtils-test.internal.js":
        return "excluded: private JavaScript guarded callback API"
    if file == "reactProdInvariant-test.internal.js":
        return "excluded: upstream production error decoder URL"
    if file == "ReactNativeAttributePayload-test.js":
        return "excluded: native view attribute payload"
    if suite.startswith(("packages/react-art/", "packages/react-native-renderer/")):
        return "review separately: other host and shared component behavior"
    if file == "ReactShallowRenderer-test.js":
        return "review separately: shallow rendering and shared component behavior"
    if suite.startswith(("packages/react-test-renderer/", "packages/create-subscription/", "packages/react-cache/",
                         "packages/react-is/")) or file in {
            "ReactDOMFrameScheduling-test.js", "describeComponentFrame-test.js", "ReactSymbols-test.internal.js"}:
        return "review separately: client interoperation"
    if suite.startswith(("packages/react-dom/", "packages/react-reconciler/", "packages/react/")):
        return "client candidate"
    return "review separately: other package or renderer"


def sites(case):
    if not case:
        return "-"
    suite = case["suite"] + ":"
    def location(site):
        path, line, column = site.rsplit(":", 2)
        return path, int(line), int(column)

    return " ".join(site.removeprefix(suite) for site in sorted(set(case["sites"]), key=location)) or "-"


def report(output):
    dev, prod = (read(output / mode) for mode in MODES)
    writer = csv.writer(sys.stdout, dialect="excel-tab", lineterminator="\n")
    writer.writerow(("suite", "upstream case", "occurrence", "scope", "development id", "production id",
                     "development", "production", "development expect sites", "production expect sites"))
    for suite, name, occurrence in sorted(dev.keys() | prod.keys()):
        key = (suite, name, occurrence)
        left, right = dev.get(key, {}), prod.get(key, {})
        writer.writerow((suite, name, occurrence, scope(suite, name), left.get("id"), right.get("id"),
                         left.get("status", "absent"), right.get("status", "absent"), sites(left), sites(right)))


def audit():
    directory = Path(__file__).parent
    with (directory / "inventory.tsv").open() as source:
        cases = list(csv.DictReader(source, dialect="excel-tab"))
    ids = {(case["suite"], mode, case[mode + " id"]): i for i, case in enumerate(cases)
           for mode in MODES if case[mode + " id"]}
    reviewed = {}
    dispositions = {}
    tests = {}
    for table in sorted(directory.glob("*-assertions.tsv")):
        with table.open() as source:
            for line, row in enumerate(csv.DictReader(source, dialect="excel-tab"), 2):
                where = f"{table.name}:{line}"
                tokens = row["upstream_case_id"].split(", ")
                indices = set()
                for token in tokens:
                    mode, colon, spec = token.rpartition(":")
                    for build in (mode,) if colon else MODES:
                        index = ids.get((row["suite"], build, spec))
                        if index is None or not cases[index]["upstream case"].endswith(row["upstream_case"]):
                            raise ValueError(f"{where}: unknown case or mismatched title in {build}: {spec}")
                        indices.add(index)
                if len(indices) != 1:
                    raise ValueError(f"{where}: mode-specific IDs refer to different cases")
                index = indices.pop()
                source_line = re.match(r"L(\d+(?::\d+)?):", row["assertion"])
                shared_site = re.match(r"(packages/[^\s]+:\d+:\d+):", row["assertion"])
                locations = reviewed.setdefault(index, set())
                if source_line:
                    locations.add(source_line[1])
                elif shared_site:
                    locations.add(shared_site[1])
                disposition = row["disposition"]
                if disposition not in {"ported", "covered", "adapted", "excluded", "upstream pending",
                                       "skipped upstream", "related", "not ported"}:
                    raise ValueError(f"{where}: unknown assertion disposition: {disposition}")
                if disposition not in {"ported", "covered"} and row["reason"].strip() in {"", "-", "—"}:
                    raise ValueError(f"{where}: explain the {disposition} assertion")
                dispositions.setdefault(index, set()).add(disposition)
                java = re.split(r"[(,]", row["java_test"], maxsplit=1)[0].strip()
                if java not in {"-", "—"}:
                    tests.setdefault(java, where)
                elif disposition in {"ported", "covered", "adapted"}:
                    raise ValueError(f"{where}: {disposition} assertion needs a Java fixture")
    root = directory.resolve().parents[3]
    for java, where in tests.items():
        cls, method = java.rsplit(".", 1)
        paths = list(root.glob(f"*/src/test/java/**/{cls}.java"))
        paths.extend((root / "tools/verification/runtime/java").rglob(cls + ".java"))
        if not any(re.search(r"\b" + re.escape(method) + r"\s*\(", path.read_text()) for path in paths):
            raise ValueError(f"{where}: Java fixture method does not exist: {java}")
    missing = []
    for index, rows in reviewed.items():
        case = cases[index]
        for mode in MODES:
            for site in case[mode + " expect sites"].split():
                if site != "-" and site not in rows and site.rsplit(":", 1)[0] not in rows:
                    missing.append(f"{case['suite']} {case[mode + ' id']} ({mode}) {site}")
    if missing:
        raise ValueError("Unmapped executed expect sites in individually mapped cases:\n" + "\n".join(missing))
    print(f"Validated source locations and {len(tests)} Java fixture methods across "
          f"{len(reviewed)} individually mapped cases. Stock inventory: {len(cases)} cases across both modes.")
    counts = Counter()
    for values in dispositions.values():
        if values & {"related", "not ported"}:
            counts["partial or pending"] += 1
        elif values & {"ported", "covered", "adapted"}:
            counts["all mapped applicable assertions have Java counterparts"] += 1
        else:
            counts["only excluded or upstream-skipped assertions"] += 1
    for label, count in sorted(counts.items()):
        print(f"  {count} cases: {label}")
    unmapped = [case for index, case in enumerate(cases) if index not in reviewed]
    print(f"  {len(unmapped)} stock cases have no individual assertion map:")
    for label, count in sorted(Counter(case["scope"] for case in unmapped).items()):
        print(f"    {count} {label}")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    run = commands.add_parser("collect")
    run.add_argument("checkout", type=Path)
    run.add_argument("output", type=Path)
    run.add_argument("--mode", choices=MODES, required=True)
    table = commands.add_parser("report")
    table.add_argument("output", type=Path)
    commands.add_parser("audit")
    args = parser.parse_args()
    if args.command == "collect":
        collect(args.checkout, args.output, args.mode)
    elif args.command == "report":
        report(args.output)
    else:
        audit()


if __name__ == "__main__":
    main()
