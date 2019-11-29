package com.example.prop;

import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class GenericTypeMultiPropModel<T>
  extends Component
{
  @Prop
  abstract T getValue();

  @Prop
  abstract String getValue2();

  @Prop
  @Nullable
  abstract String getValue3();

  @Prop
  @Nullable
  abstract String getValue4();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
