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
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NestedArraysBuilder {
  private NestedArraysBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 strings(final String[][]... strings) {
    return newBuilder().strings( strings );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 strings(String[][]... strings);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    Step3 booleans(boolean[]... booleans);
  }

  interface Step3 {
    @Contract(
        pure = true
    )
    Step4 bytes(byte[]... bytes);
  }

  interface Step4 {
    @Contract(
        pure = true
    )
    Step5 shorts(short[]... shorts);
  }

  interface Step5 {
    @Contract(
        pure = true
    )
    Step6 ints(int[]... ints);
  }

  interface Step6 {
    @Contract(
        pure = true
    )
    Step7 longs(long[]... longs);
  }

  interface Step7 {
    @Contract(
        pure = true
    )
    Step8 chars(char[]... chars);
  }

  interface Step8 {
    @Contract(
        pure = true
    )
    Step9 floats(float[]... floats);
  }

  interface Step9 {
    @Contract(
        pure = true
    )
    ReactNode doubles(double[]... doubles);
  }

  private static class ArrayKeys {
    private ArrayKeys() {
    }

    private static String $strings_0(
        @Nullable Object @Nullable [] @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($strings_1(value)));
      }
      return key.toString();
    }

    private static String $strings_1(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($strings_2(value)));
      }
      return key.toString();
    }

    private static String $strings_2(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $booleans_0(boolean @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($booleans_1(value)));
      }
      return key.toString();
    }

    private static String $booleans_1(boolean @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $bytes_0(byte @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($bytes_1(value)));
      }
      return key.toString();
    }

    private static String $bytes_1(byte @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $shorts_0(short @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($shorts_1(value)));
      }
      return key.toString();
    }

    private static String $shorts_1(short @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $ints_0(int @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($ints_1(value)));
      }
      return key.toString();
    }

    private static String $ints_1(int @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $longs_0(long @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($longs_1(value)));
      }
      return key.toString();
    }

    private static String $longs_1(long @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $chars_0(char @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($chars_1(value)));
      }
      return key.toString();
    }

    private static String $chars_1(char @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $floats_0(float @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($floats_1(value)));
      }
      return key.toString();
    }

    private static String $floats_1(float @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(0 == value ? "0" : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $doubles_0(double @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($doubles_1(value)));
      }
      return key.toString();
    }

    private static String $doubles_1(double @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(0 == value ? "0" : String.valueOf(value)));
      }
      return key.toString();
    }
  }

  private static class Builder implements Step1,
      Step2,
      Step3,
      Step4,
      Step5,
      Step6,
      Step7,
      Step8,
      Step9 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NestedArraysView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2 strings(final String[][]... strings) {
      _element.input( React4j_NestedArraysView.Inputs.strings, strings );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step3 booleans(final boolean[]... booleans) {
      _element.input( React4j_NestedArraysView.Inputs.booleans, booleans );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step4 bytes(final byte[]... bytes) {
      _element.input( React4j_NestedArraysView.Inputs.bytes, bytes );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step5 shorts(final short[]... shorts) {
      _element.input( React4j_NestedArraysView.Inputs.shorts, shorts );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step6 ints(final int[]... ints) {
      _element.input( React4j_NestedArraysView.Inputs.ints, ints );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step7 longs(final long[]... longs) {
      _element.input( React4j_NestedArraysView.Inputs.longs, longs );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step8 chars(final char[]... chars) {
      _element.input( React4j_NestedArraysView.Inputs.chars, chars );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step9 floats(final float[]... floats) {
      _element.input( React4j_NestedArraysView.Inputs.floats, floats );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode doubles(final double[]... doubles) {
      _element.input( React4j_NestedArraysView.Inputs.doubles, doubles );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(ArrayKeys.$strings_0(Objects.requireNonNull(Js.<String[][][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.strings))))) + InputKeys.frame(ArrayKeys.$booleans_0(Objects.requireNonNull(Js.<boolean[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.booleans))))) + InputKeys.frame(ArrayKeys.$bytes_0(Objects.requireNonNull(Js.<byte[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.bytes))))) + InputKeys.frame(ArrayKeys.$shorts_0(Objects.requireNonNull(Js.<short[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.shorts))))) + InputKeys.frame(ArrayKeys.$ints_0(Objects.requireNonNull(Js.<int[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.ints))))) + InputKeys.frame(ArrayKeys.$longs_0(Objects.requireNonNull(Js.<long[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.longs))))) + InputKeys.frame(ArrayKeys.$chars_0(Objects.requireNonNull(Js.<char[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.chars))))) + InputKeys.frame(ArrayKeys.$floats_0(Objects.requireNonNull(Js.<float[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.floats))))) + InputKeys.frame(ArrayKeys.$doubles_0(Objects.requireNonNull(Js.<double[][]>uncheckedCast(inputs.get(React4j_NestedArraysView.Inputs.doubles))))) + (React.enableViewNames() ? "_NestedArraysView_025db2b0" : NestedArraysView.class.getName()));
      return _element;
    }
  }
}
