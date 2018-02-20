package com.example.render;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BaseRenderComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
