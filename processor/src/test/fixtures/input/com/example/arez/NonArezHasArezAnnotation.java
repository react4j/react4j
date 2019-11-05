package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonArezHasArezAnnotation
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Action
  void handleFoo()
  {
  }
}
