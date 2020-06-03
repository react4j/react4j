package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NullabilityPropsComponentBuilder {
  private NullabilityPropsComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myProp(@Nonnull final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myProp(@Nonnull String myProp);
  }

  public interface Step2 {
    @Nonnull
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NullabilityPropsComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.props().set( React4j_NullabilityPropsComponent.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.props().set( React4j_NullabilityPropsComponent.Props.myProp2, myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
