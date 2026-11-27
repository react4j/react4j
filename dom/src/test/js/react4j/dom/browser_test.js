goog.module('react4j.dom.BrowserTest');
const Children = goog.require('react4j.dom.ChildReconciliationTest');
const CleanElements = goog.require('react4j.dom.CleanClientElementsBrowserTest');
const DomPortal = goog.require('react4j.dom.DomPortalBrowserTest');
const DomRefs = goog.require('react4j.dom.DomRefsBrowserTest');
const DomRoot = goog.require('react4j.dom.DomRootBrowserTest');
const Events = goog.require('react4j.dom.EventsBrowserTest');
const Forms = goog.require('react4j.dom.FormsBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const OriginalHost = goog.require('react4j.dom.OriginalHostCasesBrowserTest');
const Parity = goog.require('react4j.dom.RuntimeParityBrowserTest');
const Runtime = goog.require('react4j.dom.RuntimeBrowserTest');
const TestCase = goog.require('goog.testing.TestCase');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testRootValuesAndCallbacks() { DomRoot.valuesAndCallbacks(); },
  testFragmentFirstHosts() { DomRoot.fragmentFirstHosts(); },
  testCleanFragments() { DomRoot.cleanFragments(); },
  testCleanCheckboxes() { Forms.cleanCheckboxes('production' === Mode.environment); },
  testCleanInputs() { Forms.cleanInputs('production' === Mode.environment); },
  testCleanUserInteraction() { Forms.cleanUserInteraction('production' === Mode.environment); },
  testCleanBasicMounts() { CleanElements.basic('production' === Mode.environment); },
  testNestedClickOnMount() { CleanElements.nestedClickOnMount('production' === Mode.environment); },
  testHostReplacementAndQueries() { CleanElements.hostReplacementAndQueries(); },
  testEmptyComponents() { CleanElements.emptyComponents('production' === Mode.environment); },
  testCompositeMarkupMinimalism() { CleanElements.minimalism('production' === Mode.environment); },
  testCleanSpecialTypes() { CleanElements.specialTypes('production' === Mode.environment); },
  testClientRefsWithoutServerMarkup() { CleanElements.clientRefs('production' === Mode.environment); },
  testMockedUndefined() { CleanElements.mockedUndefined('production' === Mode.environment); },
  testCleanProgress() { CleanElements.progress('production' === Mode.environment); },
  testCleanClientElements() { CleanElements.run('production' === Mode.environment); },
  testPortalCommits() { DomRoot.portalCommits(); },
  testPortalEventsAndContext() { DomPortal.run(); },
  testPortalNamespaces() { DomRoot.portalNamespaces(); },
  testReconcileComponentIdentity() { DomRoot.reconcileComponents(); },
  testEmptyFragmentsAndRoots() { DomRoot.emptyFragmentsAndRoots(); },
  testDestroyRoots() { DomRoot.destroyRoots('production' === Mode.environment); },
  testMountPoints() { DomRoot.mountPoints('production' === Mode.environment); },
  testCommentAndFragmentContainers() { DomRoot.commentAndFragmentContainers(); },
  testRenderDocument() { DomRoot.renderDocument(); },
  testIframeTextDocument() { DomRoot.iframeTextDocument(); },
  testDestroyRefs() { DomRoot.destroyRefs(); },
  testRootRefs() { DomRefs.run(); },
  testOwnerlessRef() { DomRefs.ownerlessRef(); },
  testConstructorRef() { DomRefs.constructorRef(); },
  testAsyncRefs() { return DomRefs.destroyAsynchronously(); },
  testDomChildIdentity() { DomRoot.childIdentity(); },
  testKeyRefsAndUpdates() { DomRoot.keyRefsAndUpdates(); },
  testIterables() { DomRoot.iterables('production' === Mode.environment); },
  testRemountIterableChild() { DomRoot.remountIterableChild(); },
  testMixedTextChildren() { DomRoot.mixedTextChildren(); },
  testANestingDiagnostics() { Events.runNestingDiagnostics('production' === Mode.environment); },
  testAFormDiagnostics() { Events.runFormDiagnostics('production' === Mode.environment); },
  testControlsAndEvents() { Events.runControlsAndEvents(); },
  testOriginalHostCases() {
    OriginalHost.run();
    Forms.cleanSelects('production' === Mode.environment);
  },
  testHostProperties() { Events.runHostProperties(); },
  testBeforeInputFallback() { Events.runBeforeInputFallback(); },
  testBeforeInput() { Events.runBeforeInput(); },
  testEnterLeave() { Events.runEnterLeave(); },
  testUncontrolled() { Events.runUncontrolled(); },
  testKeyboardVariants() { Events.runKeyboardVariants('production' === Mode.environment); },
  testDomEventVariants() { Events.runDomEventVariants(); },
  testNativeSimulationEquivalents() { Events.nativeSimulationEquivalents(); },
  testManagedTreeTraversal() { Events.treeTraversal(); },
  testIndependentEventTargets() { Events.independentTargets(); },
  testInvalidListeners() { Events.runInvalidListeners('production' === Mode.environment); },
  testEvents() { Events.runBehavior(); },
  testEventPayloads() { Events.runPayloads(); },
  testDomSuspenseVisibility() { return Parity.suspenseVisibility(); },
  testFocusCommit() { Parity.focusCommit(); },
  testContentEditableSelectionOffsets() { Parity.nestedSelectionOffsets(); },
  testParity() {
    TestCase.getActiveTestCase().promiseTimeout = 15000;
    // React 16 development replays caught render errors through window.onerror.
    const previous = window.onerror;
    /**
     * @param {(!Event|string)} message
     * @param {string=} url
     * @param {number=} line
     * @param {number=} column
     * @param {*=} error
     * @return {boolean|undefined}
     */
    function onError(message, url, line, column, error) {
      if ('string' === typeof message && (message.endsWith('A descendant failed') || message.endsWith(' parity failure') ||
          message.includes('Maximum update depth exceeded'))) {
        return true;
      }
      if (previous) {
        previous.apply(window, arguments);
      }
      return undefined;
    }
    window.onerror = onError;
    return Promise.resolve()
        .then(() => Parity.run('production' === Mode.environment))
        .finally(() => {
          window.onerror = previous;
        });
  },
  testChildIdentity() { Children.run(); },
  testRuntime() { return Runtime.run(); },
});
