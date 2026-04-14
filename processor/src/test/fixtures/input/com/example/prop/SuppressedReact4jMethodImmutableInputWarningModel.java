package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@SuppressReact4jWarnings( "React4j:MethodBasedImmutableInput" )
@View
abstract class SuppressedReact4jMethodImmutableInputWarningModel
{
  @Nullable
  @Input( immutable = true )
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
