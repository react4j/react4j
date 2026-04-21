package com.example.constructor;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@SuppressReact4jWarnings( "React4j:ConstructorParameterOrder" )
@View
abstract class Suppressed2BadOrderView
{
  Suppressed2BadOrderView( @Input final int inputValue,
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
