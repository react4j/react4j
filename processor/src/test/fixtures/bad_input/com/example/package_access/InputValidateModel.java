package com.example.package_access;

import com.example.package_access.other.BaseInputValidateModel;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
public abstract class InputValidateModel
  extends BaseInputValidateModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
