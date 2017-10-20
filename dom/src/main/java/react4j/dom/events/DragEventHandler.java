package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface DragEventHandler
{
  public void onDragEvent( DragEvent event );
}
