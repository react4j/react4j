package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2PublicAccessPostUpdateModel
  extends Component
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicLifecycleMethod" )
  @PostUpdate
  public void postUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
