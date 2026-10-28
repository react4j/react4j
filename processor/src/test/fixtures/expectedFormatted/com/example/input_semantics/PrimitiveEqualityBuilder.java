package com.example.input_semantics;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class PrimitiveEqualityBuilder {
  private PrimitiveEqualityBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 booleanValue(final boolean booleanValue) {
    return newBuilder().booleanValue(booleanValue);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 booleanValue(boolean booleanValue);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 byteValue(byte byteValue);
  }

  interface Step3 {
    @Contract(pure = true)
    Step4 shortValue(short shortValue);
  }

  interface Step4 {
    @Contract(pure = true)
    Step5 intValue(int intValue);
  }

  interface Step5 {
    @Contract(pure = true)
    Step6 longValue(long longValue);
  }

  interface Step6 {
    @Contract(pure = true)
    Step7 charValue(char charValue);
  }

  interface Step7 {
    @Contract(pure = true)
    Step8 floatValue(float floatValue);
  }

  interface Step8 {
    @Contract(pure = true)
    Step9 doubleValue(double doubleValue);
  }

  interface Step9 extends Buildable {
    @Contract(pure = true)
    Step9 booleanBoxed(@Nullable Boolean booleanBoxed);

    @Contract(pure = true)
    Step9 byteBoxed(@Nullable Byte byteBoxed);

    @Contract(pure = true)
    Step9 shortBoxed(@Nullable Short shortBoxed);

    @Contract(pure = true)
    Step9 integerBoxed(@Nullable Integer integerBoxed);

    @Contract(pure = true)
    Step9 longBoxed(@Nullable Long longBoxed);

    @Contract(pure = true)
    Step9 characterBoxed(@Nullable Character characterBoxed);

    @Contract(pure = true)
    Step9 floatBoxed(@Nullable Float floatBoxed);

    @Contract(pure = true)
    Step9 doubleBoxed(@Nullable Double doubleBoxed);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder
      implements Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_PrimitiveEqualityView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 booleanValue(final boolean booleanValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.booleanValue, booleanValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 byteValue(final byte byteValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.byteValue, byteValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 shortValue(final short shortValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.shortValue, shortValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step5 intValue(final int intValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.intValue, intValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step6 longValue(final long longValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.longValue, longValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step7 charValue(final char charValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.charValue, charValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step8 floatValue(final float floatValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.floatValue, floatValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 doubleValue(final double doubleValue) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.doubleValue, doubleValue);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 booleanBoxed(final @Nullable Boolean booleanBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.booleanBoxed, booleanBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 byteBoxed(final @Nullable Byte byteBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.byteBoxed, byteBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 shortBoxed(final @Nullable Short shortBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.shortBoxed, shortBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 integerBoxed(final @Nullable Integer integerBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.integerBoxed, integerBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 longBoxed(final @Nullable Long longBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.longBoxed, longBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 characterBoxed(final @Nullable Character characterBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.characterBoxed, characterBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 floatBoxed(final @Nullable Float floatBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.floatBoxed, floatBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 doubleBoxed(final @Nullable Double doubleBoxed) {
      _element.input(React4j_PrimitiveEqualityView.Inputs.doubleBoxed, doubleBoxed);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
