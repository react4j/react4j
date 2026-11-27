goog.module('react4j.dom.HostTest');
const Host = goog.require('react4j.dom.DomHostBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testAttributeMutations() { Host.attributeMutations(); },
  testAttributeDiagnostics() { Host.attributeDiagnostics('production' === Mode.environment); },
  testAttributeObjectsAndBooleans() { Host.attributeObjectsAndBooleans('production' === Mode.environment); },
  testAttributeUpdates() { Host.attributeUpdates(); },
  testInnerHtml() { Host.innerHtml(); },
  testInvalidHostInputs() { Host.invalidHostInputs('production' === Mode.environment); },
  testReservedPropsAndMarkup() { Host.reservedPropsAndMarkup(); },
  testSvgNamespaces() { Host.svgNamespaces(); },
  testTextNodes() { Host.textNodes(); },
  testStyles() { Host.styles('production' === Mode.environment); },
  testStyleWarnings() { Host.styleWarnings('production' === Mode.environment); },
  testCleanStyles() { Host.cleanStyles('production' === Mode.environment); },
  testShadyDom() { Host.shadyDom('production' === Mode.environment); },
  testUnknownAttributes() { Host.unknownAttributes('production' === Mode.environment); },
  testCleanAttributeMounts() { Host.cleanAttributeMounts('production' === Mode.environment); },
  testPropertyOperations() { Host.propertyOperations('production' === Mode.environment); },
  testPortalTeardown() { Host.portalTeardown(); },
  testStaleContainers() { Host.staleContainers('production' === Mode.environment); },
});
