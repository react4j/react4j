package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class MutableDisposableNullableActAsComponentTrackingProp
{
  @ActAsComponent
  interface ActAsComponentComponent
  {
  }

  @Nullable
  @Input
  abstract ActAsComponentComponent model();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
