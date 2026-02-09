package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class MultiPublishView
{
  @Publish
  String getMyUnqualifiedContextVar()
  {
    return "";
  }

  @Publish( qualifier = "X" )
  String getMyQualifiedContextVar()
  {
    return "";
  }

  @Publish
  int getPrimitiveContextVar()
  {
    return 0;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
