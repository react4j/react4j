package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class OnPropChangeAndPostUpdateModel
{
  @Input
  abstract int getMyProp();

  @OnInputChange( phase = OnInputChange.Phase.PRE )
  void onMyPropChange( int myProp )
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
