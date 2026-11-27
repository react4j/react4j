"""Write the original React case map from a pair of compare.py runs."""

import argparse
import csv
import json
from pathlib import Path
import sys


def read(path):
    cases = {}
    for row in json.loads(path.read_text()):
        key = (row["suite"], row["id"], row["case"])
        if key in cases:
            raise ValueError(f"Repeated case in {path}: {key}")
        cases[key] = row
    return cases


def outcome(row):
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
    if row["java"] == "passed" or row["java"] == "pending":
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
    if dev.keys() != prod.keys():
        raise ValueError("The two modes discovered different cases")
    output = csv.writer(sys.stdout, dialect="excel-tab", lineterminator="\n")
    output.writerow(("suite", "id", "upstream case", "development", "production",
                     "development calls", "development assertions", "production calls", "production assertions",
                     "fixture adaptations", "development failure", "production failure"))
    for key in sorted(dev, key=lambda k: (k[0], int(k[1].removeprefix("spec")))):
        adaptations = sorted(set(dev[key]["adaptations"] + prod[key]["adaptations"]))
        output.writerow((*key, outcome(dev[key]), outcome(prod[key]),
                         dev[key]["calls"], dev[key]["assertions"], prod[key]["calls"], prod[key]["assertions"],
                         ", ".join(adaptations) or "-",
                         failure_reason(dev[key]), failure_reason(prod[key])))


if __name__ == "__main__":
    main()
