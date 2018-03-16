const toc = require('markdown-toc');
const RemarkableEmbed = require('remarkable-embed');
const fs = require('fs');

// Ugly hack as could not get the remarkable plugin working.
// Thus will regenerate each time the site config is reloaded
function updateToc(filename) {
  try {
    const path = process.cwd() + '/../docs/' + filename;
    let content = fs.readFileSync(path, 'utf8');
    content = toc.insert(content, { maxdepth: 3 }).trim() + '\n';
    fs.writeFileSync(path, content, 'utf8');
  } catch (err) {
    console.log(err);
  }
}

updateToc('faq.md');

const markdownInclude = function(code) {
  const filename = process.cwd() + '/includes/' + code;
  return fs.readFileSync(filename, 'utf8');
};

const apiUrl = function(code) {
  const elements = code.split('::');

  const label =
    elements.length >
    1 ?
    elements[0] :
    ((elements[0].match(/^annotations\./) ? '@' : '') + elements[0].replace(/^.+\./, '') );
  const classname = elements.length > 1 ? elements[1] : elements[0];
  const url = '/api/react4j/' +
              classname.replace('.', '/') + '.html' +
              (elements.length > 2 ? '#' + elements[2].replace('(', '-').replace(',', '-').replace(')', '-') : '');

  return `<a href="${url}"><code>${label}</code></a>`;
};

function parseParams(params) {
  params = params.trim();
  let args = {};
  let paramsLeft = params;
  while (paramsLeft) {
    const result = PARAM_EXTRACTOR.exec(paramsLeft);
    if (result) {
      const param = result[2] || result[3] || result[4];
      const eqIndex = param.indexOf('=');
      if (-1 === eqIndex) {
        throw Error(`Bad parameter ${param} extracted from ${params}`);
      }
      const key = param.slice(0, eqIndex);
      args[key] = param.slice(eqIndex + 1);
      paramsLeft = paramsLeft.slice(result[0].length);
    }
    else {
      break;
    }
  }
  return args;
}

const PARAM_EXTRACTOR = /(([^\s"'][^\s\}]*)|"([^\"]*)"|'([^']*)')\s*/;

function calculateFirstLine(lines, pattern, includeLine) {
  if (!pattern) {
    return 0;
  }

  const regex = new RegExp(pattern);
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    if (regex.test(line)) {
      return i + (includeLine ? 0 : 1);
    }
  }
  throw Error(`Unable to locate first line with pattern ${pattern}`);
}

function calculateLastLine(lines, startLine, pattern, includeLine) {
  if (!pattern) {
    return lines.length - 1;
  }

  const regex = new RegExp(pattern);
  for (let i = startLine; i < lines.length; i++) {
    const line = lines[i];
    if (regex.test(line)) {
      return i + (includeLine ? 1 : 0);
    }
  }
  throw Error(`Unable to locate last line with pattern ${pattern}`);
}

const fileContent = function(params, options) {
  let args = parseParams(params);

  const project = args['project'] || 'doc-examples';
  const path = args['path'] || 'src/main/java';
  const file = args['file'];
  const language = args['language'] || 'java';
  const firstLine = args['start_line'];
  const lastLine = args['end_line'];
  const includeStartLine = (args['include_start_line'] || 'true') === 'true';
  const includeEndLine = (args['include_end_line'] || 'true') === 'true';
  const stripBlock = (args['strip_block'] || 'false') === 'true';
  const elideStart = args['elide_start'] || 'DOC ELIDE START';
  const elideEnd = args['elide_end'] || 'DOC ELIDE END';
  const elideReplacement = args['elide_replacement'] || '...';

  if (!file) {
    throw Error(`Failed to specify file parameter ${file}`);
  }

  const filename = process.cwd() + '/../' + project + '/' + path + '/' + file;
  const content = fs.readFileSync(filename, 'utf8');
  const lines = content.split('\n');

  const start = calculateFirstLine(lines, firstLine, includeStartLine);
  const end = calculateLastLine(lines, start, lastLine, includeEndLine);

  const selectedLines = lines.slice(start, end);

  const elideStartRegex = new RegExp(elideStart);
  const elideEndRegex = new RegExp(elideEnd);

  let whitespaceAtStart = 0 === selectedLines.length ? 10000000 : lines[0].length;
  let lastWhitespaceAtStart = 0;
  let inElission = false;

  const newSelectedLines = [];
  for (let i = 0; i < selectedLines.length; i++) {
    const line = selectedLines[i];
    if (!inElission && elideStartRegex.test(line)) {
      inElission = true;
      newSelectedLines.push(Array(lastWhitespaceAtStart + 1).join(' ') + elideReplacement);
    }
    else if (inElission && elideEndRegex.test(line)) {
      inElission = false;
    }
    else if (!inElission) {
      if (0 !== line.length) {
        lastWhitespaceAtStart = line.search(/\S|$/);
        whitespaceAtStart = Math.min(whitespaceAtStart, lastWhitespaceAtStart);
      }
      newSelectedLines.push(line);
    }
  }
  if (stripBlock) {
    for (let i = 0; i < newSelectedLines.length; i++) {
      newSelectedLines[i] = newSelectedLines[i].slice(whitespaceAtStart);
    }
  }
  const newContent = newSelectedLines.join('\n');

  return '<pre><code>' + options.highlight(newContent, language) + '</code></pre>';
};

const embed = new RemarkableEmbed.Plugin();
embed.reg = /{@(\w+)\s*:\s*((([^\s"'][^\s\}]*|"[^\"]*"|'[^']*')\s*)+?)}/;
embed.register({
  youtube: RemarkableEmbed.extensions.youtube,
  file_content: fileContent,
  api_url: apiUrl,
  include: markdownInclude
});

const siteConfig = {
  title: 'React4j',
  tagline: 'An opinionated react java binding',
  url: 'https://react4j.github.io',
  baseUrl: '/',
  projectName: 'react4j',
  headerLinks: [
    { doc: 'overview', label: 'Docs' },
    { href: '/api', label: 'API' },
    { blog: true, label: 'Blog' },
    { href: 'https://github.com/react4j/react4j/releases', label: 'Releases', external: true },
    { href: 'https://github.com/react4j/react4j', label: 'GitHub', external: true }/*,
    { search: true }*/
  ],
/*
  algolia: {
    apiKey: 'b8c49751e0914443436822868f0c5000',
    indexName: 'react4j'
  },
*/
  users: [],
  /* path to images for header/footer */
  headerIcon: 'img/logo.svg',
  footerIcon: 'img/logo.svg',
  favicon: 'img/favicon-32x32.png',
  /* colors for website */
  colors: {
    /* This is derived from https://coolors.co/4464ad-393939-a4b0f5-4481d6-f9f9f9 */
    primaryColor: '#4481D6',
    secondaryColor: '#4464AD'
  },
  // This copyright info is used in /core/Footer.js and blog rss/atom feeds.
  copyright: 'Copyright © ' + new Date().getFullYear() + ' the React4j Project',
  organizationName: 'react4j',
  highlight: {
    // Highlight.js theme to use for syntax highlighting in code blocks
    theme: 'idea'
  },
  scripts: ['https://buttons.github.io/buttons.js'],
  repoUrl: 'https://github.com/react4j/react4j',
  editUrl: 'https://github.com/react4j/react4j/tree/master/docs/',
  markdownPlugins: [
    embed.hook
  ]
};

module.exports = siteConfig;
