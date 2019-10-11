package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MutablePropAndPostConstructComponent
  extends Component
{
  @Prop
  protected abstract String getMyProp();

  @PostConstruct
  void postConstruct()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}