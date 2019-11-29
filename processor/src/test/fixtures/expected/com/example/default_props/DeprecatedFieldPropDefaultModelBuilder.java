package com.example.default_props;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
@SuppressWarnings("deprecation")
final class DeprecatedFieldPropDefaultModelBuilder {
  private DeprecatedFieldPropDefaultModelBuilder() {
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
      _element = ReactElement.createComponentElement( React4j_DeprecatedFieldPropDefaultModel.Factory.TYPE );
      final JsPropertyMap<Object> props = _element.props();
      props.set( React4j_DeprecatedFieldPropDefaultModel.Props.myProp, DeprecatedFieldPropDefaultModel.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final ReactNode myProp(final String myProp) {
      _element.props().set( React4j_DeprecatedFieldPropDefaultModel.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
