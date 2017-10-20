package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface MouseEventHandler
{
  void onMouseEvent( MouseEvent event );
}
