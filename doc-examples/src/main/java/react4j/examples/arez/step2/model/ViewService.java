package react4j.examples.arez.step2.model;

import arez.annotations.ArezComponent;
import arez.annotations.Observable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Singleton;

@ArezComponent
@Singleton
public class ViewService
{
  @Nonnull
  private FilterMode _filterMode = FilterMode.ALL;

  ViewService()
  {
  }

  @Observable
  @Nonnull
  public FilterMode getFilterMode()
  {
    return _filterMode;
  }

  public void setFilterMode( @Nonnull final FilterMode filterMode )
  {
    _filterMode = Objects.requireNonNull( filterMode );
  }
}
