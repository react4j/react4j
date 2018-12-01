package com.example.post_render;

import java.io.IOException;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ThrowsModel
  extends Component
{
  @PostRender
  void postRender()
    throws IOException
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
