package com.example.render;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;

abstract class MyParent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
