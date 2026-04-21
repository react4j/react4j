package com.example.post_construct;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1BadNamePostConstructView
{
  @SuppressWarnings( "React4j:PostConstructName" )
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
