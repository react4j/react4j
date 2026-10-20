# Rose public application delivery

- Status: complete
- Blocked by: T02
- Spec coverage: R4 / AC4

## Delivers

Switch Rose’s application declarations and asset generators to BrowserApplicationInfo outputs with matching runtimes and coherent local configuration. Package retained trees/CSS with preserved relative URLs and distinct diagnostic maps, and make compression optional downstream. Exercise real fixture outputs while respecting the shared dependency blocker.

## Acceptance

- [x] Delivered behavior satisfies assigned spec criteria.
- [x] Meaningful verification passes and limitations are explicit.

## Validation

Rose tool unit/integration fixtures; aquery local/no Brotli/no ADVANCED/no worker/WAR; production compression enabled and disabled; immutable candidate pin and module loading.

## Evidence

Rose implementation committed as 4534f236fb0. Application declarations now use the public browser_application with three explicit runtime/optimization/map combinations. Generators consume BrowserApplicationInfo trees/inventories; retained resources are packaged alongside the scripts, diagnostic references are renamed before byte hashing, and compression is selected downstream with //tools/brotli:enabled. Local serving uses a native java_binary with Bazel Java runfiles and a directory HTTP server; Closure web_library cannot consume directory artifacts as file sources.

Seven Rose Java tool test targets passed using --override_module=react4j=/Users/peter/.codex/worktrees/be7d/react4j. BrowserApplicationDeliveryTest also passed with --//tools/brotli:enabled=false. The fixture compiles real native J2CL annotations/accessors and public browser applications, then runs the actual Rose generators/archive/Brotli tool. It verifies pruned and conservatively retained assets, map names, JS byte digests, cache boundaries, CSS-relative URLs and compressed byte round-trip. Manual bazel run of the fixture server returned scripts/maps/config/version/CSS with correct MIME and CORS headers; HTTP unit test also checks changed bytes, HEAD, traversal and symlink boundaries.

Current local aquery: two ClosureLink BUNDLE actions plus RoseLocalDevelopmentAssets; no Brotli, ADVANCED, service worker or WAR actions. Production graph with compression enabled: 26 BrotliCompress + one BrotliCompressTree; disabled: zero. Both graphs analyze. Java formatting and Buildifier applied; scoped diff checked. MODULE now names immutable React4j candidate 8614f75da3d4943259dc7b7de85f193c7469faef; remote resolution/module-lock validation waits for authorized publication after review in T04/closeout.

Actual Acal build rechecked against the candidate and still fails at Closure with 393 errors: 392 duplicate Arez/annotation/Braincheck/Zemeckis modules and one duplicate namespace. Whole Rose linking, WAR/browser delivery and end-to-end iteration parity remain blocked migration gates, not claimed passing.
