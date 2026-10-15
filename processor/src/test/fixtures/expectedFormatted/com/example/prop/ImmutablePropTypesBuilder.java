package com.example.prop;

import arez.Disposable;
import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypesBuilder {
  private ImmutablePropTypesBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 stillAnotherProp(final int stillAnotherProp) {
    return newBuilder().stillAnotherProp(stillAnotherProp);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 stillAnotherProp(int stillAnotherProp);
  }

  interface Step2 {
    @Contract(pure = true)
    Step2 myProp(ImmutablePropTypes.@Nullable MyComponent myProp);

    @Contract(pure = true)
    Step2 myOtherProp(@Nullable String myOtherProp);

    @Contract(pure = true)
    Step2 bobsProp(ImmutablePropTypes.@Nullable KeyedComponent bobsProp);

    @Contract(pure = true)
    Step2 someProp(ImmutablePropTypes.@Nullable Foo someProp);

    @Contract(pure = true)
    Step2 object(@Nullable Object object);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypes.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 stillAnotherProp(final int stillAnotherProp) {
      _element.input(React4j_ImmutablePropTypes.Inputs.stillAnotherProp, stillAnotherProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step2 myProp(final ImmutablePropTypes.@Nullable MyComponent myProp) {
      assert Disposable.isNotDisposed(myProp);
      _element.input(React4j_ImmutablePropTypes.Inputs.myProp, myProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step2 myOtherProp(final @Nullable String myOtherProp) {
      _element.input(React4j_ImmutablePropTypes.Inputs.myOtherProp, myOtherProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step2 bobsProp(final ImmutablePropTypes.@Nullable KeyedComponent bobsProp) {
      _element.input(React4j_ImmutablePropTypes.Inputs.bobsProp, bobsProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step2 someProp(final ImmutablePropTypes.@Nullable Foo someProp) {
      _element.input(React4j_ImmutablePropTypes.Inputs.someProp, someProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step2 object(final @Nullable Object object) {
      _element.input(React4j_ImmutablePropTypes.Inputs.object, object);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      final Object $object$ = inputs.get(React4j_ImmutablePropTypes.Inputs.object);
      _element.setKey(String.valueOf(
              (int) (double) inputs.get(React4j_ImmutablePropTypes.Inputs.stillAnotherProp))
          + "-"
          + String.valueOf(Identifiable.<Object>getArezId((ImmutablePropTypes.MyComponent)
              inputs.get(React4j_ImmutablePropTypes.Inputs.myProp)))
          + "-" + ((String) inputs.get(React4j_ImmutablePropTypes.Inputs.myOtherProp)) + "-"
          + Keyed.getKey((ImmutablePropTypes.KeyedComponent)
              inputs.get(React4j_ImmutablePropTypes.Inputs.bobsProp))
          + "-" + ((ImmutablePropTypes.Foo) inputs.get(React4j_ImmutablePropTypes.Inputs.someProp))
          + "-"
          + ($object$ instanceof Keyed
              ? Keyed.getKey($object$)
              : $object$ instanceof Identifiable
                  ? Identifiable.<Object>getArezId($object$)
                  : String.valueOf($object$))
          + (React.enableViewNames()
              ? "_ImmutablePropTypes_108bb2eb"
              : ImmutablePropTypes.class.getName()));
      return _element;
    }
  }
}
