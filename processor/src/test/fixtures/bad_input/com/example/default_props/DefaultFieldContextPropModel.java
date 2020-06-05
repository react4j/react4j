package com.example.default_props;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DefaultFieldContextPropModel
{
  @InputDefault
  static final String DEFAULT_MY_PROP = "Foo";

  @Input( source = Input.Source.CONTEXT )
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
