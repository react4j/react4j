package com.example.inheritance.rose;

import java.util.Date;
import javax.annotation.Nonnull;
import react4j.annotations.Input;

public interface MyConsumerView
  extends MyBaseView
{
  @Input( fromTreeContext = true )
  @Override
  @Nonnull
  Date dateRange();
}
