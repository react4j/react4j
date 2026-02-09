package com.example.prop;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ObservableViaObservedProp
{
  @Nullable
  @Input
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
