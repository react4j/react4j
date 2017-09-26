package com.example.component;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.ReactElement;
import react.core.SideComponent;

@ReactComponent
final class FinalComponent
  extends SideComponent<BaseProps, BaseState>
{
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
