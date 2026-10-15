# React4j

React4j provides a runtime, DOM bindings and an annotation processor for React views. Encrypted secrets are in `etc/secrets`; do not modify or decode them.

Use [CONTRIBUTING.md](CONTRIBUTING.md) for build commands, checks, formatting, fixture updates and contribution policy. The shared local/CI gate is `tools/check.sh`.

Read the relevant technical guide before changing its area:

- [Build and testing](docs/agents/build-and-testing.md): Bazel graph, archive boundaries, hermeticity, coverage or site actions.
- [Java and runtime](docs/agents/java-and-runtime.md): nullness contracts, runtime checks or browser configuration.
- [Code generation](docs/agents/code-generation.md): processor semantics, golden fixtures, DOM factories or React asset patches.
- [Contributing and releases](docs/agents/contributing-and-releases.md): changelog, release preparation or publication commands.

Record durable, non-obvious discoveries in the relevant guide. Keep run evidence in commits or CI; remove completed experiments and migration scaffolding.
