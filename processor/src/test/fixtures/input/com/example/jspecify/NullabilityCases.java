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

  abstract <V extends java.util.List<String>> V boundedStrings();
  abstract <V extends java.util.List<@Nullable String>> V boundedNullableStrings();
  abstract <V extends java.util.List<@Nullable String> & Serializable> V boundedIntersection();
  abstract Object objectValue();
  abstract @Nullable Object nullableObjectValue();
  abstract java.util.List<String> strings();
  abstract java.util.List<@Nullable String> nullableStrings();
  abstract java.util.ArrayList<@Nullable String> nullableArrayList();
  abstract java.util.List<java.util.List<String>> nestedStrings();
  abstract java.util.List<java.util.List<@Nullable String>> nestedNullableStrings();
  abstract String[] stringArray();
  abstract @Nullable String[] nullableElementArray();
  abstract String @Nullable [] nullableArray();
  abstract java.util.List<? extends String> upperStrings();
  abstract java.util.List<? extends @Nullable String> upperNullableStrings();
  abstract java.util.List<java.util.List<? extends String>> nestedUpperStrings();
  abstract java.util.List<java.util.List<? extends @Nullable String>> nestedUpperNullableStrings();
  abstract java.util.List<? super String> lowerStrings();
  abstract java.util.List<? super @Nullable String> lowerNullableStrings();
  abstract java.util.List<?> unbounded();
  abstract java.util.List<? extends @Nullable Object> upperObject();
  abstract Box<String>.Item<@Nullable String> ownerStrings();
  abstract Box<@Nullable String>.Item<@Nullable String> nullableOwnerStrings();
  abstract Box<String>.Strings ownerSubclass();
  abstract Box<@Nullable String>.Strings nullableOwnerSubclass();
  static class Box<A extends @Nullable Object> {
    class Item<B extends @Nullable Object> {}
    @SuppressWarnings("serial") class Strings extends java.util.ArrayList<A> {}
  }

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
