package com.example.inject;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( sting = Feature.DISABLE )
abstract class ConstructorInjectRawTypeComponent
  extends Component
{
  ConstructorInjectRawTypeComponent( @SuppressWarnings( "rawtypes" ) @Nonnull Consumer someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
