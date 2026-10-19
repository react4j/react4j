"""Shared per-file preparation for managed assets and application roots."""

visibility(["//css", "//tools/browser"])

ASSET_TOOL_ATTRIBUTES = {
    "_prepare": attr.label(default = Label("//css/bundler/src/main/java/react4j/css/bundler:prepare_asset"), executable = True, cfg = "exec"),
}

def prepare_file(ctx, source, logical, stem):
    metadata = ctx.actions.declare_file(stem + ".json")
    ctx.actions.run(
        executable = ctx.executable._prepare,
        inputs = [source],
        outputs = [metadata],
        arguments = [logical, source.path, metadata.path],
        mnemonic = "PrepareWebAsset",
    )
    return metadata
