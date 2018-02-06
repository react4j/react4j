package com.example.event_handler;

import javax.annotation.Nullable;
import react4j.annotations.EventHandler;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class EventHandlerAndPropComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @EventHandler
  @Prop
  protected abstract int getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
