package com.example.enhancer;

import java.util.ArrayList;
import java.util.HashSet;
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

    default <Step1, ReactNode> R named(Step1 first, ReactNode second)
    {
      return unitPanels();
    }

    @SuppressWarnings("NonApiType")
    default R collections(ArrayList<Class<?>> first, HashSet<Class<?>> second)
    {
      second.addAll(first);
      return panelTypes(second);
    }
  }

  public interface CountBase<R>
  {
    R count(int value);

    default <T extends Number> R number(T value) throws Exception
    {
      if (value.intValue() < 0) { throw new Exception("negative"); }
      return count(value.intValue());
    }

    default <Step2, Builder, AdvancingBuilder> R named(Step2 first, Builder second, AdvancingBuilder third)
    {
      return count(1);
    }
  }

  public interface Count<R> extends CountBase<R>
  {
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
