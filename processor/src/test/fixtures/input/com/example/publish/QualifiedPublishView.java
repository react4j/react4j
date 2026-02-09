package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class QualifiedPublishView
{
  @Publish( qualifier = "X" )
  String getMyContextVar()
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
