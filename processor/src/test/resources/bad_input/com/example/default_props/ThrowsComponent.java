package com.example.default_props;

import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class ThrowsComponent
  extends Component<BaseState, BaseContext>
{
  @Nonnull
  static JsPropertyMap<Object> getInitialProps()
    throws Exception
  {
    return JsPropertyMap.of();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
