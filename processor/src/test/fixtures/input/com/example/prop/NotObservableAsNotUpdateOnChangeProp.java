package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class NotObservableAsNotUpdateOnChangeProp
{
  @Prop( shouldUpdateOnChange = Feature.DISABLE )
  abstract Object getValue();

  @Memoize
  int someValue()
  {
    return 0;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
