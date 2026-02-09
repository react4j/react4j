package com.example.publish.other;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import javax.annotation.Nullable;

public abstract class BaseProtectedAccessPublishModel
{
  @Publish
  protected String getMyContextVar()
  {
    return "";
  }

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
