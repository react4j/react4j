package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.Context;
import react4j.Contexts;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class QualifiedNonnullContextPropModelBuilder {
  private QualifiedNonnullContextPropModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode build();
  }

  private static class ContextHolder {
    @Nonnull
    private static final Context<String> CONTEXT_myProp = Contexts.get( String.class, "my-proppo" );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_QualifiedNonnullContextPropModel.Factory.TYPE );

    @Nonnull
    private final Context.ConsumerRenderFunction<String> _$context_myProp = this::$context_myProp;

    @Nonnull
    private ReactNode build(@Nonnull final ReactElement element) {
      return element;
    }

    @Nonnull
    public final ReactNode build() {
      return ContextHolder.CONTEXT_myProp.consumer().render( _$context_myProp );
    }

    @Nonnull
    private ReactNode $context_myProp(final String myProp) {
      _element.input( React4j_QualifiedNonnullContextPropModel.Inputs.myProp, myProp );
      return build( _element.dup() );
    }
  }
}
