package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface DaggerTrueComponentDaggerFactory {
  DaggerComponent createDaggerTrueComponentDaggerComponent();

  default void bindDaggerTrueComponent() {
    DaggerTrueComponent_.setProvider( () -> createDaggerTrueComponentDaggerComponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final DaggerTrueComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerComponent {
    Provider<Component> createProvider();

    default DaggerTrueComponent get() {
      return (DaggerTrueComponent) createProvider().get();
    }
  }
}
