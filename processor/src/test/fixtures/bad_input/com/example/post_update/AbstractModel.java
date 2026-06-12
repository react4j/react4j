package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class AbstractModel
{
  @PostUpdate
  abstract void postUpdate();

  @Render
  ReactNode render()
  {
    return null;
  }
}
