package react4j.todomvc.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.ComponentId;
import org.realityforge.arez.annotations.Observable;
import org.realityforge.arez.annotations.Repository;

@Repository( extensions = { TodoRepositoryExtension.class } )
@ArezComponent
public class Todo
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

  @Action
  public void toggle()
  {
    setCompleted( !isCompleted() );
  }

  boolean shouldShowTodo( @Nonnull final FilterMode filterMode )
  {
    if ( FilterMode.ALL == filterMode )
    {
      return true;
    }
    else if ( FilterMode.ACTIVE == filterMode )
    {
      return !isCompleted();
    }
    else
    {
      return isCompleted();
    }
  }
}
