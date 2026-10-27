# Preserve native nodes through a renderable union

Use a native `Renderable` marker shared by native `ReactNode` and ordinary Java `Buildable`, detecting `Buildable` at typed composition boundaries. The [pinned J2CL compiler](../../MODULE.bazel) requires native interfaces to extend only native interfaces; wrapping every node would change the representation of native strings, numbers and arrays. This hierarchy preserves native values while supporting generated and hand-written builders under the [composition contract](../specs/renderable-composition.md).
