package com.example.prop;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ObservableViaObservedProp
{
  @Prop
  abstract Object getValue();

  @Observe
  void someValue()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
