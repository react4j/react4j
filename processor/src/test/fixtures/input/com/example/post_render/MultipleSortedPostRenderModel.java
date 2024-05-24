package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.SortOrder;
import react4j.annotations.View;

@View
abstract class MultipleSortedPostRenderModel
{
  @PostRender( sortOrder = SortOrder.APPLICATION + 50 )
  void postRender1()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 10 )
  void postRender2()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 20 )
  void postRender3()
  {
  }

  @PostRender( sortOrder = SortOrder.APPLICATION + 5 )
  void postRender4()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
