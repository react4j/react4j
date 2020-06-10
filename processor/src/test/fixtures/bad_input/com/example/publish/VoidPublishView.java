package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class VoidPublishView
{
  @Publish
  void getMyContextVar()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
