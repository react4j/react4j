package react4j.dom.events;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface FormEventHandler
{
  void onFormEvent( FormEvent event );
}
