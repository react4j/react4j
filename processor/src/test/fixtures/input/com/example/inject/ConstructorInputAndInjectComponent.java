package com.example.inject;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ConstructorInputAndInjectComponent
{
  ConstructorInputAndInjectComponent( final String someParam,
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
