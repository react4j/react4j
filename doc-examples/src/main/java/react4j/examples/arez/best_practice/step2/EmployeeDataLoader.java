package react4j.examples.arez.best_practice.step2;

import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.Autorun;

@ArezComponent
public class EmployeeDataLoader
{
  private final RemoteServiceAPI _remote;
  private final EmployeeService _service;

  public EmployeeDataLoader( final RemoteServiceAPI remote, final EmployeeService service )
  {
    _remote = remote;
    _service = service;
  }

  @Autorun
  public void loadEmployeeDataIfRequired()
  {
    if ( _service.shouldLoadEmployeeData() )
    {
      _service.setLoading( true );
      _remote.loadEmployeeData( data -> {
        _service.setLoading( false );
        _service.setEmployeeData( data );
        _service.setLoadEmployeeData( false );
      }, errorMessage -> {
        _service.setLoading( false );
        _service.setErrorMessage( errorMessage );
        _service.setLoadEmployeeData( false );
      } );
    }
  }
}
