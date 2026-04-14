package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View( type = View.Type.MAYBE_TRACKING )
abstract class ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp
{
  @ActAsComponent
  interface ActAsComponentComponent<T>
  {
  }

  @Nullable
  @Input( immutable = true )
  abstract ActAsComponentComponent<?> model();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
