package com.example.inject;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.Component;

@Generated("react4j.processor.ReactProcessor")
public interface MethodInjectReactComponentDaggerComponentExtension {
  DaggerSubcomponent getMethodInjectReactComponentDaggerSubcomponent();

  default void bindMethodInjectReactComponent() {
    React4j_MethodInjectReactComponent.InjectSupport.setProvider( () -> (MethodInjectReactComponent) getMethodInjectReactComponentDaggerSubcomponent().createProvider().get() );
  }

  @Module
  interface DaggerModule {
    @Binds
    Component bindComponent(React4j_MethodInjectReactComponent component);
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();
  }
}
