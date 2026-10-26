package com.example.lifecycle;

import org.jspecify.annotations.NullMarked;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.PreUpdate;

@NullMarked
public final class CallbackAncestors {
  private CallbackAncestors() {}

  public abstract static class Parent {
    protected void nameTrap(int ignored) {}

    @PostMount @PostUpdate @PreUpdate
    protected void first() {}

    @PostMount @PostUpdate @PreUpdate
    protected void second() {}

    @PostMount @PostUpdate @PreUpdate
    protected void disabled() {}

    @PostMount
    protected void phaseAdded() {}

    @PostMount @PostUpdate @PreUpdate
    protected void restored() {}

    protected void dormant() {}

    public void shadowed() {}
  }

  public abstract static class Middle extends Parent {
    @Override
    protected void restored() {}

    @Deprecated
    @PostMount @PostUpdate @PreUpdate
    protected void middle() {}
  }

  public interface Root {
    @PostMount @PostUpdate @PreUpdate
    default void root() {}

    @PostMount @PostUpdate @PreUpdate
    default void shadowed() {}

    @PostMount @PostUpdate @PreUpdate
    void abstractHook();
  }

  public interface Left extends Root {
    @PostMount @PostUpdate @PreUpdate
    default void left() {}
  }

  public interface Right extends Root {
    @PostMount @PostUpdate @PreUpdate
    default void right() {}
  }
}
