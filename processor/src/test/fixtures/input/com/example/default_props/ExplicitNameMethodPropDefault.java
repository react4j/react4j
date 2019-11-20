package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ExplicitNameMethodPropDefault
  extends Component
{
  @PropDefault( name = "myProp" )
  static String zoooooooooooooom()
  {
    return "Foo";
  }

  @Prop
  abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
