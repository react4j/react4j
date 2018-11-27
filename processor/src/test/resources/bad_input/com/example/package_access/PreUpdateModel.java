package com.example.package_access;

import com.example.package_access.other.BasePreUpdateModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class PreUpdateModel
  extends BasePreUpdateModel
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
