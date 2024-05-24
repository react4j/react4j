package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.SortOrder;
import react4j.annotations.View;

@View
abstract class MultipleSortedPreRenderModel
{
  @PreRender( sortOrder = SortOrder.APPLICATION + 50 )
  void preRender4()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 10 )
  void preRender2()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 20 )
  void preRender3()
  {
  }

  @PreRender( sortOrder = SortOrder.APPLICATION + 5 )
  void preRender1()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
