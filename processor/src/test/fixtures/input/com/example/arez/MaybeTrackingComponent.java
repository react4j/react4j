package com.example.arez;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.MAYBE_TRACKING )
abstract class MaybeTrackingComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
