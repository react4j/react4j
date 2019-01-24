package com.example.inject;

import arez.annotations.ComponentDependency;
import arez.annotations.PostConstruct;
import arez.component.DisposeTrackable;
import javax.inject.Inject;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class ComponentDependencyOnReactComponent
  extends Component
{
  @Inject
  String someParam;

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @ComponentDependency
  final DisposeTrackable someDependency()
  {
    return null;
  }
}
