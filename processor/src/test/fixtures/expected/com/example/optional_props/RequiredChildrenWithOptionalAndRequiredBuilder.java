package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithOptionalAndRequiredBuilder {
  private RequiredChildrenWithOptionalAndRequiredBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step2 myRequiredProp(final String myRequiredProp) {
    return newBuilder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 myRequiredProp(String myRequiredProp);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp(String myProp);

    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
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
    @Nonnull
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_RequiredChildrenWithOptionalAndRequired.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_RequiredChildrenWithOptionalAndRequired.Inputs.myProp, RequiredChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final Step2 myRequiredProp(final String myRequiredProp) {
      _element.input( React4j_RequiredChildrenWithOptionalAndRequired.Inputs.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp(final String myProp) {
      _element.input( React4j_RequiredChildrenWithOptionalAndRequired.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
      _element.input( React4j_RequiredChildrenWithOptionalAndRequired.Inputs.children, JsArray.of( children ) );
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
