package com.example.package_access;

import com.example.package_access.other.BaseInputModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
public abstract class InputModel
  extends BaseInputModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
