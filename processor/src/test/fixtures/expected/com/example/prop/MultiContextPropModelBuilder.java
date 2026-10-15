package com.example.prop;

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
final class MultiContextPropModelBuilder {
  private MultiContextPropModelBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class ContextHolder {
    private static final Context<String> CONTEXT_stringContextValue = Contexts.get( String.class );

    private static final Context<Integer> CONTEXT_intContextValue = Contexts.get( Integer.class );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiContextPropModel.Factory.TYPE );

    private final Context.ConsumerRenderFunction<String> _$context_stringContextValue = this::$context_stringContextValue;

    private final Context.ConsumerRenderFunction<Integer> _$context_intContextValue = this::$context_intContextValue;

    private ReactNode build(final ReactElement element) {
      return element;
    }

    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return ContextHolder.CONTEXT_stringContextValue.consumer().render( _$context_stringContextValue );
    }

    private ReactNode $context_stringContextValue(final @Nullable String stringContextValue) {
      _element.input( React4j_MultiContextPropModel.Inputs.stringContextValue, stringContextValue );
      return ContextHolder.CONTEXT_intContextValue.consumer().render( _$context_intContextValue );
    }

    private ReactNode $context_intContextValue(final @Nullable Integer intContextValue) {
      _element.input( React4j_MultiContextPropModel.Inputs.intContextValue, intContextValue );
      return build( _element.dup() );
    }
  }
}
