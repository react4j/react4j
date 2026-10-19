# Finalize asset URLs after linking

Keep [Checked asset](../glossary/README.md#checked-asset) declarations independent of file contents and finalize direct URLs after a [Complete link](../glossary/README.md#complete-link), so byte changes preserve Java, J2CL and Closure compilation caches without a runtime lookup table. Prepare and compress each file independently before delivery filtering: Bazel analysis cannot depend on generated liveness, so unused candidates may incur preparation on a cold build while retained files reuse cached work.
