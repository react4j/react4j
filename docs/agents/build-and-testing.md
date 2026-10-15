# Build and testing

## Setup and dependencies

Use JDK 17+, Bundler with the Ruby version in [`.ruby-version`](../../.ruby-version), and Node.js with Yarn for the website (see [`.node-version`](../../.node-version)). Bootstrap with `bundle install` and `yarn install`.

Dependency coordinates belong in [`build.yaml`](../../build.yaml); module wiring and test properties are in [`buildfile`](../../buildfile).

Zemeckis uses JSpecify type-use annotations, so the GWT path needs `jspecify` in `CORE_DEPS` and GWT 2.13.1 support. [`Gemfile`](../../Gemfile) pins a Buildr revision with that support that still generates IntelliJ `.ipr` files; preserve both capabilities when changing the pin.

## Tests

Modules use TestNG. Buildr supplies the `react4j.core.compile_target` and `react4j.dom.compile_target` properties required by JDepend tests; supply these explicitly when running tests outside Buildr.

The root [AGENTS.md](../../AGENTS.md) lists the standard build and test commands. For processor fixture changes, also read [Code generation](code-generation.md).

The full test task builds external example repositories through `downstream-test`. [`tasks/util.rb`](../../tasks/util.rb) selects branches, including `master` for several examples. Breaking API or annotation changes can fail there until downstream repositories migrate, even when local processor and API-diff tests pass.

Build flags:

| Flag | Effect |
| --- | --- |
| `TEST=no` | Skip tests. |
| `GWT=no` | Skip GWT compilation. |
| `J2CL=no` | Skip downstream J2CL variants. |
| `DOWNSTREAM=no` | Skip downstream application builds; build-statistics collection is controlled separately. |
| `BUILD_STATS=no` | Skip build-statistics collection and related tests. |
| `PRODUCT_VERSION`, `PREVIOUS_PRODUCT_VERSION` | Select versions for release, API-diff, and downstream tasks. |
| `STORE_API_DIFF=true`, `STORE_BUILD_STATISTICS=true` | Update the corresponding fixtures. |

## API diffs and downstream statistics

API-diff tests compare previous and current core/DOM jars against `api-test/src/test/resources/fixtures`. Downstream statistics fixtures live in `downstream-test/src/test/resources/fixtures`.

| Task | Command |
| --- | --- |
| Update API-diff fixtures | `bundle exec buildr update_api_diff` |
| Verify API diffs | `bundle exec buildr test_api_diff` |
| Update downstream statistics | `bundle exec buildr update_downstream_build_stats` |
| Verify downstream statistics | `bundle exec buildr test_downstream_build_stats` |

These helpers derive versions from the changelog and run clean builds. `update_api_diff` also stages its fixture; see [`tasks/api_diff.rake`](../../tasks/api_diff.rake) and [`tasks/update_downstream_build_stats.rake`](../../tasks/update_downstream_build_stats.rake).

## Website

Markdown content is in `docs/`; the Docusaurus site is in `website/`. [`tasks/site.rake`](../../tasks/site.rake) integrates Javadocs, API diffs, favicons, and GWT examples from `doc-examples/`.

- Serve: `bundle exec buildr site:serve`.
- Build into `reports/site`: `bundle exec buildr site:build`.
- Check links after building: `bundle exec buildr site:link_check`.

`bundle exec buildr ci` derives versions, packages the project, and deploys the website; it publishes changes through `site:deploy`.
