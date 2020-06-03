package com.example.package_access;

import com.example.package_access.other.BasePropDefaultModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
public abstract class PropDefaultModel
  extends BasePropDefaultModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
