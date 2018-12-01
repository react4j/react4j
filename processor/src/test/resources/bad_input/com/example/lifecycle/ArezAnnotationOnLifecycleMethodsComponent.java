package com.example.lifecycle;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezAnnotationOnLifecycleMethodsComponent
  extends ReactArezComponent
{
  @Observe
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
