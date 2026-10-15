package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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
