package com.example.inheritance.rose;

import java.util.Date;
import react4j.annotations.PostRender;
import react4j.annotations.PreRender;

@org.jspecify.annotations.NullMarked
public interface MyBaseView
{

  Date dateRange();

  @PreRender
  default void preRenderPushingDateRange()
  {
  }

  @PostRender
  default void postRenderPoppingDateRange()
  {
  }
}
