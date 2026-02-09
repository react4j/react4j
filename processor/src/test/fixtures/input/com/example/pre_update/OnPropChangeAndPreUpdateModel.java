package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class OnPropChangeAndPreUpdateModel
{
  @Input
  abstract int getMyProp();

  @OnInputChange( phase = OnInputChange.Phase.PRE )
  void onMyPropChange( int myProp )
  {
  }

  @PreUpdate
  void preUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
