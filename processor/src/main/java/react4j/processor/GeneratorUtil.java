package react4j.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;

final class GeneratorUtil
{
  private GeneratorUtil()
  {
  }

  @Nonnull
  static String getNestedClassPrefix( @Nonnull final TypeElement element )
  {
    final StringBuilder name = new StringBuilder();
    TypeElement t = element;
    while ( NestingKind.TOP_LEVEL != t.getNestingKind() )
    {
      t = (TypeElement) t.getEnclosingElement();
      name.insert( 0, t.getSimpleName() + "_" );
    }
    return name.toString();
  }
}
