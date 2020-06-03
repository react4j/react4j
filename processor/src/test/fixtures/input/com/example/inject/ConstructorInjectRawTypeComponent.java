package com.example.inject;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( sting = Feature.DISABLE )
abstract class ConstructorInjectRawTypeComponent
{
  ConstructorInjectRawTypeComponent( @SuppressWarnings( "rawtypes" ) @Nonnull Consumer someParam )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
