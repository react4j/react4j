# Native utility language module

For application developers, start with [using native utilities](../../docs/native-utilities.md).
Agents and rule authors should use the [language guide and reference](../../docs/utility-rules.md)
and [portable recipes](../../docs/utility-recipes.md).

## Implementation boundary

The pure facade is `react4j.css.utility.language.UtilityLanguage`; this package owns
grammar, matching, values, composition and feedback. Java annotation orchestration
and shared symbol/delivery infrastructure stay outside it. The processor distribution
shades the module; it adds no application runtime or separate Maven artifact.

Explicit file packaging produces a prepared `.rules.json` sidecar. Provider
compilation publishes a checked `.css.rules.json` descriptor, supporting CSS and
owned-name identities. Consumers publish `.css.fragments.json` contributions for
explicit requests. Collection deduplicates matching contributions and rejects
provider fingerprint/content conflicts. Linker and bundler consume generic CSS
metadata without depending on this package. Unrequested infinite families are never
enumerated; optimized output prunes through surviving getters.

Header-only inline expansions initially map to decoded rule source. Collection
composes these locations through the full provider's published input map, restoring
authored Java locations before delivery.

The [native specification](../../docs/specs/native-utility-language.md) owns the
contract. [ADR 0005](../../docs/adr/0005-compile-declarative-utility-rules-on-jvm.md)
and [ADR 0006](../../docs/adr/0006-publish-checked-utility-expansions.md) record the
architectural choices and rationale.
