package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class PublicAccessViaInterfacePostRenderModel
  implements PostRenderInterface
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
