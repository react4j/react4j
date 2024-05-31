package com.example.inheritance.rose;

import java.util.Date;
import javax.annotation.Nonnull;
import react4j.annotations.PostRender;
import react4j.annotations.PreRender;

public interface MyBaseView
{
  @Nonnull
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
