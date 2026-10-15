# T11 — Java documentation site

- Status: `complete`
- Blocked by: `T10`
- Spec coverage: `R10`, `AC6`

## Delivers

Build/render/check/serve the site with Java and CommonMark using compiled example assets and aggregate Javadocs. Preserve practical routes and external deployment path ownership; omit removed API-diff/statistics content.

## Acceptance criteria

- [x] Site builds without Node, links/fragments and examples validate, route mapping is checked, and deployment preparation preserves externally maintained paths without pushing.

## Validation

- tools/site.sh build; tools/site.sh check; serve help/smoke check; isolated deployment-preparation fixture and route inventory — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- `tools/site.sh build` builds the Java/CommonMark site through `//website:site` in optimized mode, using checked/generated example sources, the compiled Hello World preview files and aggregate Javadocs. `tools/site.sh check` validates 26,921 local file/fragment links with zero broken. External page links are not fetched; assets must be local, and only the four separately owned example roots are excluded from local page existence checks.
- Preserved eight `/docs/<id>.html` routes from the original sidebar, `/`, `/en/`, `/api/`, image/favicon paths, and `/examples/hello_world.html` (forwarding to its complete runnable directory). Live local HTTP checks returned 200 for all these routes plus the compiled JS. FAQ apostrophe fragments remain stable; snippet boundaries and missing sources fail explicitly.
- `bazel test //tools/site:tests //:java_format_check`: pass. Permanent renderer/link tests cover escaped sources, directives in code, tables, frontmatter, snippet failures, decoded fragments, CSS/local assets, external-asset rejection, path escapes, published slugs and deploy ownership.
- `tools/site.sh prepare-deploy <temporary-checkout>` copied the real built site, removed a stale file and preserved `.git` plus `todomvc`, `drumloop`, `webspeechdemo`, `heart-rate-monitor` contents. Tests reject source/destination overlap and ownership collisions before deletion. No git clone, commit or push was executed. Temporary fixture removed.
- CLI help, Bash syntax, buildifier and Java format pass. Site builder Ahab declaration permits reproducibility only with the `build` flag; serve/deploy preparation are operational commands.
