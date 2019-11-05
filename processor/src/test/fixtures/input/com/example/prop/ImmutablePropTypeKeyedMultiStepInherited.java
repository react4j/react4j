package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypeKeyedMultiStepInherited
  extends Component
{
  static class BaseComponent
    implements Keyed
  {
    @Nonnull
    @Override
    public String getKey()
    {
      return "";
    }
  }

  static class MiddleComponent
    extends BaseComponent
  {
  }

 static class KeyedComponent
   extends MiddleComponent
  {
  }

  @Prop( immutable = true )
  protected abstract KeyedComponent getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
