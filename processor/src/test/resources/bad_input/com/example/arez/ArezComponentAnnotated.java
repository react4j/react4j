package com.example.arez;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import jsinterop.base.JsPropertyMap;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;

@ReactComponent
@ArezComponent( allowEmpty = true, allowConcrete = true )
abstract class ArezComponentAnnotated
  extends ReactArezComponent
{
  // This method is added to avoid failures on rebuild all within IDE.
  // In normal production code, the react annotation processor will have aborted
  // prior to Arez detecting reportPropsChanged is unimplemented. However the react
  // annotation processor does not run when compiling this code from IDE as it is part
  // of tests for annotation processor.
  @Override
  protected void reportPropsChanged( @Nullable final JsPropertyMap<Object> nextProps )
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
