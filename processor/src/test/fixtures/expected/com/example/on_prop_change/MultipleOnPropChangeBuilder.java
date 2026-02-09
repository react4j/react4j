package com.example.on_prop_change;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultipleOnPropChangeBuilder {
  private MultipleOnPropChangeBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 myProp1(final boolean myProp1) {
    return newBuilder().myProp1( myProp1 );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp1(boolean myProp1);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3 myProp3(int myProp3);
  }

  public interface Step3 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultipleOnPropChange.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp1(final boolean myProp1) {
      _element.input( React4j_MultipleOnPropChange.Inputs.myProp1, myProp1 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3 myProp3(final int myProp3) {
      _element.input( React4j_MultipleOnPropChange.Inputs.myProp3, myProp3 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.input( React4j_MultipleOnPropChange.Inputs.myProp2, myProp2 );
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
