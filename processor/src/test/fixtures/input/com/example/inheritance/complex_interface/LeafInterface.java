package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

@org.jspecify.annotations.NullMarked
interface LeafInterface
  extends InnerInterface
{
  @Input( fromTreeContext = true )
  boolean zzField1InLeafInterface();

  @Input
  boolean aaField2InLeafInterface();
}
