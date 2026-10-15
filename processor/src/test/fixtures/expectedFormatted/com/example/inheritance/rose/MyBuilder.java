package com.example.inheritance.rose;

import java.util.Date;
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
  static ReactNode field2InLeaf(final boolean field2InLeaf) {
    return newBuilder().field2InLeaf(field2InLeaf);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode field2InLeaf(boolean field2InLeaf);
  }

  private static class ContextHolder {
    private static final Context<Date> CONTEXT_dateRange = Contexts.get(Date.class);

    private static final Context<Boolean> CONTEXT_field1InLeaf = Contexts.get(Boolean.class);

    private ContextHolder() {}
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MyView.Factory.TYPE);

    private final Context.ConsumerRenderFunction<Date> _$context_dateRange =
        this::$context_dateRange;

    private final Context.ConsumerRenderFunction<Boolean> _$context_field1InLeaf =
        this::$context_field1InLeaf;

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
      return ContextHolder.CONTEXT_dateRange.consumer().render(_$context_dateRange);
    }

    private ReactNode $context_dateRange(final @Nullable Date dateRange) {
      _element.input(React4j_MyView.Inputs.dateRange, dateRange);
      return ContextHolder.CONTEXT_field1InLeaf.consumer().render(_$context_field1InLeaf);
    }

    private ReactNode $context_field1InLeaf(final @Nullable Boolean field1InLeaf) {
      _element.input(React4j_MyView.Inputs.field1InLeaf, field1InLeaf);
      return build(_element.dup());
    }
  }
}
