package com.example.package_access.other;

import react4j.Component;
import react4j.annotations.Callback;

public abstract class BaseCallbackModel
  extends Component
{
  @Callback
  void handleFoo()
  {
  }
}
