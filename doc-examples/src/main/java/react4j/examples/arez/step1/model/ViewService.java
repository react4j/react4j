package react4j.examples.arez.step1.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.Observable;

@ArezComponent( singleton = true )
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
