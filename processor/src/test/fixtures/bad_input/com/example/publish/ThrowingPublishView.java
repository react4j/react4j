package com.example.publish;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowingPublishView
{
  @Publish
  String getMyContextVar()
    throws IOException
  {
    return "";
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
