package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface MethodInjectReactComponentDaggerFactory {
  DaggerSubcomponent getMethodInjectReactComponentDaggerSubcomponent();

  default void bindMethodInjectReactComponent() {
    MethodInjectReactComponent_.setProvider( () -> getMethodInjectReactComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final MethodInjectReactComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default MethodInjectReactComponent get() {
      return (MethodInjectReactComponent) createProvider().get();
    }
  }
}
