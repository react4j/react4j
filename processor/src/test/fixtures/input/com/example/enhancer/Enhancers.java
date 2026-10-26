package com.example.enhancer;

import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Enhancers
{
  private Enhancers() {}

  public interface PanelTypes<R>
  {
    R panelTypes(@Nullable Set<Class<?>> value);

    default R unitPanels()
    {
      return panelTypes(Set.of(String.class, Integer.class));
    }
  }

  public interface Count<R>
  {
    R count(int value);

    default R one()
    {
      return count(1);
    }
  }

  public interface Base<R>
  {
    R label(String value);

    default <E extends Enum<E>> R enumLabel(Class<E> value) throws Exception
    {
      return label(value.getName());
    }
  }

  public interface Label<R> extends Base<R>
  {
    default R untitled()
    {
      return label("Untitled");
    }
  }

  public interface Title<R>
  {
    R title(@Nullable String value);

    default R titled(String... words)
    {
      return title(String.join(" ", words));
    }
  }
}
