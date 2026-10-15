package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
