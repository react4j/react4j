package com.example.inject;

import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ConstructorInjectRawTypeComponent
{
  ConstructorInjectRawTypeComponent( @SuppressWarnings( "rawtypes" ) Consumer someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
