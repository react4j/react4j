"""An explicit preprocessing action whose output is a checked asset."""

visibility("private")

def _prepare_icon_impl(ctx):
    ctx.actions.expand_template(template = ctx.file.template, output = ctx.outputs.out, substitutions = {"@COLOR@": "#04c"})

prepare_icon = rule(
    implementation = _prepare_icon_impl,
    attrs = {"out": attr.output(mandatory = True), "template": attr.label(allow_single_file = True, mandatory = True)},
)
