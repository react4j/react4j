package com.example.inject;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated("react4j.processor.ReactProcessor")
public interface PublicReactComponentDaggerFactory {
  DaggerSubcomponent getPublicReactComponentDaggerSubcomponent();

  default void bindPublicReactComponent() {
    React4j_PublicReactComponent.InjectSupport.setProvider( getPublicReactComponentDaggerSubcomponent().createProvider() );
  }

  @Module
  interface DaggerModule {
    @Binds
    PublicReactComponent bindComponent(React4j_PublicReactComponent component);
  }

  @Subcomponent(
      modules = DaggerModule.class
  )
  interface DaggerSubcomponent {
    Provider<PublicReactComponent> createProvider();
  }
}
