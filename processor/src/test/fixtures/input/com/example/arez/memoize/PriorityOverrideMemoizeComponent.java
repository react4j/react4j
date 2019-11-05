package com.example.arez.memoize;

import arez.Observer;
import arez.annotations.Memoize;
import arez.annotations.PriorityOverride;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class PriorityOverrideMemoizeComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize
  String icon( String key )
  {
    return null;
  }

  @PriorityOverride
  final int iconPriority()
  {
    return Observer.Flags.PRIORITY_HIGH;
  }
}
