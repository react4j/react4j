package com.example.schedule_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.ScheduleRender;

@ReactComponent
abstract class PrivateScheduleRenderComponent
  extends Component
{
  @ScheduleRender
  private void myScheduleRender()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
