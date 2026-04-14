package com.example.prop;

import arez.annotations.ActAsComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ImmutableDisposableActAsComponentTrackingProp
{
  @ActAsComponent
  interface ActAsComponentComponent
  {
  }

  ImmutableDisposableActAsComponentTrackingProp( @Nullable @Input final ActAsComponentComponent model )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
