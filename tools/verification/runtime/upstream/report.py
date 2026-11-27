"""Write the original React case map from a pair of compare.py runs."""

import argparse
from collections import Counter
import csv
import json
from pathlib import Path
import sys


def read(path):
    cases = {}
    ids = set()
    occurrences = Counter()
    for row in sorted(json.loads(path.read_text()), key=lambda entry: int(entry["id"].removeprefix("spec"))):
        identifier = (row["suite"], row["id"])
        if identifier in ids:
            raise ValueError(f"Repeated ID in {path}: {identifier}")
        ids.add(identifier)
        key = (row["suite"], row["case"])
        occurrences[key] += 1
        cases[(*key, occurrences[key])] = row
    return cases


def outcome(row):
    if not row:
        return "absent"
    if row["stock"] != "passed":
        return "skipped" if row["stock"] == row["java"] == "pending" else "stock " + row["stock"]
    if row["java"] == "failed":
        if "Unsupported React4j runtime feature:" in row["failure"]:
            return "runtime limit"
        if "Outside the React4j test adapter:" in row["failure"]:
            return "adapter limit"
        return "failed after Java call" if row["calls"] else "failed before Java call"
    if row["java"] != "passed":
        return row["java"]
    if not row["calls"]:
        return "helper only"
    return "paired assertion" if row["assertions"] else "smoke"


def failure_reason(row):
    if not row or row["java"] in {"passed", "pending"}:
        return "-"
    lines = row["failure"].splitlines()
    for line in lines:
        for prefix in ("Unsupported React4j runtime feature:", "Outside the React4j test adapter:"):
            if prefix in line:
                return line[line.index(prefix):].strip()
    message = " ".join(" ".join(lines).split())
    return message[:197] + "..." if len(message) > 200 else message


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("development", type=Path)
    parser.add_argument("production", type=Path)
    args = parser.parse_args()
    dev, prod = read(args.development), read(args.production)
    output = csv.writer(sys.stdout, dialect="excel-tab", lineterminator="\n")
    output.writerow(("suite", "id", "upstream case", "development", "production",
                     "development calls", "development assertions", "production calls", "production assertions",
                     "fixture adaptations", "development failure", "production failure"))
    def ordering(key):
        row = dev.get(key) or prod[key]
        return key[0], key not in dev, int(row["id"].removeprefix("spec"))

    for key in sorted(dev.keys() | prod.keys(), key=ordering):
        left, right = dev.get(key, {}), prod.get(key, {})
        if left.get("id") == right.get("id"):
            label = left["id"]
        else:
            label = ", ".join(f"{mode}:{case['id']}" for mode, case in
                              (("development", left), ("production", right)) if case)
        adaptations = sorted(set(left.get("adaptations", []) + right.get("adaptations", [])))
        output.writerow((key[0], label, key[1], outcome(left), outcome(right),
                         left.get("calls", "-"), left.get("assertions", "-"),
                         right.get("calls", "-"), right.get("assertions", "-"),
                         ", ".join(adaptations) or "-",
                         failure_reason(left), failure_reason(right)))


if __name__ == "__main__":
    main()
