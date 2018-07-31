package react4j.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
  private static final List<String> LIFECYCLE_METHODS =
    Arrays.asList( "componentDidMount",
                   "componentDidUpdate",
                   "componentWillUnmount",
                   "componentDidCatch",
                   "shouldComponentUpdate" );
  /**
   * Cache of lifecycle names to methods as defined by Component.
   */
  private final HashMap<String, ExecutableElement> _componentLifecycleMethods = new HashMap<>();
  /**
   * Cache of render method method as defined by Component.
   */
  private ExecutableElement _componentRenderMethod;
  /**
   * Elements that were unresolved and have been deferred to a later compilation cycle.
   */
  @Nonnull
  private HashSet<TypeElement> _deferred = new HashSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    // Clear method caches to avoid potential inter-run problems
    _componentLifecycleMethods.clear();
    _componentRenderMethod = null;

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
    return true;
  }

  private void processingErrorMessage( @Nonnull final TypeElement target )
  {
    processingEnv
      .getMessager()
      .printMessage( ERROR,
                     "ReactProcessor unable to process " + target.getQualifiedName() +
                     " because not all of its dependencies could be resolved. Check for " +
                     "compilation errors or a circular dependency with generated code.",
                     target );
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
        processingEnv.getMessager().printMessage( ERROR, ioe.getMessage(), element );
      }
      catch ( final ReactProcessorException e )
      {
        processingEnv.getMessager().printMessage( ERROR, e.getMessage(), e.getElement() );
      }
      catch ( final Throwable e )
      {
        final StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter( sw ) );
        sw.flush();

        final String message =
          "Unexpected error will running the " + getClass().getName() + " processor. This has " +
          "resulted in a failure to process the code and has left the compiler in an invalid " +
          "state. Please report the failure to the developers so that it can be fixed.\n" +
          " Report the error at: https://github.com/react4j/react4j/issues\n" +
          "\n\n" +
          sw.toString();
        processingEnv.getMessager().printMessage( ERROR, message, element );
      }
    }
  }

  private void process( @Nonnull final TypeElement element )
    throws IOException, ReactProcessorException
  {
    final ComponentDescriptor descriptor = parse( element );
    if ( descriptor.needsHelper() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentHelper( descriptor ) );
    }
    emitTypeSpec( descriptor.getPackageName(), Generator.buildEnhancedComponent( descriptor ) );
    emitTypeSpec( descriptor.getPackageName(), Generator.buildComponentBuilder( descriptor ) );
    if ( descriptor.needsDaggerIntegration() )
    {
      emitTypeSpec( descriptor.getPackageName(), Generator.buildDaggerFactory( descriptor ) );
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

  @Nonnull
  private ComponentDescriptor parse( @Nonnull final TypeElement typeElement )
  {
    final String name = deriveComponentName( typeElement );
    final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf( typeElement );
    final ComponentDescriptor descriptor =
      new ComponentDescriptor( processingEnv.getElementUtils(),
                               processingEnv.getSourceVersion(),
                               name,
                               packageElement,
                               typeElement );

    determineComponentType( descriptor, typeElement );
    determineLifecycleMethods( typeElement, descriptor );
    determineRenderMethod( typeElement, descriptor );
    determineCallbacks( descriptor );
    determineProps( descriptor );
    determineDefaultPropsMethods( descriptor );
    determineDefaultPropsFields( descriptor );
    determineStateValues( descriptor );

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

    verifyNoUnexpectedAbstractMethod( descriptor );

    return descriptor;
  }

  private void linkStateMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> candidates =
      getMethods( descriptor.getElement() )
        .stream()
        .filter( m -> m.getModifiers().contains( Modifier.ABSTRACT ) )
        .filter( m -> null == ProcessorUtil.findAnnotationByType( descriptor.getElement(),
                                                                  Constants.PROP_ANNOTATION_CLASSNAME ) &&
                      null == ProcessorUtil.findAnnotationByType( descriptor.getElement(),
                                                                  Constants.STATE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );
    for ( final ExecutableElement method : candidates )
    {
      final ExecutableType methodType =
        (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

      if ( method.getReturnType().getKind() == TypeKind.VOID && 1 == method.getParameters().size() )
      {
        final String stateName =
          ProcessorUtil.getPropertyMutatorName( method, ProcessorUtil.SENTINEL_NAME );
        final StateValueDescriptor stateValueNamed = descriptor.findStateValueNamed( stateName );
        if ( null != stateValueNamed && !stateValueNamed.hasSetter() )
        {
          stateValueNamed.setSetter( method, methodType );
        }
      }
      else if ( method.getReturnType().getKind() != TypeKind.VOID && 0 == method.getParameters().size() )
      {
        final String stateName =
          ProcessorUtil.getPropertyAccessorName( method, ProcessorUtil.SENTINEL_NAME );
        final StateValueDescriptor stateValueNamed = descriptor.findStateValueNamed( stateName );
        if ( null != stateValueNamed && !stateValueNamed.hasGetter() )
        {
          stateValueNamed.setGetter( method, methodType );
        }
      }
    }
  }

  private void verifyNoUnexpectedAbstractMethod( @Nonnull final ComponentDescriptor descriptor )
  {
    if ( !descriptor.isArezComponent() )
    {
      final ExecutableElement abstractMethod =
        getMethods( descriptor.getElement() )
          .stream()
          .filter( m -> m.getModifiers().contains( Modifier.ABSTRACT ) )
          // @Props and @State methods are expected to be null
          .filter( m -> descriptor.getProps().stream().noneMatch( p -> p.getMethod() == m ) &&
                        descriptor.getStateValues().stream()
                          .noneMatch( p -> p.getGetter() == m || p.getSetter() == m ) )
          .findAny()
          .orElse( null );
      if ( null != abstractMethod )
      {
        throw new ReactProcessorException( "@ReactComponent target has an unexpected abstract method",
                                           abstractMethod );
      }
    }
  }

  private void determineDefaultPropsMethods( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> defaultPropsMethods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
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
      final ExecutableType methodType = (ExecutableType) processingEnv.getTypeUtils()
        .asMemberOf( descriptor.getDeclaredType(), method );
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       prop.getMethodType().getReturnType() ) )
      {
        throw new ReactProcessorException( "@PropDefault target has a return type that is not assignable to the " +
                                           "return type of the associated @Prop annotated method.", method );
      }
      prop.setDefaultMethod( method );
    }
  }

  private void determineDefaultPropsFields( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<VariableElement> defaultPropsFields =
      ProcessorUtil.getFieldElements( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_DEFAULT_ANNOTATION_CLASSNAME ) )
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

  private void determineCallbacks( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<CallbackDescriptor> callbacks =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.CALLBACK_ANNOTATION_CLASSNAME ) )
        .map( m -> createCallbackDescriptor( descriptor, m ) )
        .collect( Collectors.toList() );
    for ( final CallbackDescriptor callback : callbacks )
    {
      final ExecutableElement method = callback.getMethod();
      final TypeElement callbackType = getCallbackType( method );
      if ( ElementKind.INTERFACE != callbackType.getKind() )
      {
        throw new ReactProcessorException( "The @Callback specified an invalid type that is not an interface.",
                                           callback.getMethod() );
      }
      final CallbackDescriptor matched = callbacks.stream()
        .filter( h -> h != callback && h.getName().equals( callback.getName() ) )
        .findAny().orElse( null );
      if ( null != matched )
      {
        throw new ReactProcessorException( "The @Callback has the same name as the callback defined by " +
                                           matched.getMethod() + ".", callback.getMethod() );
      }
      final CallbackDescriptor matched2 = callbacks.stream()
        .filter( h -> h != callback &&
                      h.getMethod().getSimpleName().equals( callback.getMethod().getSimpleName() ) )
        .findAny().orElse( null );
      if ( null != matched2 )
      {
        throw new ReactProcessorException( "The @Callback has the same method name as the callback defined " +
                                           "by " + matched2.getMethod() + ".", callback.getMethod() );
      }
      final ExecutableType methodType = callback.getMethodType();
      if ( !processingEnv.getTypeUtils().isAssignable( methodType.getReturnType(),
                                                       callback.getCallbackMethod().getReturnType() ) )
      {
        throw new ReactProcessorException( "@Callback target has a return type that is not assignable to the return " +
                                           "type defined in the callback type " + callbackType.getQualifiedName() + ".",
                                           callback.getMethod() );
      }

      final List<? extends TypeMirror> parameters = methodType.getParameterTypes();
      if ( !parameters.isEmpty() )
      {
        // Our annotated callback method has parameters so they should exactly align
        // in count and type with the parameters in the callback method
        final ExecutableElement target = callback.getCallbackMethod();
        final List<? extends VariableElement> targetParameters = target.getParameters();
        if ( targetParameters.size() != parameters.size() )
        {
          throw new ReactProcessorException( "The @Callback target has " + parameters.size() + " parameters " +
                                             "but the type parameter specified a callback with method type " +
                                             callback.getCallbackType().getQualifiedName() + " that has a " +
                                             "callback method with " + targetParameters.size() + " parameters. The " +
                                             "@Callback target should have zero parameters or match the number " +
                                             "of parameter in the target method " + target.getSimpleName() + ".",
                                             callback.getMethod() );
        }
        for ( int i = 0; i < parameters.size(); i++ )
        {
          final TypeMirror parameterType = parameters.get( i );
          final VariableElement element = targetParameters.get( i );
          final TypeMirror targetParameterType = element.asType();
          final TypeMirror targetErased = processingEnv.getTypeUtils().erasure( targetParameterType );
          final TypeMirror parameterErased = processingEnv.getTypeUtils().erasure( parameterType );
          if ( !processingEnv.getTypeUtils().isAssignable( targetErased, parameterErased ) )
          {
            throw new ReactProcessorException( "The @Callback target parameter named " +
                                               callback.getMethod().getParameters().get( i ).getSimpleName() +
                                               " of type " + parameterType + " is not assignable from target type " +
                                               targetParameterType + " of parameter " + element.getSimpleName() +
                                               " in method " + callback.getCallbackType().getQualifiedName() +
                                               "." + target.getSimpleName() + ".",
                                               callback.getMethod() );
          }
        }
      }
    }

    descriptor.setCallbacks( callbacks );
  }

  @Nonnull
  private CallbackDescriptor createCallbackDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                                       @Nonnull final ExecutableElement method )
  {
    verifyNoDuplicateAnnotations( method );
    final String name = deriveCallbackName( method );
    final TypeElement callbackType = getCallbackType( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );
    final List<ExecutableElement> callbackMethods =
      getMethods( callbackType ).stream().
        filter( m11 -> m11.getModifiers().contains( Modifier.ABSTRACT ) ).
        collect( Collectors.toList() );
    if ( callbackMethods.isEmpty() )
    {
      throw new ReactProcessorException( "Method annotated with @Callback specified type " +
                                         callbackType.getQualifiedName() + " that has no abstract method and " +
                                         "thus is not a functional interface", method );
    }
    else if ( callbackMethods.size() > 1 )
    {
      throw new ReactProcessorException( "Method annotated with @Callback specified type " +
                                         callbackType.getQualifiedName() + " that has more than 1 abstract " +
                                         "method and thus is not a functional interface", method );
    }

    final boolean initCallbackContext = shouldInitCallbackContext( descriptor, method );

    if ( initCallbackContext &&
         descriptor.isArezComponent() &&
         null != ProcessorUtil.findAnnotationByType( method, Constants.ACTION_ANNOTATION_CLASSNAME ) )
    {
      final String message =
        "@Callback target is also annotated with @arez.annotations.Action but the @Callback parameter " +
        "'initCallbackContext' is not set to Feature.DISABLE which would stop react4j from also " +
        "annotating the method with @Action. Please remove @Action or change the 'initCallbackContext' to " +
        "Feature.DISABLE.";
      throw new ReactProcessorException( message, method );
    }
    MethodChecks.mustNotBeAbstract( Constants.CALLBACK_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustBeSubclassCallable( descriptor.getElement(), Constants.CALLBACK_ANNOTATION_CLASSNAME, method );
    return new CallbackDescriptor( name,
                                   method,
                                   methodType,
                                   callbackType,
                                   callbackMethods.get( 0 ),
                                   initCallbackContext );
  }

  private boolean shouldInitCallbackContext( @Nonnull final ComponentDescriptor descriptor,
                                             @Nonnull final ExecutableElement method )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                        "initCallbackContext" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return descriptor.isArezComponent();
    }
  }

  @Nonnull
  private TypeElement getCallbackType( @Nonnull final ExecutableElement method )
  {
    final DeclaredType typeMirror =
      ProcessorUtil.getTypeMirrorAnnotationParameter( processingEnv.getElementUtils(),
                                                      method,
                                                      Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                                      "value" );
    assert null != typeMirror;
    return (TypeElement) processingEnv.getTypeUtils().asElement( typeMirror );
  }

  @Nonnull
  private String deriveCallbackName( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String name =
      (String) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 method,
                                                 Constants.CALLBACK_ANNOTATION_CLASSNAME,
                                                 "name" ).getValue();

    if ( ProcessorUtil.isSentinelName( name ) )
    {
      return method.getSimpleName().toString();
    }
    else
    {
      if ( !SourceVersion.isIdentifier( name ) )
      {
        throw new ReactProcessorException( "@Callback target specified an invalid name '" + name + "'. The " +
                                           "name must be a valid java identifier.", method );
      }
      else if ( SourceVersion.isKeyword( name ) )
      {
        throw new ReactProcessorException( "@Callback target specified an invalid name '" + name + "'. The " +
                                           "name must not be a java keyword.", method );
      }
      return name;
    }
  }

  private void determineProps( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<PropDescriptor> props =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.PROP_ANNOTATION_CLASSNAME ) )
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
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        prop.getMethod(),
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "require" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return !prop.hasDefaultMethod() &&
               !prop.hasDefaultField() &&
               null == ProcessorUtil.findAnnotationByType( prop.getMethod(), Constants.NULLABLE_ANNOTATION_CLASSNAME );
    }
  }

  @Nonnull
  private PropDescriptor createPropDescriptor( @Nonnull final ComponentDescriptor descriptor,
                                               @Nonnull final ExecutableElement method )
  {
    final String name = derivePropName( method );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

    verifyNoDuplicateAnnotations( method );
    MethodChecks.mustBeAbstract( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotHaveAnyParameters( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustReturnAValue( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.PROP_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.PROP_ANNOTATION_CLASSNAME,
                                                           method );

    if ( "key".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'key' is invalid as the name references value used in the " +
                                         "reconciliation process. This value can be accessed via Component.getKey()",
                                         method );
    }
    else if ( "build".equals( name ) )
    {
      throw new ReactProcessorException( "@Prop named 'build' is invalid as it conflicts with the method named " +
                                         "build() that is used in the generated Builder classes",
                                         method );
    }
    else if ( "child".equals( name ) &&
              (
                methodType.getReturnType().getKind() != TypeKind.DECLARED &&
                !"react4j.ReactNode".equals( methodType.getReturnType().toString() )
              ) )
    {
      throw new ReactProcessorException( "@Prop named 'child' should be of type react4j.ReactNode", method );
    }
    else if ( "children".equals( name ) &&
              (
                methodType.getReturnType().getKind() != TypeKind.DECLARED &&
                !"react4j.ReactNode[]".equals( methodType.getReturnType().toString() )
              ) )
    {
      throw new ReactProcessorException( "@Prop named 'children' should be of type react4j.ReactNode[]", method );
    }

    final Element propType = processingEnv.getTypeUtils().asElement( method.getReturnType() );
    final boolean shouldUpdateOnChange = shouldUpdateOnChange( method );
    return new PropDescriptor( name, method, methodType, propType, shouldUpdateOnChange );
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

  private void determineStateValues( @Nonnull final ComponentDescriptor descriptor )
  {
    final List<ExecutableElement> methods =
      getMethods( descriptor.getElement() ).stream()
        .filter( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.STATE_ANNOTATION_CLASSNAME ) )
        .collect( Collectors.toList() );

    final Map<String, StateValueDescriptor> values = new HashMap<>();
    for ( final ExecutableElement method : methods )
    {
      final ExecutableType methodType =
        (ExecutableType) processingEnv.getTypeUtils().asMemberOf( descriptor.getDeclaredType(), method );

      parseStateValueMethod( descriptor, method, methodType, values );
    }

    final ArrayList<StateValueDescriptor> stateValues = new ArrayList<>( values.values() );
    descriptor.setStateValues( stateValues );

    linkStateMethods( descriptor );

    for ( final StateValueDescriptor stateValue : stateValues )
    {
      if ( !stateValue.hasGetter() )
      {
        throw new ReactProcessorException( "@State target defined setter but no getter was defined and no " +
                                           "getter could be automatically determined", stateValue.getSetter() );

      }
      else if ( !stateValue.hasSetter() )
      {
        throw new ReactProcessorException( "@State target defined getter but no setter was defined and no " +
                                           "setter could be automatically determined", stateValue.getGetter() );

      }
    }
    for ( final StateValueDescriptor stateValue : stateValues )
    {
      final TypeMirror returnType = stateValue.getGetterType().getReturnType();
      final TypeMirror parameterType = stateValue.getSetterType().getParameterTypes().get( 0 );
      if ( !processingEnv.getTypeUtils().isSameType( parameterType, returnType ) )
      {
        throw new ReactProcessorException( "@State property defines a setter and getter with different types." +
                                           " Getter type: " + returnType + " Setter type: " + parameterType + ".",
                                           stateValue.getGetter() );
      }
    }
  }

  private void parseStateValueMethod( @Nonnull final ComponentDescriptor descriptor,
                                      @Nonnull final ExecutableElement method,
                                      @Nonnull final ExecutableType methodType,
                                      @Nonnull final Map<String, StateValueDescriptor> values )
  {
    final String declaredName = getAnnotationParameter( method, Constants.STATE_ANNOTATION_CLASSNAME, "name" );
    final TypeMirror returnType = method.getReturnType();

    final boolean setter = TypeKind.VOID == returnType.getKind();

    verifyNoDuplicateAnnotations( method );
    MethodChecks.mustBeAbstract( Constants.STATE_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotThrowAnyExceptions( Constants.STATE_ANNOTATION_CLASSNAME, method );
    MethodChecks.mustNotBePackageAccessInDifferentPackage( descriptor.getElement(),
                                                           Constants.STATE_ANNOTATION_CLASSNAME,
                                                           method );

    final String name;
    if ( setter )
    {
      if ( 1 != method.getParameters().size() )
      {
        throw new ReactProcessorException( "@State target must have a single parameter", method );
      }

      MethodChecks.mustNotReturnAValue( Constants.STATE_ANNOTATION_CLASSNAME, method );

      name = ProcessorUtil.getPropertyMutatorName( method, declaredName );
    }
    else
    {
      MethodChecks.mustNotHaveAnyParameters( Constants.STATE_ANNOTATION_CLASSNAME, method );
      MethodChecks.mustReturnAValue( Constants.STATE_ANNOTATION_CLASSNAME, method );

      name = ProcessorUtil.getPropertyAccessorName( method, declaredName );
    }

    if ( !SourceVersion.isIdentifier( name ) )
    {
      throw new ReactProcessorException( "@State target specified an invalid name '" + name + "'. The " +
                                         "name must be a valid java identifier.", method );
    }
    else if ( SourceVersion.isKeyword( name ) )
    {
      throw new ReactProcessorException( "@State target specified an invalid name '" + name + "'. The " +
                                         "name must not be a java keyword.", method );
    }

    //Find or create descriptor
    final StateValueDescriptor stateValueDescriptor = values.computeIfAbsent( name, StateValueDescriptor::new );
    if ( setter )
    {
      if ( stateValueDescriptor.hasSetter() )
      {
        throw new ReactProcessorException( "@State target defines duplicate state property mutator for " +
                                           "property named '" + name + "'. Existing mutator: " +
                                           stateValueDescriptor.getSetter().getSimpleName(), method );
      }
      stateValueDescriptor.setSetter( method, methodType );
    }
    else
    {
      if ( stateValueDescriptor.hasGetter() )
      {
        throw new ReactProcessorException( "@State target defines duplicate state property accessor for " +
                                           "property named '" + name + "'. Existing accessor: " +
                                           stateValueDescriptor.getGetter().getSimpleName(), method );
      }
      stateValueDescriptor.setGetter( method, methodType );
    }
  }

  private void determineLifecycleMethods( @Nonnull final TypeElement typeElement,
                                          @Nonnull final ComponentDescriptor descriptor )
  {
    /*
     * Get the list of lifecycle methods that have been overridden by typeElement
     * a parent class, or by a default method method implemented by typeElement or
     * a parent class.
     */
    final Collection<ExecutableElement> lifecycleMethods = getComponentLifecycleMethods().values();
    final Elements elementUtils = processingEnv.getElementUtils();
    final Types typeUtils = processingEnv.getTypeUtils();
    final TypeElement componentType = elementUtils.getTypeElement( Constants.COMPONENT_CLASSNAME );
    final List<MethodDescriptor> overriddenLifecycleMethods =
      // Get all methods on type parent classes, and default methods from interfaces
      getMethods( typeElement ).stream()
        // Only keep methods that override the lifecycle methods
        .filter( m -> lifecycleMethods.stream().anyMatch( l -> elementUtils.overrides( m, l, typeElement ) ) )
        //Remove those that come from the base classes
        .filter( m -> m.getEnclosingElement() != componentType )
        .map( m -> new MethodDescriptor( m, (ExecutableType) typeUtils.asMemberOf( descriptor.getDeclaredType(), m ) ) )
        .collect( Collectors.toList() );

    descriptor.setLifecycleMethods( overriddenLifecycleMethods );
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
   * Return the set of lifecycle methods as defined on the base component class.
   * Used to compare against those on type class to see if they override.
   */
  @Nonnull
  private HashMap<String, ExecutableElement> getComponentLifecycleMethods()
  {
    if ( _componentLifecycleMethods.isEmpty() )
    {
      final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
      for ( final ExecutableElement method : getMethods( componentType ) )
      {
        final String methodName = method.getSimpleName().toString();
        if ( LIFECYCLE_METHODS.contains( methodName ) )
        {
          _componentLifecycleMethods.put( methodName, method );
        }
      }

    }
    return _componentLifecycleMethods;
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

  private void determineComponentType( @Nonnull final ComponentDescriptor descriptor,
                                       @Nonnull final TypeElement typeElement )
  {
    final TypeElement componentType = processingEnv.getElementUtils().getTypeElement( Constants.COMPONENT_CLASSNAME );
    final TypeMirror rawComponentType = processingEnv.getTypeUtils().erasure( componentType.asType() );

    /*
     * Arez need not be on the classpath in which case this will return a null value to arezComponentType.
     * Our code should just gracefully handle this and perform none of the arez specific checks or generation.
     */
    @Nullable
    final TypeElement arezComponentType =
      processingEnv.getElementUtils().getTypeElement( "react4j.arez.ReactArezComponent" );
    @Nullable
    final TypeMirror rawArezComponentType =
      null == arezComponentType ? null : processingEnv.getTypeUtils().erasure( arezComponentType.asType() );

    final TypeMirror type = descriptor.getDeclaredType();

    final boolean isComponent = processingEnv.getTypeUtils().isSubtype( type, rawComponentType );
    final boolean isArezComponent =
      null != rawArezComponentType && processingEnv.getTypeUtils().isSubtype( type, rawArezComponentType );

    if ( !isComponent )
    {
      throw new ReactProcessorException( "@ReactComponent target must be a subclass of react4j.Component",
                                         typeElement );
    }
    else if ( isArezComponent )
    {
      final AnnotationMirror arezAnnotation = typeElement.getAnnotationMirrors().stream().
        filter( m -> m.getAnnotationType().toString().equals( "arez.annotations.ArezComponent" ) ).
        findAny().orElse( null );
      if ( null != arezAnnotation )
      {
        throw new ReactProcessorException( "@ReactComponent target extends react4j.arez.ReactArezComponent and should " +
                                           "not be annotated with arez.annotations.ArezComponent as " +
                                           "React4j will add annotation", typeElement );
      }
    }

    final boolean runArezScheduler =
      isArezComponent &&
      (
        hasAnyAutorunMethods( typeElement ) ||
        hasAnyKeepAliveComputedMethods( typeElement ) ||
        hasAnyDependencyMethods( typeElement )
      );

    final boolean needsInjection = isInjectionRequired( typeElement );
    final boolean isDaggerPresent = needsInjection && isDaggerRequired( typeElement );

    if ( isDaggerPresent && !descriptor.getDeclaredType().getTypeArguments().isEmpty() )
    {
      throw new ReactProcessorException( "@ReactComponent target has enabled dagger injection and the class " +
                                         "has type argument but type arguments on a component are not compatible " +
                                         "with dagger injected components", typeElement );
    }

    descriptor.setNeedsInjection( needsInjection );
    descriptor.setNeedsDaggerIntegration( isDaggerPresent );
    descriptor.setArezComponent( isArezComponent );
    descriptor.setRunArezScheduler( runArezScheduler );
  }

  private boolean shouldUpdateOnChange( @Nonnull final ExecutableElement method )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        method,
                                        Constants.PROP_ANNOTATION_CLASSNAME,
                                        "shouldUpdateOnChange" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return true;
    }
  }

  private boolean isInjectionRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "inject" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return ProcessorUtil.getFieldElements( typeElement ).stream().anyMatch( this::hasInjectAnnotation ) ||
               getMethods( typeElement ).
                 stream().anyMatch( this::hasInjectAnnotation );
    }
  }

  private boolean isDaggerRequired( @Nonnull final TypeElement typeElement )
  {
    final VariableElement injectParameter = (VariableElement)
      ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                        typeElement,
                                        Constants.REACT_COMPONENT_ANNOTATION_CLASSNAME,
                                        "dagger" ).getValue();
    switch ( injectParameter.getSimpleName().toString() )
    {
      case "ENABLE":
        return true;
      case "DISABLE":
        return false;
      default:
        return null != processingEnv.getElementUtils().getTypeElement( Constants.DAGGER_MODULE_CLASSNAME );
    }
  }

  private boolean hasAnyAutorunMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.AUTORUN_ANNOTATION_CLASSNAME ) );
  }

  private boolean hasAnyDependencyMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> null != ProcessorUtil.findAnnotationByType( m, Constants.DEPENDENCY_ANNOTATION_CLASSNAME ) );
  }

  private boolean hasAnyKeepAliveComputedMethods( @Nonnull final TypeElement typeElement )
  {
    return getMethods( typeElement )
      .stream()
      .anyMatch( m -> {
        final AnnotationValue annotationValue =
          ProcessorUtil.findAnnotationValue( processingEnv.getElementUtils(),
                                             m,
                                             Constants.COMPUTED_ANNOTATION_CLASSNAME,
                                             "keepAlive" );
        return null != annotationValue && (boolean) annotationValue.getValue();
      } );
  }

  private boolean hasInjectAnnotation( final Element method )
  {
    return null != ProcessorUtil.findAnnotationByType( method, Constants.INJECT_ANNOTATION_CLASSNAME );
  }

  @SuppressWarnings( { "unchecked", "SameParameterValue" } )
  private <T> T getAnnotationParameter( @Nonnull final Element element,
                                        @Nonnull final String annotationName,
                                        @Nonnull final String parameterName )
  {
    return (T) ProcessorUtil.getAnnotationValue( processingEnv.getElementUtils(),
                                                 element,
                                                 annotationName,
                                                 parameterName ).getValue();
  }

  private void verifyNoDuplicateAnnotations( @Nonnull final ExecutableElement method )
    throws ReactProcessorException
  {
    final String[] annotationTypes =
      new String[]{ Constants.CALLBACK_ANNOTATION_CLASSNAME,
                    Constants.STATE_ANNOTATION_CLASSNAME,
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
}
