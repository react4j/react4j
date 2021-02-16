package com.example.prop;

import elemental2.core.JsArray;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponent2Builder {
  private MultiPropComponent2Builder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(String myProp);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp2(String myProp2);
  }

  public interface Step3 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent2.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp2(final String myProp2) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
      _element.input( React4j_MultiPropComponent2.Inputs.children, JsArray.of( children ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
