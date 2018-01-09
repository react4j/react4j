package react4j.examples.arez.best_practice.step2;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Observable;

@ArezComponent
public class EmployeeService
{
  private boolean _loading;
  private String _employeeData;
  private String _errorMessage;
  private boolean _loadEmployeeData;

  @Observable( name = "loadEmployeeData" )
  public boolean shouldLoadEmployeeData()
  {
    return _loadEmployeeData;
  }

  public void setLoadEmployeeData( final boolean loadEmployeeData )
  {
    _loadEmployeeData = loadEmployeeData;
  }

  @Action
  public void changeToEmployeeView()
  {
    setLoadEmployeeData( true );
  }

  //EXAMPLE ENDS HERE
  @Observable
  public boolean isLoading()
  {
    return _loading;
  }

  public void setLoading( final boolean loading )
  {
    _loading = loading;
  }

  @Observable
  public String getEmployeeData()
  {
    return _employeeData;
  }

  public void setEmployeeData( final String employeeData )
  {
    _employeeData = employeeData;
  }

  @Observable
  public String getErrorMessage()
  {
    return _errorMessage;
  }

  public void setErrorMessage( final String errorMessage )
  {
    _errorMessage = errorMessage;
  }
}
