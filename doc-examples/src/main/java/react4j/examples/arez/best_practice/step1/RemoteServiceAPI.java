package react4j.examples.arez.best_practice.step1;

import java.util.function.Consumer;

public interface RemoteServiceAPI
{
  void loadEmployeeData( Consumer<String> onSuccess, Consumer<String> onError );
}
