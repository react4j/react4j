package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface FocusEventHandler
{
  void onFocusEvent( FocusEvent event );
}
