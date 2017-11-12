package com.example.context;

import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.ReactComponent;
import react4j.core.BaseChildContext;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class BasicProviderComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nullable
  @Override
  protected BaseChildContext getChildContext()
  {
    return super.getChildContext();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
