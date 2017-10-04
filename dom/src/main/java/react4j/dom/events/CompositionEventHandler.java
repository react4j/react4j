package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface CompositionEventHandler
{
  void onCompositionEvent( CompositionEvent event );
}
