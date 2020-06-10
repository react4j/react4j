package com.example.publish;

import react4j.ReactNode;
import react4j.annotations.Publish;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class TypeParameterized2PublishView<T>
{
  @Publish
  T getMyContextVar()
  {
    return null;
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
