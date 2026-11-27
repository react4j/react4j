# Browser Applications Specification

## Purpose

Build browser applications with consistent React runtime configuration and JavaScript compilation settings, including applications whose deployment tools supply their launch documents.

## Requirements

1. The application includes the J2CL-compiled Java renderer. Its development and production settings are independent of Closure BUNDLE and ADVANCED optimization.
2. Explicit application defines override the selected framework defaults. BUNDLE runtime values and compiled values use the same compiler define grammar, primitive types and override order.
3. Applications may supply their launch documents downstream. A BUNDLE application without a supplied document exposes its effective runtime configuration for loading before its application script.
4. Bazel defaults to production settings and ADVANCED under `-c opt`, and development settings and BUNDLE otherwise.
5. Unsupported explicit modes fail with a clear diagnostic.

## Related contracts

[CSS source maps](css-source-maps.md) governs independent map publication; [checked web assets](web-assets.md) governs application files and downstream delivery.
