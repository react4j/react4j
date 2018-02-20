package com.example.optional_props;

import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class ExplicitOptional
  extends Component
{
  @Prop( require = Feature.DISABLE )
  protected abstract String getMyOptionalProp();

  @Prop
  protected abstract String getMyRequiredProp();

  @Prop( require = Feature.DISABLE )
  protected abstract String getMyOtherOptionalProp();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
