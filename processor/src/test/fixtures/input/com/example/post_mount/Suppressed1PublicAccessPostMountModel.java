package com.example.post_mount;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class Suppressed1PublicAccessPostMountModel
  extends Component
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @PostMount
  public void postMount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
