package com.example.arez.memoize;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class PublicMemoizeComponent
  extends Component
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Memoize
  public String getIcon( String key )
  {
    return null;
  }
}
