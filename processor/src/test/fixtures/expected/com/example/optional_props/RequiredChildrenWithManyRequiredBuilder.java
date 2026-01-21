package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithManyRequiredBuilder {
  private RequiredChildrenWithManyRequiredBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 myRequiredProp1(final String myRequiredProp1) {
    return newBuilder().myRequiredProp1( myRequiredProp1 );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myRequiredProp1(String myRequiredProp1);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3 myRequiredProp2(String myRequiredProp2);
  }

  public interface Step3 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step4 myRequiredProp3(String myRequiredProp3);
  }

  public interface Step4 {
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

  private static class Builder implements Step1, Step2, Step3, Step4 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_RequiredChildrenWithManyRequired.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myRequiredProp1(final String myRequiredProp1) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp1, myRequiredProp1 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3 myRequiredProp2(final String myRequiredProp2) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp2, myRequiredProp2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step4 myRequiredProp3(final String myRequiredProp3) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp3, myRequiredProp3 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode children(final ReactNode... children) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.children, JsArray.of( children ) );
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
