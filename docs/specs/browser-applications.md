# Browser Applications Specification

## Purpose

Build browser applications with consistent React runtime configuration and JavaScript compilation settings, including applications whose deployment tools supply their launch documents.

## Requirements

1. React development/production runtime selection and Closure BUNDLE/ADVANCED optimization selection are independent.
2. Explicit application defines override the selected framework defaults. BUNDLE runtime values and compiled values use the same compiler define grammar, primitive types and override order.
3. Applications may supply their launch documents downstream. A BUNDLE application without a supplied document exposes its effective runtime configuration for loading before its application script.
4. Omitted runtime and optimization settings preserve the ordinary Bazel defaults: production React and ADVANCED under `-c opt`, development React and BUNDLE otherwise.
5. Unsupported explicit modes fail with a clear diagnostic.

## Related contracts

[CSS source maps](css-source-maps.md) governs independent map publication; [checked web assets](web-assets.md) governs application files and downstream delivery.
