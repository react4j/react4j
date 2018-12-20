package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class RequiredChildrenWithOptionalAndRequiredBuilder {
  private RequiredChildrenWithOptionalAndRequiredBuilder() {
  }

  @Nonnull
  static Step2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Step2 key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Step3 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    Step3 myRequiredProp(String myRequiredProp);
  }

  public interface Step3 {
    @Nonnull
    ReactNode myProp(String myProp);

    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
  }

  public interface Step4 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_RequiredChildrenWithOptionalAndRequired.Factory.TYPE );
      _element.props().set( React4j_RequiredChildrenWithOptionalAndRequired.Props.myProp, RequiredChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final Step2 key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Override
    @Nonnull
    public final Step3 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_RequiredChildrenWithOptionalAndRequired.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.props().set( React4j_RequiredChildrenWithOptionalAndRequired.Props.myProp, myProp );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
      _element.props().set( React4j_RequiredChildrenWithOptionalAndRequired.Props.children, JsArray.of( children ) );
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
      _element.complete();
      return _element;
    }
  }
}
