package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MultiContextPropModel
  extends Component
{
  @Prop( source = Prop.Source.CONTEXT )
  abstract String getStringContextValue();

  @Prop( source = Prop.Source.CONTEXT )
  abstract int getIntContextValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
