package com.example.lifecycle;

import org.jspecify.annotations.NullMarked;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.NO_RENDER)
abstract class OrderedCallbacksView extends CallbackAncestors.Middle
    implements CallbackAncestors.Left, CallbackAncestors.Right {
  void localTrap(int ignored) {}

  @Override @PostMount @PostUpdate @PreUpdate
  protected void second() {}

  @PostMount @PostUpdate @PreUpdate
  void childFirst() {}

  @Override @PostMount @PostUpdate @PreUpdate
  protected void first() {}

  @Override
  protected void disabled() {}

  @Override @PostMount @PostUpdate @PreUpdate
  protected void restored() {}

  @Override @PostMount @PostUpdate @PreUpdate
  protected void phaseAdded() {}

  @Override @PostMount @PostUpdate @PreUpdate
  protected void dormant() {}

  @PostMount @PostUpdate @PreUpdate
  void nameTrap() {}

  @PostMount @PostUpdate @PreUpdate
  void localTrap() {}

  @Override
  public void abstractHook() {}
}
