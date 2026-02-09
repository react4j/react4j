package com.example.prop_validate;

import java.util.BitSet;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
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
