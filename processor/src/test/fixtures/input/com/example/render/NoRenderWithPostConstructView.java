package com.example.render;

import arez.annotations.PostConstruct;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
@org.jspecify.annotations.NullMarked
abstract class NoRenderWithPostConstructView
{
  @PostConstruct
  void postConstruct()
  {
  }
}
