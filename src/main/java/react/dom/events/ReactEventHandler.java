package react.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface ReactEventHandler
{
  void onReactEvent( SyntheticEvent event );
}
