package com.example.on_prop_changed;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChanged;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsOnPropChanged
  extends Component
{
  @OnPropChanged
  void onMyPropChanged( String oldValue )
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
