package react4j.processor;

import com.google.auto.common.SuperficialValidation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import static javax.tools.Diagnostic.Kind.*;

public abstract class AbstractStandardProcessor
  extends AbstractProcessor
{
  @Nonnull
  private Set<TypeElement> _deferred = new HashSet<>();
  private int _invalidTypeCount;

  @Override
  public boolean process( @Nonnull final Set<? extends TypeElement> annotations, @Nonnull final RoundEnvironment env )
  {
    final TypeElement annotation = processingEnv.getElementUtils().getTypeElement( getRootAnnotationClassname() );
    @SuppressWarnings( "unchecked" )
    final Set<TypeElement> elements = (Set<TypeElement>) env.getElementsAnnotatedWith( annotation );

    final Map<String, String> options = processingEnv.getOptions();
    final String deferUnresolvedValue = options.get( getOptionPrefix() + ".defer.unresolved" );
    final boolean deferUnresolved = null == deferUnresolvedValue || "true".equals( deferUnresolvedValue );

    if ( deferUnresolved )
    {
      final Collection<TypeElement> elementsToProcess = getElementsToProcess( elements );
      processElements( env, elementsToProcess );
      if ( env.getRootElements().isEmpty() && !_deferred.isEmpty() )
      {
        _deferred.forEach( e -> processingErrorMessage( env, e ) );
        _deferred.clear();
      }
    }
    else
    {
      processElements( env, new ArrayList<>( elements ) );
    }
    if ( env.processingOver() )
    {
      if ( 0 != _invalidTypeCount )
      {
        processingEnv
          .getMessager()
          .printMessage( ERROR, getClass().getSimpleName() + " failed to process " + _invalidTypeCount +
                                " types. See earlier warnings for further details." );
      }
      _invalidTypeCount = 0;
    }
    return true;
  }

  @Nonnull
  protected abstract String getRootAnnotationClassname();

  @Nonnull
  protected abstract String getIssueTrackerURL();

  @Nonnull
  protected abstract String getOptionPrefix();

  private void processingErrorMessage( @Nonnull final RoundEnvironment env, @Nonnull final TypeElement target )
  {
    reportError( env,
                 getClass().getSimpleName() + " unable to process " + target.getQualifiedName() +
                 " because not all of its dependencies could be resolved. Check for " +
                 "compilation errors or a circular dependency with generated code.",
                 target );
  }

  private void reportError( @Nonnull final RoundEnvironment env,
                            @Nonnull final String message,
                            @Nullable final Element element )
  {
    final String deferErrorsValue = processingEnv.getOptions().get( getOptionPrefix() + ".defer.errors" );
    final boolean deferErrors = null == deferErrorsValue || "true".equals( deferErrorsValue );
    _invalidTypeCount++;
    if ( !deferErrors || env.errorRaised() || env.processingOver() )
    {
      processingEnv.getMessager().printMessage( ERROR, message, element );
    }
    else
    {
      processingEnv.getMessager().printMessage( MANDATORY_WARNING, message, element );
    }
  }

  private void processElements( @Nonnull final RoundEnvironment env, @Nonnull final Collection<TypeElement> elements )
  {
    for ( final TypeElement element : elements )
    {
      try
      {
        process( element );
      }
      catch ( final IOException ioe )
      {
        reportError( env, ioe.getMessage(), element );
      }
      catch ( final ProcessorException e )
      {
        final Element errorLocation = e.getElement();
        final Element outerElement = getOuterElement( errorLocation );
        if ( !env.getRootElements().contains( outerElement ) )
        {
          final String location;
          if ( errorLocation instanceof ExecutableElement )
          {
            final ExecutableElement executableElement = (ExecutableElement) errorLocation;
            final TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            location = typeElement.getQualifiedName() + "." + executableElement.getSimpleName();
          }
          else if ( errorLocation instanceof VariableElement )
          {
            final VariableElement variableElement = (VariableElement) errorLocation;
            final TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            location = typeElement.getQualifiedName() + "." + variableElement.getSimpleName();
          }
          else
          {
            assert errorLocation instanceof TypeElement;
            final TypeElement typeElement = (TypeElement) errorLocation;
            location = typeElement.getQualifiedName().toString();
          }

          final StringWriter sw = new StringWriter();
          processingEnv.getElementUtils().printElements( sw, errorLocation );
          sw.flush();

          final String message =
            "An error was generated processing the element " + element.getSimpleName() +
            " but the error was triggered by code not currently being compiled but inherited or " +
            "implemented by the element and may not be highlighted by your tooling or IDE. The " +
            "error occurred at " + location + " and may look like:\n" + sw.toString();

          reportError( env, e.getMessage(), element );
          reportError( env, message, null );
        }
        reportError( env, e.getMessage(), e.getElement() );
      }
      catch ( final Throwable e )
      {
        final StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter( sw ) );
        sw.flush();

        final String message =
          "Unexpected error running the " + getClass().getName() + " processor. This has " +
          "resulted in a failure to process the code and has left the compiler in an invalid " +
          "state. Please report the failure to the developers so that it can be fixed.\n" +
          " Report the error at: " + getIssueTrackerURL() + "\n" +
          "\n\n" +
          sw.toString();
        reportError( env, message, element );
      }
    }
  }

  @Nonnull
  private Collection<TypeElement> getElementsToProcess( @Nonnull final Collection<TypeElement> elements )
  {
    final List<TypeElement> deferred = _deferred
      .stream()
      .map( e -> processingEnv.getElementUtils().getTypeElement( e.getQualifiedName() ) )
      .collect( Collectors.toList() );
    _deferred = new HashSet<>();

    final List<TypeElement> elementsToProcess = new ArrayList<>();
    collectElementsToProcess( elements, elementsToProcess );
    collectElementsToProcess( deferred, elementsToProcess );
    return elementsToProcess;
  }

  private void collectElementsToProcess( @Nonnull final Collection<TypeElement> elements,
                                         @Nonnull final List<TypeElement> elementsToProcess )
  {
    for ( final TypeElement element : elements )
    {
      if ( SuperficialValidation.validateElement( element ) )
      {
        elementsToProcess.add( element );
      }
      else
      {
        _deferred.add( element );
      }
    }
  }

  /**
   * Return the outer enclosing element.
   * This is either the top-level class, interface, enum, etc within a package.
   * This helps identify the top level compilation units.
   */
  @Nonnull
  private Element getOuterElement( @Nonnull final Element element )
  {
    Element result = element;
    while ( !( result.getEnclosingElement() instanceof PackageElement ) )
    {
      result = result.getEnclosingElement();
    }
    return result;
  }

  protected abstract void process( @Nonnull final TypeElement element )
    throws IOException, ProcessorException;
}
