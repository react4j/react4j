package com.example.constructor;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BadOrderView
{
  BadOrderView( @Input final int inputValue,
                final String service,
                @Input( fromTreeContext = true ) final boolean treeValue )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
