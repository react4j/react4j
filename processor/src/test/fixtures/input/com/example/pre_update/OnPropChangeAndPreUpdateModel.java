package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.PreUpdate;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OnPropChangeAndPreUpdateModel
  extends Component
{
  @Prop
  protected abstract int getMyProp();

  @OnPropChange( phase = OnPropChange.Phase.PRE )
  void onMyPropChange( int myProp )
  {
  }

  @PreUpdate
  void preUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
