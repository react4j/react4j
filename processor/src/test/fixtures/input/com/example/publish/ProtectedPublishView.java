package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class ProtectedPublishView
{
  @Publish
  protected String getMyContextVar()
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
