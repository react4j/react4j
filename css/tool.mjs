import fs from 'node:fs/promises';
import { createInterface } from 'node:readline';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { brotliCompressSync } from 'node:zlib';
import postcss from 'postcss';
import selectorParser from 'postcss-selector-parser';
import prettier from 'prettier';
import stylelint from 'stylelint';
import recommended from 'stylelint-config-recommended';
import { transform, bundleAsync, browserslistToTargets, Features } from 'lightningcss';
import browserslist from 'browserslist';

export const config = recommended;
// Resolved only from the integrity-pinned browser data in pnpm-lock.yaml.
export const browsers = browserslist(['last 2 Chrome major versions', 'last 2 Edge major versions', 'last 2 Firefox major versions', 'last 2 Safari major versions', 'last 2 ios_saf major versions'], { config: false });
export const targets = browserslistToTargets(browsers);


function failure(message, offset = 0) {
  const error = new Error(message);
  error.offset = offset;
  throw error;
}

function offsetAt(text, line, column) {
  const lines = text.split('\n');
  return lines.slice(0, line - 1).reduce((n, s) => n + s.length + 1, 0) + column - 1;
}

async function lint(text) {
  const result = await stylelint.lint({ code: text, codeFilename: 'style.css', config, configBasedir: path.dirname(fileURLToPath(import.meta.resolve('stylelint-config-recommended'))) });
  const warning = result.results.flatMap(r => r.warnings)[0];
  if (warning) failure(warning.text, offsetAt(text, warning.line, warning.column));
}

function encode(name) {
  return name.split('').map(c => /[A-Za-z0-9]/.test(c) ? c : c === '.' ? '-' : '_' + c.charCodeAt(0).toString(16).padStart(4, '0') + '_').join('');
}

export async function component(css, root, accessors) {
  const prefix = `.${root} {\n`;
  const wrapped = prefix + css + '\n}\n';
  try {
    const tree = postcss.parse(wrapped, { from: 'style.css' });
    const container = tree.first;
    if (tree.nodes.length !== 1 || container.type !== 'rule' || container.selector !== `.${root}`) {
      failure('Component CSS must remain inside its implicit root');
    }
    const found = new Set();
    tree.walkAtRules(rule => {
      if (!['media', 'supports', 'container'].includes(rule.name)) {
        failure(`Unsupported component at-rule @${rule.name}`, rule.source.start.offset);
      }
    });
    tree.walkDecls(decl => {
      if (decl.important) failure('Component !important declarations defeat shared/component precedence', decl.source.start.offset);
      if (['animation', 'animation-name'].includes(decl.prop)) failure('Component keyframe bindings are outside this proof', decl.source.start.offset);
    });
    tree.walkRules(rule => {
      if (rule === container) return;
      const ast = selectorParser().astSync(rule);
      ast.each(selector => {
        if (selector.first?.type !== 'nesting') failure('Component selectors must start with &', rule.source.start.offset);
        selector.walk(node => {
          if (['combinator', 'tag', 'id', 'universal'].includes(node.type)
              || (node.type === 'nesting' && node !== selector.first)
              || (node.type === 'pseudo' && node.nodes?.length && ![':is', ':not', ':where'].includes(node.value))
              || (node.type === 'pseudo' && [':has', ':global', ':local', ':host', ':host-context', ':root', ':scope'].includes(node.value))) {
            failure('Component selectors must target the same owned element; descendant and cross-component reach are unsupported', rule.source.start.offset + node.sourceIndex);
          }
          if (node.type === 'class') {
            if (!accessors.includes(node.value) || node.value === 'style') {
              failure(`Local selector .${node.value} has no matching modifier accessor`, rule.source.start.offset + node.sourceIndex);
            }
            found.add(node.value);
          }
        });
      });
    });
    for (const name of accessors) {
      if (name !== 'style' && !found.has(name)) failure(`Accessor ${name}() has no matching local selector`);
    }
    await lint(wrapped);
    const formatted = await prettier.format(wrapped, { parser: 'css' });
    const body = formatted.slice(formatted.indexOf('{') + 2, formatted.lastIndexOf('}')).split('\n').map(line => line.startsWith('  ') ? line.slice(2) : line).join('\n');
    tree.walkRules(rule => {
      if (rule === container) return;
      rule.selector = selectorParser(selectors => selectors.walkClasses(node => { node.value = `${root}--${encode(node.value)}`; })).processSync(rule.selector);
    });
    const output = transform({ filename: 'component.css', code: Buffer.from(tree.toString()), targets, include: Features.Nesting }).code.toString();
    return { css: output, formatted: body };
  } catch (error) {
    const wrappedOffset = error.offset ?? error.input?.offset ?? (error.line ? offsetAt(wrapped, error.line, error.column) : 0);
    error.offset = Math.max(0, Math.min(css.length, wrappedOffset - prefix.length));
    error.message = error.reason ?? error.message;
    throw error;
  }
}

export async function bundle(shared, fragments) {
  for (const css of shared) {
    await lint(css);
    const tree = postcss.parse(css);
    tree.walkAtRules(rule => {
      if (['import', 'layer'].includes(rule.name)) failure(`Shared @${rule.name} is outside this proof`);
    });
    tree.walkDecls(decl => { if (decl.important) failure('Shared !important declarations defeat component precedence'); });
  }
  const files = new Map();
  const imports = ['@layer shared, components;'];
  for (const [layer, sources] of [['shared', shared], ['components', fragments]]) {
    for (const [index, css] of sources.entries()) {
      const filename = `${layer}-${index}.css`;
      files.set(`/jcss/${filename}`, css);
      imports.push(`@import "${filename}" layer(${layer});`);
    }
  }
  files.set('/jcss/styles.css', imports.join('\n'));
  const result = await bundleAsync({
    filename: '/jcss/styles.css', targets, minify: true,
    resolver: {
      resolve: specifier => `/jcss/${specifier}`,
      read: filename => {
        if (!files.has(filename)) throw new Error(`Undeclared CSS input: ${filename}`);
        return files.get(filename);
      },
    },
  });
  return result.code;
}

async function main() {
  const [mode, ...args] = process.argv.slice(2);
  if (mode === 'serve') {
    for await (const request of createInterface({ input: process.stdin })) {
      const [root, encoded, bindings] = request.split('\t');
      try {
        const result = await component(Buffer.from(encoded, 'base64').toString('utf8'), root, bindings.split(','));
        process.stdout.write(`ok\t${Buffer.from(result.css).toString('base64')}\t${Buffer.from(result.formatted).toString('base64')}\n`);
      } catch (error) {
        process.stdout.write(`error\t${error.offset ?? 0}\t${Buffer.from(error.message).toString('base64')}\n`);
      }
    }
  } else if (mode === 'bundle') {
    const [output, ...inputs] = args;
    const shared = [], fragments = [];
    for (const input of inputs) {
      const css = await fs.readFile(input.slice(2), 'utf8');
      (input.startsWith('s:') ? shared : fragments).push(css);
    }
    const css = await bundle(shared, fragments);
    await fs.writeFile(output, css);
    await fs.writeFile(output + '.br', brotliCompressSync(css));
  } else if (mode === 'format' || mode === 'check') {
    for (const input of args) {
      const original = await fs.readFile(input, 'utf8');
      await lint(original);
      const formatted = await prettier.format(original, { parser: 'css' });
      if (original !== formatted) {
        if (mode === 'format') await fs.writeFile(input, formatted);
        else { process.stderr.write(`${input}: CSS requires formatting\n`); process.exitCode = 1; }
      }
    }
  } else if (mode === 'targets') {
    process.stdout.write(JSON.stringify({ browsers, targets }, null, 2) + '\n');
  } else {
    throw new Error(`Unknown CSS tool mode: ${mode}`);
  }
}

if (process.argv[1]?.endsWith('tool.mjs')) await main();
