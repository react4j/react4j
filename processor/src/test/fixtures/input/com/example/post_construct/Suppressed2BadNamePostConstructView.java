package com.example.post_construct;

import arez.annotations.PostConstruct;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@SuppressReact4jWarnings( "React4j:PostConstructName" )
@View
abstract class Suppressed2BadNamePostConstructView
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
