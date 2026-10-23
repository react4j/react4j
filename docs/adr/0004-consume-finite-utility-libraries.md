# Consume finite utility libraries

Consume an already compiled, flattened [Utility CSS library](../glossary/README.md#utility-css-library) rather than interpreting a utility language or invoking its generator during application compilation. Publishers prepare utility CSS beforehand; React4j owns checked Java bindings, naming and liveness. This preserves [JVM-only processing](../specs/web-assets.md) and supports independent utility systems without maintaining a Tailwind-compatible generator.
