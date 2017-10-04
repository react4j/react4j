---
title: Frequently Asked Questions
category: General
order: 1
toc: true
---

---

#### Design

##### Where is the equivalent of React's stateless function components?

The project originally supported stateless functional components defined by an interface that had
a single render method. You could define the rendering of the method using a simple lambda method
that accepted a prop. This resulted in code that looked like: 

```java
class MyComponent {
  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  static class Props extends BaseProps {
    int param;
  }

  @ReactComponent
  public static final StatelessComponent<Props> COMPONENT = (props) -> {
    return span(new HtmlProps().className("myclass"), String.valueOf( props.param ));
  };
}
```

Contrast this with the code that used normal components:

```java
@ReactComponent
class MyComponent extends Component<MyComponent.Props, BaseState> {
  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  static class Props extends BaseProps {
    int param;
  }

  public ReactElement<?, ?> render() {
    return span(new HtmlProps().className("myclass"), String.valueOf( props.param ));
  };
}
```

In java, the StatelessComponent approach seemed to offer few advantages as the two approaches were
roughly equal in the amount of ceremony required and identical from a performance standpoint. However the
StatelessComponent approach required more work if you needed to refactor the components later to add state,
or use the lifecycle callback methods.

So we removed StatelessComponent and simplified our application, the library and supporting tools. (The
annotation processor is significantly simpler as a result of this change.)
