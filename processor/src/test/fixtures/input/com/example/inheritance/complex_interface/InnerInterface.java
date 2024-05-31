package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

interface InnerInterface
  extends RootInterface
{
  @Input
  boolean zzField1InInnerInterface();

  @Input
  boolean aaField2InInnerInterface();
}
