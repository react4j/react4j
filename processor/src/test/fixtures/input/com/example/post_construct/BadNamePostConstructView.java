package com.example.post_construct;

import arez.annotations.PostConstruct;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class BadNamePostConstructView
{
  @PostConstruct
  void initialize()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
