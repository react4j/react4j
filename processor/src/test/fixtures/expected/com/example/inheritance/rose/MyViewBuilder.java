package com.example.inheritance.rose;

import arez.component.Identifiable;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.Context;
import react4j.Contexts;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MyViewBuilder {
  private MyViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode field2InLeaf(final boolean field2InLeaf) {
    return newBuilder().field2InLeaf( field2InLeaf );
  }

  public interface Step1 {
    @Nonnull
    ReactNode field2InLeaf(boolean field2InLeaf);
  }

  private static class ContextHolder {
    @Nonnull
    private static final Context<Date> CONTEXT_dateRange = Contexts.get( Date.class );

    @Nonnull
    private static final Context<Boolean> CONTEXT_field1InLeaf = Contexts.get( Boolean.class );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MyView.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode field2InLeaf(final boolean field2InLeaf) {
      _element.input( React4j_MyView.Inputs.field2InLeaf, field2InLeaf );
      return build();
    }

    @Nonnull
    private ReactNode build(@Nonnull final ReactElement element) {
      final JsPropertyMap<Object> inputs = element.inputs();
      final Object $dateRange$ = inputs.get( React4j_MyView.Inputs.dateRange );
      element.setKey( ( $dateRange$ instanceof Keyed ? Keyed.getKey( $dateRange$ ) : $dateRange$ instanceof Identifiable ? Identifiable.<Object>getArezId( $dateRange$ ) : String.valueOf( $dateRange$ ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_MyView.Inputs.field1InLeaf ) ) + ( React.enableViewNames() ? "_MyView_479d2c9f" : MyView.class.getName() ) );
      return element;
    }

    @Nonnull
    public final ReactNode build() {
      return ContextHolder.CONTEXT_dateRange.consumer().render( v0 -> ContextHolder.CONTEXT_field1InLeaf.consumer().render( v1 -> build( _element.input( React4j_MyView.Inputs.dateRange, v0 ).input( React4j_MyView.Inputs.field1InLeaf, v1 ).dup() ) ) );
    }
  }
}
