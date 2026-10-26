package com.example.lifecycle;

import react4j.JsError;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.OnError;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
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

  @PostMount
  @PostUpdate
  void afterCommit()
  {
  }

  @PostUpdate
  void postUpdate()
  {
  }

  @PostMount
  void postMount()
  {
  }

  @OnError
  void onError( final JsError error, final ReactErrorInfo info )
  {
  }
}
