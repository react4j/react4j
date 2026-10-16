import assert from 'node:assert/strict';
import { component, bundle } from './tool.mjs';

const root = 'jcss-com-biz-ui-SaveButton';
const source = `color: var(--on-accent);
&:focus-visible { outline: 2px solid var(--focus); }
&:disabled { opacity: 0.5; }
&.success { background-color: var(--success); }
&.cancel { background-color: var(--cancel); border: 2px dotted currentcolor; }
`;
const first = await component(source, root, ['style', 'success', 'cancel']);
assert.match(first.css, /\.jcss-com-biz-ui-SaveButton\.jcss-com-biz-ui-SaveButton--success/);
assert.ok(!first.css.includes('&'));
assert.equal((await component(first.formatted, root, ['style', 'success', 'cancel'])).formatted, first.formatted);
assert.equal((await component(first.formatted, root, ['style', 'success', 'cancel'])).css, first.css);
assert.equal((await component(source, root, ['style', 'success', 'cancel'])).css, first.css);

for (const [css, accessors, expected] of [
  ['&.sucess { color: red; }', ['style', 'success'], /no matching modifier accessor/],
  ['color: red;', ['style', 'success'], /no matching local selector/],
  ['/* &.success {} */ color: red;', ['style', 'success'], /no matching local selector/],
  ['content: "&.success";', ['style', 'success'], /no matching local selector/],
  ['& .success { color: red; }', ['style', 'success'], /same owned element/],
  ['& + & { color: red; }', ['style'], /same owned element/],
  ['&:has(.success) { color: red; }', ['style', 'success'], /same owned element/],
  ['&::part(success) { color: red; }', ['style'], /same owned element/],
  ['color: red !important;', ['style'], /precedence/],
  ['@keyframes pulse { to { opacity: 0; } }', ['style'], /Unsupported component at-rule/],
  ['} body { color: red; } /*', ['style'], /Unclosed comment/],
  ['colour: red;', ['style'], /Unknown property/],
  ['color red;', ['style'], /Unknown word/],
]) {
  await assert.rejects(component(css, root, accessors), expected);
}

await assert.rejects(component('color: red;\n&.typo { color: blue; }', root, ['style']), error => {
  assert.equal(error.offset, 'color: red;\n&'.length);
  return /typo/.test(error.message);
});
await assert.rejects(component('color: red;\n&: { color: blue; }', root, ['style']), error => {
  assert.equal(error.offset, 'color: red;\n&'.length);
  return /Expected a pseudo-class/.test(error.message);
});
const conditional = await component('@media (width > 1px) { &.success { color: green; } }', root, ['style', 'success']);
assert.match(conditional.css, /@media/);
assert.match(conditional.css, /jcss-com-biz-ui-SaveButton--success/);
const shared = ':root { --success: #080; --cancel: #a00; --on-accent: #fff; --focus: #06f; }';
assert.deepEqual(await bundle([shared], [first.css]), await bundle([shared], [first.css]));
await assert.rejects(bundle(['button { color: red !important; }'], [first.css]), /precedence/);
console.log('CSS parser, modifier validation, ownership, formatting and deterministic bundling passed');
