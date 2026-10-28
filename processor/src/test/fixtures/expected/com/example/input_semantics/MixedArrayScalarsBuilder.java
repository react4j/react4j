package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.ArrayKeyStrategy;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MixedArrayScalarsBuilder {
  private MixedArrayScalarsBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 values(final String... values) {
    return newBuilder().values( values );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 values(String... values);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    Step3 count(int count);
  }

  interface Step3 {
    @Contract(
        pure = true
    )
    Step4 letter(char letter);
  }

  interface Step4 {
    @Contract(
        pure = true
    )
    Step5 id(long id);
  }

  interface Step5 {
    @Contract(
        pure = true
    )
    Step6 rate(float rate);
  }

  interface Step6 {
    @Contract(
        pure = true
    )
    Step7 flag(boolean flag);
  }

  interface Step7 {
    @Contract(
        pure = true
    )
    Step8 size(double size);
  }

  interface Step8 {
    @Contract(
        pure = true
    )
    Step9 tiny(byte tiny);
  }

  interface Step9 {
    @Contract(
        pure = true
    )
    Step10 small(short small);
  }

  interface Step10 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode wrapped(@Nullable Long wrapped);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1,
      Step2,
      Step3,
      Step4,
      Step5,
      Step6,
      Step7,
      Step8,
      Step9,
      Step10 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MixedArrayScalarsView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2 values(final String... values) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.values, values );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step3 count(final int count) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.count, count );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step4 letter(final char letter) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.letter, letter );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step5 id(final long id) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.id, id );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step6 rate(final float rate) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.rate, rate );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step7 flag(final boolean flag) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.flag, flag );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step8 size(final double size) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.size, size );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step9 tiny(final byte tiny) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.tiny, tiny );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step10 small(final short small) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.small, small );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode wrapped(final @Nullable Long wrapped) {
      _element.input( React4j_MixedArrayScalarsView.Inputs.wrapped, wrapped );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<String[]>uncheckedCast(inputs.get(React4j_MixedArrayScalarsView.Inputs.values))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.count)).asInt())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.letter)).asChar())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.id)).asLong())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.rate)).asFloat())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.flag)).asBoolean())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.size)).asDouble())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.tiny)).asByte())) + InputKeys.frame(String.valueOf(Objects.requireNonNull(inputs.getAsAny(React4j_MixedArrayScalarsView.Inputs.small)).asShort())) + InputKeys.frame(String.valueOf(Js.<@Nullable Long>uncheckedCast(inputs.get(React4j_MixedArrayScalarsView.Inputs.wrapped)))) + (React.enableViewNames() ? "_MixedArrayScalarsView_36952042" : MixedArrayScalarsView.class.getName()));
      return _element;
    }
  }
}
