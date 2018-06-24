package com.example.nested;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.Component;

@Generated("react4j.processor.ReactProcessor")
public interface NestedCompleteComponent_BasicReactComponentDaggerFactory {
  DaggerSubcomponent getBasicReactComponentDaggerSubcomponent();

  default void bindBasicReactComponent() {
    NestedCompleteComponent_React4j_BasicReactComponent.setProvider( () -> getBasicReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final NestedCompleteComponent_React4j_BasicReactComponent component) {
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
