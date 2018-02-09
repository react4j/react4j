package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BadNameProp
  extends Component<BaseState>
{
  @Prop( name = "-key" )
  protected abstract String getMyKey();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
