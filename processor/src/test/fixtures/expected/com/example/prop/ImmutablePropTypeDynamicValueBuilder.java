package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeDynamicValueBuilder {
  private ImmutablePropTypeDynamicValueBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode myProp(final Object myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(Object myProp);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypeDynamicValue.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final Object myProp) {
      _element.setKey( ( myProp instanceof Keyed ? Keyed.getKey( myProp ) : myProp instanceof Identifiable ? Identifiable.<Object>getArezId( myProp ) : String.valueOf( myProp ) ) + ( React.enableViewNames() ? "_ImmutablePropTypeDynamicValue_a98f656b" : ImmutablePropTypeDynamicValue.class.getName() ) );
      _element.input( React4j_ImmutablePropTypeDynamicValue.Inputs.myProp, myProp );
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
