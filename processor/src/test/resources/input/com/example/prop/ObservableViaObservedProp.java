package com.example.prop;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ObservableViaObservedProp
  extends ReactArezComponent
{
  @Prop
  protected abstract Object getValue();

  @Observe
  void someValue()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
