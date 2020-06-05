package com.example.input;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ArezComponentInputView
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Input
  abstract String getValue();

  @Input
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
