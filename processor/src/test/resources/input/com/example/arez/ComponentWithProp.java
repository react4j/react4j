package com.example.arez;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithProp
  extends ReactArezComponent<BaseContext>
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
