# Build and testing

[CONTRIBUTING.md](../../CONTRIBUTING.md) owns build commands, verification policy and publication boundaries.

Bazel ignores a dependency module's overrides. `tools/export_bazel_config.sh` promotes React4j's pinned module configuration, compiler patches and Maven lock into the application root. Keep that export at the same React4j revision; updating the library alone can leave the consumer using incompatible compiler configuration.

Hermeticity declarations must describe the action being run. Browser tests need native process and loopback access; scope exceptions to that execution boundary. Even a shell command that only copies a file introduces a host shell dependency, so use declared tools for artifact transformations. Serving and publishing are operational commands, not reproducible build actions.

Compilation mode propagates through dependencies. When the site includes development and production applications together, preserve explicit mode transitions: an optimized site build must still emit development applications.
