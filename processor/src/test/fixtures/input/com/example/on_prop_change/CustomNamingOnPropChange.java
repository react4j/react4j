package com.example.on_prop_change;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.OnPropChange;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CustomNamingOnPropChange
  extends Component
{
  @OnPropChange
  void onPropChange( boolean lastMyProp1 )
  {
  }

  @OnPropChange
  void onPropChange( String prevMyProp2 )
  {
  }

  @OnPropChange
  void onPropChange( int myProp3 )
  {
  }

  @Prop
  protected abstract boolean getMyProp1();

  @Prop
  protected abstract String getMyProp2();

  @Prop
  protected abstract int getMyProp3();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
