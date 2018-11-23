package com.example.on_prop_change;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsOnPropChange
  extends Component
{
  @OnPropChange
  void onMyPropChange( String oldValue )
    throws IOException
  {
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
