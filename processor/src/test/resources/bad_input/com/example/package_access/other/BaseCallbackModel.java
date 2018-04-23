package com.example.package_access.other;

import react4j.annotations.Callback;
import react4j.core.Component;

public abstract class BaseCallbackModel
  extends Component
{
  @Callback
  void handleFoo()
  {
  }
}
