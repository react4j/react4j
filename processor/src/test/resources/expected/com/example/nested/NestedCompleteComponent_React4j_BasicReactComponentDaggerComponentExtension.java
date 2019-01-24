package com.example.nested;

import dagger.Subcomponent;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("arez.processor.ArezProcessor")
public interface NestedCompleteComponent_React4j_BasicReactComponentDaggerComponentExtension {
  DaggerSubcomponent getBasicReactComponentDaggerSubcomponent();

  @Subcomponent
  interface DaggerSubcomponent {
    Arez_NestedCompleteComponent_React4j_BasicReactComponent.Factory createFactory();

    void inject(@Nonnull Arez_NestedCompleteComponent_React4j_BasicReactComponent component);
  }
}
