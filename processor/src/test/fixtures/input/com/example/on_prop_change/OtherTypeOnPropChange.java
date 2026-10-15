package com.example.on_prop_change;

import java.util.BitSet;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class OtherTypeOnPropChange
{
  @OnInputChange
  void onMyPropChange( @Nullable BitSet myProp )
  {
  }

  @Nullable
  @Input
  abstract BitSet getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
