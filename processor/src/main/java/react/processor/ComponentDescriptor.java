package react.processor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class ComponentDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final PackageElement _packageElement;
  @Nonnull
  private final TypeElement _element;

  public ComponentDescriptor( @Nonnull final String name,
                              @Nonnull final PackageElement packageElement,
                              @Nonnull final TypeElement element )
  {
    _name = Objects.requireNonNull( name );
    _packageElement = Objects.requireNonNull( packageElement );
    _element = Objects.requireNonNull( element );

    if ( ElementKind.CLASS != element.getKind() )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a class", element );
    }
    else if ( element.getModifiers().contains( Modifier.ABSTRACT ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must not be abstract", element );
    }
    else if ( element.getModifiers().contains( Modifier.FINAL ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must not be final", element );
    }
    else if ( NestingKind.TOP_LEVEL != element.getNestingKind() &&
              !element.getModifiers().contains( Modifier.STATIC ) )
    {
      throw new ReactProcessorException( "@ReactComponent target must not be a non-static nested class", element );
    }
    //final List<ExecutableElement> constructors = element.getEnclosedElements().stream().
    //  filter( m -> m.getKind() == ElementKind.CONSTRUCTOR ).
    //  map( m -> (ExecutableElement) m ).
    //  collect( Collectors.toList() );
    //if ( 1 != constructors.size() || !isConstructorValid( constructors.get( 0 ) ) )
    //{
    //  throw new ReactProcessorException( "@ReactComponent target must have a single constructor that accepts " +
    //                                     "the native react component", element );
    //}
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  @Nonnull
  PackageElement getPackageElement()
  {
    return _packageElement;
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  DeclaredType getDeclaredType()
  {
    return (DeclaredType) _element.asType();
  }

  @Nonnull
  String getConstructorFactoryName()
  {
    return _element.getSimpleName() + "_";
  }

  private boolean isConstructorValid( @Nonnull final ExecutableElement executableElement )
  {
    if ( 1 != executableElement.getParameters().size() )
    {
      return false;
    }
    //TODO:
    return true;
  }
}
