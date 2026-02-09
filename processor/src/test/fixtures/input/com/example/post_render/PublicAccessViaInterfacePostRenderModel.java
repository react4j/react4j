package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class PublicAccessViaInterfacePostRenderModel
  implements PostRenderInterface
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
