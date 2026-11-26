package react4j.internal;

import react4j.JsError;
import react4j.ReactErrorInfo;

/**
 * Optional lifecycle interface for componentDidCatch react lifecycle.
 */
public interface OnComponentDidCatch {
    void componentDidCatch(JsError error, ReactErrorInfo info);
}
