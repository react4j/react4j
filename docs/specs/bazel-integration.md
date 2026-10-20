# Bazel Integration Specification

## Purpose

Allow applications to combine React4j with other libraries through supported Bazel entry points and shared dependencies.

## Requirements

1. Public dependency aliases preserve the referenced target's providers and reuse its compilation.
2. Shared runtime libraries have one J2CL compilation in an application's linked graph, including dependencies reached through other libraries.
3. Applications control dependency-version resolution and the compiler checks applied to their own sources. Offering a processor or checker alias does not enable it automatically.
4. Implementation dependencies remain private unless they are explicitly supported public entry points.
