package react4j.examples.dagger.gen;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import javax.inject.Provider;
import react4j.Component;
import react4j.examples.dagger.MyComponent;
import react4j.examples.dagger.MyComponent_;

public interface MyComponentDaggerFactory
{
  DaggerSubcomponent getMyComponentDaggerSubcomponent();

  default void bindMyComponent()
  {
    MyComponent_.setProvider( () -> getMyComponentDaggerSubcomponent().get() );
  }

  @Module
  final class DaggerModule
  {
    @Provides
    static Component provideComponent( final MyComponent_ component )
    {
      return component;
    }
  }

  @Subcomponent( modules = DaggerModule.class )
  interface DaggerSubcomponent
  {
    Provider<Component> createProvider();

    default MyComponent get()
    {
      return (MyComponent) createProvider().get();
    }
  }
}
