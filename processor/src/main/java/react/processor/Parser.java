package react.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import react.annotations.ReactComponent;

final class Parser
{
  private Parser()
  {
  }

  @Nonnull
  static ComponentDescriptor parse( final PackageElement packageElement, final TypeElement typeElement )
  {
    final ReactComponent component = typeElement.getAnnotation( ReactComponent.class );
    assert null != component;
    final String name =
      ProcessorUtil.isSentinelName( component.name() ) ? typeElement.getSimpleName().toString() : component.name();

    return new ComponentDescriptor( name, packageElement, typeElement );
  }
}
