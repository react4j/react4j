package com.example.inheritance.complex_interface;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.Contexts;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MyBuilder {
  private MyBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 aaField2InRootInterface(final boolean aaField2InRootInterface) {
    return newBuilder().aaField2InRootInterface(aaField2InRootInterface);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 aaField2InRootInterface(boolean aaField2InRootInterface);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 aaField2InInnerInterface(boolean aaField2InInnerInterface);
  }

  interface Step3 {
    @Contract(pure = true)
    Step4 aaField2InLeafInterface(boolean aaField2InLeafInterface);
  }

  interface Step4 {
    @Contract(pure = true)
    ReactNode field2InLeaf(boolean field2InLeaf);
  }

  private static class ContextHolder {
    private static final Context<Boolean> CONTEXT_zzField1InRootInterface =
        Contexts.get(Boolean.class);

    private static final Context<Boolean> CONTEXT_zzField1InInnerInterface =
        Contexts.get(Boolean.class);

    private static final Context<Boolean> CONTEXT_zzField1InLeafInterface =
        Contexts.get(Boolean.class);

    private static final Context<Boolean> CONTEXT_field1InLeaf = Contexts.get(Boolean.class);

    private ContextHolder() {}
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MyView.Factory.TYPE);

    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InRootInterface =
        this::$context_zzField1InRootInterface;

    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InInnerInterface =
        this::$context_zzField1InInnerInterface;

    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InLeafInterface =
        this::$context_zzField1InLeafInterface;

    private final Context.ConsumerRenderFunction<Boolean> _$context_field1InLeaf =
        this::$context_field1InLeaf;

    @Override
    @Contract(pure = true)
    public final Step2 aaField2InRootInterface(final boolean aaField2InRootInterface) {
      _element.input(React4j_MyView.Inputs.aaField2InRootInterface, aaField2InRootInterface);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 aaField2InInnerInterface(final boolean aaField2InInnerInterface) {
      _element.input(React4j_MyView.Inputs.aaField2InInnerInterface, aaField2InInnerInterface);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 aaField2InLeafInterface(final boolean aaField2InLeafInterface) {
      _element.input(React4j_MyView.Inputs.aaField2InLeafInterface, aaField2InLeafInterface);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode field2InLeaf(final boolean field2InLeaf) {
      _element.input(React4j_MyView.Inputs.field2InLeaf, field2InLeaf);
      return build();
    }

    private ReactNode build(final ReactElement element) {
      return element;
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return ContextHolder.CONTEXT_zzField1InRootInterface.consumer()
          .render(_$context_zzField1InRootInterface);
    }

    private ReactNode $context_zzField1InRootInterface(
        final @Nullable Boolean zzField1InRootInterface) {
      _element.input(React4j_MyView.Inputs.zzField1InRootInterface, zzField1InRootInterface);
      return ContextHolder.CONTEXT_zzField1InInnerInterface.consumer()
          .render(_$context_zzField1InInnerInterface);
    }

    private ReactNode $context_zzField1InInnerInterface(
        final @Nullable Boolean zzField1InInnerInterface) {
      _element.input(React4j_MyView.Inputs.zzField1InInnerInterface, zzField1InInnerInterface);
      return ContextHolder.CONTEXT_zzField1InLeafInterface.consumer()
          .render(_$context_zzField1InLeafInterface);
    }

    private ReactNode $context_zzField1InLeafInterface(
        final @Nullable Boolean zzField1InLeafInterface) {
      _element.input(React4j_MyView.Inputs.zzField1InLeafInterface, zzField1InLeafInterface);
      return ContextHolder.CONTEXT_field1InLeaf.consumer().render(_$context_field1InLeaf);
    }

    private ReactNode $context_field1InLeaf(final @Nullable Boolean field1InLeaf) {
      _element.input(React4j_MyView.Inputs.field1InLeaf, field1InLeaf);
      return build(_element.dup());
    }
  }
}
