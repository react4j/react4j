package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface MethodInjectReactComponentDaggerFactory {
  DaggerComponent createMethodInjectReactComponentDaggerComponent();

  default void bindMethodInjectReactComponent() {
    MethodInjectReactComponent_.setProvider( () -> createMethodInjectReactComponentDaggerComponent().get() );
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
  interface DaggerComponent {
    Provider<Component> createProvider();

    default MethodInjectReactComponent get() {
      return (MethodInjectReactComponent) createProvider().get();
    }
  }
}
