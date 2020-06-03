package com.example.optional_props;

import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ExplicitOptional
{
  @Prop( require = Feature.DISABLE )
  abstract String getMyOptionalProp();

  @Prop
  abstract String getMyRequiredProp();

  @Prop( require = Feature.DISABLE )
  abstract String getMyOtherOptionalProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
