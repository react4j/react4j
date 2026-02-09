package com.example.inheritance.simple;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class LeafView
  extends InnerView
  implements LeafInterface
{
  @Input
  abstract boolean field1InLeaf();

  @Input
  abstract boolean field2InLeaf();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
