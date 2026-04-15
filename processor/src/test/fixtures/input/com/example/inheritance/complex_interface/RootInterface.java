package com.example.inheritance.complex_interface;

import react4j.annotations.Input;

interface RootInterface
{
  @Input( source = Input.Source.CONTEXT )
  boolean zzField1InRootInterface();

  @Input
  boolean aaField2InRootInterface();
}
