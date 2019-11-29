package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeEnumBuilder {
  private ImmutablePropTypeEnumBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final ImmutablePropTypeEnum.Foo myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(ImmutablePropTypeEnum.Foo myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypeEnum.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final ImmutablePropTypeEnum.Foo myProp) {
      _element.setKey( ImmutablePropTypeEnum.class.getName() + myProp.name() );
      _element.props().set( React4j_ImmutablePropTypeEnum.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
