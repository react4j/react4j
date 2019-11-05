package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class PropDefaultWithColorfulNameBuilder {
  private PropDefaultWithColorfulNameBuilder() {
  }

  @Nonnull
  static ReactNode myProp12$23(final String myProp12$23) {
    return new Builder().myProp12$23( myProp12$23 );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp12$23(String myProp12$23);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_PropDefaultWithColorfulName.Factory.TYPE );
      final JsPropertyMap<Object> props = _element.props();
      props.set( React4j_PropDefaultWithColorfulName.Props.myProp12$23, PropDefaultWithColorfulName.DEFAULT_MY_PROP12$23 );
    }

    @Override
    @Nonnull
    public final ReactNode myProp12$23(final String myProp12$23) {
      _element.props().set( React4j_PropDefaultWithColorfulName.Props.myProp12$23, myProp12$23 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
