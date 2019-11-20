package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.PostUpdate;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class OnPropChangeAndPreUpdateModel
  extends Component
{
  @Prop
  abstract int getMyProp();

  @OnPropChange( phase = OnPropChange.Phase.PRE )
  void onMyPropChange( int myProp )
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
