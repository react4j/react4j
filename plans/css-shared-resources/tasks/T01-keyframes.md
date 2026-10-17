# T01 — Shared keyframes

Status: complete. Blocked by: none. Coverage: R1, R3; AC1, AC3.

Deliver @Keyframes and typed repeatable @CssImport, CLASS-retained declaration contracts, sidecar metadata production/validation, J2CL metadata retention and bundle dependency traversal for shared animations. Keep existing component roots conservative. Validate source/compiled/header-jar imports, exact representative goldens, malformed declarations and alias/reference matrices, emitted CSS/maps, unused imports and deduplication. Run focused processor, internal, bundler and integration tests. Evidence: tools/update_goldens.sh css passed all 385 packaged processor cases, including paired shared-animation goldens. bazel test //css:tests passed all seven suites (compiler/metadata/bundler units and the separate javac/J2CL/header-jar integration graph). buildifier_check and tools/java_format.sh check passed. Existing generated Java/CSS/maps are unchanged; metadata goldens are added.
