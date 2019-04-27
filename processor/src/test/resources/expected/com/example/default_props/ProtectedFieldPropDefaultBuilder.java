package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ProtectedFieldPropDefaultBuilder {
  private ProtectedFieldPropDefaultBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(String myProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_ProtectedFieldPropDefault.Factory.TYPE );
      _element.props().set( React4j_ProtectedFieldPropDefault.Props.myProp, ProtectedFieldPropDefault.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.props().set( React4j_ProtectedFieldPropDefault.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
