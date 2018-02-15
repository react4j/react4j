package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.core.Component;

@Generated("react4j.processor.ReactProcessor")
public interface InjectTrueComponentDaggerFactory {
  DaggerSubcomponent getInjectTrueComponentDaggerSubcomponent();

  default void bindInjectTrueComponent() {
    React4j_InjectTrueComponent.setProvider( () -> getInjectTrueComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final React4j_InjectTrueComponent component) {
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
