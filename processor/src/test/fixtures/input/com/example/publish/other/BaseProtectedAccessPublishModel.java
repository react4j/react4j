package com.example.publish.other;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPublishModel
{
  @Publish
  protected String getMyContextVar()
  {
    return "";
  }

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
