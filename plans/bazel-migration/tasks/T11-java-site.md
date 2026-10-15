# T11 — Java documentation site

- Status: `in_progress`
- Blocked by: `T10`
- Spec coverage: `R10`, `AC6`

## Delivers

Build/render/check/serve the site with Java and CommonMark using compiled example assets and aggregate Javadocs. Preserve practical routes and external deployment path ownership; omit removed API-diff/statistics content.

## Acceptance criteria

- [ ] Site builds without Node, links/fragments and examples validate, route mapping is checked, and deployment preparation preserves externally maintained paths without pushing.

## Validation

- tools/site.sh build; tools/site.sh check; serve help/smoke check; isolated deployment-preparation fixture and route inventory — proves the delivered behavior. Record actual labels, commands and outcomes below; do not mark complete with skipped required checks.

## Evidence

- pending
