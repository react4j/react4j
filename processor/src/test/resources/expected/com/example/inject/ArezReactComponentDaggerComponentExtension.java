package com.example.inject;

import javax.annotation.Generated;

@Generated("react4j.processor.ReactProcessor")
public interface ArezReactComponentDaggerComponentExtension extends React4j_ArezReactComponentDaggerComponentExtension {
  default void bindArezReactComponent() {
    React4j_ArezReactComponentDaggerComponentExtension.super.bindArezReactComponent();
    React4j_ArezReactComponent.InjectSupport.setProvider( () -> (ArezReactComponent) getArezReactComponentDaggerSubcomponent().createProvider().get() );
  }

  @Override
  DaggerSubcomponent getArezReactComponentDaggerSubcomponent();
}
