package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NonJavaBeanPropComponent
  extends Component
{
  @Prop
  protected abstract String window();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
