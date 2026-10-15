package com.example.inheritance.rose;

import java.util.Date;
import react4j.annotations.Input;

@org.jspecify.annotations.NullMarked
public interface MyConsumerView
  extends MyBaseView
{
  @Input( fromTreeContext = true )
  @Override

  Date dateRange();
}
