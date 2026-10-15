package com.example.on_error;

import com.example.on_error.other.BaseProtectedAccessOnErrorModel;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ProtectedAccessFromBaseOnErrorModel
  extends BaseProtectedAccessOnErrorModel
{
}
