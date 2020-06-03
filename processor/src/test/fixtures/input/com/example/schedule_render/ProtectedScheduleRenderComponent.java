package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;

@View
abstract class ProtectedScheduleRenderComponent
{
  @ScheduleRender
  protected abstract void myScheduleRender();

  @Render
  ReactNode render()
  {
    return null;
  }
}
