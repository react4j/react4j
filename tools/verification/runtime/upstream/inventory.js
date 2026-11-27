// Jest 23 installs its globals before loading the stock React test setup.
'use strict';
const fs = require('fs');
const path = require('path');
const root = process.cwd();
const sites = new Set();
global.react4jRecordExpect = (original, site) => {
  sites.add(site);
  return original;
};
require(path.join(root, 'scripts/jest/setupTests'));

const suite = path.relative(root, expect.getState().testPath);
const output = path.join(process.env.REACT4J_INVENTORY_DIR, suite.split(path.sep).join('_') + '.jsonl');
jasmine.getEnv().addReporter({
  specStarted() { sites.clear(); },
  specDone(spec) {
    const row = {suite, id: spec.id, name: spec.fullName, status: spec.status, sites: Array.from(sites)};
    fs.appendFileSync(output, JSON.stringify(row) + '\n');
  },
});
