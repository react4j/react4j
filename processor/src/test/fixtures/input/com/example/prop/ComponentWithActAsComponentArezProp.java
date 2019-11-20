package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ComponentWithActAsComponentArezProp
  extends Component
{
  @ActAsComponent
  interface Model
  {
  }

  @Prop
  abstract Model getModel();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
