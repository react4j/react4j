# Processor migration coverage inventory

Every original fixture path remains present and is declared through explicit Bazel fixture groups. The original hashes are in processor-fixture-baseline.json. Source counts: 351 input and 158 bad input; expected outputs: 476 per formatting mode. No GWT imports or GWT fixture cases were found to remove.

All original TestNG test methods have the following owners. T05 validates representative goldens/diagnostics plus both classpath/formatter regressions; T06/T07 own completion of the full semantic and golden suites. Original unknown-nullness warning expectations and any now-invalid input contracts must be replaced with the agreed JSpecify diagnostics/valid fixtures, with dispositions recorded here.

| Original test method | Bazel family |
| --- | --- |
| `processSuccessfulCompile` | `generated_goldens` |
| `processSuccessfulStingCompile` | `generated_goldens` |
| `processSuccessfulRawTypeCompileWithoutSting` | `regressions` |
| `nestedView` | `generated_goldens` |
| `nestedNestedView` | `generated_goldens` |
| `nestedCompleteComponent` | `generated_goldens` |
| `renderFromParent` | `generated_goldens` |
| `trackingInheritedAutoObserveMethodView` | `generated_goldens` |
| `validProtectedAccessOnError` | `generated_goldens` |
| `validPublicAccessViaInterfaceOnError` | `generated_goldens` |
| `validProtectedAccessOnPropChange` | `generated_goldens` |
| `validPublicAccessViaInterfaceOnPropChange` | `generated_goldens` |
| `validProtectedAccessPostMount` | `generated_goldens` |
| `validPublicAccessViaInterfacePostMount` | `generated_goldens` |
| `validProtectedAccessPostMountOrUpdate` | `generated_goldens` |
| `validPublicAccessViaInterfacePostMountOrUpdate` | `generated_goldens` |
| `validProtectedAccessPostRender` | `generated_goldens` |
| `validPublicAccessViaInterfacePostRender` | `generated_goldens` |
| `validProtectedAccessPostUpdate` | `generated_goldens` |
| `validPublicAccessViaInterfacePostUpdate` | `generated_goldens` |
| `validProtectedAccessPreRender` | `generated_goldens` |
| `validPublicAccessViaInterfacePreRender` | `generated_goldens` |
| `validProtectedAccessPreUpdate` | `generated_goldens` |
| `validPublicAccessViaInterfacePreUpdate` | `generated_goldens` |
| `validProtectedAccessProp` | `generated_goldens` |
| `validPublicAccessViaInterfaceProp` | `generated_goldens` |
| `validProtectedAccessPropValidate` | `generated_goldens` |
| `validPublicAccessViaInterfacePropValidate` | `generated_goldens` |
| `validProtectedAccessPublish` | `generated_goldens` |
| `validInheritanceSimple` | `generated_goldens` |
| `validInheritanceComplexInterface` | `generated_goldens` |
| `validInheritanceRose` | `generated_goldens` |
| `processFailedCompile` | `diagnostics` |
| `processFailedCompileInheritedPackageAccessInDifferentPackage` | `diagnostics` |
| `processFailedCompileInheritedPackageAccessFieldInDifferentPackage` | `diagnostics` |
| `processCompileWithWarnings` | `diagnostics` |
| `processCompileWithWarnings` | `diagnostics` |
| `formatGeneratedSourceFailsClearlyWithoutJdkExports` | `regressions` |
