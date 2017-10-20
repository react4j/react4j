package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface ReactEventHandler
{
  void onReactEvent( SyntheticEvent event );
}
