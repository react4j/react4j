package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class PropAndEventHandlerComponent
  extends Component<BaseState>
{
  @Prop
  @EventHandler
  protected abstract int getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
