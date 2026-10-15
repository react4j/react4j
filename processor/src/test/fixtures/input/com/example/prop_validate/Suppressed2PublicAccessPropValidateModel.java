package com.example.prop_validate;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputValidate;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class Suppressed2PublicAccessPropValidateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @InputValidate
  public void validateMyProp( @Nullable String prop )
  {
  }

  @Nullable
  @Input
  abstract String getMyProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
