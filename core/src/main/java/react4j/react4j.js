/**
 * This file provides the @defines for react4j configuration options.
 * See ReactConfig.java for details.
 */
goog.provide('react4j');

const {addSystemPropertyFromGoogDefine} = goog.require('jre');

/** @define {string} */
react4j.environment = goog.define('react4j.environment', 'production');
addSystemPropertyFromGoogDefine('react4j.environment', react4j.environment);
/** @define {string} */
react4j.enable_component_names = goog.define('react4j.enable_view_names', 'false');
addSystemPropertyFromGoogDefine('react4j.enable_view_names', react4j.enable_component_names);
/** @define {string} */
react4j.check_invariants = goog.define('react4j.check_invariants', 'false');
addSystemPropertyFromGoogDefine('react4j.check_invariants', react4j.check_invariants);
/** @define {string} */
react4j.minimize_input_keys = goog.define('react4j.minimize_input_keys', 'true');
addSystemPropertyFromGoogDefine('react4j.minimize_input_keys', react4j.minimize_input_keys);
/** @define {string} */
react4j.validate_input_values = goog.define('react4j.validate_input_values', 'false');
addSystemPropertyFromGoogDefine('react4j.validate_input_values', react4j.validate_input_values);
/** @define {string} */
react4j.store_debug_data_as_state = goog.define('react4j.store_debug_data_as_state', 'false');
addSystemPropertyFromGoogDefine('react4j.store_debug_data_as_state', react4j.store_debug_data_as_state);
