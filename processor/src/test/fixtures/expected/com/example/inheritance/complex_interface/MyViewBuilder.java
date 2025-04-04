package com.example.inheritance.complex_interface;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.Context;
import react4j.Contexts;
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
  static Step2 aaField2InRootInterface(final boolean aaField2InRootInterface) {
    return newBuilder().aaField2InRootInterface( aaField2InRootInterface );
  }

  public interface Step1 {
    @Nonnull
    Step2 aaField2InRootInterface(boolean aaField2InRootInterface);
  }

  public interface Step2 {
    @Nonnull
    Step3 aaField2InInnerInterface(boolean aaField2InInnerInterface);
  }

  public interface Step3 {
    @Nonnull
    Step4 aaField2InLeafInterface(boolean aaField2InLeafInterface);
  }

  public interface Step4 {
    @Nonnull
    ReactNode field2InLeaf(boolean field2InLeaf);
  }

  private static class ContextHolder {
    @Nonnull
    private static final Context<Boolean> CONTEXT_zzField1InRootInterface = Contexts.get( Boolean.class );

    @Nonnull
    private static final Context<Boolean> CONTEXT_zzField1InInnerInterface = Contexts.get( Boolean.class );

    @Nonnull
    private static final Context<Boolean> CONTEXT_zzField1InLeafInterface = Contexts.get( Boolean.class );

    @Nonnull
    private static final Context<Boolean> CONTEXT_field1InLeaf = Contexts.get( Boolean.class );

    private ContextHolder() {
    }
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MyView.Factory.TYPE );

    @Nonnull
    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InRootInterface = this::$context_zzField1InRootInterface;

    @Nonnull
    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InInnerInterface = this::$context_zzField1InInnerInterface;

    @Nonnull
    private final Context.ConsumerRenderFunction<Boolean> _$context_zzField1InLeafInterface = this::$context_zzField1InLeafInterface;

    @Nonnull
    private final Context.ConsumerRenderFunction<Boolean> _$context_field1InLeaf = this::$context_field1InLeaf;

    @Override
    @Nonnull
    public final Step2 aaField2InRootInterface(final boolean aaField2InRootInterface) {
      _element.input( React4j_MyView.Inputs.aaField2InRootInterface, aaField2InRootInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 aaField2InInnerInterface(final boolean aaField2InInnerInterface) {
      _element.input( React4j_MyView.Inputs.aaField2InInnerInterface, aaField2InInnerInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step4 aaField2InLeafInterface(final boolean aaField2InLeafInterface) {
      _element.input( React4j_MyView.Inputs.aaField2InLeafInterface, aaField2InLeafInterface );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode field2InLeaf(final boolean field2InLeaf) {
      _element.input( React4j_MyView.Inputs.field2InLeaf, field2InLeaf );
      return build();
    }

    @Nonnull
    private ReactNode build(@Nonnull final ReactElement element) {
      final JsPropertyMap<Object> inputs = element.inputs();
      element.setKey( String.valueOf( (boolean) inputs.get( React4j_MyView.Inputs.zzField1InRootInterface ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_MyView.Inputs.zzField1InInnerInterface ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_MyView.Inputs.zzField1InLeafInterface ) ) + "-" + String.valueOf( (boolean) inputs.get( React4j_MyView.Inputs.field1InLeaf ) ) + ( React.enableViewNames() ? "_MyView_106d73cd" : MyView.class.getName() ) );
      return element;
    }

    @Nonnull
    public final ReactNode build() {
      return ContextHolder.CONTEXT_zzField1InRootInterface.consumer().render( _$context_zzField1InRootInterface );
    }

    @Nonnull
    private ReactNode $context_zzField1InRootInterface(final boolean zzField1InRootInterface) {
      _element.input( React4j_MyView.Inputs.zzField1InRootInterface, zzField1InRootInterface );
      return ContextHolder.CONTEXT_zzField1InInnerInterface.consumer().render( _$context_zzField1InInnerInterface );
    }

    @Nonnull
    private ReactNode $context_zzField1InInnerInterface(final boolean zzField1InInnerInterface) {
      _element.input( React4j_MyView.Inputs.zzField1InInnerInterface, zzField1InInnerInterface );
      return ContextHolder.CONTEXT_zzField1InLeafInterface.consumer().render( _$context_zzField1InLeafInterface );
    }

    @Nonnull
    private ReactNode $context_zzField1InLeafInterface(final boolean zzField1InLeafInterface) {
      _element.input( React4j_MyView.Inputs.zzField1InLeafInterface, zzField1InLeafInterface );
      return ContextHolder.CONTEXT_field1InLeaf.consumer().render( _$context_field1InLeaf );
    }

    @Nonnull
    private ReactNode $context_field1InLeaf(final boolean field1InLeaf) {
      _element.input( React4j_MyView.Inputs.field1InLeaf, field1InLeaf );
      return build( _element.dup() );
    }
  }
}
