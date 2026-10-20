# Bundle CSS variants from one link

Bundle each [CSS variant](../glossary/README.md#css-variant) separately from the same [Complete link](../glossary/README.md#complete-link)'s [Liveness report](../glossary/README.md#liveness-report) and [CSS name table](../glossary/README.md#css-name-table). This allows themes with different global roots and shared styles to reuse one JavaScript compilation. Assign names during linking: allocating them independently during bundling could disagree with the names already emitted in JavaScript.
