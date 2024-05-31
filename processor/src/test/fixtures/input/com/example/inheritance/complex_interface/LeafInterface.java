package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

interface LeafInterface
  extends InnerInterface
{
  @Input
  boolean zzField1InLeafInterface();

  @Input
  boolean aaField2InLeafInterface();
}
