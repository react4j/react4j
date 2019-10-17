/**
 * Core React4j API.
 */
@OmitPattern( type = "^react4j\\.[^\\.]+$", symbol = "^\\$clinit$" )
// Make sure the generated classes do not have clinits unless required
@OmitPattern( type = "^.*\\.React4j_[^\\.]+$", symbol = "^\\$clinit$" )
@KeepPattern( type = "^.*\\.React4j_[^\\.]+\\$Factory$", symbol = "^\\$clinit$" )
package react4j;

import grim.annotations.KeepPattern;
import grim.annotations.OmitPattern;
