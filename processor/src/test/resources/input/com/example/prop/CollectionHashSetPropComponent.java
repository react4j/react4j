package com.example.prop;

import java.util.HashSet;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CollectionHashSetPropComponent
  extends Component
{
  @Prop
  protected abstract HashSet<String> getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
