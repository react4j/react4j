package com.example.optional_props;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ChildrenEnhancer;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithManyRequiredBuilder {
  private RequiredChildrenWithManyRequiredBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step1 myRequiredProp1(final @Nullable String myRequiredProp1) {
    return newBuilder().myRequiredProp1( myRequiredProp1 );
  }

  @Contract(
      pure = true
  )
  static Step1 myRequiredProp2(final @Nullable String myRequiredProp2) {
    return newBuilder().myRequiredProp2( myRequiredProp2 );
  }

  @Contract(
      pure = true
  )
  static Step1 myRequiredProp3(final @Nullable String myRequiredProp3) {
    return newBuilder().myRequiredProp3( myRequiredProp3 );
  }

  static ReactNode children(final Renderable @Nullable ... children) {
    return newBuilder().children( children );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  static ReactNode children(final Stream<? extends Renderable> arg0) {
    return RequiredChildrenWithManyRequiredBuilder.newBuilder().children(arg0);
  }

  interface Step1 extends Buildable, ChildrenEnhancer<ReactNode> {
    @Contract(
        pure = true
    )
    Step1 myRequiredProp1(@Nullable String myRequiredProp1);

    @Contract(
        pure = true
    )
    Step1 myRequiredProp2(@Nullable String myRequiredProp2);

    @Contract(
        pure = true
    )
    Step1 myRequiredProp3(@Nullable String myRequiredProp3);

    ReactNode children(Renderable @Nullable ... children);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_RequiredChildrenWithManyRequired.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp1(final @Nullable String myRequiredProp1) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp1, myRequiredProp1 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp2(final @Nullable String myRequiredProp2) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp2, myRequiredProp2 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp3(final @Nullable String myRequiredProp3) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp3, myRequiredProp3 );
      return this;
    }

    @Override
    public final ReactNode children(final Renderable @Nullable ... children) {
      final var $react4j$children = Renderable.toReactNodes( children );
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.children, $react4j$children );
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
