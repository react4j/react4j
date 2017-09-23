package gwt.react.todo_mvc.client.model;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsMethod;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.Computed;
import org.realityforge.arez.annotations.Container;
import org.realityforge.arez.annotations.Observable;

@SuppressWarnings( "WeakerAccess" )
@Container
public class PersonModel
{
  @Nonnull
  private String _firstName;
  @Nonnull
  private String _lastName;

  @Nonnull
  public static PersonModel create( @Nonnull final String firstName, @Nonnull final String lastName )
  {
    return new Arez_PersonModel( firstName, lastName );
  }

  PersonModel( @Nonnull final String firstName, @Nonnull final String lastName )
  {
    _firstName = firstName;
    _lastName = lastName;
  }

  @Observable
  @Nonnull
  public String getFirstName()
  {
    return _firstName;
  }

  public void setFirstName( @Nonnull final String firstName )
  {
    _firstName = firstName;
  }

  @Observable
  @Nonnull
  public String getLastName()
  {
    return _lastName;
  }

  public void setLastName( @Nonnull final String lastName )
  {
    _lastName = lastName;
  }

  @Computed
  @Nonnull
  public String getFullName()
  {
    return getFirstName() + " " + getLastName();
  }

  @JsMethod
  @Action
  public void updateFirstName( @Nonnull final String firstName )
  {
    setFirstName( firstName );
  }
}
