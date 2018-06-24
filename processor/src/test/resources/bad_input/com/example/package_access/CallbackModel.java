package com.example.package_access;

import com.example.package_access.other.BaseCallbackModel;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class CallbackModel
  extends BaseCallbackModel
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
