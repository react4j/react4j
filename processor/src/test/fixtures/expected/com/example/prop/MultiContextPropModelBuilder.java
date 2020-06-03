package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.Context;
import react4j.Contexts;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiContextPropModelBuilder {
  private MultiContextPropModelBuilder() {
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
    private static final Context<String> CONTEXT_stringContextValue = Contexts.get( String.class );

    @Nonnull
    private static final Context<Integer> CONTEXT_intContextValue = Contexts.get( Integer.class );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiContextPropModel.Factory.TYPE );

    @Nonnull
    private ReactNode build(@Nonnull final ReactElement element) {
      element.complete();
      return element;
    }

    @Nonnull
    public final ReactNode build() {
      return ContextHolder.CONTEXT_stringContextValue.consumer().render( v0 -> ContextHolder.CONTEXT_intContextValue.consumer().render( v1 -> build( _element.prop( React4j_MultiContextPropModel.Props.stringContextValue, v0 ).prop( React4j_MultiContextPropModel.Props.intContextValue, v1 ).dup() ) ) );
    }
  }
}
