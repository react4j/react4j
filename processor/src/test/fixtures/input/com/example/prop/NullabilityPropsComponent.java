package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class NullabilityPropsComponent
{
  @Prop
  @Nonnull
  abstract String getMyProp();

  @Prop
  @Nullable
  abstract String getMyProp2();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
