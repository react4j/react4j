# T02: Isolated verification and closeout

- Status: pending
- Blocked by: T01
- Coverage: R3, AC3, AC4

Extend the existing disposable downstream consumer check to copy the real examples unchanged, build every application variant and run their browser tests in both modes. Keep existing positive and negative API/compiler checks. Run the full local gate, simplify, classify durable knowledge and pass independent implementation review. Commit evidence, remove this exact plan tree and push the updated PR without merging.

Validation: `tools/check_bazel_consumer.sh`, `tools/check.sh`, `git diff --check`, independent read-only review.

Evidence: pending.
