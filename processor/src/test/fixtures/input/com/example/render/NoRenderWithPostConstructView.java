package com.example.render;

import arez.annotations.PostConstruct;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPostConstructView
{
  @PostConstruct
  void postConstruct()
  {
  }
}
