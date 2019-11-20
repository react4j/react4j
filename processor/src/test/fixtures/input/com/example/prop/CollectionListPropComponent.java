package com.example.prop;

import java.util.List;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class CollectionListPropComponent
  extends Component
{
  @Prop
  abstract List<String> getMyProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
