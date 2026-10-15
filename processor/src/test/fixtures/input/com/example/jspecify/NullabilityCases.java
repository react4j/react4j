package com.example.jspecify;

import java.io.Serializable;
import org.jspecify.annotations.*;

@NullMarked
abstract class NullabilityCases<T extends @Nullable Object,
                                N extends @NonNull Number & Serializable,
                                P extends @Nullable Number & Serializable>
{
  abstract T parametric();
  @javax.annotation.Nullable abstract String legacyNullable();
  @org.jetbrains.annotations.Nullable abstract String legacyJetbrainsNullable();
  abstract @Nullable T nullable();
  abstract @NonNull T nonnull();
  abstract N intersection();
  abstract P markedIntersection();

  @NullUnmarked
  abstract static class Unmarked<U extends @Nullable Number & @Nullable Serializable>
  {
    abstract U intersection();
    abstract String unspecified();
    @javax.annotation.Nonnull abstract String legacyNonnull();
    @org.jetbrains.annotations.NotNull abstract String legacyJetbrainsNonnull();
  }

  abstract static class Base<V extends @Nullable Object>
  {
    abstract V value();
  }

  abstract static class NonNullChild extends Base<@NonNull String> {}
  abstract static class NullableChild extends Base<@Nullable String> {}
  abstract static class Middle<V extends @Nullable Object> extends Base<V> {}
  abstract static class Concrete extends Middle<@NonNull String> {}
  abstract static class Parametric<V extends @Nullable Object> extends Base<V> {}
  abstract static class Bounded<V> extends Base<V> {}

  @NullUnmarked
  abstract static class UnmarkedChild extends Base<String> {}
}
