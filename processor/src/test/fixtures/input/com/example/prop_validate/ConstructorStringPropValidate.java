package com.example.prop_validate;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ConstructorStringPropValidate
{
  ConstructorStringPropValidate( @Nullable @Input final String myProp )
  {
  }

  @InputValidate
  void validateMyProp( String prop )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
