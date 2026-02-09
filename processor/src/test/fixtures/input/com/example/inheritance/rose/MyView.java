package com.example.inheritance.rose;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

// An example of inheritance that appears all over Rose
// That was being broken so added in here to avoid in future
@View
abstract class MyView
  implements MyConsumerView
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
