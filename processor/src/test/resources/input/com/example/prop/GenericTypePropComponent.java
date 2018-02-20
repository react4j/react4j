package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class GenericTypePropComponent<T>
  extends Component
{
  @Prop
  protected abstract T getValue();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
