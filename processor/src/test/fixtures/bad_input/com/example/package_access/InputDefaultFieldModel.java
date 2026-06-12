package com.example.package_access;

import com.example.package_access.other.BaseInputDefaultFieldModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
public abstract class InputDefaultFieldModel
  extends BaseInputDefaultFieldModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
