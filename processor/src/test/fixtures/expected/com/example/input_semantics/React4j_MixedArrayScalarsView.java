package com.example.input_semantics;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_input_semantics_MixedArrayScalarsView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MixedArrayScalarsView extends MixedArrayScalarsView {
  React4j_MixedArrayScalarsView(final react4j.internal.NativeView $$react4j$$_nativeView,
      final String[] values, final int count, final char letter, final long id, final float rate,
      final boolean flag, final double size, final byte tiny, final short small,
      final @Nullable Long wrapped) {
    super( values, count, letter, id, rate, flag, size, tiny, small, wrapped );
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "MixedArrayScalars" );
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$values = inputs.get( Inputs.values );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$values, () -> "Required input named 'values' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$count = inputs.get( Inputs.count );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$count, () -> "Required input named 'count' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$letter = inputs.get( Inputs.letter );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$letter, () -> "Required input named 'letter' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$id = inputs.get( Inputs.id );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$id, () -> "Required input named 'id' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$rate = inputs.get( Inputs.rate );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$rate, () -> "Required input named 'rate' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$flag = inputs.get( Inputs.flag );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$flag, () -> "Required input named 'flag' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$size = inputs.get( Inputs.size );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$size, () -> "Required input named 'size' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$tiny = inputs.get( Inputs.tiny );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$tiny, () -> "Required input named 'tiny' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$small = inputs.get( Inputs.small );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$small, () -> "Required input named 'small' is missing from view named 'MixedArrayScalarsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MixedArrayScalarsView) this).dispose();
  }

  @Nullable ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return Renderable.toReactNode( render() );
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String values = React.shouldMinimizeInputKeys() ? "a" : "values";

    static final String count = React.shouldMinimizeInputKeys() ? "b" : "count";

    static final String letter = React.shouldMinimizeInputKeys() ? "c" : "letter";

    static final String id = React.shouldMinimizeInputKeys() ? "d" : "id";

    static final String rate = React.shouldMinimizeInputKeys() ? "e" : "rate";

    static final String flag = React.shouldMinimizeInputKeys() ? "f" : "flag";

    static final String size = React.shouldMinimizeInputKeys() ? "g" : "size";

    static final String tiny = React.shouldMinimizeInputKeys() ? "h" : "tiny";

    static final String small = React.shouldMinimizeInputKeys() ? "i" : "small";

    static final String wrapped = React.shouldMinimizeInputKeys() ? "j" : "wrapped";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MixedArrayScalarsView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      final String[] values = Objects.requireNonNull( inputs.getAsAny( Inputs.values ) ).cast();
      final int count = Objects.requireNonNull( inputs.getAsAny( Inputs.count ) ).asInt();
      final char letter = Objects.requireNonNull( inputs.getAsAny( Inputs.letter ) ).asChar();
      final long id = Objects.requireNonNull( inputs.getAsAny( Inputs.id ) ).asLong();
      final float rate = Objects.requireNonNull( inputs.getAsAny( Inputs.rate ) ).asFloat();
      final boolean flag = Objects.requireNonNull( inputs.getAsAny( Inputs.flag ) ).asBoolean();
      final double size = Objects.requireNonNull( inputs.getAsAny( Inputs.size ) ).asDouble();
      final byte tiny = Objects.requireNonNull( inputs.getAsAny( Inputs.tiny ) ).asByte();
      final short small = Objects.requireNonNull( inputs.getAsAny( Inputs.small ) ).asShort();
      Long wrapped;
      if ( React.shouldCheckInvariants() ) {
        final var raw$wrapped = inputs.getAsAny( Inputs.wrapped );
        wrapped = null != raw$wrapped ? raw$wrapped.cast() : null;
      } else {
        wrapped = Js.uncheckedCast( inputs.getAsAny( Inputs.wrapped ) );
      }
      view = new Arez_React4j_MixedArrayScalarsView( this, values, count, letter, id, rate, flag, size, tiny, small, wrapped );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_MixedArrayScalarsView.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate( nextInputs );
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
