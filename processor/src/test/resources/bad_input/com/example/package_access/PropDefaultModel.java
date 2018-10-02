package com.example.package_access;

import com.example.package_access.other.BasePropDefaultModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class PropDefaultModel
  extends BasePropDefaultModel
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
