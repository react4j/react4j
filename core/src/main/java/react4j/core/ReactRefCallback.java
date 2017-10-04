package react4j.core;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface ReactRefCallback
{
  void passRef( Object refElement );
}
