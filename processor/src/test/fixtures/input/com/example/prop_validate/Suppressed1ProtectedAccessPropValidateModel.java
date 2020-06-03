package com.example.prop_validate;

import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.PropValidate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1ProtectedAccessPropValidateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @PropValidate
  protected void validateMyProp( String prop )
  {
  }

  @Prop
  abstract String getMyProp();

  @Render
  ReactNode render()
  {
    return null;
  }
}
