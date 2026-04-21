package com.example.inject;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ConstructorInjectRawTypeComponent
{
  ConstructorInjectRawTypeComponent( @SuppressWarnings( "rawtypes" ) @Nonnull Consumer someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
