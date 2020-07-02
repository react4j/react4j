package com.example.render;

import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.View;

@View( requireRender = false )
abstract class NoRenderWithPostMountOrUpdateView
{
  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }
}
