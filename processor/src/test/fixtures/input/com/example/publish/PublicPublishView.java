package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class PublicPublishView
{
  @Publish
  public String getMyContextVar()
  {
    return "";
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
