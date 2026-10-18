package com.example.prop;

import arez.Disposable;
import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeMultipleBuilder {
  private ImmutablePropTypeMultipleBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 booleanProp(final boolean booleanProp) {
    return newBuilder().booleanProp(booleanProp);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 booleanProp(boolean booleanProp);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 byteProp(byte byteProp);
  }

  interface Step3 {
    @Contract(pure = true)
    Step4 charProp(char charProp);
  }

  interface Step4 {
    @Contract(pure = true)
    Step5 doubleProp(double doubleProp);
  }

  interface Step5 {
    @Contract(pure = true)
    Step6 floatProp(float floatProp);
  }

  interface Step6 {
    @Contract(pure = true)
    Step7 intProp(int intProp);
  }

  interface Step7 {
    @Contract(pure = true)
    Step8 longProp(long longProp);
  }

  interface Step8 {
    @Contract(pure = true)
    Step9 shortProp(short shortProp);
  }

  interface Step9 {
    @Contract(pure = true)
    Step10 enumProp(ImmutablePropTypeMultiple.MyEnum enumProp);
  }

  interface Step10 {
    @Contract(pure = true)
    Step11 keyedComponentProp(ImmutablePropTypeMultiple.KeyedComponent keyedComponentProp);
  }

  interface Step11 {
    @Contract(pure = true)
    Step12 identifiableProp(ImmutablePropTypeMultiple.IdentifiableComponent identifiableProp);
  }

  interface Step12 {
    @Contract(pure = true)
    Step13 objectProp(Object objectProp);
  }

  interface Step13 {
    @Contract(pure = true)
    Step14 arezComponentLikeProp(
        ImmutablePropTypeMultiple.ArezComponentLikeComponent arezComponentLikeProp);
  }

  interface Step14 {
    @Contract(pure = true)
    Step15 arezComponentProp(ImmutablePropTypeMultiple.ArezComponentComponent arezComponentProp);
  }

  interface Step15 {
    @Contract(pure = true)
    Step16 arezComponentWithExplicitRequireIdProp(
        ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent
            arezComponentWithExplicitRequireIdProp);
  }

  interface Step16 {
    @Contract(pure = true)
    Step16 nullableEnumProp(ImmutablePropTypeMultiple.@Nullable MyEnum nullableEnumProp);

    @Contract(pure = true)
    Step16 nullableKeyedComponentProp(
        ImmutablePropTypeMultiple.@Nullable KeyedComponent nullableKeyedComponentProp);

    @Contract(pure = true)
    Step16 nullableIdentifiableProp(
        ImmutablePropTypeMultiple.@Nullable IdentifiableComponent nullableIdentifiableProp);

    @Contract(pure = true)
    Step16 nullableBoxedBooleanProp(@Nullable Boolean nullableBoxedBooleanProp);

    @Contract(pure = true)
    Step16 nullableBoxedByteProp(@Nullable Byte nullableBoxedByteProp);

    @Contract(pure = true)
    Step16 nullableBoxedDoubleProp(@Nullable Double nullableBoxedDoubleProp);

    @Contract(pure = true)
    Step16 nullableBoxedFloatProp(@Nullable Float nullableBoxedFloatProp);

    @Contract(pure = true)
    Step16 nullableBoxedIntProp(@Nullable Integer nullableBoxedIntProp);

    @Contract(pure = true)
    Step16 nullableBoxedLongProp(@Nullable Long nullableBoxedLongProp);

    @Contract(pure = true)
    Step16 nullableBoxedShortProp(@Nullable Short nullableBoxedShortProp);

    @Contract(pure = true)
    Step16 nullableObjectProp(@Nullable Object nullableObjectProp);

    @Contract(pure = true)
    Step16 nullableArezComponentLikeProp(
        ImmutablePropTypeMultiple.@Nullable ArezComponentLikeComponent
            nullableArezComponentLikeProp);

    @Contract(pure = true)
    Step16 nullableArezComponentProp(
        ImmutablePropTypeMultiple.@Nullable ArezComponentComponent nullableArezComponentProp);

    @Contract(pure = true)
    Step16 nullableArezComponentWithExplicitRequireIdProp(
        ImmutablePropTypeMultiple.@Nullable ArezComponentWithExplicitRequireIdComponent
            nullableArezComponentWithExplicitRequireIdProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder
      implements Step1,
          Step2,
          Step3,
          Step4,
          Step5,
          Step6,
          Step7,
          Step8,
          Step9,
          Step10,
          Step11,
          Step12,
          Step13,
          Step14,
          Step15,
          Step16 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeMultiple.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 booleanProp(final boolean booleanProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.booleanProp, booleanProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 byteProp(final byte byteProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.byteProp, byteProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 charProp(final char charProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.charProp, charProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step5 doubleProp(final double doubleProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.doubleProp, doubleProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step6 floatProp(final float floatProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.floatProp, floatProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step7 intProp(final int intProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.intProp, intProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step8 longProp(final long longProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.longProp, longProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step9 shortProp(final short shortProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.shortProp, shortProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step10 enumProp(final ImmutablePropTypeMultiple.MyEnum enumProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.enumProp, enumProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step11 keyedComponentProp(
        final ImmutablePropTypeMultiple.KeyedComponent keyedComponentProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.keyedComponentProp, keyedComponentProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step12 identifiableProp(
        final ImmutablePropTypeMultiple.IdentifiableComponent identifiableProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.identifiableProp, identifiableProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step13 objectProp(final Object objectProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.objectProp, objectProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step14 arezComponentLikeProp(
        final ImmutablePropTypeMultiple.ArezComponentLikeComponent arezComponentLikeProp) {
      assert Disposable.isNotDisposed(arezComponentLikeProp);
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.arezComponentLikeProp, arezComponentLikeProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step15 arezComponentProp(
        final ImmutablePropTypeMultiple.ArezComponentComponent arezComponentProp) {
      assert Disposable.isNotDisposed(arezComponentProp);
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.arezComponentProp, arezComponentProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 arezComponentWithExplicitRequireIdProp(
        final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent
            arezComponentWithExplicitRequireIdProp) {
      assert Disposable.isNotDisposed(arezComponentWithExplicitRequireIdProp);
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.arezComponentWithExplicitRequireIdProp,
          arezComponentWithExplicitRequireIdProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableEnumProp(
        final ImmutablePropTypeMultiple.@Nullable MyEnum nullableEnumProp) {
      _element.input(React4j_ImmutablePropTypeMultiple.Inputs.nullableEnumProp, nullableEnumProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableKeyedComponentProp(
        final ImmutablePropTypeMultiple.@Nullable KeyedComponent nullableKeyedComponentProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableKeyedComponentProp,
          nullableKeyedComponentProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableIdentifiableProp(
        final ImmutablePropTypeMultiple.@Nullable IdentifiableComponent nullableIdentifiableProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableIdentifiableProp,
          nullableIdentifiableProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedBooleanProp(final @Nullable Boolean nullableBoxedBooleanProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedBooleanProp,
          nullableBoxedBooleanProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedByteProp(final @Nullable Byte nullableBoxedByteProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedByteProp, nullableBoxedByteProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedDoubleProp(final @Nullable Double nullableBoxedDoubleProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedDoubleProp,
          nullableBoxedDoubleProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedFloatProp(final @Nullable Float nullableBoxedFloatProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedFloatProp, nullableBoxedFloatProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedIntProp(final @Nullable Integer nullableBoxedIntProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedIntProp, nullableBoxedIntProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedLongProp(final @Nullable Long nullableBoxedLongProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedLongProp, nullableBoxedLongProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableBoxedShortProp(final @Nullable Short nullableBoxedShortProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedShortProp, nullableBoxedShortProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableObjectProp(final @Nullable Object nullableObjectProp) {
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableObjectProp, nullableObjectProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableArezComponentLikeProp(
        final ImmutablePropTypeMultiple.@Nullable ArezComponentLikeComponent
            nullableArezComponentLikeProp) {
      assert Disposable.isNotDisposed(nullableArezComponentLikeProp);
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableArezComponentLikeProp,
          nullableArezComponentLikeProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableArezComponentProp(
        final ImmutablePropTypeMultiple.@Nullable ArezComponentComponent
            nullableArezComponentProp) {
      assert Disposable.isNotDisposed(nullableArezComponentProp);
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableArezComponentProp,
          nullableArezComponentProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step16 nullableArezComponentWithExplicitRequireIdProp(
        final ImmutablePropTypeMultiple.@Nullable ArezComponentWithExplicitRequireIdComponent
            nullableArezComponentWithExplicitRequireIdProp) {
      assert Disposable.isNotDisposed(nullableArezComponentWithExplicitRequireIdProp);
      _element.input(
          React4j_ImmutablePropTypeMultiple.Inputs.nullableArezComponentWithExplicitRequireIdProp,
          nullableArezComponentWithExplicitRequireIdProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      final Object $objectProp$ = inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.objectProp);
      final Object $nullableObjectProp$ =
          inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableObjectProp);
      _element.setKey(String.valueOf((boolean) Objects.requireNonNull(
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.booleanProp)))
          + "-"
          + String.valueOf((byte) (double)
              Objects.requireNonNull(inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.byteProp)))
          + "-"
          + String.valueOf((char)
              Objects.requireNonNull(inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.charProp)))
          + "-"
          + String.valueOf((double) Objects.requireNonNull(
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.doubleProp)))
          + "-"
          + String.valueOf((float) (double) Objects.requireNonNull(
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.floatProp)))
          + "-"
          + String.valueOf((int) (double)
              Objects.requireNonNull(inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.intProp)))
          + "-"
          + String.valueOf((long) (double)
              Objects.requireNonNull(inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.longProp)))
          + "-"
          + String.valueOf((short) (double) Objects.requireNonNull(
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.shortProp)))
          + "-"
          + ((ImmutablePropTypeMultiple.MyEnum)
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.enumProp))
          + "-"
          + Keyed.getKey((ImmutablePropTypeMultiple.KeyedComponent)
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.keyedComponentProp))
          + "-"
          + String.valueOf(
              Identifiable.<Object>getArezId((ImmutablePropTypeMultiple.IdentifiableComponent)
                  inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.identifiableProp)))
          + "-"
          + ($objectProp$ instanceof Keyed
              ? Keyed.getKey($objectProp$)
              : $objectProp$ instanceof Identifiable
                  ? Identifiable.<Object>getArezId($objectProp$)
                  : String.valueOf($objectProp$))
          + "-"
          + String.valueOf(
              Identifiable.<Object>getArezId((ImmutablePropTypeMultiple.ArezComponentLikeComponent)
                  inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.arezComponentLikeProp)))
          + "-"
          + String.valueOf(
              Identifiable.<Object>getArezId((ImmutablePropTypeMultiple.ArezComponentComponent)
                  inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.arezComponentProp)))
          + "-"
          + String.valueOf(Identifiable.<Object>getArezId(
              (ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent) inputs.get(
                  React4j_ImmutablePropTypeMultiple.Inputs.arezComponentWithExplicitRequireIdProp)))
          + "-"
          + ((ImmutablePropTypeMultiple.MyEnum)
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableEnumProp))
          + "-"
          + Keyed.getKey((ImmutablePropTypeMultiple.KeyedComponent)
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableKeyedComponentProp))
          + "-"
          + String.valueOf(
              Identifiable.<Object>getArezId((ImmutablePropTypeMultiple.IdentifiableComponent)
                  inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableIdentifiableProp)))
          + "-"
          + String.valueOf((Boolean)
              inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedBooleanProp))
          + "-"
          + String.valueOf(
              (Byte) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedByteProp))
          + "-"
          + String.valueOf(
              (Double) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedDoubleProp))
          + "-"
          + String.valueOf(
              (Float) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedFloatProp))
          + "-"
          + String.valueOf(
              (Integer) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedIntProp))
          + "-"
          + String.valueOf(
              (Long) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedLongProp))
          + "-"
          + String.valueOf(
              (Short) inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableBoxedShortProp))
          + "-"
          + ($nullableObjectProp$ instanceof Keyed
              ? Keyed.getKey($nullableObjectProp$)
              : $nullableObjectProp$ instanceof Identifiable
                  ? Identifiable.<Object>getArezId($nullableObjectProp$)
                  : String.valueOf($nullableObjectProp$))
          + "-"
          + String.valueOf(Identifiable.<Object>getArezId(
              (ImmutablePropTypeMultiple.ArezComponentLikeComponent) inputs.get(
                  React4j_ImmutablePropTypeMultiple.Inputs.nullableArezComponentLikeProp)))
          + "-"
          + String.valueOf(
              Identifiable.<Object>getArezId((ImmutablePropTypeMultiple.ArezComponentComponent)
                  inputs.get(React4j_ImmutablePropTypeMultiple.Inputs.nullableArezComponentProp)))
          + "-"
          + String.valueOf(Identifiable.<Object>getArezId(
              (ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent) inputs.get(
                  React4j_ImmutablePropTypeMultiple.Inputs
                      .nullableArezComponentWithExplicitRequireIdProp)))
          + (React.enableViewNames()
              ? "_ImmutablePropTypeMultiple_c22e3e8e"
              : ImmutablePropTypeMultiple.class.getName()));
      return _element;
    }
  }
}
