package react4j.processor;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

@SuppressWarnings( "SameParameterValue" )
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
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must be abstract", method );
    }
  }

  private static void mustBeStatic( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( !method.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must be static", method );
    }
  }

  private static void mustNotBeStatic( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( method.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not be static", method );
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

  private static void mustNotBePrivate( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( method.getModifiers().contains( Modifier.PRIVATE ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not be private", method );
    }
  }

  static void mustNotBePublic( @Nonnull final String annotationName, @Nonnull final Element method )
    throws ReactProcessorException
  {
    if ( method.getModifiers().contains( Modifier.PUBLIC ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not be public", method );
    }
  }

  static void mustNotHaveAnyParameters( @Nonnull final String annotationName, @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( !method.getParameters().isEmpty() )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not have any parameters", method );
    }
  }

  static void mustReturnAValue( @Nonnull final String annotationName, @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( TypeKind.VOID == method.getReturnType().getKind() )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must return a value", method );
    }
  }

  static void mustNotReturnAValue( @Nonnull final String annotationName, @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( TypeKind.VOID != method.getReturnType().getKind() )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not return a value", method );
    }
  }

  static void mustNotThrowAnyExceptions( @Nonnull final String annotationName, @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    if ( !method.getThrownTypes().isEmpty() )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not throw any exceptions", method );
    }
  }

  static void mustNotBeAbstract( @Nonnull final String annotationName, @Nonnull final Element element )
    throws ReactProcessorException
  {
    if ( element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                         " target must not be abstract", element );
    }
  }

  /**
   * Verifies that the method is not static, abstract or private.
   * The intent is to verify that it can be instance called by sub-class in same package.
   */
  static void mustBeSubclassCallable( @Nonnull final TypeElement targetType,
                                      @Nonnull final String annotationName,
                                      @Nonnull final Element method )
    throws ReactProcessorException
  {
    mustNotBeStatic( annotationName, method );
    mustNotBePrivate( annotationName, method );
    mustNotBePackageAccessInDifferentPackage( targetType, annotationName, method );
  }

  static void mustBeStaticallySubclassCallable( @Nonnull final TypeElement targetType,
                                                @Nonnull final String annotationName,
                                                @Nonnull final Element method )
    throws ReactProcessorException
  {
    mustBeStatic( annotationName, method );
    mustNotBePrivate( annotationName, method );
    mustNotBePackageAccessInDifferentPackage( targetType, annotationName, method );
  }

  static void mustNotBePackageAccessInDifferentPackage( @Nonnull final TypeElement component,
                                                        @Nonnull final String annotationName,
                                                        @Nonnull final Element method )
    throws ReactProcessorException
  {
    final Set<Modifier> modifiers = method.getModifiers();
    final boolean isPackageAccess =
      !modifiers.contains( Modifier.PRIVATE ) &&
      !modifiers.contains( Modifier.PROTECTED ) &&
      !modifiers.contains( Modifier.PUBLIC );

    if ( isPackageAccess )
    {
      final PackageElement packageElement = getPackageElement( component );
      final PackageElement methodPackageElement = getPackageElement( (TypeElement) method.getEnclosingElement() );
      if ( !Objects.equals( packageElement.getQualifiedName(), methodPackageElement.getQualifiedName() ) )
      {
        throw new ReactProcessorException( "@" + ProcessorUtil.toSimpleName( annotationName ) +
                                           " target must not be package access if the method is in a different " +
                                           "package from the @ReactComponent", method );
      }
    }
  }

  @Nonnull
  private static PackageElement getPackageElement( @Nonnull final TypeElement element )
  {
    Element enclosingElement = element.getEnclosingElement();
    while ( null != enclosingElement )
    {
      if ( enclosingElement instanceof PackageElement )
      {
        return (PackageElement) enclosingElement;
      }
      enclosingElement = enclosingElement.getEnclosingElement();
    }
    throw new IllegalStateException();
  }

  /**
   * Verifies that the method follows conventions of a lifecycle hook.
   * The intent is to verify that it can be instance called by sub-class in same
   * package at a lifecycle stage. It should not raise errors, return values or accept
   * parameters.
   */
  static void mustBeLifecycleHook( @Nonnull final TypeElement targetType,
                                   @Nonnull final String annotationName,
                                   @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    mustNotBeAbstract( annotationName, method );
    mustNotBePublic( annotationName, method );
    mustBeSubclassCallable( targetType, annotationName, method );
    mustNotHaveAnyParameters( annotationName, method );
    mustNotReturnAValue( annotationName, method );
    mustNotThrowAnyExceptions( annotationName, method );
  }
}
