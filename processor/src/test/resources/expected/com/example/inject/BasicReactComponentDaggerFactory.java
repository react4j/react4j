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
    BasicReactComponent_.setProvider( () -> getBasicReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final BasicReactComponent_ component) {
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
