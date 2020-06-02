package react4j;

import javax.annotation.Nullable;

/**
 * The base java class that mirrors the react component.
 */
public abstract class Component
{
  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected abstract ReactNode render();
}
