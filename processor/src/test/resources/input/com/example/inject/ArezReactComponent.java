package com.example.inject;

import arez.annotations.PostConstruct;
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

  @PostConstruct
  final void postConstruct2()
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
