package com.example.render;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class BaseRenderComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
