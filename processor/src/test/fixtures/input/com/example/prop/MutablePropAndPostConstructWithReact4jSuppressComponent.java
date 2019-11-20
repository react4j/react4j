package com.example.prop;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class MutablePropAndPostConstructWithReact4jSuppressComponent
  extends Component
{
  @SuppressReact4jWarnings( "React4j:MutablePropAccessedInPostConstruct" )
  @Prop
  abstract String getMyProp();

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
