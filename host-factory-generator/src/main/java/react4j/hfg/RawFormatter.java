package react4j.hfg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

final class RawFormatter
  extends Formatter
{
  @Override
  public String format( final LogRecord logRecord )
  {
    final String output = logRecord.getMessage() + "\n";
    final Throwable throwable = logRecord.getThrown();
    if ( null != throwable )
    {
      final StringWriter sw = new StringWriter();
      final PrintWriter writer = new PrintWriter( sw );
      throwable.printStackTrace( writer );
      writer.close();
      return output + sw;
    }
    else
    {
      return output;
    }
  }
}
