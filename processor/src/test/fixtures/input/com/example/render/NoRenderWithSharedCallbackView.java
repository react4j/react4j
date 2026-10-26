package com.example.render;

import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
@org.jspecify.annotations.NullMarked
abstract class NoRenderWithSharedCallbackView
{
  @PostMount
  @PostUpdate
  void afterCommit()
  {
  }
}
