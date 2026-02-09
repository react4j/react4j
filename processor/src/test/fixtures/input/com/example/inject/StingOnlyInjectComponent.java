package com.example.inject;

import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class StingOnlyInjectComponent
{
  StingOnlyInjectComponent( @Nonnull String someParam )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
