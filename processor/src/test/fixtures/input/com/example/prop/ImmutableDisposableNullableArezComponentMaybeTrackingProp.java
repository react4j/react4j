package com.example.prop;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View( type = View.Type.MAYBE_TRACKING )
abstract class ImmutableDisposableNullableArezComponentMaybeTrackingProp
{
  @ArezComponent( allowEmpty = true, disposeOnDeactivate = true )
  static abstract class Model
  {
  }

  @Nullable
  @Input( immutable = true )
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
