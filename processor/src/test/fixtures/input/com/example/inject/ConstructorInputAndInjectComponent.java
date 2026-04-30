package com.example.inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;

@View
abstract class ConstructorInputAndInjectComponent
{
  ConstructorInputAndInjectComponent( @Nonnull final String someParam,
                                      @Nullable @Input final String myProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
