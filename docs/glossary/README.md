# Domain Glossary

## Language

### CSS symbol

A managed class, ID, animation, custom-property, font-family or container name owned by a CSS definition. One checked accessor value can contain several CSS symbols, such as a component root and modifier.

### Utility CSS library

A finite stylesheet definition supplying managed classes and other [CSS symbol](#css-symbol) definitions to checked Java style declarations.

### Utility style

A Java style definition selecting class lists from one [Utility CSS library](#utility-css-library), while owning its own component identity markers.

### Stable CSS symbol

A [CSS symbol](#css-symbol) whose name is intentionally preserved when a link compresses other names.

### CSS name table

The assignment of readable and compiled spellings to every [CSS symbol](#css-symbol) in a [Complete link](#complete-link), shared by all of its [CSS variant](#css-variant) outputs.

### Style owner

The annotated Java definition that authors a stylesheet and its unconditional global inclusions. An accessor belongs to its Style owner even when it returns an imported [CSS symbol](#css-symbol).

### Complete link

A JavaScript compilation with an explicit application or exported-library boundary, within which generated style and [Checked asset](#checked-asset) values can be analyzed after optimization.

### Liveness report

Evidence from a [Complete link](#complete-link) identifying the generated accessor values that survive optimization, bound to its JavaScript output and checked declarations.

### CSS variant

A stylesheet bundle with its own explicit global roots and shared CSS, which can reuse the [Liveness report](#liveness-report) from the same JavaScript compilation.

### Checked asset

A file identified by a canonical logical path, exposed through checked Java declarations and available to Java or CSS consumers independently of its final deployment URL.

### Asset inventory

A build-only account of retained files and pruned [Checked asset](#checked-asset) candidates, connecting their source identities to deployment URLs, contents and retention reasons.
