package com.example.inject;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.Component;

@Generated("react4j.processor.ReactProcessor")
public interface ArezReactComponentDaggerFactory {
  DaggerSubcomponent getArezReactComponentDaggerSubcomponent();

  default void bindArezReactComponent() {
    React4j_ArezReactComponent.InjectSupport.setProvider( () -> getArezReactComponentDaggerSubcomponent().get() );
  }

  @Module
  interface DaggerModule {
    @Binds
    Component bindComponent(Arez_React4j_ArezReactComponent component);
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
