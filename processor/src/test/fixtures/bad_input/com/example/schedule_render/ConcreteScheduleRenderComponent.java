package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class ConcreteScheduleRenderComponent
{
  @ScheduleRender
  void myScheduleRender()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
