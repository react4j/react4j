# Contributing and releases

Follow [`CONTRIBUTING.md`](../../CONTRIBUTING.md) and [`CODE_OF_CONDUCT.md`](../../CODE_OF_CONDUCT.md) when preparing contributions.

Every user-facing behavior change needs a [`CHANGELOG.md`](../../CHANGELOG.md) entry under `Unreleased` in the same change. This includes generated-code behavior and developer-facing warnings. Release automation adds `Changes in this release:`; leave that header out of `Unreleased`.

Release orchestration is in [`tasks/release.rake`](../../tasks/release.rake), including Maven Central publishing, website deployment, tagging, GitHub releases, and downstream updates. Build/test helpers and the publishing behavior of `ci` are described in [Build and testing](build-and-testing.md).
