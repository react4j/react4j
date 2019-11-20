package com.example.optional_props;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ExplicitOptional
  extends Component
{
  @Prop( require = Feature.DISABLE )
  abstract String getMyOptionalProp();

  @Prop
  abstract String getMyRequiredProp();

  @Prop( require = Feature.DISABLE )
  abstract String getMyOtherOptionalProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
