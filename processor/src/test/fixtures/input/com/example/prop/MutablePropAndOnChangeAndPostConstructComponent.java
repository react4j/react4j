package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MutablePropAndOnChangeAndPostConstructComponent
  extends Component
{
  @Prop
  abstract String getMyProp();

  @PostConstruct
  void postConstruct()
  {
  }

  @OnPropChange
  void onMyPropChange( String myProp )
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
