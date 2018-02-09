package com.example.prop;

import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class MultipleChildrenPropsComponent
  extends Component<BaseState>
{
  @Prop
  protected abstract ReactNode[] getChildren();

  @Prop
  protected abstract ReactNode getChild();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
