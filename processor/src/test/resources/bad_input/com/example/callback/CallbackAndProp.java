package com.example.callback;

import javax.annotation.Nullable;
import react4j.annotations.Callback;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class CallbackAndProp
  extends Component<BaseState>
{
  @Callback
  @Prop
  protected abstract int getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
