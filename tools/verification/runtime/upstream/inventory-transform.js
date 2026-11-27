// Runs in the pinned upstream checkout with the same compiler and Jest transforms as stock.
'use strict';
const path = require('path');
const parser = require('@babel/parser');
const coffee = require('coffee-script');
const stock = require(path.join(process.cwd(), 'scripts/jest/preprocessor'));

function javascriptSites(source, typescript) {
  const pending = [parser.parse(source, {sourceType: 'unambiguous', allowReturnOutsideFunction: true,
    plugins: ['jsx', typescript ? 'typescript' : 'flow', 'classProperties', 'objectRestSpread',
      'asyncGenerators', 'dynamicImport', 'exportDefaultFrom', 'exportNamespaceFrom', 'decorators-legacy']})];
  const sites = [];
  while (pending.length) {
    const node = pending.pop();
    if (node.type === 'CallExpression' && node.callee.type === 'Identifier' && node.callee.name === 'expect') {
      const callee = node.callee;
      sites.push([callee.start, callee.loc.start.line, callee.loc.start.column + 1]);
    }
    for (const value of Object.values(node)) {
      if (Array.isArray(value)) pending.push(...value.filter(child => child && child.type));
      else if (value && value.type) pending.push(value);
    }
  }
  return sites;
}

function coffeescriptSites(source) {
  const lines = [0];
  for (let i = 0; i < source.length; i++) if (source[i] === '\n') lines.push(i + 1);
  const tokens = coffee.tokens(source);
  return tokens.filter(([tag, value], i) =>
    tag === 'IDENTIFIER' && value === 'expect' && tokens[i + 1][0] === 'CALL_START'
  ).map(([, , pos]) => [lines[pos.first_line] + pos.first_column, pos.first_line + 1, pos.first_column + 1]);
}

module.exports.process = function(source, file) {
  const suite = path.relative(process.cwd(), file);
  if (suite.includes('/__tests__/') && /\.(js|ts|coffee)$/.test(suite)) {
    const sites = suite.endsWith('.coffee') ? coffeescriptSites(source) : javascriptSites(source, suite.endsWith('.ts'));
    for (const [offset, line, column] of sites.sort((a, b) => b[0] - a[0])) {
      const site = JSON.stringify(suite + ':' + line + ':' + column);
      source = source.slice(0, offset) + 'global.react4jRecordExpect(expect, ' + site + ')' + source.slice(offset + 6);
    }
    if (suite.endsWith('.ts')) {
      source += '\ndeclare var global: {react4jRecordExpect<T>(original: T, site: string): T};\n';
    }
  }
  return stock.process(source, file);
};
