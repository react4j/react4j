package com.example.prop;

import java.io.IOException;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PropThrowsExceptionComponent
  extends Component
{
  @Prop
  protected abstract String getMyProp()
    throws IOException;

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
