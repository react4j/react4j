package react4j.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

final class MethodChecks
{
  private MethodChecks()
  {
  }

  static void mustBeAbstract( @Nonnull final String annotationName, @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( !method.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ReactProcessorException( "@" +
                                         ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must be abstract", method );
    }
  }

  static void mustBeStatic( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( !method.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must be static", method );
    }
  }

  static void mustBeFinal( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( !method.getModifiers().contains( Modifier.FINAL ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must be final", method );
    }
  }

  static void mustNotBePrivate( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( method.getModifiers().contains( Modifier.PRIVATE ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not be private", method );
    }
  }

  static void mustNotHaveAnyParameters( @Nonnull final String annotationName,
                                        @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( !method.getParameters().isEmpty() )
    {
      throw new ReactProcessorException( "@" +
                                         ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not have any parameters", method );
    }
  }

  static void mustReturnAValue( @Nonnull final String annotationName,
                                @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( TypeKind.VOID == method.getReturnType().getKind() )
    {
      throw new ReactProcessorException( "@" +
                                         ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must return a value", method );
    }
  }

  static void mustNotThrowAnyExceptions( @Nonnull final String annotationName,
                                         @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( !method.getThrownTypes().isEmpty() )
    {
      throw new ReactProcessorException( "@" +
                                         ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not throw any exceptions", method );
    }
  }
}
