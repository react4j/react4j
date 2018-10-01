package com.example.prop;

import arez.annotations.Computed;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class NotObservableAsNotUpdateOnChangeProp
  extends ReactArezComponent
{
  @Prop( shouldUpdateOnChange = Feature.DISABLE )
  protected abstract Object getValue();

  @Computed
  int someValue()
  {
    return 0;
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
