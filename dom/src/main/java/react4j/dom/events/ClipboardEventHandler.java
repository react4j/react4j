package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface ClipboardEventHandler
{
  void onClipboardEvent( ClipboardEvent event );
}
