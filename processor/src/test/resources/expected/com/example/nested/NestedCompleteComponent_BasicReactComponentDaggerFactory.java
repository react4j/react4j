package com.example.nested;

import dagger.Binds;
import dagger.Module;
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
  abstract class DaggerModule {
    @Binds
    abstract Component bindComponent(NestedCompleteComponent_React4j_BasicReactComponent component);
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
