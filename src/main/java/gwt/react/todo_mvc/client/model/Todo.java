package gwt.react.todo_mvc.client.model;

import java.util.Objects;
import javax.annotation.Nonnull;

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

  @Nonnull
  public String getId()
  {
    return _id;
  }

  public void setId( @Nonnull String id )
  {
    _id = Objects.requireNonNull( id );
  }

  @Nonnull
  public String getTitle()
  {
    return _title;
  }

  public void setTitle( @Nonnull final String title )
  {
    _title = Objects.requireNonNull( title );
  }

  public boolean isCompleted()
  {
    return _completed;
  }

  public void setCompleted( final boolean completed )
  {
    _completed = completed;
  }
}
