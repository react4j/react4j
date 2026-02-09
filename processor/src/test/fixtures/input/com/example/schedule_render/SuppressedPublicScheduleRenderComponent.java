package com.example.schedule_render;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class SuppressedPublicScheduleRenderComponent
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @ScheduleRender
  public abstract void myScheduleRender();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
