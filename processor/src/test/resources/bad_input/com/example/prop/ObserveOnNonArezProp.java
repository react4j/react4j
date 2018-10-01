package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ObserveOnNonArezProp
  extends Component
{
  @Prop( observable = Feature.ENABLE )
  protected abstract Object getMyKey();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
