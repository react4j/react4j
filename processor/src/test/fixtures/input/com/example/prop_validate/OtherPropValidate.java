package com.example.prop_validate;

import java.util.BitSet;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class OtherPropValidate
{
  @InputValidate
  void validateMyProp( BitSet prop )
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
