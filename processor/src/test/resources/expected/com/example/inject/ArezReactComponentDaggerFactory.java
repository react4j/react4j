package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.core.Component;

@Generated("react4j.processor.ReactProcessor")
public interface ArezReactComponentDaggerFactory {
  DaggerSubcomponent getArezReactComponentDaggerSubcomponent();

  default void bindArezReactComponent() {
    ArezReactComponent_.setProvider( () -> getArezReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final Arez_ArezReactComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default ArezReactComponent get() {
      return (ArezReactComponent) createProvider().get();
    }
  }
}
