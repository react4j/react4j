package com.example.inject;

import javax.annotation.Nullable;
import javax.inject.Inject;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
abstract class ArezReactComponent
  extends ReactArezComponent
{
  @Inject
  String someParam;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
