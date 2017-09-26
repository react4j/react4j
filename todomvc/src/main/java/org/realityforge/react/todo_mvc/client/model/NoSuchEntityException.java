package org.realityforge.react.todo_mvc.client.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.arez.Arez;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

@SuppressWarnings( "GwtInconsistentSerializableClass" )
public class NoSuchEntityException
  extends NoResultException
{
  @Nullable
  private final String _type;
  @Nullable
  private final Object _id;

  public NoSuchEntityException( @Nullable final String type, @Nullable final Object id )
  {
    Guards.invariant( () -> Arez.context().areNamesEnabled() || null == type,
                      () -> "Attempted to pass type when constructing NoSuchEntityException() when Arez.context().areNamesEnabled() returns false" );
    Guards.invariant( () -> Arez.context().areNamesEnabled() || null == id,
                      () -> "Attempted to pass id when constructing NoSuchEntityException() when Arez.context().areNamesEnabled() returns false" );
    _type = Arez.context().areNamesEnabled() ? Objects.requireNonNull( type ) : null;
    _id = Arez.context().areNamesEnabled() ? Objects.requireNonNull( id ) : null;
  }

  @Nonnull
  public String getType()
  {
    Guards.invariant( () -> Arez.context().areNamesEnabled(),
                      () -> "Attempted to call NoSuchEntityException.getType() when Arez.context().areNamesEnabled() returns false" );
    assert null != _type;
    return _type;
  }

  @Nonnull
  public Object getId()
  {
    Guards.invariant( () -> Arez.context().areNamesEnabled(),
                      () -> "Attempted to call NoSuchEntityException.getId() when Arez.context().areNamesEnabled() returns false" );
    assert null != _id;
    return _id;
  }

  @Override
  public String toString()
  {
    if ( BrainCheckConfig.verboseErrorMessages() )
    {
      return "NoSuchEntityException[type=" + _type + ", id=" + _id + ']';
    }
    else
    {
      return super.toString();
    }
  }
}
