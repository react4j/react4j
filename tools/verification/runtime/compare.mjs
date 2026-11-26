// Usage: node compare.mjs PAIRED_RUN.json
import assert from 'node:assert/strict';
import {readFile} from 'node:fs/promises';

const {reference, candidate} = JSON.parse(await readFile(process.argv[2], 'utf8'));
assert.deepEqual(candidate.observation, reference.observation);

function median(values) {
  const sorted = values.toSorted((a, b) => a - b);
  const middle = Math.floor(sorted.length / 2);
  return sorted.length % 2 ? sorted[middle] : (sorted[middle - 1] + sorted[middle]) / 2;
}
function measurements(run) {
  const warm = run.samples.slice(1);
  assert(warm.length > 0, 'Performance comparison requires warm samples');
  const result = {jsBytes:run.jsBytes, gzipBytes:run.gzipBytes};
  for (const key of Object.keys(warm[0])) result[key] = median(warm.map(sample => sample[key]));
  return result;
}
const baseline = measurements(reference);
const current = measurements(candidate);
const rows = Object.keys(baseline).map(metric => ({
  metric, baseline:baseline[metric], candidate:current[metric],
  ratio:(current[metric] / baseline[metric]).toFixed(2),
}));
console.table(rows);
for (const {metric, baseline, candidate} of rows) {
  const tolerance = Math.max(baseline * 0.1, metric.endsWith('Ms') ? 1 : 0);
  assert(candidate <= baseline + tolerance, metric + ' exceeded the comparison budget');
}
