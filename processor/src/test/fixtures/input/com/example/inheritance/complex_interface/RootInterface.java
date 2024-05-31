package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

interface RootInterface
{
  @Input(immutable = true, source = Input.Source.CONTEXT)
  boolean zzField1InRootInterface();

  @Input
  boolean aaField2InRootInterface();
}
