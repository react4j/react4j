package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class CustomNamePropBuilder {
  private CustomNamePropBuilder() {
  }

  @Nonnull
  static ReactNode foo(final String foo) {
    return new Builder().foo( foo );
  }

  public interface Step1 {
    @Nonnull
    ReactNode foo(String foo);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CustomNameProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode foo(final String foo) {
      _element.props().set( React4j_CustomNameProp.Props.foo, foo );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
