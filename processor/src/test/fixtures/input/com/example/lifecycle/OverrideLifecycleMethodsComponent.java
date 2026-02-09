package com.example.lifecycle;

import akasha.core.JsError;
import javax.annotation.Nonnull;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.PostMount;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class OverrideLifecycleMethodsComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @PreUpdate
  void preUpdate()
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }

  @PostMountOrUpdate
  void postMountOrUpdate()
  {
  }

  @PostMount
  void postMount()
  {
  }

  @OnError
  void onError( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }
}
