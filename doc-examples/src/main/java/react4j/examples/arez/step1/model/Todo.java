package react4j.examples.arez.step1.model;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentIdRef;
import arez.annotations.Observable;
import java.util.Objects;
import javax.annotation.Nonnull;

@ArezComponent
public abstract class Todo
{
  @Nonnull
  private String _title;
  private boolean _completed;

  Todo( @Nonnull final String title, final boolean completed )
  {
    _title = Objects.requireNonNull( title );
    _completed = completed;
  }

  @ComponentIdRef
  public abstract int getId();

  @Observable
  @Nonnull
  public String getTitle()
  {
    return _title;
  }

  public void setTitle( @Nonnull final String title )
  {
    _title = Objects.requireNonNull( title );
  }

  @Observable
  public boolean isCompleted()
  {
    return _completed;
  }

  public void setCompleted( final boolean completed )
  {
    _completed = completed;
  }
}
