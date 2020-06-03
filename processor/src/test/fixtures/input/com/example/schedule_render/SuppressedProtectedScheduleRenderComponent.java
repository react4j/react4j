package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class SuppressedProtectedScheduleRenderComponent
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @ScheduleRender
  protected abstract void myScheduleRender();

  @Render
  ReactNode render()
  {
    return null;
  }
}
