package com.example.prop;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ImmutableDisposableArezComponentTrackingProp
{
  @ArezComponent( allowEmpty = true, observable = Feature.ENABLE )
  static abstract class Model
  {
  }

  ImmutableDisposableArezComponentTrackingProp( @Nonnull @Input final Model model )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
