package com.example.inheritance.simple;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class LeafView
  extends InnerView
  implements LeafInterface
{
  @Input
  abstract boolean field1InLeaf();

  @Input
  abstract boolean field2InLeaf();

  @Render
  ReactNode render()
  {
    return null;
  }
}
