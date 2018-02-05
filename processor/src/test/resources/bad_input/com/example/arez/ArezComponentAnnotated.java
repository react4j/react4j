package com.example.arez;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.ReactNode;

@ReactComponent
@ArezComponent( allowEmpty = true, allowConcrete = true )
abstract class ArezComponentAnnotated
  extends ReactArezComponent<BaseProps, BaseContext>
{
  // This annotation is added to avoid failures on rebuild all within IDE.
  // In normal production code, the react annotation processor will have aborted
  // prior to Arez detecting reportPropsChanged is unimplemented. However the react
  // annotation processor does not run when compiling this code from IDE as it is part
  // of tests for annotation processor.
  @Override
  protected void reportPropsChanged( @Nullable final BaseProps nextProps )
  {
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
