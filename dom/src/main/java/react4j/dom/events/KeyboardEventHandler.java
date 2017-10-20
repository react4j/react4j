package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface KeyboardEventHandler
{
  void onKeyboardEvent( KeyboardEvent event );
}
