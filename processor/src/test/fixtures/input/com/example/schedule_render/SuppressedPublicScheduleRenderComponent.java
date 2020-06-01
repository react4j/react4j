package com.example.schedule_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class SuppressedPublicScheduleRenderComponent
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @ScheduleRender
  public abstract void myScheduleRender();

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
