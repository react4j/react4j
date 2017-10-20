package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface CompositionEventHandler
{
  void onCompositionEvent( CompositionEvent event );
}
