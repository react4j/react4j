package com.example.on_prop_change;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CustomNamingOnPropChangeBuilder {
  private CustomNamingOnPropChangeBuilder() {
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
  static Step3 myProp1(final boolean myProp1) {
    return new Builder().myProp1( myProp1 );
  }

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    Step3 myProp1(boolean myProp1);
  }

  public interface Step3 {
    @Nonnull
    Step4 myProp2(String myProp2);
  }

  public interface Step4 {
    @Nonnull
    ReactNode myProp3(int myProp3);
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CustomNamingOnPropChange.Factory.TYPE );

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
    public final Step3 myProp1(final boolean myProp1) {
      _element.props().set( React4j_CustomNamingOnPropChange.Props.myProp1, myProp1 );
      return this;
    }

    @Override
    @Nonnull
    public final Step4 myProp2(final String myProp2) {
      _element.props().set( React4j_CustomNamingOnPropChange.Props.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp3(final int myProp3) {
      _element.props().set( React4j_CustomNamingOnPropChange.Props.myProp3, myProp3 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
