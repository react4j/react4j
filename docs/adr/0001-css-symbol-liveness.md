# Preserve symbolic style values through Closure optimization

Generated accessor methods can disappear through inlining before CSS reachability is known. Preserve each accessor's [CSS symbol](../glossary/README.md#css-symbol) identities as compiler-only values during one JVM Closure compilation, collect surviving values after optimization, then lower them to ordinary strings. This preserves identity through inlining without guessing from method names, scanning arbitrary final strings or compiling JavaScript twice.
