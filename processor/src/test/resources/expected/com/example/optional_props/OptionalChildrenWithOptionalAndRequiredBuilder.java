package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class OptionalChildrenWithOptionalAndRequiredBuilder {
  private OptionalChildrenWithOptionalAndRequiredBuilder() {
  }

  @Nonnull
  static Step2 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myRequiredProp(String myRequiredProp);
  }

  public interface Step2 {
    @Nonnull
    Step2 myProp(String myProp);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    Step2 children(ReactNode... children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_OptionalChildrenWithOptionalAndRequired.Factory.TYPE );
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myProp, OptionalChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final Step2 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myProp(final String myProp) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Override
    @Nonnull
    public final Step2 children(final ReactNode... children) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.children, JsArray.of( children ) );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
