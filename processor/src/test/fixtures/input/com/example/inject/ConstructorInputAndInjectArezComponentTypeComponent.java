package com.example.inject;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import sting.Named;

@Named( "blah" )
@View
abstract class ConstructorInputAndInjectArezComponentTypeComponent
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @ComponentDependency
  @Nonnull
  final Model _model;

  ConstructorInputAndInjectArezComponentTypeComponent( @Nonnull final String someParam,
                                                       @Nonnull @Input final Model model )
  {
    _model = model;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
