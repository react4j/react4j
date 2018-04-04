package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class GenericTypeMultiPropComponent<T>
  extends Component
{
  @Prop
  protected abstract T getValue();

  @Prop
  protected abstract String getValue2();

  @Prop
  @Nullable
  protected abstract String getValue3();

  @Prop
  @Nullable
  protected abstract String getValue4();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
