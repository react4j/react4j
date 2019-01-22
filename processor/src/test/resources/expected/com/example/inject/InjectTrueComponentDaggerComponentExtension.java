package com.example.inject;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.Component;

@Generated("react4j.processor.ReactProcessor")
public interface InjectTrueComponentDaggerComponentExtension {
  DaggerSubcomponent getInjectTrueComponentDaggerSubcomponent();

  default void bindInjectTrueComponent() {
    React4j_InjectTrueComponent.InjectSupport.setProvider( () -> (InjectTrueComponent) getInjectTrueComponentDaggerSubcomponent().createProvider().get() );
  }

  @Module
  interface DaggerModule {
    @Binds
    Component bindComponent(Arez_React4j_InjectTrueComponent component);
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();
  }
}
