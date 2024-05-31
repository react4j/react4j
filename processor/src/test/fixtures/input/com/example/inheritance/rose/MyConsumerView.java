package com.example.inheritance.rose;

import java.util.Date;
import javax.annotation.Nonnull;
import react4j.annotations.Input;

public interface MyConsumerView
  extends MyBaseView
{
  @Input( immutable = true, source = Input.Source.CONTEXT )
  @Override
  @Nonnull
  Date dateRange();
}
