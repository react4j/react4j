package react4j.examples.arez.step1.model;

import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import arez.annotations.Observable;
import arez.annotations.Repository;
import java.util.Objects;
import javax.annotation.Nonnull;

@Repository( extensions = { TodoRepositoryExtension.class } )
@ArezComponent
public abstract class Todo
{
  @Nonnull
  private String _id;
  @Nonnull
  private String _title;
  private boolean _completed;

  Todo( @Nonnull final String id, @Nonnull final String title, final boolean completed )
  {
    _id = Objects.requireNonNull( id );
    _title = Objects.requireNonNull( title );
    _completed = completed;
  }

  @ComponentId
  @Nonnull
  public final String getId()
  {
    return _id;
  }

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
