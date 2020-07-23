package com.example.render;

import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPostMountOrUpdateView
{
  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }
}
