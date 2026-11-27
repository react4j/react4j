goog.module('react4j.dom.FormsTest');
const Forms = goog.require('react4j.dom.FormsBrowserTest');
const Mode = goog.require('react4j.browser.Mode');
const testSuite = goog.require('goog.testing.testSuite');
testSuite({
  testAFormWarnings() { Forms.formWarnings('production' === Mode.environment); },
  testOptionChildren() { Forms.optionChildren('production' === Mode.environment); },
  testOptionValues() { Forms.optionValues(); },
  testSelectDefaults() { Forms.selectDefaults(); },
  testSelectControlled() { Forms.selectControlled(); },
  testSelectChanges() { Forms.selectChanges(); },
  testSelectNonTextValues() { Forms.selectNonTextValues(); },
  testTextareaValues() { Forms.textareaValues(); },
  testTextareaChildren() { Forms.textareaChildren('production' === Mode.environment); },
  testTextareaMutations() { Forms.textareaMutations(); },
  testTextareaNonTextValues() { Forms.textareaNonTextValues(); },
  testInputValues() { Forms.inputValues(); },
  testInputNumbers() { Forms.inputNumbers(); },
  testInputMutations() { Forms.inputMutations(); },
  testInputAttributes() { Forms.inputAttributes(); },
  testInputNonTextValues() { Forms.inputNonTextValues(); },
  testInputChanges() { Forms.inputChanges(); },
  testInputRadioGroups() { Forms.inputRadioGroups(); },
  testInputWriteOrder() { Forms.inputWriteOrder(); },
});
