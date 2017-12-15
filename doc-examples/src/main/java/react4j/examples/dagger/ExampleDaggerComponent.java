package react4j.examples.dagger;

import dagger.Component;
import javax.inject.Singleton;
import react4j.examples.dagger.gen.MyComponentDaggerFactory;

@Singleton
@Component( modules = { SomeDaggerModule.class } )
public interface ExampleDaggerComponent
  extends MyComponentDaggerFactory
{
}
