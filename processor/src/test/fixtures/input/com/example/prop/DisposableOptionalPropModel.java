package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class DisposableOptionalPropModel
{
  @Prop( disposable = Feature.ENABLE, require = Feature.DISABLE )
  abstract Object getValue();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
