package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface WheelEventHandler
{
  void onWheelEvent( WheelEvent event );
}
