package com.example.constructor;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class GoodOrderView
{
  GoodOrderView( final String service,
                 @Input( fromTreeContext = true ) final boolean treeValue,
                 @Input final int inputValue )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
