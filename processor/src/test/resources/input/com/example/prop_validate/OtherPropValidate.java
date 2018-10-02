package com.example.prop_validate;

import java.util.ArrayList;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OtherPropValidate
  extends Component
{
  @PropValidate
  void validateMyProp( ArrayList prop )
  {
  }

  @Prop
  protected abstract ArrayList getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
