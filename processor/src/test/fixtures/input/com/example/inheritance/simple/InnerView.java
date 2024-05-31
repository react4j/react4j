package com.example.inheritance.simple;

import react4j.annotations.Input;

abstract class InnerView
  extends RootView
  implements InnerInterface
{
  @Input
  abstract boolean field1InInner();

  @Input
  abstract boolean field2InInner();
}
