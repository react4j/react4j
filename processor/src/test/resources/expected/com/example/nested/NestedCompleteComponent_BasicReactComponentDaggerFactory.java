package com.example.nested;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface NestedCompleteComponent_BasicReactComponentDaggerFactory {
  DaggerSubcomponent getBasicReactComponentDaggerSubcomponent();

  default void bindBasicReactComponent() {
    NestedCompleteComponent_BasicReactComponent_.setProvider( () -> getBasicReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final NestedCompleteComponent_BasicReactComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default NestedCompleteComponent.BasicReactComponent get() {
      return (NestedCompleteComponent.BasicReactComponent) createProvider().get();
    }
  }
}
