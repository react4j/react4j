package com.example.render;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

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
