package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class MultiScheduleRenderComponent
{
  @ScheduleRender
  abstract void myScheduleRender1();

  @ScheduleRender
  abstract void myScheduleRender2();

  @ScheduleRender( skipShouldComponentUpdate = false )
  abstract void myScheduleRender3();

  @ScheduleRender( skipShouldComponentUpdate = false )
  abstract void myScheduleRender4();

  @Render
  ReactNode render()
  {
    return null;
  }
}
