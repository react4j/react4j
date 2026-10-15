package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

@org.jspecify.annotations.NullMarked
interface RootInterface
{
  @Input( fromTreeContext = true )
  boolean zzField1InRootInterface();

  @Input
  boolean aaField2InRootInterface();
}
