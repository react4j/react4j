package org.realityforge.react.todo_mvc.client.model;

public class NoResultException
  extends RuntimeException
{
  public NoResultException()
  {
  }

  public NoResultException( final String message )
  {
    super( message );
  }

  public NoResultException( final String message, final Throwable cause )
  {
    super( message, cause );
  }
}
