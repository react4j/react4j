package com.example.prop;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
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
  @Contract(
      pure = true
  )
  static Step2 myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp(String myProp);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3 myProp2(String myProp2);
  }

  public interface Step3 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode children(ReactNode... children);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent2.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3 myProp2(final String myProp2) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode children(final ReactNode... children) {
      _element.input( React4j_MultiPropComponent2.Inputs.children, JsArray.of( children ) );
      return build();
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
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
