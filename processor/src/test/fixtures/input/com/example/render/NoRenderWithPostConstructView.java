package com.example.render;

import arez.annotations.PostConstruct;
import react4j.annotations.View;

@View( requireRender = false )
abstract class NoRenderWithPostConstructView
{
  @PostConstruct
  void postConstruct()
  {
  }
}
