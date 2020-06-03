package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.PreUpdate;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class OnPropChangeAndPreUpdateModel
{
  @Prop
  abstract int getMyProp();

  @OnPropChange( phase = OnPropChange.Phase.PRE )
  void onMyPropChange( int myProp )
  {
  }

  @PreUpdate
  void preUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
