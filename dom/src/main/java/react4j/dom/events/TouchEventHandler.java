package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface TouchEventHandler
{
  void onTouchEvent( TouchEvent event );
}
