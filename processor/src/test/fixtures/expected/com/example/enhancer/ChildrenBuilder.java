package com.example.enhancer;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
public final class ChildrenBuilder {
  private ChildrenBuilder() {
  }

  public static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  public static Step1 title(final @Nullable String title) {
    return newBuilder().title( title );
  }

  @Contract(
      pure = true
  )
  public static ReactNode children(final ReactNode @Nullable ... children) {
    return newBuilder().children( children );
  }

  @Contract(
      pure = true
  )
  public static ReactNode build() {
    return newBuilder().build();
  }

  public static ReactNode children(final Stream<? extends ReactNode> arg0) {
    return ChildrenBuilder.newBuilder().children(arg0);
  }

  public static ReactNode empty() {
    return ChildrenBuilder.newBuilder().empty();
  }

  public interface Step1 extends Enhancers.Children<ReactNode> {
    @Contract(
        pure = true
    )
    Step1 title(@Nullable String title);

    @Contract(
        pure = true
    )
    ReactNode children(ReactNode @Nullable ... children);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ChildrenView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step1 title(final @Nullable String title) {
      _element.input( React4j_ChildrenView.Inputs.title, title );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode children(final ReactNode @Nullable ... children) {
      _element.input( React4j_ChildrenView.Inputs.children, children );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
