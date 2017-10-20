package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface UIEventHandler
{
  void onUIEvent( UIEvent event );
}
