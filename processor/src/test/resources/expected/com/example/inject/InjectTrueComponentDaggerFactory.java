package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.core.Component;

public interface InjectTrueComponentDaggerFactory {
  DaggerSubcomponent getInjectTrueComponentDaggerSubcomponent();

  default void bindInjectTrueComponent() {
    InjectTrueComponent_.setProvider( () -> getInjectTrueComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final InjectTrueComponent_ component) {
      return component;
    }
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
