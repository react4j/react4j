package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class DisposableOptionalProp
  extends ReactArezComponent
{
  @Prop( disposable = Feature.ENABLE, require = Feature.DISABLE )
  protected abstract Object getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
