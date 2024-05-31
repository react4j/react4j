package com.example.inheritance.simple;

import react4j.annotations.Input;

abstract class RootView
  implements RootInterface
{
  @Input
  abstract boolean field1InRoot();

  @Input
  abstract boolean field2InRoot();
}
