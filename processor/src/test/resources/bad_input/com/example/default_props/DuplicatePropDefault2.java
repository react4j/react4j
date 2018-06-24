package com.example.default_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropDefault;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicatePropDefault2
  extends Component
{
  @PropDefault( name = "myProp" )
  public static String getMyPropDefault1()
  {
    return "Foo";
  }

  @PropDefault( name = "myProp" )
  public static String getMyPropDefault2()
  {
    return "Foo";
  }

  @Prop
  protected abstract String getMyProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
