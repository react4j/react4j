package com.example.inject;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.internal.NativeComponent;
import sting.Eager;
import sting.Injectable;

@Generated("react4j.processor.React4jProcessor")
@Injectable
@Eager
public final class Jsr330NamedInjectComponentFactory {
  @Nonnull
  private final String someParam;

  @Inject
  @SuppressWarnings({
      "Sting:Jsr330InjectPresent",
      "Sting:Jsr330NamedPresent"
  })
  Jsr330NamedInjectComponentFactory(@Nonnull @Named("blah") final String someParam) {
    this.someParam = Objects.requireNonNull( someParam );
    InjectSupport.setFactory( this );
  }

  @Nonnull
  public static React4j_Jsr330NamedInjectComponent create(
      @Nonnull final NativeComponent component) {
    return InjectSupport.create( component );
  }

  private static final class InjectSupport {
    private static Jsr330NamedInjectComponentFactory c_factory;

    private static void setFactory(@Nonnull final Jsr330NamedInjectComponentFactory factory) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null == c_factory, () -> "Attempted to instantiate the React4j component factory for the component named 'Jsr330NamedInjectComponent' a second time" );
      }
      c_factory = factory;
    }

    @Nonnull
    private static React4j_Jsr330NamedInjectComponent create(
        @Nonnull final NativeComponent component) {
      if ( React.shouldCheckInvariants() ) {
        Guards.invariant( () -> null != c_factory, () -> "Attempted to create an instance of the React4j component named 'Jsr330NamedInjectComponent' before the component factory has been initialized. Please see the documentation at https://react4j.github.io/dependency_injection for directions how to configure dependency injection." );
      }
      return new Arez_React4j_Jsr330NamedInjectComponent( component, c_factory.someParam );
    }
  }
}
