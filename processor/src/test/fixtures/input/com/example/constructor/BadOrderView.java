package com.example.constructor;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
