package com.example.arez;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class ComponentWithChildProp
  extends Component
{
  @Prop
  abstract ReactNode getChild();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
