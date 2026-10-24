import json, os, re, subprocess, tempfile
from pathlib import Path

root = Path.cwd()
plan = root / 'plans/native-utility-documentation'
assert plan.is_dir()
def run(args, expected=0):
    p = subprocess.run([str(x) for x in args], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    assert p.returncode == expected, (args, p.returncode, p.stdout, p.stderr)
    return p.stdout

author = (root / 'docs/utility-rules.md').read_text()
sh = re.findall(r'```sh\n(.*?)\n```', author, re.S)
commands = '\n'.join(sh[:2])
result = run(['bash', '-e', '-c', commands])
assert 'checked:' in result and 'p-137' in result and 'Scales:' in result
print('Documented build, checked validate, describe and unseen candidate expansion: pass')
setup = sh[0]
result = run(['bash', '-e', '-c', setup + '\n' + sh[2]], 1)
for code in ['U_VALUE_TYPE', 'U_VALUE_RANGE', 'U_UNKNOWN_UTILITY']:
    assert code in result, result
print('Documented failing batch: all three diagnostic codes and status 1 verified')

cli = (root / 'bazel-bin/css/processor/src/main/java/react4j/css/processor/utility_rules').resolve()
runfiles = Path(str(cli) + '.runfiles')
jdk = next(runfiles.glob('rules_java++toolchains+remotejdk*/bin/javac')).resolve().parent
processor = (root / 'bazel-bin/css/processor/css-processor.jar').resolve()
core = (root / 'bazel-bin/css/core/src/main/java/react4j/css/libcss.jar').resolve()
jspecify = runfiles / '+http_file+jspecify/file/org/jspecify/jspecify/1.0.1/jspecify-1.0.1.jar'

with tempfile.TemporaryDirectory(prefix='react4j-native-guide-') as name:
    temp = Path(name)
    recipes = re.findall(r'```css\n(.*?)\n```', (root/'docs/utility-recipes.md').read_text(), re.S)
    source = temp / 'recipes.css'
    source.write_text('\n'.join(recipes))
    jar = temp / 'inputs.jar'
    run([jdk/'java','-cp',processor,'react4j.css.processor.CssInputJar',jar,'demo/recipes.css',source])
    java = temp / 'RecipeUtilities.java'
    provider_code = re.findall(r'```java\n(.*?)\n```',(root/'docs/utility-recipes.md').read_text(), re.S)[0]
    java.write_text('package demo;\nimport react4j.css.*;\n' + provider_code)
    classes = temp / 'classes'
    cp = os.pathsep.join(map(str,[core,jspecify,jar]))
    run([jdk/'javac','--release','17','-classpath',cp,'--processor-path',processor,'-Areact4j.css.format_generated_source=false','-d',classes,java])
    checked = classes/'demo/RecipeUtilities.css.rules.json'
    assert checked.is_file()
    positive = [
        '[&_th]:p-3', '[&_th]:bg-table-tint', '[&_th]:not-first:border-l',
        '[&_th]:nth-[n+3]:border-l', '[&_th:not(:first-child):not(:nth-child(2))]:p-3',
        '[&_td_div:not(.iconStack)]:w-full', 'th:not-first:border-l',
        'sticky', 'bottom-0', 'shadow-footer', 'shadow-[0_-1px_5px_1px_rgba(var(--cs1),.2)]',
        '-ml-2','bg-overlay','fill-panel','left-[anchor(left)]','min-w-[anchor-size(width)]']
    data = json.loads(run([cli,'expand',checked,*positive]))
    assert len(data['expansions']) == len(positive) and not data['diagnostics']
    consumer = temp / 'RecipeStyles.java'
    methods = []
    for i, candidate in enumerate(positive):
        methods.append('@UtilityCss(' + json.dumps(candidate) + ') abstract String ' + ('style' if i == 0 else 'value'+str(i)) + '();')
    consumer.write_text('package demo; import react4j.css.*; @UtilityStyles(RecipeUtilities.class) abstract class RecipeStyles {' + '\n'.join(methods) + '}')
    run([jdk/'javac','--release','17','-classpath',os.pathsep.join([str(classes),cp]),'--processor-path',processor,'-Areact4j.css.format_generated_source=false','-d',classes,consumer])
    assert (classes/'demo/Styled_RecipeStyles.class').is_file()
    for candidate, code in [('bg-[12px]','U_VALUE_TYPE'),('p-[anchor(left)]','U_VALUE_CONTEXT')]:
        data = json.loads(run([cli,'expand',checked,candidate],1))
        assert any(d['code'] == code for d in data['diagnostics']), data
    print('Recipe source: packaged explicit input and real provider/consumer javac compilation pass; 16 positive expansions and 2 negative cases verified')

    blocks = re.findall(r'```css\n(.*?)\n```',author,re.S)
    base = blocks[0]
    requests = [
        ['p-3','p-1.5','p-panel','p-[10%]','-ml-2','bg-[rgba(var(--cs5),.05)]'],
        ['duration-150','opacity-.5','cols-[200px_minmax(0,1fr)]','aspect-16/9'],
        ['th:hover:bg-link','[&_th]:nth-[n+3]:border-l'],
        ['card-min-small:starting:opacity-0','compact:p-3','scrollable-down:p-3'],
        ['popover-scope','popover-trigger','popover-panel','popover-match-width']]
    assert len(blocks) == len(requests), len(blocks)
    for i, block in enumerate(blocks):
        current = temp / ('reference'+str(i)+'.css')
        current.write_text(block if i == 0 else base + '\n' + block)
        data = json.loads(run([cli,'validate',current,checked]))
        assert data['status'] == 'checked'
        descriptor = temp / ('reference'+str(i)+'.json')
        descriptor.write_text(json.dumps(data['library']))
        expanded = json.loads(run([cli,'expand',descriptor,*requests[i]]))
        assert len(expanded['expansions']) == len(requests[i])
    escaping = temp/'escaping.css'
    escaping.write_text(base + '\n@utility "content-{value}" (value:arbitrary(<string>)) { content:param(value); }')
    data = json.loads(run([cli,'validate',escaping,checked]))
    descriptor = temp/'escaping.json';descriptor.write_text(json.dumps(data['library']))
    data = json.loads(run([cli,'expand',descriptor,"content-['Save_changes']", "content-['Save\\_changes']"]))
    by_candidate = {e['candidate']: e['css'] for e in data['expansions']}
    assert "Save changes" in by_candidate["content-['Save_changes']"]
    assert "Save_changes" in by_candidate["content-['Save\\_changes']"]
    java_strings = re.search(r'```java\n(@UtilityCss\("content-.*?\n)```', author, re.S).group(1)
    probe = temp / 'StringProbe.java'
    literal = '"""\n@utility-rules; @utility "content-{value}" (value:arbitrary(<string>)) { content:param(value); }\n"""'
    probe.write_text('package demo; import react4j.css.*; @UtilityCssLibrary(' + literal + ') class StringUtilities {} @UtilityStyles(StringUtilities.class) abstract class StringStyles { abstract String style();' + java_strings + '}')
    run([jdk/'javac','--release','17','-classpath',cp,'--processor-path',processor,'-Areact4j.css.format_generated_source=false','-d',classes,probe])
    assert (classes/'demo/Styled_StringStyles.class').is_file()
    for candidate in ['p-3']:
        data = json.loads(run([cli,'expand',descriptor,candidate,candidate],1))
        assert data['diagnostics'][0]['code'] == 'U_CANDIDATE_SYNTAX'
    data = json.loads(run([cli,'expand',descriptor,''],1))
    assert data['diagnostics'][0]['code'] == 'U_CANDIDATE_SYNTAX'
    namespaces = temp/'namespaces.css'
    namespaces.write_text(base + '\n@utility Flex { display:grid; } @variant flex { &:focus { @slot; } }')
    data = json.loads(run([cli,'validate',namespaces,checked]))
    descriptor.write_text(json.dumps(data['library']))
    data = json.loads(run([cli,'expand',descriptor,'flex','Flex','flex:flex']))
    assert len(data['expansions']) == 3
    print('All 5 author-reference CSS sections: checked binding and 19 expansions pass; Java string escaping, empty/duplicate rejection and case-sensitive separate namespaces verified')
