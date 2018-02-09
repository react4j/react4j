package com.example.inject;

import javax.inject.Inject;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent( inject = Feature.DISABLE, dagger = Feature.ENABLE )
abstract class InjectFalseDaggerTrueComponent
  extends Component<BaseState>
{
  @Inject
  String someParam;

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
