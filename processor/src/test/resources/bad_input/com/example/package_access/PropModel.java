package com.example.package_access;

import com.example.package_access.other.BasePropModel;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.ReactNode;

@ReactComponent
public abstract class PropModel
  extends BasePropModel
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
