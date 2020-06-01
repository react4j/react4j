/**
 * Core React4j API.
 */
@OmitPattern( type = "^react4j\\.[^.]+$", symbol = "^\\$clinit$" )
// Make sure the generated classes do not have clinits unless required
@OmitPattern( type = "^.*\\.React4j_[^.]+$", symbol = "^\\$clinit$" )
@KeepPattern( type = "^.*\\.React4j_[^.]+\\$Factory$", symbol = "^\\$clinit$" )
@OmitPattern( type = "^.*\\.React4j_[^.]+$", symbol = "^\\$\\$react4j\\$\\$_storeDebugDataAsState", unless = "react4j.store_debug_data_as_state" )
package react4j;

import grim.annotations.KeepPattern;
import grim.annotations.OmitPattern;
