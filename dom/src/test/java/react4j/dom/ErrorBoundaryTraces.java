package react4j.dom;

import org.jspecify.annotations.NullMarked;

/** Expected lifecycle traces from React 16.6's MIT-licensed error boundary tests. */
@NullMarked
final class ErrorBoundaryTraces {
    private ErrorBoundaryTraces() {}

    static String expected(boolean derived, int scenario, int assertion) {
        return switch ((derived ? "D" : "L") + scenario + "." + assertion) {
            case "D10.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                RetryErrorBoundary constructor
                RetryErrorBoundary componentWillMount
                RetryErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                RetryErrorBoundary static getDerivedStateFromError [!]
                RetryErrorBoundary componentWillMount
                RetryErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                ErrorBoundary componentDidMount
                """.trim();
            case "D10.1",
                    "D11.1",
                    "D12.1",
                    "D13.1",
                    "D15.1",
                    "D17.1",
                    "D18.1",
                    "D19.1",
                    "D20.1",
                    "D21.1",
                    "D23.1",
                    "D24.1",
                    "D28.0",
                    "D29.0",
                    "D30.0",
                    "D31.1",
                    "D32.1",
                    "D33.1",
                    "D34.1",
                    "L10.1",
                    "L11.1",
                    "L12.1",
                    "L13.1",
                    "L15.1",
                    "L17.1",
                    "L18.1",
                    "L19.1",
                    "L20.1",
                    "L21.1",
                    "L23.1",
                    "L24.1",
                    "L28.0",
                    "L29.0",
                    "L30.0",
                    "L31.1",
                    "L32.1",
                    "L33.1",
                    "L34.1" -> "ErrorBoundary componentWillUnmount";
            case "D11.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentWillMountErrorBoundary constructor
                BrokenComponentWillMountErrorBoundary componentWillMount [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                ErrorBoundary componentDidMount
                """.trim();
            case "D12.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRenderErrorBoundary constructor
                BrokenRenderErrorBoundary componentWillMount
                BrokenRenderErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                BrokenRenderErrorBoundary static getDerivedStateFromError
                BrokenRenderErrorBoundary componentWillMount
                BrokenRenderErrorBoundary render error [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                ErrorBoundary componentDidMount
                """.trim();
            case "D13.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                Normal constructor
                Normal componentWillMount
                Normal render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                Normal constructor
                Normal componentWillMount
                Normal render
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                ErrorBoundary componentDidMount
                """.trim();
            case "D14.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                Error message ref is set to [object HTMLDivElement]
                ErrorBoundary componentDidMount
                """.trim();
            case "D14.1", "D22.2", "L14.1", "L22.2" -> """
                ErrorBoundary componentWillUnmount
                Error message ref is set to null
                """.trim();
            case "D15.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillMount
                ErrorBoundary render error
                ErrorBoundary componentDidMount
                """.trim();
            case "D17.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenConstructor constructor [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D18.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenComponentWillMount constructor
                BrokenComponentWillMount componentWillMount [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D19.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillReceiveProps componentWillReceiveProps [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                BrokenComponentWillReceiveProps componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D20.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillUpdate componentWillReceiveProps
                BrokenComponentWillUpdate componentWillUpdate [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                BrokenComponentWillUpdate componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D21.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D22.0", "L22.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                Child1 ref is set to [object HTMLDivElement]
                ErrorBoundary componentDidMount
                """.trim();
            case "D22.1" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Child1 ref is set to null
                Error message ref is set to [object HTMLDivElement]
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D23.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenComponentWillUnmount componentWillReceiveProps
                BrokenComponentWillUnmount componentWillUpdate
                BrokenComponentWillUnmount render
                BrokenComponentWillUnmount componentWillUnmount [!]
                Normal componentWillUnmount
                BrokenComponentWillUnmount componentDidUpdate
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                BrokenComponentWillUnmount componentWillUnmount [!]
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D24.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillUnmount componentWillReceiveProps
                BrokenComponentWillUnmount componentWillUpdate
                BrokenComponentWillUnmount render
                BrokenComponentWillUnmount componentWillUnmount [!]
                BrokenComponentWillUnmount componentDidUpdate
                Normal componentDidUpdate
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Normal componentWillUnmount
                BrokenComponentWillUnmount componentWillUnmount [!]
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D25.0" -> """
                OuterErrorBoundary componentWillReceiveProps
                OuterErrorBoundary componentWillUpdate
                OuterErrorBoundary render success
                InnerErrorBoundary componentWillReceiveProps
                InnerErrorBoundary componentWillUpdate
                InnerErrorBoundary render success
                BrokenComponentWillUnmount componentWillUnmount [!]
                InnerErrorBoundary componentDidUpdate
                OuterErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                InnerErrorBoundary componentWillUpdate
                InnerErrorBoundary render error
                InnerErrorBoundary componentDidUpdate
                """.trim();
            case "D25.1", "L25.1" -> """
                OuterErrorBoundary componentWillUnmount
                InnerErrorBoundary componentWillUnmount
                """.trim();
            case "D26.0", "L26.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal constructor
                Normal componentWillMount
                Normal render
                Normal componentDidMount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D26.1", "L26.1" -> """
                ErrorBoundary componentWillUnmount
                Normal componentWillUnmount
                """.trim();
            case "D31.0" -> """
                Stateful render [!]
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D32.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentWillUnmount constructor
                BrokenComponentWillUnmount componentWillMount
                BrokenComponentWillUnmount render
                Normal constructor
                Normal componentWillMount
                Normal render
                BrokenComponentDidMount constructor
                BrokenComponentDidMount componentWillMount
                BrokenComponentDidMount render
                LastChild constructor
                LastChild componentWillMount
                LastChild render
                Normal componentDidMount
                BrokenComponentWillUnmount componentDidMount
                BrokenComponentDidMount componentDidMount [!]
                LastChild componentDidMount
                ErrorBoundary componentDidMount
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                BrokenComponentWillUnmount componentWillUnmount [!]
                Normal componentWillUnmount
                BrokenComponentDidMount componentWillUnmount
                LastChild componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D33.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentDidUpdate componentDidUpdate [!]
                ErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                BrokenComponentDidUpdate componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D34.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentDidMountErrorBoundary constructor
                BrokenComponentDidMountErrorBoundary componentWillMount
                BrokenComponentDidMountErrorBoundary render success
                BrokenComponentDidMountErrorBoundary componentDidMount [!]
                ErrorBoundary componentDidMount
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                BrokenComponentDidMountErrorBoundary componentWillUnmount
                ErrorBoundary componentDidUpdate
                """.trim();
            case "D35.0" -> """
                OuterErrorBoundary componentWillReceiveProps
                OuterErrorBoundary componentWillUpdate
                OuterErrorBoundary render success
                InnerUnmountBoundary componentWillReceiveProps
                InnerUnmountBoundary componentWillUpdate
                InnerUnmountBoundary render success
                InnerUpdateBoundary componentWillReceiveProps
                InnerUpdateBoundary componentWillUpdate
                InnerUpdateBoundary render success
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentWillUnmount componentWillUnmount [!]
                BrokenComponentWillUnmount componentWillUnmount [!]
                InnerUnmountBoundary componentDidUpdate
                BrokenComponentDidUpdate componentDidUpdate [!]
                BrokenComponentDidUpdate componentDidUpdate [!]
                InnerUpdateBoundary componentDidUpdate
                OuterErrorBoundary componentDidUpdate
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary static getDerivedStateFromError
                InnerUnmountBoundary componentWillUpdate
                InnerUnmountBoundary render error
                ErrorBoundary static getDerivedStateFromError
                ErrorBoundary static getDerivedStateFromError
                InnerUpdateBoundary componentWillUpdate
                InnerUpdateBoundary render error
                BrokenComponentDidUpdate componentWillUnmount
                BrokenComponentDidUpdate componentWillUnmount
                InnerUnmountBoundary componentDidUpdate
                InnerUpdateBoundary componentDidUpdate
                """.trim();
            case "D35.1", "L35.1" -> """
                OuterErrorBoundary componentWillUnmount
                InnerUnmountBoundary componentWillUnmount
                InnerUpdateBoundary componentWillUnmount
                """.trim();
            case "D37.0" -> """
                NoopErrorBoundary constructor
                NoopErrorBoundary componentWillMount
                NoopErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                NoopErrorBoundary static getDerivedStateFromError
                NoopErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                """.trim();
            case "L10.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                RetryErrorBoundary constructor
                RetryErrorBoundary componentWillMount
                RetryErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                RetryErrorBoundary componentDidMount
                RetryErrorBoundary componentDidCatch [!]
                ErrorBoundary componentDidMount
                RetryErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                RetryErrorBoundary componentWillUnmount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L11.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentWillMountErrorBoundary constructor
                BrokenComponentWillMountErrorBoundary componentWillMount [!]
                ErrorBoundary componentDidMount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L12.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRenderErrorBoundary constructor
                BrokenRenderErrorBoundary componentWillMount
                BrokenRenderErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                BrokenRenderErrorBoundary componentDidMount
                BrokenRenderErrorBoundary componentDidCatch
                ErrorBoundary componentDidMount
                BrokenRenderErrorBoundary render error [!]
                BrokenRenderErrorBoundary componentWillUnmount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L13.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                Normal constructor
                Normal componentWillMount
                Normal render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                Normal constructor
                Normal componentWillMount
                Normal render
                ErrorBoundary componentDidMount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L14.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary componentDidMount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Error message ref is set to [object HTMLDivElement]
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L15.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                ErrorBoundary componentDidMount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L17.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenConstructor constructor [!]
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L18.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenComponentWillMount constructor
                BrokenComponentWillMount componentWillMount [!]
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L19.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillReceiveProps componentWillReceiveProps [!]
                Normal componentWillUnmount
                BrokenComponentWillReceiveProps componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L20.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillUpdate componentWillReceiveProps
                BrokenComponentWillUpdate componentWillUpdate [!]
                Normal componentWillUnmount
                BrokenComponentWillUpdate componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L21.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                Normal2 constructor
                Normal2 componentWillMount
                Normal2 render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                Normal componentWillUnmount
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L22.1" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                Child1 ref is set to null
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                Error message ref is set to [object HTMLDivElement]
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L23.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenComponentWillUnmount componentWillReceiveProps
                BrokenComponentWillUnmount componentWillUpdate
                BrokenComponentWillUnmount render
                BrokenComponentWillUnmount componentWillUnmount [!]
                Normal componentWillUnmount
                BrokenComponentWillUnmount componentDidUpdate
                ErrorBoundary componentDidUpdate
                BrokenComponentWillUnmount componentWillUnmount [!]
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L24.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                Normal componentWillReceiveProps
                Normal componentWillUpdate
                Normal render
                BrokenComponentWillUnmount componentWillReceiveProps
                BrokenComponentWillUnmount componentWillUpdate
                BrokenComponentWillUnmount render
                BrokenComponentWillUnmount componentWillUnmount [!]
                BrokenComponentWillUnmount componentDidUpdate
                Normal componentDidUpdate
                ErrorBoundary componentDidUpdate
                Normal componentWillUnmount
                BrokenComponentWillUnmount componentWillUnmount [!]
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L25.0" -> """
                OuterErrorBoundary componentWillReceiveProps
                OuterErrorBoundary componentWillUpdate
                OuterErrorBoundary render success
                InnerErrorBoundary componentWillReceiveProps
                InnerErrorBoundary componentWillUpdate
                InnerErrorBoundary render success
                BrokenComponentWillUnmount componentWillUnmount [!]
                InnerErrorBoundary componentDidUpdate
                OuterErrorBoundary componentDidUpdate
                InnerErrorBoundary componentDidCatch
                InnerErrorBoundary componentWillUpdate
                InnerErrorBoundary render error
                InnerErrorBoundary componentDidUpdate
                """.trim();
            case "L31.0" -> """
                Stateful render [!]
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L32.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentWillUnmount constructor
                BrokenComponentWillUnmount componentWillMount
                BrokenComponentWillUnmount render
                Normal constructor
                Normal componentWillMount
                Normal render
                BrokenComponentDidMount constructor
                BrokenComponentDidMount componentWillMount
                BrokenComponentDidMount render
                LastChild constructor
                LastChild componentWillMount
                LastChild render
                Normal componentDidMount
                BrokenComponentWillUnmount componentDidMount
                BrokenComponentDidMount componentDidMount [!]
                LastChild componentDidMount
                ErrorBoundary componentDidMount
                BrokenComponentWillUnmount componentWillUnmount [!]
                Normal componentWillUnmount
                BrokenComponentDidMount componentWillUnmount
                LastChild componentWillUnmount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary componentDidUpdate
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L33.0" -> """
                ErrorBoundary componentWillReceiveProps
                ErrorBoundary componentWillUpdate
                ErrorBoundary render success
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentDidUpdate componentDidUpdate [!]
                ErrorBoundary componentDidUpdate
                BrokenComponentDidUpdate componentWillUnmount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L34.0" -> """
                ErrorBoundary constructor
                ErrorBoundary componentWillMount
                ErrorBoundary render success
                BrokenComponentDidMountErrorBoundary constructor
                BrokenComponentDidMountErrorBoundary componentWillMount
                BrokenComponentDidMountErrorBoundary render success
                BrokenComponentDidMountErrorBoundary componentDidMount [!]
                ErrorBoundary componentDidMount
                BrokenComponentDidMountErrorBoundary componentWillUnmount
                ErrorBoundary componentDidCatch
                ErrorBoundary componentWillUpdate
                ErrorBoundary render error
                ErrorBoundary componentDidUpdate
                """.trim();
            case "L35.0" -> """
                OuterErrorBoundary componentWillReceiveProps
                OuterErrorBoundary componentWillUpdate
                OuterErrorBoundary render success
                InnerUnmountBoundary componentWillReceiveProps
                InnerUnmountBoundary componentWillUpdate
                InnerUnmountBoundary render success
                InnerUpdateBoundary componentWillReceiveProps
                InnerUpdateBoundary componentWillUpdate
                InnerUpdateBoundary render success
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentDidUpdate componentWillReceiveProps
                BrokenComponentDidUpdate componentWillUpdate
                BrokenComponentDidUpdate render
                BrokenComponentWillUnmount componentWillUnmount [!]
                BrokenComponentWillUnmount componentWillUnmount [!]
                InnerUnmountBoundary componentDidUpdate
                BrokenComponentDidUpdate componentDidUpdate [!]
                BrokenComponentDidUpdate componentDidUpdate [!]
                InnerUpdateBoundary componentDidUpdate
                OuterErrorBoundary componentDidUpdate
                BrokenComponentDidUpdate componentWillUnmount
                BrokenComponentDidUpdate componentWillUnmount
                InnerUnmountBoundary componentDidCatch
                InnerUnmountBoundary componentDidCatch
                InnerUpdateBoundary componentDidCatch
                InnerUpdateBoundary componentDidCatch
                InnerUnmountBoundary componentWillUpdate
                InnerUnmountBoundary render error
                InnerUpdateBoundary componentWillUpdate
                InnerUpdateBoundary render error
                InnerUnmountBoundary componentDidUpdate
                InnerUpdateBoundary componentDidUpdate
                """.trim();
            case "L37.0" -> """
                NoopErrorBoundary constructor
                NoopErrorBoundary componentWillMount
                NoopErrorBoundary render
                BrokenRender constructor
                BrokenRender componentWillMount
                BrokenRender render [!]
                NoopErrorBoundary componentDidMount
                NoopErrorBoundary componentDidCatch
                """.trim();
            case "L37.1" -> "NoopErrorBoundary componentWillUnmount";
            default -> throw new AssertionError("Missing trace " + scenario + "." + assertion);
        };
    }
}
