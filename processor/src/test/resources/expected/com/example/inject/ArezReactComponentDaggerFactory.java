package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface ArezReactComponentDaggerFactory {
  DaggerComponent createArezReactComponentDaggerComponent();

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final Arez_ArezReactComponent_ component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerComponent {
    Provider<Component> createProvider();

    default ArezReactComponent get() {
      return (ArezReactComponent) createProvider().get();
    }
  }
}
