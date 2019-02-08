package react4j.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes React annotated source and generates models from the annotations.
 */
@SuppressWarnings( "Duplicates" )
@AutoService( Processor.class )
@SupportedAnnotationTypes( { "react4j.annotations.*" } )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public final class ReactProcessor
  extends AbstractProcessor
{
  /**
   * Cache of render method method as defined by Component.
   */
  private ExecutableElement _componentRenderMethod;
  /**
   * Elements that were unresolved and have been deferred to a later compilation cycle.
   */
  @Nonnull
  private HashSet<TypeElement> _deferred = new HashSet<>();
  private int _invalidTypeCount;
  private RoundEnvironment _env;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear method caches to avoid potential inter-run problems
    _componentRenderMethod = null;

    _env = env;

    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME );
    final Set<? extends Element> elements = env.getElementsAnnotatedWith( annotation );
    final Collection<Element> elementsToProcess = getElementsToProcess( elements );
    processElements( elementsToProcess );
    if ( env.getRootElements().isEmpty() && !_deferred.isEmpty() )
    {
      _deferred.forEach( this::processingErrorMessage );
      _deferred.clear();
    }
    if ( _env.processingOver() )
    {
      if ( 0 != _invalidTypeCount )
      {
        processingEnv
          .getMessager()
          .printMessage( ERROR, "ReactProcessor failed to process " + _invalidTypeCount +
                                " types. See earlier warnings for further details." );
      }
      _invalidTypeCount = 0;
    }
    _env = null;
    return true;
  }

  private void processingErrorMessage( @Nonnull final TypeElement target )
  {
    reportError( "ReactProcessor unable to process " + target.getQualifiedName() +
                 " because not all of its dependencies could be resolved. Check for " +
                 "compilation errors or a circular dependency with generated code.",
                 target );
  }

  private void reportError( @Nonnull final String message, @Nullable final Element element )
  {
    _invalidTypeCount++;
    if ( _env.errorRaised() || _env.processingOver() )
    {
      processingEnv.getMessager().printMessage( ERROR, message, element );
    }
    else
    {
      processingEnv.getMessager().printMessage( MANDATORY_WARNING, message, element );
    }
  }

  @Nonnull
  private Collection<Element> getElementsToProcess( @Nonnull final Set<? extends Element> elements )
  {
    final List<TypeElement> deferred = _deferred
      .stream()
      .map( e -> processingEnv.getElementUtils().getTypeElement( e.getQualifiedName() ) )
      .collect( Collectors.toList() );
    _deferred = new HashSet<>();

    final ArrayList<Element> elementsToProcess = new ArrayList<>();
    collectElementsToProcess( elements, elementsToProcess );
    collectElementsToProcess( deferred, elementsToProcess );
    return elementsToProcess;
  }

  private void collectElementsToProcess( @Nonnull final Collection<? extends Element> elements,
                                         @Nonnull final ArrayList<Element> elementsToProcess )
  {
    for ( final Element element : elements )
    {
      if ( SuperficialValidation.validateElement( element ) )
      {
        elementsToProcess.add( element );
      }
      else
      {
        _deferred.add( (TypeElement) element );
      }
    }
  }

  private void processElements( @Nonnull final Collection<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      try
      {
        process( (TypeElement) element );
      }
      catch ( final IOException ioe )
      {
        reportError( ioe.getMessage(), element );
      }
      catch ( final ReactProcessorException e )
      {
        final Element errorLocation = e.getElement();
        final Element outerElement = getOuterElement( errorLocation );
        if ( !_env.getRootElements().contains( outerElement ) )
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

          reportError( e.getMessage(), element );
          reportError( message, null );
        }
        reportError( e.getMessage(), e.getElement() );
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
          " Report the error at: https://github.com/react4j/react4j/issues\n" +
          "\n\n" +
          sw.toString();
        reportError( message, element );
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

  private void process( @Nonnull final TypeElement element )
    throws IOException, ReactProcessorException
  {
    final ComponentDescriptor descriptor = parse( element );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildEnhancedComponent( descriptor ) );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentBuilder( descriptor ) );
    if ( descriptor.needsInjection() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildDaggerComponentExtension( descriptor ) );
    }
  }

  private void emitTypeSpec( @Nonnull final String packageName, @Nonnull final TypeSpec typeSpec )
    throws IOException
  {
    JavaFile.builder( packageName, typeSpec ).
      skipJavaLangImports( true ).
      build().
      writeTo( processingEnv.getFiler() );
  }

  /**
   * Return true if there is any injection points that are not through the constructor.
   */
  private boolean nonConstructorInjections( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
             .stream()
             .anyMatch( e -> ProcessorUtil.hasAnnotationOfType( e, Constants.INJECT_ANNOTATION_CLASSNAME ) ) ||
           ProcessorUtil.getFieldElements( typeElement )
             .stream()
             .anyMatch( e -> ProcessorUtil.hasAnnotationOfType( e, Constants.INJECT_ANNOTATION_CLASSNAME ) );
  }

  @Nonnull
  private ComponentDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final String name = deriveComponentName( typeElement );
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentType type = extractComponentType( typeElement );
    final boolean nonConstructorInjections = nonConstructorInjections( typeElement );
    final ComponentDescriptor descriptor =
      new ComponentDescriptor( processingEnv.getElementUtils(),
                               processingEnv.getSourceVersion(),
                               name,
                               packageElement,
                               typeElement,
                               type,
                               nonConstructorInjections );

    determineComponentCapabilities( descriptor, typeElement );
    determineRenderMethod( typeElement, descriptor );
    determineProps( descriptor );
    determinePropValidatesMethods( descriptor );
    determineOnPropChangeMethods( descriptor );
    determineDefaultPropsMethods( descriptor );
    determineDefaultPropsFields( descriptor );
    determinePreUpdateMethod( typeElement, descriptor );
    determinePostRenderMethod( typeElement, descriptor );
    determinePostUpdateMethod( typeElement, descriptor );
    determinePostMountMethod( typeElement, descriptor );
    determineOnErrorMethod( typeElement, descriptor );

    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      if ( !isPropRequired( prop ) )
      {
        prop.markAsOptional();
      }
    }

    /*
     * Sorting must occur after @PropDefault has been processed to ensure the sorting
     * correctly sorts optional props after required props.
     */
    descriptor.sortProps();

    verifyPropsNotAnnotatedWithArezAnnotations( descriptor );
    verifyPropsNotCollectionOfArezComponents( descriptor );

    return descriptor;
  }

  private void verifyPropsNotCollectionOfArezComponents( @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final ExecutableElement method = prop.getMethod();
      final TypeMirror returnType = method.getReturnType();
      if ( TypeKind.DECLARED == returnType.getKind() )
      {
        final DeclaredType declaredType = (DeclaredType) returnType;
        final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if ( isCollection( declaredType ) )
        {
          if ( 1 == typeArguments.size() && isArezComponent( typeArguments.get( 0 ) ) )
          {
            throw new ReactProcessorException( "@Prop target is a collection that contains Arez components. " +
                                               "This is not a safe pattern when the arez components can be disposed.",
                                               method );
          }
        }
        else if ( isMap( declaredType ) )
        {
          if ( 2 == typeArguments.size() &&
               ( isArezComponent( typeArguments.get( 0 ) ) ||
                 isArezComponent( typeArguments.get( 1 ) ) ) )
          {
            throw new ReactProcessorException( "@Prop target is a collection that contains Arez components. " +
                                               "This is not a safe pattern when the arez components can be disposed.",
                                               method );
          }
        }
      }
      else if ( TypeKind.ARRAY == returnType.getKind() )
      {
        final ArrayType arrayType = (ArrayType) returnType;
        if ( isArezComponent( arrayType.getComponentType() ) )
        {
          throw new ReactProcessorException( "@Prop target is an array that contains Arez components. " +
                                             "This is not a safe pattern when the arez components can be disposed.",
                                             method );
        }
      }
    }
  }

  private boolean isCollection( @Nonnull final DeclaredType declaredType )
  {
    final TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement( declaredType );
    final String classname = returnType.getQualifiedName().toString();
    /*
     * For the time being lets just list out a bunch of collections. We can ge more specific when/if
     * it is ever required
     */
    return Collection.class.getName().equals( classname ) ||
           Set.class.getName().equals( classname ) ||
           List.class.getName().equals( classname ) ||
           HashSet.class.getName().equals( classname ) ||
           ArrayList.class.getName().equals( classname );
  }

  private boolean isMap( @Nonnull final DeclaredType declaredType )
  {
    final TypeElement returnType = (TypeElement) processingEnv.getTypeUtils().asElement( declaredType );
    final String classname = returnType.getQualifiedName().toString();
    /*
     * For the time being lets just list out a bunch of collections. We can ge more specific when/if
     * it is ever required
     */
    return Map.class.getName().equals( classname ) || HashMap.class.getName().equals( classname );
  }

  private boolean isArezComponent( @Nonnull final TypeMirror typeMirror )
  {
    return typeMirror instanceof DeclaredType &&
           processingEnv.getTypeUtils()
             .asElement( typeMirror )
             .getAnnotationMirrors()
             .stream()
             .anyMatch( a -> a.getAnnotationType().toString().equals( Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) );
  }

  private void verifyPropsNotAnnotatedWithArezAnnotations( @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final PropDescriptor prop : descriptor.getProps() )
    {
      final ExecutableElement method = prop.getMethod();
      for ( final AnnotationMirror mirror : method.getAnnotationMirrors() )
      {
        final String classname = mirror.getAnnotationType().toString();
        if ( isArezAnnotation( classname ) )
        {
          throw new ReactProcessorException( "@Prop target must not be annotated with any arez annotations but " +
                                             "is annotated by '" + classname + "'.", method );
        }
      }
    }
  }

  private void determineOnPropChangeMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    final ArrayList<OnPropChangeDescriptor> onPropChangeDescriptors = new ArrayList<>();
    for ( final ExecutableElement method : methods )
    {
      final VariableElement phase = (VariableElement)
        ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                          method,
                                          Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME,
                                          "phase" ).getValue();
      final boolean preUpdate = phase.getSimpleName().toString().equals( "PRE" );

      final List<? extends VariableElement> parameters = method.getParameters();
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      final List<? extends TypeMirror> parameterTypes = methodType.getParameterTypes();

      MethodChecks.mustBeSubclassCallable( descriptor.getElement(),
                                           Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME,
                                           method );
      MethodChecks.mustNotThrowAnyExceptions( Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, method );
      MethodChecks.mustNotReturnAValue( Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, method );
      MethodChecks.mustNotBePublic( Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME, method );

      final int parameterCount = parameters.size();
      if ( 0 == parameterCount )
      {
        throw new ReactProcessorException( "@OnPropChange target must have at least 1 parameter.", method );
      }
      final ArrayList<PropDescriptor> propDescriptors = new ArrayList<>( parameterCount );
      for ( int i = 0; i < parameterCount; i++ )
      {
        final VariableElement parameter = parameters.get( i );
        final String name = deriveOnPropChangeName( descriptor, parameter );
        final PropDescriptor prop = descriptor.findPropNamed( name );
        if ( null == prop )
        {
          throw new ReactProcessorException( "@OnPropChange target has a parameter named '" +
                                             parameter.getSimpleName() + "' and the parameter is associated with a " +
                                             "@Prop named '" + name + "' but there is no corresponding @Prop " +
                                             "annotated method.", parameter );
        }
        final Types typeUtils = processingEnv.getTypeUtils();
        if ( !typeUtils.isAssignable( parameterTypes.get( i ), prop.getMethodType().getReturnType() ) )
        {
          throw new ReactProcessorException( "@OnPropChange target has a parameter named '" +
                                             parameter.getSimpleName() + "' and the parameter type is not " +
                                             "assignable to the return type of the associated @Prop annotated method.",
                                             method );
        }
        final boolean mismatchedNullability =
          (
            ProcessorUtil.hasAnnotationOfType( parameter, Constants.NONNULL_ANNOTATION_CLASSNAME ) &&
            ProcessorUtil.hasAnnotationOfType( prop.getMethod(), Constants.NULLABLE_ANNOTATION_CLASSNAME )
          ) ||
          (
            ProcessorUtil.hasAnnotationOfType( parameter, Constants.NULLABLE_ANNOTATION_CLASSNAME ) &&
            ProcessorUtil.hasAnnotationOfType( prop.getMethod(), Constants.NONNULL_ANNOTATION_CLASSNAME ) );

        if ( mismatchedNullability )
        {
          throw new ReactProcessorException( "@OnPropChange target has a parameter named '" +
                                             parameter.getSimpleName() + "' that has a nullability annotation " +
                                             "incompatible with the associated @Prop method named " +
                                             method.getSimpleName(), method );
        }
        if ( prop.isImmutable() )
        {
          throw new ReactProcessorException( "@OnPropChange target has a parameter named '" +
                                             parameter.getSimpleName() + "' that is associated with a @Prop " +
                                             "annotated method and the prop is specified as immutable.", method );
        }
        propDescriptors.add( prop );
      }
      onPropChangeDescriptors.add( new OnPropChangeDescriptor( method, propDescriptors, preUpdate ) );
    }
    descriptor.setOnPropChangeDescriptors( onPropChangeDescriptors );
  }

  @Nonnull
  private String deriveOnPropChangeName( @Nonnull final ComponentDescriptor descriptor,
                                         @Nonnull final VariableElement parameter )
  {
    final AnnotationValue value =
      ProcessorUtil.findAnnotationValue( descriptor.getElements(),
                                         parameter,
                                         Constants.PROP_REF_ANNOTATION_CLASSNAME,
                                         "value" );

    if ( null != value )
    {
      return (String) value.getValue();
    }
    else
    {
      final String parameterName = parameter.getSimpleName().toString();
      if ( ProcessorUtil.LAST_PROP_PATTERN.matcher( parameterName ).matches() ||
           ProcessorUtil.PREV_PROP_PATTERN.matcher( parameterName ).matches() )
      {
        return Character.toLowerCase( parameterName.charAt( 4 ) ) + parameterName.substring( 5 );
      }
      else if ( ProcessorUtil.PROP_PATTERN.matcher( parameterName ).matches() )
      {
        return parameterName;
      }
      else
      {
        throw new ReactProcessorException( "@OnPropChange target has a parameter named '" + parameterName +
                                           "' is not explicitly associated with a prop using @PropRef nor does it " +
                                           "follow required naming conventions 'prev[MyProp]', 'last[MyProp]' or " +
                                           "'[myProp]'.", parameter );
      }
    }
  }

  private void determinePropValidatesMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : methods )
    {
      final String name = derivePropValidateName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropValidate target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", method );
      }
      if ( 1 != method.getParameters().size() )
      {
        throw new ReactProcessorException( "@PropValidate target must have exactly 1 parameter", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getParameterTypes().get( 0 ),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropValidate target has a parameter type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", method );
      }
      prop.setValidateMethod( method );
    }
  }

  @Nonnull
  private String derivePropValidateName( @Nonnull final Element element )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 element,
                                                 Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      final String deriveName = ProcessorUtil.deriveName( element, ProcessorUtil.VALIDATE_PROP_PATTERN, name );
      if ( null == deriveName )
      {
        throw new ReactProcessorException( "@PropValidate target has not specified name nor is it named according " +
                                           "to the convention 'validate[Name]Prop'.", element );
      }
      return deriveName;
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@PropValidate target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  private void determineDefaultPropsMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> defaultPropsMethods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final ExecutableElement method : defaultPropsMethods )
    {
      final String name = derivePropDefaultName( method );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", method );
      }
      final ExecutableType methodType = resolveMethodType( descriptor, method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropDefault target has a return type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", method );
      }
      MethodChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     method );
      prop.setDefaultMethod( method );
    }
  }

  private void determineDefaultPropsFields( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<VariableElement> defaultPropsFields =
      ProcessorUtil.getFieldElements( descriptor.getElement() ).stream()
        .filter( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    for ( final VariableElement field : defaultPropsFields )
    {
      final String name = derivePropDefaultName( field );
      final PropDescriptor prop = descriptor.findPropNamed( name );
      if ( null == prop )
      {
        throw new ReactProcessorException( "@PropDefault target for prop named '" + name + "' has no corresponding " +
                                           "@Prop annotated method.", field );
      }
      if ( !processingEnv.getTypeUtils().isAssignable( field.asType(), prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropDefault target has a type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", field );
      }
      MethodChecks.mustBeStaticallySubclassCallable( descriptor.getElement(),
                                                     Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                     field );
      prop.setDefaultField( field );
    }
  }

  @Nonnull
  private String derivePropDefaultName( @Nonnull final Element element )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 element,
                                                 Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      if ( element instanceof ExecutableElement )
      {
        final String deriveName = ProcessorUtil.deriveName( element, ProcessorUtil.DEFAULT_GETTER_PATTERN, name );
        if ( null == deriveName )
        {
          throw new ReactProcessorException( "@PropDefault target has not specified name nor is it named according " +
                                             "to the convention 'get[Name]Default'.", element );
        }
        return deriveName;
      }
      else
      {
        final String fieldName = element.getSimpleName().toString();
        boolean matched = true;
        final int lengthPrefix = "DEFAULT_".length();
        final int length = fieldName.length();
        if ( fieldName.startsWith( "DEFAULT_" ) && length > lengthPrefix )
        {
          for ( int i = lengthPrefix; i < length; i++ )
          {
            final char ch = fieldName.charAt( i );
            if ( Character.isLowerCase( ch ) ||
                 (
                   ( i != lengthPrefix || !Character.isJavaIdentifierStart( ch ) ) &&
                   ( i == lengthPrefix || !Character.isJavaIdentifierPart( ch ) )
                 ) )
            {
              matched = false;
              break;
            }
          }
        }
        else
        {
          matched = false;
        }
        if ( matched )
        {
          return uppercaseConstantToPascalCase( fieldName.substring( lengthPrefix ) );
        }
        else
        {
          throw new ReactProcessorException( "@PropDefault target has not specified name nor is it named according " +
                                             "to the convention 'DEFAULT_[NAME]'.", element );
        }
      }
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", element );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@PropDefault target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", element );
      }
      return name;
    }
  }

  @Nonnull
  private String uppercaseConstantToPascalCase( @Nonnull final String candidate )
  {
    final String s = candidate.toLowerCase();
    final StringBuilder sb = new StringBuilder();
    boolean uppercase = false;
    for ( int i = 0; i < s.length(); i++ )
    {
      final char ch = s.charAt( i );
      if ( '_' == ch )
      {
        uppercase = true;
      }
      else if ( uppercase )
      {
        sb.append( Character.toUpperCase( ch ) );
        uppercase = false;
      }
      else
      {
        sb.append( ch );
      }
    }
    return sb.toString();
  }

  private void determineProps( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.PROP_ANNOTATION_CLASSNAME ) )
        .map( m -> createPropDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );

    final PropDescriptor childrenProp =
      props.stream().filter( p -> p.getName().equals( "children" ) ).findAny().orElse( null );
    final PropDescriptor childProp =
      props.stream().filter( p -> p.getName().equals( "child" ) ).findAny().orElse( null );
    if ( null != childrenProp && null != childProp )
    {
      throw new ReactProcessorException( "Multiple candidate children @Prop annotated methods: " +
                                         childrenProp.getMethod().getSimpleName() + " and " +
                                         childProp.getMethod().getSimpleName(),
                                         childrenProp.getMethod() );
    }

    descriptor.setProps( props );
  }

  private boolean isPropRequired( @Nonnull final PropDescriptor prop )
  {
    final VariableElement parameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        prop.getMethod(),
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "require" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return !prop.hasDefaultMethod() &&
               !prop.hasDefaultField() &&
               !ProcessorUtil.hasAnnotationOfType( prop.getMethod(), Constants.NULLABLE_ANNOTATION_CLASSNAME );
    }
  }

  @Nonnull
  private PropDescriptor createPropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final ExecutableElement method )
  {
    final String name = derivePropName( method );
    final ExecutableType methodType = resolveMethodType( descriptor, method );

    verifyNoDuplicateAnnotations( method );
    MethodChecks.mustBeAbstract( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePublic( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotHaveAnyParameters( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustReturnAValue( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.PROP_ANNOTATION_CLASSNAME,
                                                           method );
    final TypeMirror returnType = method.getReturnType();
    if ( "key".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'key' is invalid as the name references value used in the " +
                                         "reconciliation process.", method );
    }
    else if ( "build".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'build' is invalid as it conflicts with the method named " +
                                         "build() that is used in the generated Builder classes",
                                         method );
    }
    else if ( "child".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode".equals( returnType.toString() ) ) )
    {
      throw new ReactProcessorException( "@Prop named 'child' should be of type react4j.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              ( returnType.getKind() != TypeKind.DECLARED && !"react4j.ReactNode[]".equals( returnType.toString() ) ) )
    {
      throw new ReactProcessorException( "@Prop named 'children' should be of type react4j.ReactNode[]", method );
    }

    if ( returnType instanceof TypeVariable )
    {
      final TypeVariable typeVariable = (TypeVariable) returnType;
      final String typeVariableName = typeVariable.asElement().getSimpleName().toString();
      List<? extends TypeParameterElement> typeParameters = method.getTypeParameters();
      if ( typeParameters.stream().anyMatch( p -> p.getSimpleName().toString().equals( typeVariableName ) ) )
      {
        throw new ReactProcessorException( "@Prop named '" + name + "' is has a type variable as a return type " +
                                           "that is declared on the method.", method );
      }
    }

    final Element propType = processingEnv.getTypeUtils().asElement( returnType );
    final boolean immutable = isPropImmutable( method );
    final boolean shouldUpdateOnChange = shouldUpdateOnChange( method, immutable );
    final boolean observable = isPropObservable( descriptor, method, shouldUpdateOnChange, immutable );
    final boolean disposable = null != propType && isPropDisposable( method, propType );
    final TypeName typeName = TypeName.get( returnType );
    if ( typeName.isBoxedPrimitive() &&
         ProcessorUtil.hasAnnotationOfType( method, Constants.NONNULL_ANNOTATION_CLASSNAME ) )
    {
      throw new ReactProcessorException( "@Prop named '" + name + "' is a boxed primitive annotated with a " +
                                         "@Nonnull annotation. The return type should be the primitive type.",
                                         method );
    }
    final ImmutablePropKeyStrategy strategy = immutable ? getImmutablePropKeyStrategy( typeName, propType ) : null;
    if ( immutable && null == strategy )
    {
      throw new ReactProcessorException( "@Prop named '" + name + "' has specified the 'immutable' parameter as " +
                                         "true but the annotation processor can not extract a key part from the " +
                                         "type. This is because the type is not recognized as conforming to the " +
                                         "rules as documented in the javadocs for the immutable parameter of " +
                                         "the @Prop annotation.",
                                         method );
    }
    return new PropDescriptor( descriptor,
                               name,
                               method,
                               methodType,
                               shouldUpdateOnChange,
                               observable,
                               disposable,
                               strategy );
  }

  @Nullable
  private ImmutablePropKeyStrategy getImmutablePropKeyStrategy( @Nonnull final TypeName typeName,
                                                                @Nullable final Element element )
  {
    if ( typeName.toString().equals( "java.lang.String" ) )
    {
      return ImmutablePropKeyStrategy.IS_STRING;
    }
    else if ( typeName.isBoxedPrimitive() || typeName.isPrimitive() )
    {
      return ImmutablePropKeyStrategy.TO_STRING;
    }
    else if ( null != element )
    {
      if ( ElementKind.CLASS == element.getKind() &&
           ProcessorUtil.hasAnnotationOfType( element, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME ) &&
           isIdRequired( (TypeElement) element ) )
      {
        return ImmutablePropKeyStrategy.AREZ_IDENTIFIABLE;
      }
      else if ( ElementKind.CLASS == element.getKind() || ElementKind.INTERFACE == element.getKind() )
      {
        final TypeElement keyedType = processingEnv.getElementUtils().getTypeElement( Constants.KEYED_CLASSNAME );
        if ( processingEnv.getTypeUtils().isAssignable( element.asType(), keyedType.asType() ) )
        {
          return ImmutablePropKeyStrategy.KEYED;
        }
      }
      else if ( ElementKind.ENUM == element.getKind() )
      {
        return ImmutablePropKeyStrategy.ENUM;
      }
    }
    return null;
  }

  /**
   * The logic from this method has been cloned from Arez.
   * One day we should consider improving Arez so that this is not required somehow?
   */
  private boolean isIdRequired( @Nonnull final TypeElement element )
  {
    final VariableElement requireIdParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        element,
                                        Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME,
                                        "requireId" ).getValue();
    switch ( requireIdParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        if ( ProcessorUtil.hasAnnotationOfType( element, Constants.REPOSITORY_ANNOTATION_CLASSNAME ) )
        {
          return true;
        }
        else
        {
          return ProcessorUtil
            .getMethods( element, processingEnv.getTypeUtils() )
            .stream()
            .anyMatch( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.COMPONENT_ID_ANNOTATION_CLASSNAME ) ||
                            ProcessorUtil.hasAnnotationOfType( m, Constants.COMPONENT_ID_REF_ANNOTATION_CLASSNAME ) );
        }
    }
  }

  @Nonnull
  private String derivePropName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String specifiedName =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.PROP_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    final String name = ProcessorUtil.getPropertyAccessorName( method, specifiedName );
    if ( !SourceVersion.isIdentifier( name ) )
    {
      throw new ReactProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                         "name must be a valid java identifier.", method );
    }
    else if ( SourceVersion.isKeyword( name ) )
    {
      throw new ReactProcessorException( "@Prop target specified an invalid name '" + specifiedName + "'. The " +
                                         "name must not be a java keyword.", method );
    }
    else
    {
      return name;
    }
  }

  private void determinePreUpdateMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( ProcessorUtil.hasAnnotationOfType( method, Constants.PRE_UPDATE_ANNOTATION_CLASSNAME ) )
      {
        descriptor.setPreUpdate( method );
      }
    }
  }

  private void determineOnErrorMethod( @Nonnull final TypeElement typeElement,
                                       @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( ProcessorUtil.hasAnnotationOfType( method, Constants.ON_ERROR_ANNOTATION_CLASSNAME ) )
      {
        descriptor.setOnError( method );
      }
    }
  }

  private void determinePostRenderMethod( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( ProcessorUtil.hasAnnotationOfType( method, Constants.POST_RENDER_ANNOTATION_CLASSNAME ) )
      {
        descriptor.setPostRender( method );
      }
    }
  }

  private void determinePostUpdateMethod( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( ProcessorUtil.hasAnnotationOfType( method, Constants.POST_UPDATE_ANNOTATION_CLASSNAME ) )
      {
        descriptor.setPostUpdate( method );
      }
    }
  }

  private void determinePostMountMethod( @Nonnull final TypeElement typeElement,
                                         @Nonnull final ComponentDescriptor descriptor )
  {
    for ( final ExecutableElement method : getMethods( typeElement ) )
    {
      if ( ProcessorUtil.hasAnnotationOfType( method, Constants.POST_MOUNT_ANNOTATION_CLASSNAME ) )
      {
        descriptor.setPostMount( method );
      }
    }
  }

  private ExecutableType resolveMethodType( @Nonnull final ComponentDescriptor descriptor,
                                            @Nonnull final ExecutableElement method )
  {
    return (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
  }

  private void determineRenderMethod( @Nonnull final TypeElement typeElement,
                                      @Nonnull final ComponentDescriptor descriptor )
  {
    /*
     * Get the render method that has been overridden by the typeElement, a parent class, or by a default
     * method method implemented by the typeElement or a parent class.
     */
    final ExecutableElement renderMethod = getComponentRenderMethod();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
    final MethodDescriptor overriddenRenderMethod =
      // Get all methods on type parent classes, and default methods from interfaces
      getMethods( typeElement ).stream()
        // Only keep method if they override the render method
        .filter( m -> elementUtils.overrides( m, renderMethod, typeElement ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .findAny().
        orElse( null );

    if ( null == overriddenRenderMethod )
    {
      throw new ReactProcessorException( "The react component does not override the render method.", typeElement );
    }
  }

  @Nonnull
  private String deriveComponentName( @Nonnull final TypeElement typeElement )
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 typeElement,
                                                 Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return typeElement.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", typeElement );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@ReactComponent target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", typeElement );
      }
      return name;
    }
  }

  /**
   * Return the set of render methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private ExecutableElement getComponentRenderMethod()
  {
    if ( null == _componentRenderMethod )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : getMethods( componentType ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( "render".equals( methodName ) )
        {
          _componentRenderMethod = method;
          break;
        }
      }
    }
    return _componentRenderMethod;
  }

  private void determineComponentCapabilities( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    final TypeMirror declaredType = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( declaredType, rawComponentType );

    if ( !isComponent )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a subclass of react4j.Component",
                                         typeElement );
    }
    else
    {
      final AnnotationMirror arezAnnotation = typeElement.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null != arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target should not be annotated with the " +
                                           "arez.annotations.ArezComponent as React4j will add the annotation.",
                                           typeElement );
      }
    }

    if ( descriptor.needsInjection() && !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      throw new ReactProcessorException( "@ReactComponent target has enabled injection integration but the class " +
                                         "has type arguments which is incompatible with injection integration.",
                                         typeElement );
    }

    final boolean hasArezElements =
      descriptor.trackRender() ||
      getMethods( typeElement ).stream().anyMatch( e -> e.getAnnotationMirrors()
        .stream()
        .map( a -> a.getAnnotationType().toString() )
        .anyMatch( n -> n.startsWith( "arez.annotations." ) &&
                        !(
                          // Ignore these annotations as they do not create disposable elements
                          n.endsWith( "PostConstruct" ) ||
                          n.endsWith( "ContextRef" ) ||
                          n.endsWith( "ComponentTypeNameRef" ) ||
                          n.endsWith( "ComponentNameRef" ) ||
                          n.endsWith( "ComponentIdRef" ) ||
                          n.endsWith( "ComponentId" ) ||
                          n.endsWith( "Action" )
                        )
        )
      ) ||
      ProcessorUtil
        .getFieldElements( typeElement )
        .stream()
        .anyMatch( e -> e.getAnnotationMirrors()
          .stream()
          .map( a -> a.getAnnotationType().toString() )
          .anyMatch( n -> n.equals( Constants.CASCADE_DISPOSE_ANNOTATION_CLASSNAME ) ||
                          n.equals( Constants.COMPONENT_DEPENDENCY_ANNOTATION_CLASSNAME ) )
        );

    descriptor.setHasArezElements( hasArezElements );

    ensureMemoizeMatchesExpectations( typeElement );

    descriptor.setMemoizeMethods( getMemoizeMethods( typeElement ) );
  }

  private ComponentType extractComponentType( @Nonnull final TypeElement typeElement )
  {
    final VariableElement declaredTypeEnum = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "type" ).getValue();
    return ComponentType.valueOf( declaredTypeEnum.getSimpleName().toString() );
  }

  private void ensureMemoizeMatchesExpectations( @Nonnull final TypeElement typeElement )
  {
    final TypeElement computedElement =
      processingEnv.getElementUtils().getTypeElement( Constants.MEMOIZE_ANNOTATION_CLASSNAME );
    final Set<String> parameters = computedElement.getEnclosedElements()
      .stream()
      .map( e -> e.getSimpleName().toString() )
      .collect( Collectors.toSet() );
    if ( !( parameters.contains( "name" ) &&
            parameters.contains( "priority" ) &&
            parameters.contains( "keepAlive" ) &&
            parameters.contains( "reportResult" ) &&
            parameters.contains( "observeLowerPriorityDependencies" ) &&
            parameters.contains( "depType" ) &&
            6 == parameters.size() ) )
    {
      throw new ReactProcessorException( "The @" + Constants.MEMOIZE_ANNOTATION_CLASSNAME + " annotation was " +
                                         "expected to have the parameters name, priority, keepAlive, reportResult, " +
                                         "depType and observeLowerPriorityDependencies but has " + parameters +
                                         ". The react4j annotation processor needs to be updated to " +
                                         "handle the change in parameters.", typeElement );

    }
  }

  private boolean shouldUpdateOnChange( @Nonnull final ExecutableElement method,
                                        final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "shouldUpdateOnChange" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ReactProcessorException( "@Prop target has specified both immutable=true and " +
                                             "shouldUpdateOnChange=ENABLE which is an invalid combination.",
                                             method );
        }
        return true;
      case "DISABLE":
        return false;
      default:
        return !immutable;
    }
  }

  private boolean isPropObservable( @Nonnull final ComponentDescriptor descriptor,
                                    @Nonnull final ExecutableElement method,
                                    final boolean shouldUpdateOnChange,
                                    final boolean immutable )
  {
    final VariableElement parameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "observable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        if ( immutable )
        {
          throw new ReactProcessorException( "@Prop target has specified both immutable=true and " +
                                             "observable=ENABLE which is an invalid combination.",
                                             method );
        }
        return true;
      case "DISABLE":
        return false;
      default:
        return shouldUpdateOnChange && hasAnyArezObserverMethods( descriptor.getElement() );
    }
  }

  private boolean hasAnyArezObserverMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> ProcessorUtil.hasAnnotationOfType( m, Constants.MEMOIZE_ANNOTATION_CLASSNAME ) ||
                      ( ProcessorUtil.hasAnnotationOfType( m, Constants.OBSERVE_ANNOTATION_CLASSNAME ) &&
                        ( !m.getParameters().isEmpty() || !m.getSimpleName().toString().equals( "trackRender" ) ) ) );
  }

  private boolean isPropImmutable( @Nonnull final ExecutableElement method )
  {
    return (Boolean) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                       method,
                                                       Constants.PROP_ANNOTATION_CLASSNAME,
                                                       "immutable" ).getValue();
  }

  private boolean isPropDisposable( @Nonnull final ExecutableElement method, @Nonnull final Element propType )
  {
    final VariableElement parameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "disposable" ).getValue();
    switch ( parameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return ElementKind.CLASS == propType.getKind() &&
               ProcessorUtil.hasAnnotationOfType( propType, Constants.AREZ_COMPONENT_ANNOTATION_CLASSNAME );
    }
  }

  /**
   * Return @Memoize that have not had the priority parameter explicitly set.
   */
  @Nonnull
  private List<MethodDescriptor> getMemoizeMethods( @Nonnull final TypeElement typeElement )
  {
    final DeclaredType type = (DeclaredType) typeElement.asType();
    return getMethods( typeElement )
      .stream()
      .filter( method -> !method.getModifiers().contains( Modifier.PRIVATE ) )
      .filter( method -> {
        final AnnotationMirror mirror =
          ProcessorUtil.findAnnotationByType( method, Constants.MEMOIZE_ANNOTATION_CLASSNAME );
        return null != mirror &&
               mirror.getElementValues().keySet().stream()
                 .noneMatch( v -> "priority".equals( v.getSimpleName().toString() ) );
      } )
      .map( m -> new MethodDescriptor( m, (ExecutableType) processingEnv.getTypeUtils().asMemberOf( type, m ) ) )
      .collect( Collectors.toList() );
  }

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String[] annotationTypes =
      new String[]{ Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME,
                    Constants.PROP_VALIDATE_ANNOTATION_CLASSNAME,
                    Constants.ON_PROP_CHANGE_ANNOTATION_CLASSNAME,
                    Constants.PROP_ANNOTATION_CLASSNAME };
    for ( int i = 0; i < annotationTypes.length; i++ )
    {
      final String type1 = annotationTypes[ i ];
      final Object annotation1 = ProcessorUtil.findAnnotationByType( method, type1 );
      if ( null != annotation1 )
      {
        for ( int j = i + 1; j < annotationTypes.length; j++ )
        {
          final String type2 = annotationTypes[ j ];
          final Object annotation2 = ProcessorUtil.findAnnotationByType( method, type2 );
          if ( null != annotation2 )
          {
            final String message =
              "Method can not be annotated with both @" + ProcessorUtil.toSimpleName( type1 ) +
              " and @" + ProcessorUtil.toSimpleName( type2 );
            throw new ReactProcessorException( message, method );
          }
        }
      }
    }
  }

  @Nonnull
  private List<ExecutableElement> getMethods( @Nonnull final TypeElement typeElement )
  {
    return ProcessorUtil.getMethods( typeElement, processingEnv.getTypeUtils() );
  }

  private boolean isArezAnnotation( @Nonnull final String classname )
  {
    return classname.startsWith( "arez.annotations." );
  }
}
