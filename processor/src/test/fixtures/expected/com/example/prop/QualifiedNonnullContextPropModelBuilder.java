package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Context;
import react4j.Contexts;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class QualifiedNonnullContextPropModelBuilder {
  private QualifiedNonnullContextPropModelBuilder() {
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

  interface Step1 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class ContextHolder {
    private static final Context<String> CONTEXT_myProp = Contexts.get( String.class, "my-proppo" );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_QualifiedNonnullContextPropModel.Factory.TYPE );

    private final Context.ConsumerRenderFunction<String> _$context_myProp = this::$context_myProp;

    private ReactNode build(final ReactElement element) {
      return element;
    }

    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return ContextHolder.CONTEXT_myProp.consumer().render( _$context_myProp );
    }

    private ReactNode $context_myProp(final @Nullable String myProp) {
      _element.input( React4j_QualifiedNonnullContextPropModel.Inputs.myProp, myProp );
      return build( _element.dup() );
    }
  }
}
