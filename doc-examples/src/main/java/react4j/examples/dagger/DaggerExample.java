package react4j.examples.dagger;

public class DaggerExample
{
  public static void main( String[] args )
  {
    final ExampleDaggerComponent component = DaggerExampleDaggerComponent.create();
    component.bindMyComponent();
  }
}
