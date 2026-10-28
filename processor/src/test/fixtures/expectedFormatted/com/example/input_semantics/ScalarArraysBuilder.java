package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.ArrayKeyStrategy;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ScalarArraysBuilder {
  private ScalarArraysBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 strings(final String... strings) {
    return newBuilder().strings(strings);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 strings(String... strings);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 booleans(boolean... booleans);
  }

  interface Step3 {
    @Contract(pure = true)
    Step4 bytes(byte... bytes);
  }

  interface Step4 {
    @Contract(pure = true)
    Step5 shorts(short... shorts);
  }

  interface Step5 {
    @Contract(pure = true)
    Step6 ints(int... ints);
  }

  interface Step6 {
    @Contract(pure = true)
    Step7 longs(long... longs);
  }

  interface Step7 {
    @Contract(pure = true)
    Step8 chars(char... chars);
  }

  interface Step8 {
    @Contract(pure = true)
    Step9 floats(float... floats);
  }

  interface Step9 {
    @Contract(pure = true)
    ReactNode doubles(double... doubles);
  }

  private static class Builder
      implements Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ScalarArraysView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 strings(final String... strings) {
      _element.input(React4j_ScalarArraysView.Inputs.strings, strings);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 booleans(final boolean... booleans) {
      _element.input(React4j_ScalarArraysView.Inputs.booleans, booleans);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 bytes(final byte... bytes) {
      _element.input(React4j_ScalarArraysView.Inputs.bytes, bytes);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step5 shorts(final short... shorts) {
      _element.input(React4j_ScalarArraysView.Inputs.shorts, shorts);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step6 ints(final int... ints) {
      _element.input(React4j_ScalarArraysView.Inputs.ints, ints);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step7 longs(final long... longs) {
      _element.input(React4j_ScalarArraysView.Inputs.longs, longs);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step8 chars(final char... chars) {
      _element.input(React4j_ScalarArraysView.Inputs.chars, chars);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 floats(final float... floats) {
      _element.input(React4j_ScalarArraysView.Inputs.floats, floats);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode doubles(final double... doubles) {
      _element.input(React4j_ScalarArraysView.Inputs.doubles, doubles);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<String[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.strings))),
              1,
              ArrayKeyStrategy.STRING))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(Js.<boolean[]>uncheckedCast(
                  inputs.get(React4j_ScalarArraysView.Inputs.booleans))),
              1,
              ArrayKeyStrategy.BOOLEAN))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<byte[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.bytes))),
              1,
              ArrayKeyStrategy.BYTE))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<short[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.shorts))),
              1,
              ArrayKeyStrategy.SHORT))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<int[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.ints))),
              1,
              ArrayKeyStrategy.INT))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<long[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.longs))),
              1,
              ArrayKeyStrategy.LONG))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<char[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.chars))),
              1,
              ArrayKeyStrategy.CHAR))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<float[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.floats))),
              1,
              ArrayKeyStrategy.FLOAT))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(
                  Js.<double[]>uncheckedCast(inputs.get(React4j_ScalarArraysView.Inputs.doubles))),
              1,
              ArrayKeyStrategy.DOUBLE))
          + (React.enableViewNames()
              ? "_ScalarArraysView_b69331f7"
              : ScalarArraysView.class.getName()));
      return _element;
    }
  }
}
