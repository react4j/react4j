package com.example.prop_validate;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( String prop )
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
