package com.example.arez;

import arez.Disposable;
import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NonObservableComponentDependencyViewBuilder {
  private NonObservableComponentDependencyViewBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(@Nonnull final NonObservableComponentDependencyView.Model model) {
    return newBuilder().model( model );
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(@Nonnull NonObservableComponentDependencyView.Model model);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonObservableComponentDependencyView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(@Nonnull final NonObservableComponentDependencyView.Model model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_NonObservableComponentDependencyView_2755cc8f" : NonObservableComponentDependencyView.class.getName() ) );
      assert Disposable.isNotDisposed( model );
      Objects.requireNonNull( model );
      _element.input( React4j_NonObservableComponentDependencyView.Inputs.model, model );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
