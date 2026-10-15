package com.example.arez;

import arez.Disposable;
import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MaybeTrackingHasComponentDependencyFieldBuilder {
  private MaybeTrackingHasComponentDependencyFieldBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode model(final MaybeTrackingHasComponentDependencyFieldView.Model model) {
    return newBuilder().model( model );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode model(MaybeTrackingHasComponentDependencyFieldView.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MaybeTrackingHasComponentDependencyFieldView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode model(final MaybeTrackingHasComponentDependencyFieldView.Model model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_MaybeTrackingHasComponentDependencyFieldView_8376a9ac" : MaybeTrackingHasComponentDependencyFieldView.class.getName() ) );
      assert Disposable.isNotDisposed( model );
      _element.input( React4j_MaybeTrackingHasComponentDependencyFieldView.Inputs.model, model );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
