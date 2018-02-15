package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.core.Component;

@Generated("react4j.processor.ReactProcessor")
public interface BasicReactComponentDaggerFactory {
  DaggerSubcomponent getBasicReactComponentDaggerSubcomponent();

  default void bindBasicReactComponent() {
    React4j_BasicReactComponent.setProvider( () -> getBasicReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final React4j_BasicReactComponent component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default BasicReactComponent get() {
      return (BasicReactComponent) createProvider().get();
    }
  }
}
