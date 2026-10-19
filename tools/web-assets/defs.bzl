"""Shared per-file preparation for managed assets and application roots."""

ASSET_TOOL_ATTRIBUTES = {
    "_brotli": attr.label(default = Label("@brotli//:brotli"), executable = True, cfg = "exec"),
    "_prepare": attr.label(default = Label("//css/bundler/src/main/java/react4j/css/bundler:prepare_asset"), executable = True, cfg = "exec"),
}

def prepare_file(ctx, source, logical, stem):
    compressed = ctx.actions.declare_file(stem + ".br")
    metadata = ctx.actions.declare_file(stem + ".json")
    ctx.actions.run(
        executable = ctx.executable._brotli,
        inputs = [source],
        outputs = [compressed],
        arguments = ["-q", "11", "-f", "-o", compressed.path, source.path],
        mnemonic = "BrotliCompress",
    )
    ctx.actions.run(
        executable = ctx.executable._prepare,
        inputs = [source, compressed],
        outputs = [metadata],
        arguments = [logical, source.path, compressed.path, metadata.path],
        mnemonic = "PrepareWebAsset",
    )
    return metadata, compressed
