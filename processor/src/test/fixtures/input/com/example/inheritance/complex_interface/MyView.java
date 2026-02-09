package com.example.inheritance.complex_interface;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class MyView
  implements LeafInterface
{
  @Input( immutable = true, source = Input.Source.CONTEXT )
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
