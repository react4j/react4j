# Processor migration coverage inventory

T05 retained every original fixture path and declared it through explicit Bazel fixture groups. T07 dispositions are recorded below. The original hashes are in processor-fixture-baseline.json. Source counts: 351 input and 158 bad input; expected outputs: 476 per formatting mode. No GWT imports or GWT fixture cases were found to remove.

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

## T07 dispositions

- All 38 original TestNG method declarations remain, including both warning-test overloads, inherited/package-access tests, no-Sting compilation, and the formatter subprocess regression. Inherited-view helpers now declare their builder as well as their view output; the complete-set assertion exposed the old omission.
- Removed four obsolete fixtures: `input/com/example/input/{UnspecifiedNullabilityInputView,SuppressedNullabilityInputView}.java` and `input/com/example/render/{UnspecifiedNullabilityRenderView,SuppressedNullabilityRenderView}.java`. Their missing-explicit-annotation warnings/suppressions no longer exist under JSpecify declaration scopes. The new unknown-contract diagnostic, marked/unmarked source/compiled inspector, and legacy-annotation-ignored regressions own that contract coverage.
- Nullable handler parameters are now explicit on existing change/validation fixtures, including inherited interface/base declarations. This preserves their original nullable-input intent under a marked default. Non-null mismatch diagnostic cases remain. The boxed-primitive diagnostic now describes effective non-nullness instead of a particular legacy annotation.
- Strict checking covers every generated Java body in both golden modes and the no-Sting family. Successful warning/suppression fixtures also receive a separate strict body check. The negative control inserts a null return into an otherwise valid marked generated class and requires the NullAway error.
- Added focused source/compiled inherited bindings, enclosing/nested arguments, upper/lower wildcard bounds, nullable array versus element types, parametric inputs, presence/default/omission diagnostics, and marked/unmarked constructor/injection cases. Generated expected files are produced by the processor, never edited by hand.
- Inventory after these additions: 356 input, 163 bad input, and 490 expected Java files per formatting mode. All original bad-input and expected-output paths remain. The four obsolete warning/suppression paths above are the complete removed-path list from the T05 manifest.

- The baseline method-name capture originally included helper calls after the 38 TestNG declarations. It is now regenerated from `@Test public void` declarations in the original inventory commit; all 38 remain.

- The composed presence/default fixture also covers deprecated input methods/defaults alongside concrete collection signatures and nullable context publication. Deprecation is emitted on builder methods; combined suppression entries prevent duplicate annotations.
