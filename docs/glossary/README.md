# Domain Glossary

## Language

### CSS symbol

A generated class, ID, animation or custom-property name owned by a CSS definition. Checked Java accessors expose these names to application code; one accessor value can contain several CSS symbols, such as a component root and modifier.

### Style owner

The annotated Java definition that authors a stylesheet and its unconditional global inclusions. An accessor belongs to its Style owner even when it returns an imported [CSS symbol](#css-symbol).

### Complete link

A JavaScript compilation with an explicit application or exported-library boundary, within which generated style values can be analyzed after optimization.

### Liveness report

Evidence from a [Complete link](#complete-link) identifying the generated accessor values that survive optimization, bound to that link's inputs.

### CSS variant

A stylesheet bundle with its own explicit global roots and shared CSS, which can reuse the [Liveness report](#liveness-report) from the same JavaScript compilation.
