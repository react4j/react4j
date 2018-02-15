package com.example.inject;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;
import react4j.core.Component;

@Generated("react4j.processor.ReactProcessor")
public interface DaggerTrueComponentDaggerFactory {
  DaggerSubcomponent getDaggerTrueComponentDaggerSubcomponent();

  default void bindDaggerTrueComponent() {
    React4j_DaggerTrueComponent.setProvider( () -> getDaggerTrueComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule {
    @Provides
    static Component provideComponent(final React4j_DaggerTrueComponent component) {
      return component;
    }
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<Component> createProvider();

    default DaggerTrueComponent get() {
      return (DaggerTrueComponent) createProvider().get();
    }
  }
}
