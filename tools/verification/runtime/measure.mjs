// Usage: node measure.mjs CHROME REFERENCE_DIRECTORY CANDIDATE_DIRECTORY [UNPACKED_REACT_DEVTOOLS]
import {spawn} from 'node:child_process';
import {mkdtemp, readFile, rm} from 'node:fs/promises';
import {createServer} from 'node:http';
import {tmpdir} from 'node:os';
import {extname, join, resolve, sep} from 'node:path';
import {gzipSync} from 'node:zlib';
import assert from 'node:assert/strict';

const [chrome, reference, candidate, extension] = process.argv.slice(2);
if (!candidate || !reference || !chrome) throw Error('Expected Chrome and two built TodoMVC directories');
const profile = await mkdtemp(join(tmpdir(), 'react4j-browser-'));
const types = {'.html':'text/html', '.js':'text/javascript', '.css':'text/css', '.svg':'image/svg+xml'};
async function serve(path) {
  const directory = resolve(path);
  const server = createServer(async (request, response) => {
    try {
      const url = new URL(request.url, 'http://localhost');
      const file = resolve(directory, '.' + url.pathname);
      if (!file.startsWith(directory + sep)) throw Error('Invalid path');
      const data = await readFile(file);
      response.writeHead(200, {'Content-Type':types[extname(file)] || 'application/octet-stream'});
      response.end(data);
    } catch {
      response.writeHead(404).end();
    }
  });
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  return {directory, server, url:'http://127.0.0.1:' + server.address().port + '/index.html'};
}
const sites = {reference:await serve(reference), candidate:await serve(candidate)};
const browser = spawn(chrome, ['--headless=new', '--no-first-run', '--disable-back-forward-cache', '--disable-background-networking', '--disable-dev-shm-usage',
  '--user-data-dir=' + profile, '--remote-debugging-port=0',
  ...(extension ? ['--auto-open-devtools-for-tabs', '--disable-extensions-except=' + extension, '--load-extension=' + extension] : []),
  'about:blank'], {stdio:['ignore','ignore','pipe']});
const stopped = new Promise(resolve => browser.once('close', resolve));
let diagnostics = '';
browser.stderr.on('data', chunk => { diagnostics = (diagnostics + chunk).slice(-4096); });
let socket;
const delay = ms => new Promise(resolve => setTimeout(resolve, ms));
async function until(probe) {
  const deadline = Date.now() + 30000;
  do {
    if (browser.exitCode !== null || browser.signalCode !== null) throw Error(diagnostics);
    const value = await probe();
    if (value) return value;
    await delay(20);
  } while (Date.now() < deadline);
  throw Error('Browser timeout: ' + diagnostics);
}
try {
  const port = await until(async () => (await readFile(join(profile, 'DevToolsActivePort'), 'utf8')
    .catch(() => '')).split('\n')[0]);
  const pages = await (await fetch('http://127.0.0.1:' + port + '/json/list')).json();
  socket = new WebSocket(pages.find(p => p.type === 'page').webSocketDebuggerUrl);
  await new Promise((ok, fail) => { socket.onopen = ok; socket.onerror = fail; });
  const pending = new Map();
  const exceptions = [];
  const extensionErrors = [];
  let id = 0;
  socket.onmessage = ({data}) => {
    const message = JSON.parse(data);
    if (message.method === 'Runtime.exceptionThrown') exceptions.push(message.params.exceptionDetails);
    if (message.method === 'Runtime.consoleAPICalled' && message.params.type === 'error') {
      const args = message.params.args.map(arg => arg.value ?? arg.description).join(' ');
      if (args.includes('React DevTools encountered an error')) extensionErrors.push(args);
    }
    const handler = pending.get(message.id);
    if (handler) { pending.delete(message.id); handler(message); }
  };
  async function send(method, params = {}) {
    const key = ++id;
    const result = new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        pending.delete(key);
        reject(Error('Chrome did not answer ' + method + ': ' + diagnostics));
      }, 30000);
      pending.set(key, message => { clearTimeout(timeout); resolve(message); });
    });
    socket.send(JSON.stringify({id:key, method, params}));
    const message = await result;
    if (message.error) throw Error(JSON.stringify(message.error));
    return message.result;
  }
  async function evaluate(expression) {
    const result = await send('Runtime.evaluate', {expression, awaitPromise:true, returnByValue:true});
    if (result.exceptionDetails) throw Error(result.exceptionDetails.exception?.description || result.exceptionDetails.text);
    return result.result.value;
  }
  await send('Runtime.enable');
  const workload = await readFile(new URL('./todo-workload.js', import.meta.url), 'utf8');
  const probe = extension && await readFile(new URL('./devtools.js', import.meta.url), 'utf8');
  const results = {reference:{samples:[]}, candidate:{samples:[]}};
  const titles = Array.from({length:50}, (_, i) => 'Row ' + i);
  for (let run = 0; run < (extension ? 1 : 11); run++) {
    for (const variant of (run % 2 ? ['candidate', 'reference'] : ['reference', 'candidate'])) {
      const site = sites[variant];
      const output = results[variant];
      const url = site.url + '?run=' + run;
      await send('Page.navigate', {url});
      await until(() => evaluate('location.href === ' + JSON.stringify(url) +
        ' && document.readyState === "complete" && !!document.querySelector("input[placeholder]")'));
      if (probe) await evaluate(probe);
      const {timings, ...observation} = await evaluate(workload);
      assert.deepEqual(observation, {titles, completed:25, retained:true, entry:''});
      if (probe) observation.devtools = await evaluate('window.finishDevToolsProbe()');
      output.observation = observation;
      await send('HeapProfiler.collectGarbage');
      const {usedSize} = await send('Runtime.getHeapUsage');
      output.samples.push({...timings, heapBytes:usedSize});
      if (!run) {
        const sources = await evaluate('Array.from(document.scripts, s => s.src && new URL(s.src).pathname).filter(Boolean)');
        output.jsBytes = 0;
        output.gzipBytes = 0;
        for (const name of sources) {
          const script = await readFile(join(site.directory, name));
          output.jsBytes += script.length;
          output.gzipBytes += gzipSync(script, {level:9}).length;
        }
      }
      await evaluate('localStorage.clear()');
    }
    assert.deepEqual(results.candidate.observation, results.reference.observation);
  }
  assert.deepEqual(exceptions, []);
  assert.deepEqual(extensionErrors, []);
  console.log(JSON.stringify({browser:await evaluate('navigator.userAgent'), ...results}, null, 2));
} finally {
  socket?.close();
  browser.kill();
  await stopped;
  for (const site of Object.values(sites)) site.server.close();
  await rm(profile, {recursive:true, force:true, maxRetries:4});
}
