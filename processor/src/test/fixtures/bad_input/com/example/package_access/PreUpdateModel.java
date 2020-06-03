package com.example.package_access;

import com.example.package_access.other.BasePreUpdateModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
public abstract class PreUpdateModel
  extends BasePreUpdateModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
