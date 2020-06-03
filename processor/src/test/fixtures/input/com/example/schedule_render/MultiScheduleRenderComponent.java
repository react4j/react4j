package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;

@View
abstract class MultiScheduleRenderComponent
{
  @ScheduleRender
  abstract void myScheduleRender1();

  @ScheduleRender
  abstract void myScheduleRender2();

  @ScheduleRender( skipShouldViewUpdate = false )
  abstract void myScheduleRender3();

  @ScheduleRender( skipShouldViewUpdate = false )
  abstract void myScheduleRender4();

  @Render
  ReactNode render()
  {
    return null;
  }
}
