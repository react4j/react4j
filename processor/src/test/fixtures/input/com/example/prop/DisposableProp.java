package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class DisposableProp
  extends Component
{
  @Prop( disposable = Feature.ENABLE )
  abstract Object getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
