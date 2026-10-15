# React4j

React4j is an opinionated Java binding for React, with a runtime, DOM bindings, and an annotation processor that generates view APIs.

Encrypted secrets are in `etc/secrets`; do not modify or decode them.

## Build and verification

- Package all modules: `bundle exec buildr clean package`.
- Run all tests: `bundle exec buildr test` (includes external downstream builds).
- Test processor changes: `bundle exec buildr react4j:processor:test`.

## Task-specific guidance

Read the relevant guide before working in its area:

- [Build and testing](docs/agents/build-and-testing.md): environment setup, test configuration, downstream builds, API diffs, or website tasks.
- [Java and runtime](docs/agents/java-and-runtime.md): Java source changes, GWT/J2CL compatibility, or runtime configuration.
- [Code generation](docs/agents/code-generation.md): annotation processing, generated APIs, processor fixtures, DOM factories, or bundled JavaScript assets.
- [Contributing and releases](docs/agents/contributing-and-releases.md): user-facing behavior changes, changelog edits, commits, pull requests, or release automation.

Record durable, non-obvious discoveries in the relevant guide.
