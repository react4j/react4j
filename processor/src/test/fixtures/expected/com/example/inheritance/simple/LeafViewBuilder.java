package com.example.inheritance.simple;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class LeafViewBuilder {
  private LeafViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 zzField1InRootInterface(final boolean zzField1InRootInterface) {
    return newBuilder().zzField1InRootInterface( zzField1InRootInterface );
  }

  public interface Step1 {
    @Nonnull
    Step2 zzField1InRootInterface(boolean zzField1InRootInterface);
  }

  public interface Step2 {
    @Nonnull
    Step3 aaField2InRootInterface(boolean aaField2InRootInterface);
  }

  public interface Step3 {
    @Nonnull
    Step4 field1InRoot(boolean field1InRoot);
  }

  public interface Step4 {
    @Nonnull
    Step5 field2InRoot(boolean field2InRoot);
  }

  public interface Step5 {
    @Nonnull
    Step6 zzField1InInnerInterface(boolean zzField1InInnerInterface);
  }

  public interface Step6 {
    @Nonnull
    Step7 aaField2InInnerInterface(boolean aaField2InInnerInterface);
  }

  public interface Step7 {
    @Nonnull
    Step8 field1InInner(boolean field1InInner);
  }

  public interface Step8 {
    @Nonnull
    Step9 field2InInner(boolean field2InInner);
  }

  public interface Step9 {
    @Nonnull
    Step10 zzField1InLeafInterface(boolean zzField1InLeafInterface);
  }

  public interface Step10 {
    @Nonnull
    Step11 aaField2InLeafInterface(boolean aaField2InLeafInterface);
  }

  public interface Step11 {
    @Nonnull
    Step12 field1InLeaf(boolean field1InLeaf);
  }

  public interface Step12 {
    @Nonnull
    ReactNode field2InLeaf(boolean field2InLeaf);
  }

  private static class Builder implements Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9, Step10, Step11, Step12 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_LeafView.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 zzField1InRootInterface(final boolean zzField1InRootInterface) {
      _element.input( React4j_LeafView.Inputs.zzField1InRootInterface, zzField1InRootInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 aaField2InRootInterface(final boolean aaField2InRootInterface) {
      _element.input( React4j_LeafView.Inputs.aaField2InRootInterface, aaField2InRootInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step4 field1InRoot(final boolean field1InRoot) {
      _element.input( React4j_LeafView.Inputs.field1InRoot, field1InRoot );
      return this;
    }

    @Override
    @Nonnull
    public final Step5 field2InRoot(final boolean field2InRoot) {
      _element.input( React4j_LeafView.Inputs.field2InRoot, field2InRoot );
      return this;
    }

    @Override
    @Nonnull
    public final Step6 zzField1InInnerInterface(final boolean zzField1InInnerInterface) {
      _element.input( React4j_LeafView.Inputs.zzField1InInnerInterface, zzField1InInnerInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step7 aaField2InInnerInterface(final boolean aaField2InInnerInterface) {
      _element.input( React4j_LeafView.Inputs.aaField2InInnerInterface, aaField2InInnerInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step8 field1InInner(final boolean field1InInner) {
      _element.input( React4j_LeafView.Inputs.field1InInner, field1InInner );
      return this;
    }

    @Override
    @Nonnull
    public final Step9 field2InInner(final boolean field2InInner) {
      _element.input( React4j_LeafView.Inputs.field2InInner, field2InInner );
      return this;
    }

    @Override
    @Nonnull
    public final Step10 zzField1InLeafInterface(final boolean zzField1InLeafInterface) {
      _element.input( React4j_LeafView.Inputs.zzField1InLeafInterface, zzField1InLeafInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step11 aaField2InLeafInterface(final boolean aaField2InLeafInterface) {
      _element.input( React4j_LeafView.Inputs.aaField2InLeafInterface, aaField2InLeafInterface );
      return this;
    }

    @Override
    @Nonnull
    public final Step12 field1InLeaf(final boolean field1InLeaf) {
      _element.input( React4j_LeafView.Inputs.field1InLeaf, field1InLeaf );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode field2InLeaf(final boolean field2InLeaf) {
      _element.input( React4j_LeafView.Inputs.field2InLeaf, field2InLeaf );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
