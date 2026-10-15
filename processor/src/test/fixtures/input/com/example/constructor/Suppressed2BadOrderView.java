package com.example.constructor;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@SuppressReact4jWarnings( "React4j:ConstructorParameterOrder" )
@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
