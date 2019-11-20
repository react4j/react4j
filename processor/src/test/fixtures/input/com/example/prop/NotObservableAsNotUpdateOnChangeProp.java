package com.example.prop;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class NotObservableAsNotUpdateOnChangeProp
  extends Component
{
  @Prop( shouldUpdateOnChange = Feature.DISABLE )
  abstract Object getValue();

  @Memoize
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
