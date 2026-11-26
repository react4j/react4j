package react4j;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsProperty;
import org.jspecify.annotations.Nullable;

/**
 * Context is designed to share data that can be considered "global" for a tree of views.
 * In a typical React application, data is passed top-down (parent to child) via inputs, but this can
 * be cumbersome for certain types of inputs (e.g. locale preference, UI theme) that are required by
 * many views within an application. Context provides a way to share values like this between
 * views without having to explicitly pass a input through every level of the tree.
 */
public final class Context<T extends @Nullable Object> {
    private final @Nullable T _defaultValue;
    private final Provider<T> _provider = new Provider<>(this);
    private final Consumer<T> _consumer = new Consumer<>(this);

    public Context() {
        this(null);
    }

    Context(@Nullable final T defaultValue) {
        _defaultValue = defaultValue;
    }

    public @Nullable T defaultValue() {
        return _defaultValue;
    }

    public static final class Provider<T extends @Nullable Object> {
        private final Context<T> _context;

        private Provider(final Context<T> context) {
            _context = context;
        }

        @SuppressWarnings("unusable-by-js")
        @JsProperty(name = "_context")
        public Context<T> context() {
            return _context;
        }
    }

    public static final class Consumer<T extends @Nullable Object> {
        private final Context<T> _context;

        private Consumer(final Context<T> context) {
            _context = context;
        }

        @SuppressWarnings("unusable-by-js")
        @JsProperty(name = "_context")
        public Context<T> context() {
            return _context;
        }
    }

    /**
     * Create a builder for the Provider component.
     *
     * @return a builder for the Provider component.
     */
    public ProviderBuilder<T> provider() {
        return new ProviderBuilder<>(this);
    }

    /**
     * Create a provider component that provides specified value to specified children.
     *
     * @param value    the value to provider to children.
     * @param children the child elements.
     * @return the element.
     */
    public ReactNode provide(@Nullable final T value, final @Nullable Renderable @Nullable ... children) {
        return provider().value(value).children(children);
    }

    /**
     * Create a builder for the Consumer component.
     *
     * @return a builder for the Consumer component.
     */
    public ConsumerBuilder<T> consumer() {
        return new ConsumerBuilder<>(this);
    }

    /**
     * A Builder for the Provider component.
     * A provider component is a react component that allows Consumers to subscribe to context changes.
     */
    public static final class ProviderBuilder<ST extends @Nullable Object> implements Buildable {

        private final ReactElement _element;

        private ProviderBuilder(final Context<ST> context) {
            _element = ReactElement.createContextElement(context._provider);
        }

        /**
         * Specify the key for the component if required.
         *
         * @param key the key for the component.
         * @return the builder.
         */
        public ProviderBuilder<ST> key(final String key) {
            _element.setKey(key);
            return this;
        }

        public ProviderBuilder<ST> value(@Nullable final ST value) {
            _element.input("value", value);
            return this;
        }

        public ReactNode children(final @Nullable Renderable @Nullable ... children) {
            _element.input("children", Renderable.toReactNodes(children));
            return build();
        }

        @Override
        public ReactNode build() {
            return _element;
        }
    }

    /**
     * Interface used to type the render function input.
     */
    @JsFunction
    @FunctionalInterface
    public interface ConsumerRenderFunction<T extends @Nullable Object> {
        /**
         * Render the specified tree for context value.
         *
         * @param value the context value.
         * @return the rendered react node tree.
         */
        @Nullable
        Renderable render(@Nullable T value);
    }

    /**
     * A Builder for the Consumer component.
     * A Consumer component is a react component that subscribes to context changes.
     */
    public static final class ConsumerBuilder<ST extends @Nullable Object> {

        private final ReactElement _element;

        private ConsumerBuilder(final Context<ST> context) {
            _element = ReactElement.createContextElement(context._consumer);
        }

        /**
         * Specify the key for component if required.
         *
         * @param key the key for the component.
         * @return the builder.
         */
        public ConsumerBuilder<ST> key(final String key) {
            _element.setKey(key);
            return this;
        }

        /**
         * Specify the child render function.
         *
         * @param render the child render function.
         * @return the creates react node.
         */
        public ReactNode render(final ConsumerRenderFunction<ST> render) {
            _element.input(
                    "children", (ConsumerRenderFunction<ST>) value -> Renderable.toReactNode(render.render(value)));
            return _element;
        }
    }
}
