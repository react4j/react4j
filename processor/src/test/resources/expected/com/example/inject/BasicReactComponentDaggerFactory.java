package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface BasicReactComponentDaggerFactory {
  DaggerComponent createBasicReactComponentDaggerComponent();

  default void bindBasicReactComponent() {
    BasicReactComponent_.setProvider( () -> createBasicReactComponentDaggerComponent().get() );
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
  interface DaggerComponent {
    Provider<Component> createProvider();

    default BasicReactComponent get() {
      return (BasicReactComponent) createProvider().get();
    }
  }
}
