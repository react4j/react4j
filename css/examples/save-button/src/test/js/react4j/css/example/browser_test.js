goog.module('react4j.css.example.BrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const Test = goog.require('react4j.css.example.CssBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
/** @define {string} */
const variant = goog.define('react4j.css.example.variant', '');
/** @type {!Test} */
let test;
testSuite({
  setUpPage() { TestCase.getActiveTestCase().promiseTimeout = 30000; },
  setUp() { test = new Test('production' === Mode.environment, variant); return test.setUp(); },
  tearDown() { test.tearDown(); },
  testPackagedAssetsAtAnApplicationMountAndNestedRoute() { return test.testPackagedAssetsAtAnApplicationMountAndNestedRoute(); },
  testClassNamingPolicy() { return test.testClassNamingPolicy(); },
  testGeneratedBindingsAreSingletons() { return test.testGeneratedBindingsAreSingletons(); },
  testInliningKeepsModifierAndRootWhileDeadValuesLoseTheirCss() { return test.testInliningKeepsModifierAndRootWhileDeadValuesLoseTheirCss(); },
  testInterfaceImplementationsKeepIndependentClasses() { return test.testInterfaceImplementationsKeepIndependentClasses(); },
  testSharedKeyframesAreReusedAndUnreferencedDefinitionsAreExcluded() { return test.testSharedKeyframesAreReusedAndUnreferencedDefinitionsAreExcluded(); },
  testEqualReadablePropertyAliasesKeepOnlyTheirOwnProvider() { return test.testEqualReadablePropertyAliasesKeepOnlyTheirOwnProvider(); },
  testCheckedIdsFragmentsAndJavaOnlyResourceBindings() { return test.testCheckedIdsFragmentsAndJavaOnlyResourceBindings(); },
  testEqualGetterTextPreservesBindingKindAndLiveness() { return test.testEqualGetterTextPreservesBindingKindAndLiveness(); },
  testEqualImportedIdsKeepOnlyTheLiveProviderChain() { return test.testEqualImportedIdsKeepOnlyTheLiveProviderChain(); },
  testExplicitGlobalSelectionAndDependencyFirstCascade() { return test.testExplicitGlobalSelectionAndDependencyFirstCascade(); },
  testComponentImportsRetainGlobalsWithoutExplicitRoots() { return test.testComponentImportsRetainGlobalsWithoutExplicitRoots(); },
  testCheckedPeerSelectorFollowsFocus() { return test.testCheckedPeerSelectorFollowsFocus(); },
  testGeneratedBindingsAndCascade() { return test.testGeneratedBindingsAndCascade(); },
  testTypedPropertiesUpdateAndRemoveValues() { return test.testTypedPropertiesUpdateAndRemoveValues(); },
  testFocusAndIndependentFragmentReordering() { return test.testFocusAndIndependentFragmentReordering(); },
});
