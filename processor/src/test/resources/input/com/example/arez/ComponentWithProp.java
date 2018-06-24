package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ComponentWithProp
  extends ReactArezComponent
{
  @Prop
  protected abstract String getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
