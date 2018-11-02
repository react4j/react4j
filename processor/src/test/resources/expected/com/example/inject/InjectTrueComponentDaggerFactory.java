package com.example.inject;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.Component;

@Generated("react4j.processor.ReactProcessor")
public interface InjectTrueComponentDaggerFactory {
  DaggerSubcomponent getInjectTrueComponentDaggerSubcomponent();

  default void bindInjectTrueComponent() {
    React4j_InjectTrueComponent.setProvider( () -> getInjectTrueComponentDaggerSubcomponent().get() );
  }

  @Module
  abstract class DaggerModule {
    @Binds
    abstract Component bindComponent(React4j_InjectTrueComponent component);
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default InjectTrueComponent get() {
      return (InjectTrueComponent) createProvider().get();
    }
  }
}
