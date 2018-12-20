package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class MultiPropComponent4Builder {
  private MultiPropComponent4Builder() {
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
  static Step3 myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp(String myProp);
  }

  public interface Step3 {
    @Nonnull
    Step3 myProp2(@Nullable String myProp2);

    @Nonnull
    Step3 myProp3(@Nullable String myProp3);

    @Nonnull
    Step3 myProp4(@Nullable String myProp4);

    @Nonnull
    Step3 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_MultiPropComponent4.Factory.TYPE );

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
    public final Step3 myProp(final String myProp) {
      _element.props().set( React4j_MultiPropComponent4.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp2(@Nullable final String myProp2) {
      _element.props().set( React4j_MultiPropComponent4.Props.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp3(@Nullable final String myProp3) {
      _element.props().set( React4j_MultiPropComponent4.Props.myProp3, myProp3 );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 myProp4(@Nullable final String myProp4) {
      _element.props().set( React4j_MultiPropComponent4.Props.myProp4, myProp4 );
      return this;
    }

    @Override
    @Nonnull
    public final Step3 child(@Nullable final ReactNode child) {
      _element.props().set( React4j_MultiPropComponent4.Props.child, JsArray.of( child ) );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
