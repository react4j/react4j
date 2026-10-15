# Contributing and releases

[CONTRIBUTING.md](../../CONTRIBUTING.md) owns contribution and verification policy. The [release guide](../../tools/release/README.md) owns release operations and their dependency publication prerequisites.

Put user-visible behavior, generated API and diagnostic changes under `Unreleased` in `CHANGELOG.md`. The release preparation command adds `Changes in this release:` to a dated release; leave that header out of Unreleased.

Keep product and Arez Maven version inputs separate. Pinned migrated dependency sources do not imply their contents are already published under an older Maven version. Signing/upload require a compatible published Arez release; Proton is shaded into the processor. Build/check/unsigned validation must remain independent of signing credentials, repository secrets and remote writes.
