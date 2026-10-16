package com.example.schedule_render;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_schedule_render_MultiScheduleRenderComponent",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultiScheduleRenderComponent extends MultiScheduleRenderComponent {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultiScheduleRenderComponent(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  @Override
  void myScheduleRender1() {
    $$react4j$$_nativeView.forceUpdate();
  }

  @Override
  void myScheduleRender2() {
    $$react4j$$_nativeView.forceUpdate();
  }

  @Override
  void myScheduleRender3() {
    $$react4j$$_nativeView.setState( Js.uncheckedCast( JsPropertyMap.of() ) );
  }

  @Override
  void myScheduleRender4() {
    $$react4j$$_nativeView.setState( Js.uncheckedCast( JsPropertyMap.of() ) );
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "MultiScheduleRenderComponent" );
    }
    return viewConstructor;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultiScheduleRenderComponent) this).dispose();
  }

  @Nullable ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MultiScheduleRenderComponent view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_MultiScheduleRenderComponent( this );
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return false;
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final @Nullable ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
